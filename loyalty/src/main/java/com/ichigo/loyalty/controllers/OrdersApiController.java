package com.ichigo.loyalty.controllers;

import com.ichigo.loyalty.api.OrdersApi;
import com.ichigo.loyalty.model.OrderItem;
import com.ichigo.loyalty.model.OrderListInner;
import com.ichigo.loyalty.services.OrderServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class OrdersApiController implements OrdersApi {

    Logger logger = LoggerFactory.getLogger(OrdersApiController.class);

    @Autowired
    OrderServiceImpl orderServiceImpl;

    @Override
    public ResponseEntity<Void> createNewOrder(OrderItem orderItem) {

        logger.info("new order, customerId:{}, customerName:{}, orderid:{}, totalInCents:{}, date:{}"
                , orderItem.getCustomerId()
                , orderItem.getCustomerName()
                , orderItem.getOrderId()
                , orderItem.getTotalInCents()
                , orderItem.getDate());

        orderServiceImpl.addOrder(orderItem);

        return null;
    }

    @Override
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<List<OrderListInner>> listOrders(String customerid) {
        var orders = orderServiceImpl.getOrdersByCustomerId(customerid);

        if (orders.size() == 0) {
            return new ResponseEntity<>(orders, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
}
