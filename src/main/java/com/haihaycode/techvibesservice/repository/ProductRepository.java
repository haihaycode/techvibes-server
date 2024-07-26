package com.haihaycode.techvibesservice.repository;


import com.haihaycode.techvibesservice.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    @Query("SELECT p FROM ProductEntity p WHERE "
            + "(:minPrice IS NULL OR p.price >= :minPrice) AND "
            + "(:maxPrice IS NULL OR p.price <= :maxPrice) AND "
            + "(:startDate IS NULL OR p.createDate >= :startDate) AND "
            + "(:endDate IS NULL OR p.createDate <= :endDate) AND "
            + "(:available IS NULL OR p.available = :available) AND "
            + "(:categoryId IS NULL OR (p.category.id = :categoryId AND p.category.available = :availableCategory)) AND "
            + "(:availableCategory IS NULL OR p.category.available = :availableCategory) AND "
            + "(:keyword IS NULL OR (p.name LIKE %:keyword% OR p.description LIKE %:keyword% OR p.category.name LIKE %:keyword%))"
    )
    Page<ProductEntity> findProductsByCriteria(
            @Param("minPrice") Long minPrice,
            @Param("maxPrice") Long maxPrice,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            @Param("available") Boolean available,
            @Param("availableCategory") Boolean availableCategory,
            Pageable pageable
    );

    @Query("SELECT p FROM ProductEntity p WHERE "
            + "(:availableCategory IS NULL OR p.category.available = :availableCategory) AND "
            + "(:available IS NULL OR p.available = :available) AND "
            + "p.id = :id")
    Optional<ProductEntity> findProductById(
            @Param("id") Long id,
            @Param("available") Boolean available,
            @Param("availableCategory") Boolean availableCategory
    );

}
