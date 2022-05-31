package com.ichigo.loyalty.repositories;

import com.ichigo.loyalty.entity.OrderTbl;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Transactional
public interface OrdersRepository extends CrudRepository<OrderTbl, String> {
    @Modifying
    @Query(value =
            "INSERT IGNORE INTO order_tbl " +
                    "VALUES(?1, ?2, ?3, ?4)", nativeQuery = true)
    void addOrderTbl(String orderId, String customerId, int totalInCents, Timestamp orderDate);

    @Query(value = "SELECT " +
            "* " +
            "FROM order_tbl " +
            "WHERE customer_id = ?1 ORDER BY order_date DESC", nativeQuery = true)
    List<OrderTbl> findByCustomerId(String customerid);

    @Query(value = "SELECT c.tier" +
            "    , CONCAT(CAST(YEAR(CURDATE() + INTERVAL -1 YEAR) AS CHAR), '/01/01') start_date" +
            "    , SUM(o.total_in_cents) amount_from_start_date " +
            "FROM " +
            "    customer c JOIN order_tbl o ON c.customer_id = o.customer_id " +
            "WHERE " +
            "    YEAR(o.order_date) >= YEAR(CURDATE() + INTERVAL -1 YEAR)" +
            "    AND c.customer_id = ?1 " +
            "GROUP BY" +
            "    o.customer_id", nativeQuery = true)
    Map<String, Object> getCustomerInfo1(String customer_id);

    @Query(value = "SELECT " +
            "    CASE" +
            "        WHEN c.tier = 'Silver' AND SUM(o.total_in_cents) < 10000 THEN 'Bronze'" +
            "        WHEN c.tier = 'Gold' and SUM(o.total_in_cents) < 10000 THEN 'Bronze'" +
            "        WHEN c.tier = 'Gold' and SUM(o.total_in_cents) >= 10000 AND SUM(o.total_in_cents) < 50000 THEN 'Silver'" +
            "        ELSE ''" +
            "    END down_tier" +
            "    , CONCAT(CAST(YEAR(CURDATE() + INTERVAL +1 YEAR) AS CHAR), '/01/01') down_grade_date" +
            "    , CASE" +
            "        WHEN c.tier = 'Silver' AND SUM(o.total_in_cents) < 10000 THEN 10000 - SUM(o.total_in_cents)" +
            "        WHEN c.tier = 'Gold' AND SUM(o.total_in_cents) < 50000 THEN 50000 - SUM(o.total_in_cents)" +
            "        ELSE 0" +
            "      END amount_to_keep_tier  " +
            "    , CASE" +
            "        WHEN SUM(o.total_in_cents) < 10000 THEN 10000 - SUM(o.total_in_cents)" +
            "        WHEN SUM(o.total_in_cents) >= 10000 AND SUM(o.total_in_cents) < 50000 THEN 50000 - SUM(o.total_in_cents)" +
            "        ELSE 0" +
            "      END amount_to_next_tier  " +
            "FROM " +
            "    customer c LEFT JOIN order_tbl o ON c.customer_id = o.customer_id " +
            "WHERE " +
            "    YEAR(o.order_date) >= YEAR(CURDATE())" +
            "    AND c.customer_id = ?1 " +
            "GROUP BY" +
            "    o.customer_id", nativeQuery = true)
    Map<String, Object> getCustomerInfo2(String customer_id);


}
