package com.haihaycode.techvibesservice.controller;

import com.haihaycode.techvibesservice.entity.OrderEntity;
import com.haihaycode.techvibesservice.model.ResponseWrapper;
import com.haihaycode.techvibesservice.security.UserPrincipal;
import com.haihaycode.techvibesservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/public")
@CrossOrigin("*")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/orders")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<ResponseWrapper<Page<OrderEntity>>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date endDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startUpdateDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endUpdateDate,
            @RequestParam(required = false) Long statusId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Optional<String> sort,
            @AuthenticationPrincipal UserPrincipal userPrincipal
            ) {
        String sortField = "id";
        Sort.Direction sortDirection = Sort.Direction.DESC;

        if (sort.isPresent()) {
            String[] sortParams = sort.get().split(",");
            sortField = sortParams[0];
            if (sortParams.length > 1) {
                sortDirection = Sort.Direction.fromString(sortParams[1]);
            }
        }
        Pageable pageable = PageRequest.of(page, limit, Sort.by(new Sort.Order(sortDirection, sortField)));
        ResponseWrapper<Page<OrderEntity>> response = new ResponseWrapper<>(HttpStatus.OK, "Orders retrieved successfully",orderService.findOrdersByCriteria(keyword, minPrice, maxPrice, startDate, endDate,
                startUpdateDate, endUpdateDate,userPrincipal.getUserId() , statusId, pageable));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/look-up-orders")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<OrderEntity>> getOrderDetails(@RequestParam String orderCode, @RequestParam int phone) {
        return orderService.getOrderDetails(orderCode, phone)
                .map(order -> ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Order found",order)))
                .orElse(ResponseEntity.status(404).body(new ResponseWrapper<>(HttpStatus.NOT_FOUND, "Order not found",null)));
    }
}
