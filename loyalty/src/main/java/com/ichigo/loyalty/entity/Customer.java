package com.ichigo.loyalty.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Customer {
    @Id
    private String customerId;
    private String customerName;
    private String tier;
    private Timestamp createTime;
    private Timestamp updateTime;
}
