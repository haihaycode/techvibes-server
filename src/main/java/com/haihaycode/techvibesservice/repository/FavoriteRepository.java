package com.haihaycode.techvibesservice.repository;

import com.haihaycode.techvibesservice.entity.FavoriteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteEntity, Long> {
    Page<FavoriteEntity> findByUserUserId(Long userId, Pageable pageable);
    Page<FavoriteEntity> findByProductId(Long productId, Pageable pageable);
}
