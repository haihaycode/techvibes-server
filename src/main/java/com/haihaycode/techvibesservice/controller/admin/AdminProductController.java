package com.haihaycode.techvibesservice.controller.admin;

import com.haihaycode.techvibesservice.entity.CategoryEntity;
import com.haihaycode.techvibesservice.entity.ProductEntity;
import com.haihaycode.techvibesservice.model.ProductRequest;
import com.haihaycode.techvibesservice.model.ResponseWrapper;
import com.haihaycode.techvibesservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AdminProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/product")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<Page<ProductEntity>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) Optional<String> sort,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date endDate,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) Boolean availableCategory) {

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
                startDate,
                endDate,
                categoryId,
                keyword,
                available,
                availableCategory,
                pageable);
        ResponseWrapper<Page<ProductEntity>> response = new ResponseWrapper<>(HttpStatus.OK, "Products retrieved successfully", products);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/product/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<ProductEntity>> getProductById(@PathVariable("id") Long productId) {
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Products retrieved successfully", productService.getProductById(productId)));
    }

    @PostMapping(value = "/product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<ProductEntity>> createProduct(@RequestPart ProductRequest request,
                                                                        @RequestParam("file") Optional<MultipartFile> file) {
        ResponseWrapper<ProductEntity> response = new ResponseWrapper<>(HttpStatus.CREATED, "Products created successfully.", productService.createProduct(request, file));
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/product/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<ProductEntity>> updateProduct(@PathVariable Long id,
                                                                        @RequestPart ProductRequest request,
                                                                        @RequestParam("file") Optional<MultipartFile> file) {
        ResponseWrapper<ProductEntity> response = new ResponseWrapper<>(HttpStatus.CREATED, "Products updated successfully.", productService.updateProduct(id, request, file));
        return ResponseEntity.ok(response);
    }

    @PatchMapping(value = "/product/{id}/availability")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<ProductEntity>> availableProduct(@PathVariable Long id) {
        ResponseWrapper<ProductEntity> response = new ResponseWrapper<>(HttpStatus.OK, "Categories updated successfully.", productService.availableProduct(id));
        return ResponseEntity.ok(response);
    }


    @GetMapping("/product/export/excel")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<InputStreamResource> exportProductsToExcel() {
        ByteArrayInputStream inputStream = productService.exportProductsToExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=products.xlsx");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(inputStream));
    }

    @GetMapping("/product/export/excel/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<InputStreamResource> exportProductsByIdToExcel(@PathVariable Long id) {
        ByteArrayInputStream inputStream = productService.exportProductByIdToExcel(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=product_" + id + ".xlsx");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(inputStream));
    }

    @PostMapping("/product/import/excel")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<String>> importProductsFromExcel(@RequestParam("file") MultipartFile file) {
        productService.importProductsFromExcel(file);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Products imported successfully.", null));

    }


}
