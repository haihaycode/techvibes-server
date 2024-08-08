package com.haihaycode.techvibesservice.controller;

import com.haihaycode.techvibesservice.entity.ProductEntity;
import com.haihaycode.techvibesservice.model.ResponseWrapper;
import com.haihaycode.techvibesservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/public")
@CrossOrigin("*")   
public class productController {
    @Autowired
    private ProductService productService;

    @GetMapping("/product")//true/true
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<Page<ProductEntity>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) Optional<String> sort,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword
            ) {

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
        Page<ProductEntity> products = productService.findProductsByCriteria(
                minPrice,
                maxPrice,
                null,
                null,
                categoryId,
                keyword,
                true,
                true,
                pageable);
        ResponseWrapper<Page<ProductEntity>> response = new ResponseWrapper<>(HttpStatus.OK, "Products retrieved successfully", products);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{id}")//true/true
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<ProductEntity>> getProductById(@PathVariable("id") Long productId) {
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Products retrieved successfully", productService.getProductForClientById(productId)));
    }
}
