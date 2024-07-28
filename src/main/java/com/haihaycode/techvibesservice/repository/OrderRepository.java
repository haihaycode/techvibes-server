package com.haihaycode.techvibesservice.repository;

import com.haihaycode.techvibesservice.entity.OrderDetailEntity;
import com.haihaycode.techvibesservice.entity.OrderEntity;
import com.haihaycode.techvibesservice.entity.ProductEntity;
import com.haihaycode.techvibesservice.model.Report.ProductSalesReport;
import com.haihaycode.techvibesservice.model.Report.RevenueReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    @Query("SELECT o FROM OrderEntity o " +
            "JOIN o.account a " +
            "JOIN o.orderDetails od " +
            "WHERE (:keyword IS NULL OR " +
            "o.email LIKE %:keyword% OR " +
            "o.address LIKE %:keyword% OR " +
            "o.fullName LIKE %:keyword% OR " +
            "o.notes LIKE %:keyword% OR " +
            "o.orderCode LIKE %:keyword%) " +
            "AND (:minPrice IS NULL OR o.totalPrice >= :minPrice) " +
            "AND (:maxPrice IS NULL OR o.totalPrice <= :maxPrice) " +
            "AND (:startDate IS NULL OR o.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR o.createDate <= :endDate) " +
            "AND (:startUpdateDate IS NULL OR o.updateDate >= :startUpdateDate) " +
            "AND (:endUpdateDate IS NULL OR o.updateDate <= :endUpdateDate) " +
            "AND (:accountId IS NULL OR a.userId = :accountId) " +
            "AND (:statusId IS NULL OR o.orderStatus.id = :statusId)")
    Page<OrderEntity> findOrdersByCriteria(
            @Param("keyword") String keyword,
            @Param("minPrice") Long minPrice,
            @Param("maxPrice") Long maxPrice,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("startUpdateDate") Date startUpdateDate,
            @Param("endUpdateDate") Date endUpdateDate,
            @Param("accountId") Long accountId,
            @Param("statusId") Long statusId,
            Pageable pageable);


    @Query("SELECT SUM(od.price) " +
            "FROM OrderDetailEntity od " +
            "JOIN od.order o " +
            "JOIN o.account a " +
            "WHERE (:startDate IS NULL OR o.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR o.createDate <= :endDate) " +
            "AND (:accountId IS NULL OR a.userId = :accountId) " +
            "AND (:statusId IS NULL OR o.orderStatus.id = :statusId)")
    Long getTotalRevenueBetweenDates(@Param("startDate") Date startDate,
                                     @Param("endDate") Date endDate,
                                     @Param("accountId") Long accountId,
                                     @Param("statusId") Long statusId);


    @Query("SELECT od.product " +
            "FROM OrderDetailEntity od " +
            "JOIN od.order o " +
            "JOIN o.account a " +
            "WHERE (:startDate IS NULL OR o.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR o.createDate <= :endDate) " +
            "AND (:accountId IS NULL OR a.userId = :accountId) " +
            "AND (:statusId IS NULL OR o.orderStatus.id = :statusId)" +
            "GROUP BY od.product.id, od.product.name")
    Page<ProductEntity> getSalesByProductBetweenDates(@Param("startDate") Date startDate,
                                                      @Param("endDate") Date endDate,
                                                      @Param("accountId") Long accountId,
                                                      @Param("statusId") Long statusId,
                                                      Pageable pageable);
    @Query("SELECT o " +
            "FROM OrderDetailEntity od " +
            "JOIN od.order o " +
            "JOIN o.account a " +
            "WHERE (:startDate IS NULL OR o.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR o.createDate <= :endDate) " +
            "AND (:accountId IS NULL OR a.userId = :accountId) " +
            "AND (:statusId IS NULL OR o.orderStatus.id = :statusId)" +
            "GROUP BY od.product.id, od.product.name")
    Page<OrderEntity> getOrder(@Param("startDate") Date startDate,
                                                      @Param("endDate") Date endDate,
                                                      @Param("accountId") Long accountId,
                                                      @Param("statusId") Long statusId,
                                                      Pageable pageable);


    @Query("SELECT od " +
            "FROM OrderDetailEntity od " +
            "JOIN od.order o " +
            "JOIN o.account a " +
            "WHERE (:orderId IS NULL OR o.id = :orderId)")
    Page<OrderDetailEntity> getOrderDetailByOrderId(@Param("orderId") Long orderId, Pageable pageable);

    @Query("SELECT od.product " +
            "FROM OrderDetailEntity od " +
            "JOIN od.order o " +
            "JOIN o.account a " +
            "WHERE (:orderId IS NULL OR o.id = :orderId)")
    Page<ProductEntity> getProductsByOrderId(@Param("orderId") Long orderId, Pageable pageable);
}
