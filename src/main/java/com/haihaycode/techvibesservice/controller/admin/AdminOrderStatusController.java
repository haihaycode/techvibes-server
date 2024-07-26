package com.haihaycode.techvibesservice.controller.admin;

import com.haihaycode.techvibesservice.entity.OrderStatusEntity;
import com.haihaycode.techvibesservice.model.ResponseWrapper;
import com.haihaycode.techvibesservice.security.UserPrincipal;
import com.haihaycode.techvibesservice.service.OrderStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminOrderStatusController {
    @Autowired
    private OrderStatusService orderStatusService;
    @GetMapping("/order-status")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<List<OrderStatusEntity>>> get() {
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "OrderStatus fetched successful" , orderStatusService.getAll()));
    }
    @GetMapping("/order-status/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<OrderStatusEntity>> getById( @PathVariable Long id) {
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "OrderStatus fetched successful" , orderStatusService.getById(id)));
    }
}
