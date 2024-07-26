package com.haihaycode.techvibesservice.controller.admin;

import com.haihaycode.techvibesservice.entity.CategoryEntity;
import com.haihaycode.techvibesservice.model.CategoryRequest;
import com.haihaycode.techvibesservice.model.ResponseWrapper;
import com.haihaycode.techvibesservice.service.CategoryService;
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
public class AdminCategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/category")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ResponseWrapper<Page<CategoryEntity>>> searchCategories(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "available", required = false) Boolean available,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
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

        Page<CategoryEntity> categories = categoryService.findCategoriesByCriteria(keyword, available, startDate, endDate, pageable);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Categories fetched successfully", categories));
    }


    @GetMapping("/category/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ResponseWrapper<CategoryEntity>> getCategoryById(@PathVariable Long id) {
        CategoryEntity category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Categories fetched successfully", category));
    }


    @PostMapping(value = "/category", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<CategoryEntity>> createCategory(@RequestPart CategoryRequest request,
                                                                          @RequestParam("file") Optional<MultipartFile> file) {
        ResponseWrapper<CategoryEntity> response = new ResponseWrapper<>(HttpStatus.CREATED, "Categories created successfully.", categoryService.createCategory(request, file));
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/category/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<CategoryEntity>> updateCategory(@PathVariable Long id,
                                                                          @RequestPart CategoryRequest request,
                                                                          @RequestParam("file") Optional<MultipartFile> file) {
        ResponseWrapper<CategoryEntity> response = new ResponseWrapper<>(HttpStatus.OK, "Categories updated successfully.", categoryService.updateCategory(id, request, file));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/category/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<CategoryEntity>> updateCategory(@PathVariable Long id) {
        ResponseWrapper<CategoryEntity> response = new ResponseWrapper<>(HttpStatus.OK, "Categories updated successfully.", categoryService.availableCategory(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/export/excel")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<InputStreamResource> exportCategoriesToExcel() {
        ByteArrayInputStream inputStream = categoryService.exportCategoriesToExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=categories.xlsx");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(inputStream));
    }

    @GetMapping("/category/export/excel/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<InputStreamResource> exportCategoryByIdToExcel(@PathVariable Long id) {
        ByteArrayInputStream inputStream = categoryService.exportCategoryByIdToExcel(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=category_" + id + ".xlsx");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(inputStream));
    }

    @PostMapping("/category/import/excel")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<String>> importCategoriesFromExcel(@RequestParam("file") MultipartFile file) {
        categoryService.importCategoriesFromExcel(file);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Categories imported successfully.", null));

    }


}
