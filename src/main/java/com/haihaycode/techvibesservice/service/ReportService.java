package com.haihaycode.techvibesservice.service;

import com.haihaycode.techvibesservice.entity.OrderDetailEntity;
import com.haihaycode.techvibesservice.entity.OrderEntity;
import com.haihaycode.techvibesservice.entity.ProductEntity;
import com.haihaycode.techvibesservice.model.Report.CustomerReport;
import com.haihaycode.techvibesservice.model.Report.OrderStatusReport;
import com.haihaycode.techvibesservice.model.Report.ProductSalesReport;
import com.haihaycode.techvibesservice.model.Report.RevenueReport;
import com.haihaycode.techvibesservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
@Transactional
public class ReportService {
    @Autowired
    private OrderRepository orderRepository;

    public Long getTotalRevenueBetweenDates(Date startDate, Date endDate, Long accountId, Long statusId) {
        return orderRepository.getTotalRevenueBetweenDates(startDate, endDate, accountId, statusId);
    }
    public Page<ProductEntity> getSalesByProductBetweenDates(Date startDate, Date endDate, Long accountId, Long statusId, Pageable pageable) {
        return orderRepository.getSalesByProductBetweenDates(startDate, endDate,accountId,statusId,pageable);
    }
    public Page<OrderEntity> getOrders(Date startDate, Date endDate, Long accountId, Long statusId, Pageable pageable) {
        return orderRepository.getOrder(startDate, endDate, accountId, statusId, pageable);
    }
    public Page<OrderDetailEntity> getOrderDetailsByOrderId(Long orderId, Pageable pageable) {
        return orderRepository.getOrderDetailByOrderId(orderId, pageable);
    }
    public Page<ProductEntity> getProductsByOrderId(Long orderId, Pageable pageable) {
        return orderRepository.getProductsByOrderId(orderId, pageable);
    }
}
