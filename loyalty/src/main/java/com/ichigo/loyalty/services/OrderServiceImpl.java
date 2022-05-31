package com.ichigo.loyalty.services;

import com.ichigo.loyalty.model.CustomerInfo;
import com.ichigo.loyalty.model.OrderItem;
import com.ichigo.loyalty.model.OrderListInner;
import com.ichigo.loyalty.repositories.CustomerRepository;
import com.ichigo.loyalty.repositories.OrdersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl {
    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Transactional
    //@CacheEvict(value = "CustomerInfo", allEntries=true)
    @CacheEvict(value = {"CustomerInfo", "customerOrders"}, key="#item.customerId")
    public void addOrder(OrderItem item) {
        // add new customer, if exists skip
        var customer = customerRepository.findCustomerByCustomerId(item.getCustomerId());
        if (customer == null) {
            customerRepository.addCustomer(item.getCustomerId(), item.getCustomerName());
        }

        // add new order, if exists skip
        ordersRepository.addOrderTbl(
                item.getOrderId()
                , item.getCustomerId()
                , item.getTotalInCents()
                , new Timestamp(item.getDate().getTime()));

        // calculate new customer's tier
        if (customer == null) {
            customerRepository.updateCustomerTier(item.getCustomerId());
        }
    }

    @Cacheable("customerOrders")
    public List<OrderListInner> getOrdersByCustomerId(String customerId) {

        var orderList = ordersRepository.findByCustomerId(customerId);
        return orderList.stream().map(o -> {
            var inner = new OrderListInner();
            inner.setOrderdate(o.getOrderDate());
            inner.setOrderid(o.getOrderId());
            inner.setOrdertotal(o.getTotalInCents());
            return inner;
        }).collect(Collectors.toList());
    }

    @Cacheable("CustomerInfo")
    public CustomerInfo getCustomerInfoById(String customerId) {

        //check if customer exists
        var customer = customerRepository.findCustomerByCustomerId(customerId);
        if (customer == null) {
            return null;
        }

        var res = new CustomerInfo();
        var info1 = ordersRepository.getCustomerInfo1(customerId);
        var info2 = ordersRepository.getCustomerInfo2(customerId);

        res.setAmountspent(((BigDecimal)info1.get("amount_from_start_date")).intValue());

        res.setTier((String)info1.get("tier"));
        var formatter = new SimpleDateFormat("yyyy/MM/dd");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            res.setStartdate(formatter.parse((String) info1.get("start_date")));
        } catch (ParseException e) {
            log.error(e.toString());
        }

        if (info2.size() == 0) {
            var now = LocalDate.now();
            var firstOfNextYear = now.with(TemporalAdjusters.firstDayOfNextYear());
            var downDate = Date.from(firstOfNextYear.atStartOfDay(ZoneId.of("UTC")).toInstant());
            res.setDowntier("Bronze");
            res.setDowndate(downDate);
            switch ((String)info1.get("tier")) {
                case "Gold":
                    res.setSpendneeded(0);
                    res.setAmount4next(50000);
                    break;
                case "Silver":
                    res.setSpendneeded(10000);
                    res.setAmount4next(50000);
                    break;
                default:
                    res.setSpendneeded(0);
                    res.setAmount4next(10000);
            }

        } else {
            res.setSpendneeded(((BigDecimal) info2.get("amount_to_keep_tier")).intValue());
            res.setDowntier((String) info2.get("down_tier"));
            res.setAmount4next(((BigDecimal)info2.get("amount_to_next_tier")).intValue());
            try {
                res.setStartdate(formatter.parse((String) info1.get("start_date")));
                res.setDowndate(formatter.parse((String) info2.get("down_grade_date")));
            } catch (ParseException e) {
                log.error(e.toString());
            }
        }
        return res;
    }
}
