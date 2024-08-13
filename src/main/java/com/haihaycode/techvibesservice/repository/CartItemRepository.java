package com.haihaycode.techvibesservice.repository;

import com.haihaycode.techvibesservice.entity.CartItemEntity;
import com.haihaycode.techvibesservice.entity.CartEntity;
import com.haihaycode.techvibesservice.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    Optional<CartItemEntity> findByCartAndProduct(CartEntity cart, ProductEntity product);
    List<CartItemEntity> findByCartId(Long cartId);
}
