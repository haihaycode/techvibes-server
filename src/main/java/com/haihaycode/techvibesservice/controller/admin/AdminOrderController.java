package com.haihaycode.techvibesservice.controller.admin;

import com.haihaycode.techvibesservice.entity.FavoriteEntity;
import com.haihaycode.techvibesservice.entity.OrderEntity;
import com.haihaycode.techvibesservice.model.ResponseWrapper;
import com.haihaycode.techvibesservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminOrderController {
    @Autowired
    private OrderService orderService;
    @GetMapping("/orders")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<Page<OrderEntity>>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startUpdateDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endUpdateDate,
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) Long statusId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Optional<String> sort
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
                startUpdateDate, endUpdateDate, accountId, statusId, pageable));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<OrderEntity>> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK,"Orders retrieved successfully",orderService.findOrderById(id)));
    }
    @PutMapping("/orders/{orderId}/status/{statusId}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<OrderEntity>> updateOrderStatus(@PathVariable Long orderId, @PathVariable Long statusId, @RequestBody  String notes) {
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK,"Orders Status updated successfully",orderService.updateOrderStatus(orderId, statusId,notes)));
    }

    @GetMapping("/order/export/excel/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<InputStreamResource> exportOrdersByIdToExcel(@PathVariable Long id) {
        ByteArrayInputStream inputStream = orderService.exportOrderAndProductsToExcel(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=order_" + id + ".xlsx");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(inputStream));
    }


    @GetMapping("/order/export/excel")
//    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<InputStreamResource> exportAllOrdersAndProductsToExcel() {
        ByteArrayInputStream inputStream = orderService.exportAllOrdersAndProductsToExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=orders.xlsx");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(inputStream));
    }

}
