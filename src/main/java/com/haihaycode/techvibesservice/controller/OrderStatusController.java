package com.haihaycode.techvibesservice.controller;

import com.haihaycode.techvibesservice.entity.OrderStatusEntity;
import com.haihaycode.techvibesservice.model.ResponseWrapper;
import com.haihaycode.techvibesservice.service.OrderStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class OrderStatusController {
    @Autowired
    private OrderStatusService orderStatusService;
    @GetMapping("/order-status")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<List<OrderStatusEntity>>> getAll() {
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "OrderStatus fetched successful" , orderStatusService.getAll()));
    }


}
