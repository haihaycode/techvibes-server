package com.haihaycode.techvibesservice.repository;

import com.haihaycode.techvibesservice.entity.OrderDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {
    // Custom queries can be added here if needed
}
