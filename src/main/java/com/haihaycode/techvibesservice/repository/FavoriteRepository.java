package com.haihaycode.techvibesservice.repository;

import com.haihaycode.techvibesservice.entity.FavoriteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteEntity, Long> {
    Page<FavoriteEntity> findByUserUserId(Long userId, Pageable pageable);
    Page<FavoriteEntity> findByProductId(Long productId, Pageable pageable);
    @Query("SELECT f FROM FavoriteEntity f WHERE f.user.userId = :userId AND f.product.id = :productId")
    Optional<FavoriteEntity> findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Query("SELECT f FROM FavoriteEntity f WHERE "
            + "(:userId IS NULL OR f.user.userId = :userId) AND "
            + "(:productId IS NULL OR f.product.id = :productId) AND "
            + "(:startDate IS NULL OR f.createDate >= :startDate) AND "
            + "(:endDate IS NULL OR f.createDate <= :endDate)")
    Page<FavoriteEntity> findFavoritesByCriteria(
            @Param("userId") Long userId,
            @Param("productId") Long productId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable
    );
}
