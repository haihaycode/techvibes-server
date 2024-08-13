package com.haihaycode.techvibesservice.repository;

import com.haihaycode.techvibesservice.entity.AddressEntity;
import com.haihaycode.techvibesservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
    List<AddressEntity> findByUser(UserEntity user);
    Optional<AddressEntity> findByIdAndUser(Long id, UserEntity user);
}
