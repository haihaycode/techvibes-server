package com.haihaycode.techvibesservice.repository;

import com.haihaycode.techvibesservice.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    // Custom queries can be added here if needed
}
