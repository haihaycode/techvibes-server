package com.haihaycode.techvibesservice.controller.admin;

import com.haihaycode.techvibesservice.entity.OrderDetailEntity;
import com.haihaycode.techvibesservice.entity.OrderEntity;
import com.haihaycode.techvibesservice.entity.ProductEntity;
import com.haihaycode.techvibesservice.model.Report.ProductSalesCount;
import com.haihaycode.techvibesservice.model.ResponseWrapper;
import com.haihaycode.techvibesservice.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/reports")
@CrossOrigin("*")
public class AdminReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/total-revenue")//tính doanh thu theo thoi gian
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<Long>> getTotalRevenue(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date endDate,
            @RequestParam(value = "accountId", required = false) Long accountId,
            @RequestParam(value = "statusId", required = false) Long statusId) {
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK,"Total Revenue",reportService.getTotalRevenueBetweenDates(startDate, endDate, accountId, statusId)));
    }

    @GetMapping("/product-sales")
    public List<ProductSalesCount> getProductSalesCount(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date endDate,
            @RequestParam(value = "accountId", required = false) Long accountId,
            @RequestParam(value = "statusId", required = false) Long statusId) {
        return reportService.getProductSalesCount(startDate, endDate, accountId, statusId);
    }

    @GetMapping("/sales-report")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<Page<ProductEntity>>> getSalesReport(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Optional<String> sort,
            @RequestParam(value = "accountId", required = false) Long accountId,
            @RequestParam(value = "statusId", required = false) Long statusId,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
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
        Page<ProductEntity> salesReport = reportService.getSalesByProductBetweenDates(startDate, endDate, accountId, statusId,pageable);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Sales Report", salesReport));
    }

    @GetMapping("/getOrders")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<Page<OrderEntity>>> getOrdersBytime(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam(value = "accountId", required = false) Long accountId,
            @RequestParam(value = "statusId", required = false) Long statusId,
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
        Page<OrderEntity> orders = reportService.getOrders(startDate, endDate, accountId, statusId, pageable);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Order List", orders));
    }

    @GetMapping("/order-details")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<Page<OrderDetailEntity>>> getOrderDetailsByOrderId(
            @RequestParam(value = "orderId", required = false) Long orderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Optional<String> sort) {
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
        Page<OrderDetailEntity> orderDetails = reportService.getOrderDetailsByOrderId(orderId, pageable);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Order Details", orderDetails));
    }

    @GetMapping("/products-by-order")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<Page<ProductEntity>>> getProductsByOrderId(
            @RequestParam(value = "orderId", required = false) Long orderId,
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
        Page<ProductEntity> products = reportService.getProductsByOrderId(orderId, pageable);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Products by Order", products));
    }

}
