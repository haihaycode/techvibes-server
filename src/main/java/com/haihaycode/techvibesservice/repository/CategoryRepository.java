package com.haihaycode.techvibesservice.repository;

import com.haihaycode.techvibesservice.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    // Custom queries can be added here if needed
}
