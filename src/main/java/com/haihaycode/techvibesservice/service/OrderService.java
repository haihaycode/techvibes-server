package com.haihaycode.techvibesservice.service;

import com.haihaycode.techvibesservice.entity.OrderEntity;
import com.haihaycode.techvibesservice.entity.OrderStatusEntity;
import com.haihaycode.techvibesservice.entity.ProductEntity;
import com.haihaycode.techvibesservice.exception.ResourceNotFoundException;
import com.haihaycode.techvibesservice.repository.OrderRepository;
import com.haihaycode.techvibesservice.repository.OrderStatusRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository ;

    public Page<OrderEntity> findOrdersByCriteria(String keyword, Long minPrice, Long maxPrice, Date startDate, Date endDate,
                                          Date startUpdateDate, Date endUpdateDate, Long accountId, Long statusId,
                                          Pageable pageable) {
        return orderRepository.findOrdersByCriteria(keyword, minPrice, maxPrice, startDate, endDate,
                startUpdateDate, endUpdateDate, accountId, statusId, pageable);
    }
    public OrderEntity findOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
    }

    public List<OrderEntity> getAllOrder() {
        return orderRepository.findAll();
    }

    //client tra cứu đơn hàng đựa trên mã đơn hàng và số điện thoại
    public Optional<OrderEntity> getOrderDetails(String orderCode, int phone) {
        return orderRepository.findByOrderCodeAndPhone(orderCode, phone);
    }

    public OrderEntity updateOrderStatus(Long orderId, Long statusId,String notes) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        OrderStatusEntity status = orderStatusRepository.findById(statusId)
                .orElseThrow(() -> new ResourceNotFoundException("Status not found with ID: " + statusId));
        order.setOrderStatus(status);
        order.setNotes(notes);
        return orderRepository.save(order);
    }




    public ByteArrayInputStream exportOrderAndProductsToExcel(Long orderId) {
        Optional<OrderEntity> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }
        OrderEntity order = orderOptional.get();
        List<ProductEntity> products = order.getOrderDetails().stream()
                .map(orderDetail -> orderDetail.getProduct()) // Assuming you have a method to get the product from order detail
                .toList();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Order and Products");
            // Create header row for order
            Row orderHeaderRow = sheet.createRow(0);
            orderHeaderRow.createCell(0).setCellValue("Order ID");
            orderHeaderRow.createCell(1).setCellValue("Email");
            orderHeaderRow.createCell(2).setCellValue("Address");
            orderHeaderRow.createCell(3).setCellValue("Full Name");
            orderHeaderRow.createCell(4).setCellValue("Notes");
            orderHeaderRow.createCell(5).setCellValue("Order Code");
            orderHeaderRow.createCell(6).setCellValue("Total Price");
            orderHeaderRow.createCell(7).setCellValue("Create Date");
            orderHeaderRow.createCell(8).setCellValue("Update Date");
            orderHeaderRow.createCell(9).setCellValue("Status ID");
            orderHeaderRow.createCell(10).setCellValue("User ID");

            // Create data row for order
            Row orderRow = sheet.createRow(1);
            orderRow.createCell(0).setCellValue(order.getId());
            orderRow.createCell(1).setCellValue(order.getEmail());
            orderRow.createCell(2).setCellValue(order.getAddress());
            orderRow.createCell(3).setCellValue(order.getFullName());
            orderRow.createCell(4).setCellValue(order.getNotes() != null ? order.getNotes() : "");
            orderRow.createCell(5).setCellValue(order.getOrderCode());
            orderRow.createCell(6).setCellValue(order.getTotalPrice() != null ? order.getTotalPrice().toString() : "0");
            orderRow.createCell(7).setCellValue(order.getCreateDate() != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getCreateDate()) : "");
            orderRow.createCell(8).setCellValue(order.getUpdateDate() != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getUpdateDate()) : "");
            orderRow.createCell(9).setCellValue(order.getOrderStatus() != null ? Long.toString( order.getOrderStatus().getId()) : "");
            orderRow.createCell(10).setCellValue(order.getAccount() != null ? Long.toString( order.getAccount().getUserId()) : "");

            // Create header row for products
            Row productHeaderRow = sheet.createRow(3);
            productHeaderRow.createCell(0).setCellValue("Product ID");
            productHeaderRow.createCell(1).setCellValue("Product Name");
            productHeaderRow.createCell(2).setCellValue("Image");
            productHeaderRow.createCell(3).setCellValue("Price");
            productHeaderRow.createCell(4).setCellValue("Description");
            productHeaderRow.createCell(5).setCellValue("Category ID");

            // Create data rows for products
            int productRowNum = 4;
            for (ProductEntity product : products) {
                Row productRow = sheet.createRow(productRowNum++);

                productRow.createCell(0).setCellValue(product.getId());
                productRow.createCell(1).setCellValue(product.getName());
                productRow.createCell(2).setCellValue(product.getImage());
                productRow.createCell(3).setCellValue(product.getPrice() != null ? product.getPrice().toString() : "0");
                productRow.createCell(4).setCellValue(product.getDescription() != null ? product.getDescription() : "");
                productRow.createCell(5).setCellValue(product.getCategory() != null ? String.valueOf(product.getCategory().getId()) : "0");
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to export order and products to Excel", e);
        }
    }
    public ByteArrayInputStream exportAllOrdersAndProductsToExcel() {
        List<OrderEntity> orders = orderRepository.findAll(); // Fetch all orders

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Orders and Products");

            CellStyle orderStyle = workbook.createCellStyle();
            orderStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            orderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle productStyle = workbook.createCellStyle();
            productStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            productStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Create header row for orders
            Row orderHeaderRow = sheet.createRow(0);
            orderHeaderRow.createCell(0).setCellValue("Order ID");
            orderHeaderRow.createCell(1).setCellValue("Email");
            orderHeaderRow.createCell(2).setCellValue("Address");
            orderHeaderRow.createCell(3).setCellValue("Full Name");
            orderHeaderRow.createCell(4).setCellValue("Notes");
            orderHeaderRow.createCell(5).setCellValue("Order Code");
            orderHeaderRow.createCell(6).setCellValue("Total Price");
            orderHeaderRow.createCell(7).setCellValue("Create Date");
            orderHeaderRow.createCell(8).setCellValue("Update Date");
            orderHeaderRow.createCell(9).setCellValue("Status ID");
            orderHeaderRow.createCell(10).setCellValue("User ID");

            int orderRowNum = 1;
            // Create data rows for orders
            for (OrderEntity order : orders) {
                Row orderRow = sheet.createRow(orderRowNum++);

                orderRow.createCell(0).setCellValue(order.getId());
                orderRow.createCell(1).setCellValue(order.getEmail());
                orderRow.createCell(2).setCellValue(order.getAddress());
                orderRow.createCell(3).setCellValue(order.getFullName());
                orderRow.createCell(4).setCellValue(order.getNotes() != null ? order.getNotes() : "");
                orderRow.createCell(5).setCellValue(order.getOrderCode());
                orderRow.createCell(6).setCellValue(order.getTotalPrice() != null ? order.getTotalPrice().toString() : "0" );
                orderRow.createCell(7).setCellValue(order.getCreateDate() != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getCreateDate()) : "");
                orderRow.createCell(8).setCellValue(order.getUpdateDate() != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getUpdateDate()) : "");
                orderRow.createCell(9).setCellValue(order.getOrderStatus() != null ? Long.toString(order.getOrderStatus().getId()) : "");
                orderRow.createCell(10).setCellValue(order.getAccount() != null ? Long.toString(order.getAccount().getUserId()) : "");

                // Apply order style
                for (Cell cell : orderRow) {
                    cell.setCellStyle(orderStyle);
                }

                // Add products for the order
                List<ProductEntity> products = order.getOrderDetails().stream()
                        .map(orderDetail -> orderDetail.getProduct())
                        .toList();



                if (!products.isEmpty()) {
                    // Create header row for products (if not already created)
                    Row productHeaderRow = sheet.createRow(orderRowNum++);
                    productHeaderRow.createCell(0).setCellValue("Product ID");
                    productHeaderRow.createCell(1).setCellValue("Product Name");
                    productHeaderRow.createCell(2).setCellValue("Image");
                    productHeaderRow.createCell(3).setCellValue("Price");
                    productHeaderRow.createCell(4).setCellValue("Description");
                    productHeaderRow.createCell(5).setCellValue("Category ID");

                    // Create data rows for products
                    for (ProductEntity product : products) {
                        Row productRow = sheet.createRow(orderRowNum++);

                        productRow.createCell(0).setCellValue(product.getId());
                        productRow.createCell(1).setCellValue(product.getName());
                        productRow.createCell(2).setCellValue(product.getImage());
                        productRow.createCell(3).setCellValue(product.getPrice() != null ? product.getPrice().toString() : "0");
                        productRow.createCell(4).setCellValue(product.getDescription() != null ? product.getDescription() : "");
                        productRow.createCell(5).setCellValue(product.getCategory() != null ? String.valueOf(product.getCategory().getId()) : "0");

                        // Apply product row style
                        for (Cell cell : productRow) {
                            cell.setCellStyle(productStyle);
                        }
                    }
                }
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to export orders and products to Excel", e);
        }
    }

}
