package com.haihaycode.techvibesservice.repository;

import com.haihaycode.techvibesservice.entity.CartEntity;
import com.haihaycode.techvibesservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {
    CartEntity findByUser(UserEntity user);


}
