package com.ichigo.loyalty.repositories;

import com.ichigo.loyalty.entity.Customer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, String> {

    @Modifying
    @Query(value = "INSERT INTO  customer (customer_id, customer_name) VALUES(?1, ?2)"
            , nativeQuery = true)
    void addCustomer(String customerId, String customerName);

    @Modifying
    @Query(value = "WITH new_tiers AS (" +
            "  SELECT customer_id, " +
            "  CASE" +
            "   WHEN SUM(total_in_cents) < 10000 THEN 'Bronze'" +
            "   WHEN SUM(total_in_cents) >= 10000 AND SUM(total_in_cents) < 50000 THEN 'Silver'" +
            "   WHEN SUM(total_in_cents) > 50000 THEN 'Gold'" +
            "  END tier" +
            "  FROM order_tbl WHERE YEAR(order_date) >= YEAR(CURDATE() + INTERVAL -1 YEAR) AND customer_id = ?1" +
            " )" +
            " UPDATE customer c, new_tiers n SET c.tier = n.tier, c.update_time = NOW() WHERE c.customer_id = n.customer_id", nativeQuery = true)
    void updateCustomerTier(String customerId);

    Customer findCustomerByCustomerId(String customerId);
}
