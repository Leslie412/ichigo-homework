package com.ichigo.loyalty.controllers;

import com.ichigo.loyalty.api.TiersApi;
import com.ichigo.loyalty.model.CustomerInfo;
import com.ichigo.loyalty.repositories.OrdersRepository;
import com.ichigo.loyalty.services.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TiersApiController implements TiersApi {

    @Autowired
    OrderServiceImpl orderService;

    @Override
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<CustomerInfo> getCustomerInfoByID(String customerid) {

        var customerInfo = orderService.getCustomerInfoById(customerid);
        if (customerInfo == null) {
            return new ResponseEntity<CustomerInfo>(customerInfo, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<CustomerInfo>(customerInfo, HttpStatus.OK);
    }
}
