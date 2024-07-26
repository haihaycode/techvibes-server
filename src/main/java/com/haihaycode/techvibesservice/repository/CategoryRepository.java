package com.haihaycode.techvibesservice.repository;

import com.haihaycode.techvibesservice.entity.CategoryEntity;
import com.haihaycode.techvibesservice.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    @Query("SELECT c FROM CategoryEntity c "
            + "WHERE (:keyword IS NULL OR c.name LIKE %:keyword% OR c.description LIKE %:keyword%) AND "
            + "(:available IS NULL OR c.available = :available) AND "
            + "(:startDate IS NULL OR c.createDate >= :startDate) AND "
            + "(:endDate IS NULL OR c.createDate <= :endDate)")
    Page<CategoryEntity> findCategoriesByCriteria(@Param("keyword") String keyword,
                                                  @Param("available") Boolean available,
                                                  @Param("startDate") Date startDate,
                                                  @Param("endDate") Date endDate,
                                                  Pageable pageable);
}
