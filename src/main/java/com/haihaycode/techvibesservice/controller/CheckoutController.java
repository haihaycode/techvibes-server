package com.haihaycode.techvibesservice.controller;

import com.haihaycode.techvibesservice.entity.OrderEntity;
import com.haihaycode.techvibesservice.model.ResponseWrapper;
import com.haihaycode.techvibesservice.security.UserPrincipal;
import com.haihaycode.techvibesservice.service.CheckoutService;
import com.haihaycode.techvibesservice.service.vnpay.VNPAYService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/checkout")
@CrossOrigin("*")
public class CheckoutController {

    @Autowired
    private CheckoutService checkoutService;
    @Autowired
    private VNPAYService vnpayService;

    @PostMapping("/cod")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN','STAFF')")
    public ResponseEntity<OrderEntity> checkout(@AuthenticationPrincipal UserPrincipal principal) {
        OrderEntity order = checkoutService.placeOrder(principal.getUserId());
        return ResponseEntity.ok(order);
    }


    @PostMapping("/bank")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<String>> pay(@AuthenticationPrincipal UserPrincipal principal, HttpServletRequest request) {
        String paymentUrl = checkoutService.placeOrderVNPAY(principal.getUserId(), request);
        return new ResponseEntity<>(new ResponseWrapper<>(HttpStatus.OK, paymentUrl, paymentUrl), HttpStatus.OK);
    }

}
