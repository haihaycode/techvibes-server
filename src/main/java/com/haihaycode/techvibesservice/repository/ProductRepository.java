package com.haihaycode.techvibesservice.repository;


import com.haihaycode.techvibesservice.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    @Query("SELECT p FROM ProductEntity p WHERE "
            + "(:minPrice IS NULL OR p.price >= :minPrice) AND "
            + "(:maxPrice IS NULL OR p.price <= :maxPrice) AND "
            + "(:startDate IS NULL OR p.createDate >= :startDate) AND "
            + "(:endDate IS NULL OR p.createDate <= :endDate) AND "
            + "(:available IS NULL OR p.available = :available) AND "
            + "(:categoryName IS NULL OR p.category.name LIKE %:categoryName%) AND "
            + "(:keyword IS NULL OR (p.name LIKE %:keyword% OR p.description LIKE %:keyword%  OR p.descriptionSort LIKE %:keyword% OR p.category.name LIKE %:keyword%)) "
    )
    Page<ProductEntity> findProductsByCriteria(@Param("minPrice") Long minPrice,
                                         @Param("maxPrice") Long maxPrice,
                                         @Param("startDate") Date startDate,
                                         @Param("endDate") Date endDate,
                                         @Param("categoryName") String categoryName,
                                         @Param("keyword") String keyword,
                                         @Param("available") Boolean available,
                                         Pageable pageable);
}
