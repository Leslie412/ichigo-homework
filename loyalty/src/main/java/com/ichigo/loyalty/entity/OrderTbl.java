package com.ichigo.loyalty.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@IdClass(OrderID.class)
public class OrderTbl {
    @Id
    private String orderId;
    @Id
    private String customerId;
    private int totalInCents;
    private Timestamp orderDate;
}
