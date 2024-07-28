package com.haihaycode.techvibesservice.controller;

import com.haihaycode.techvibesservice.entity.CategoryEntity;
import com.haihaycode.techvibesservice.model.ResponseWrapper;
import com.haihaycode.techvibesservice.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
@RestController
@RequestMapping("/api/public")
public class categoryController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/category")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<Page<CategoryEntity>>> searchCategories(
            @RequestParam(value = "keyword", required = false) String keyword,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Optional<String> sort) {

        String sortField = "id";
        Sort.Direction sortDirection = Sort.Direction.DESC;

        if (sort.isPresent()) {
            String[] sortParams = sort.get().split(",");
            sortField = sortParams[0];
            if (sortParams.length > 1) {
                sortDirection = Sort.Direction.fromString(sortParams[1]);
            }
        }
        Pageable pageable = PageRequest.of(page, limit, Sort.by(new Sort.Order(sortDirection, sortField)));
        Page<CategoryEntity> categories = categoryService.findCategoriesByCriteria(keyword, true, null, null, pageable);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Categories fetched successfully", categories));
    }
}
