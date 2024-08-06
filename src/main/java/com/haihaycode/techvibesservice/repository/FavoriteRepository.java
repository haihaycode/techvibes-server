package com.haihaycode.techvibesservice.repository;

import com.haihaycode.techvibesservice.entity.FavoriteEntity;
import com.haihaycode.techvibesservice.entity.ProductEntity;
import com.haihaycode.techvibesservice.entity.UserEntity;
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
    @Query("SELECT f.product FROM FavoriteEntity f WHERE f.user.userId = :userId")
    Page<ProductEntity> findByUserId(@Param("userId") Long userId, Pageable pageable);
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

    @Query("SELECT f.product.id as productId, f.product.name as productName, f.product.price as productPrice, COUNT(f) as favoriteCount "
            + "FROM FavoriteEntity f "
            + "WHERE (:userId IS NULL OR f.user.userId = :userId) AND "
            + "(:startDate IS NULL OR f.createDate >= :startDate) AND "
            + "(:endDate IS NULL OR f.createDate <= :endDate) "
            + "GROUP BY f.product.id, f.product.name")
    Page<Object[]> findFavoritesByCriteria(
            @Param("userId") Long userId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable
    );
    @Query("SELECT f.user FROM FavoriteEntity f WHERE f.product.id = :productId")
    Page<UserEntity> findUsersByProductId(@Param("productId") Long productId, Pageable pageable);
}
