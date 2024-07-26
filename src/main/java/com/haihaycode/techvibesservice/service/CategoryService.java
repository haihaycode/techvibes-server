package com.haihaycode.techvibesservice.service;

import com.haihaycode.techvibesservice.entity.CategoryEntity;
import com.haihaycode.techvibesservice.entity.RoleEntity;
import com.haihaycode.techvibesservice.entity.UserEntity;
import com.haihaycode.techvibesservice.exception.ExcelExportException;
import com.haihaycode.techvibesservice.exception.InvalidInputException;
import com.haihaycode.techvibesservice.exception.ResourceNotFoundException;
import com.haihaycode.techvibesservice.model.CategoryRequest;
import com.haihaycode.techvibesservice.repository.CategoryRepository;
import com.haihaycode.techvibesservice.service.ExportService.ExcelDateParserService;
import com.haihaycode.techvibesservice.service.image.ImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;
    private final ExcelDateParserService excelDateParserService;

    public Page<CategoryEntity> findCategoriesByCriteria(String keyword, Boolean available, Date startDate, Date endDate, Pageable pageable) {
        return categoryRepository.findCategoriesByCriteria(keyword, available, startDate, endDate, pageable);
    }

    public CategoryEntity getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }


    public CategoryEntity createCategory(CategoryRequest request, Optional<MultipartFile> file) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName(request.getName());
        categoryEntity.setDescription(request.getDescription());
        categoryEntity.setCreateDate(new Date());
        categoryEntity.setAvailable(true);
        if (file.isPresent() && !file.get().isEmpty()) {
            try {
                categoryEntity.setImage(imageService.saveImage(file.get(), "image/directory/category/"));
            } catch (IOException e) {
                throw new InvalidInputException(e.getMessage());
            }
        }
        return categoryRepository.save(categoryEntity);
    }

    public CategoryEntity updateCategory(Long id, CategoryRequest request, Optional<MultipartFile> file) {
        var categoryEntity = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        categoryEntity.setName(request.getName());
        categoryEntity.setDescription(request.getDescription());
        categoryEntity.setUpdateDate(new Date());
        categoryEntity.setAvailable(true);
        if (file.isPresent() && !file.get().isEmpty()) {
            try {
                imageService.deleteImage(categoryEntity.getImage(), "image/directory/category/");
                categoryEntity.setImage(imageService.saveImage(file.get(), "image/directory/category/"));
            } catch (IOException e) {
                throw new InvalidInputException(e.getMessage());
            }
        }
        return categoryRepository.save(categoryEntity);
    }

    public CategoryEntity availableCategory(Long id) {
        var categoryEntity = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        categoryEntity.setAvailable(!categoryEntity.getAvailable());
        return categoryRepository.save(categoryEntity);
    }

    public ByteArrayInputStream exportCategoriesToExcel() {
        List<CategoryEntity> categories = categoryRepository.findAll();
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Categories");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Category ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Description");
            headerRow.createCell(3).setCellValue("Image");
            headerRow.createCell(4).setCellValue("Create Date");
            headerRow.createCell(5).setCellValue("Update Date");
            headerRow.createCell(6).setCellValue("Available");

            // Create data rows
            int rowNum = 1;
            for (CategoryEntity category : categories) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(category.getId());
                row.createCell(1).setCellValue(category.getName());
                row.createCell(2).setCellValue(category.getDescription());
                row.createCell(3).setCellValue(category.getImage());
                row.createCell(4).setCellValue(category.getCreateDate() != null ? category.getCreateDate().toString() : null);
                row.createCell(5).setCellValue(category.getUpdateDate() != null ? category.getUpdateDate().toString() : null);
                row.createCell(6).setCellValue(category.getAvailable() ? "Yes" : "No");
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new ExcelExportException("Failed to export categories to Excel");
        }
    }

    public ByteArrayInputStream exportCategoryByIdToExcel(Long categoryId) {
        Optional<CategoryEntity> categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isEmpty()) {
            throw new ResourceNotFoundException("Category not found with ID: " + categoryId);
        }

        CategoryEntity category = categoryOptional.get();
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Category");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Category ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Description");
            headerRow.createCell(3).setCellValue("Image");
            headerRow.createCell(4).setCellValue("Create Date");
            headerRow.createCell(5).setCellValue("Update Date");
            headerRow.createCell(6).setCellValue("Available");

            // Create data row
            Row row = sheet.createRow(1);

            row.createCell(0).setCellValue(category.getId());
            row.createCell(1).setCellValue(category.getName());
            row.createCell(2).setCellValue(category.getDescription());
            row.createCell(3).setCellValue(category.getImage());
            row.createCell(4).setCellValue(category.getCreateDate() != null ? category.getCreateDate().toString() : null);
            row.createCell(5).setCellValue(category.getUpdateDate() != null ? category.getUpdateDate().toString() : null);
            row.createCell(6).setCellValue(category.getAvailable() ? "Yes" : "No");

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new ExcelExportException("Failed to export category to Excel");
        }
    }

    public void importCategoriesFromExcel(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ExcelExportException("Failed to import categories from Excel");
        }

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<CategoryEntity> categories = new ArrayList<>();

            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }

                CategoryEntity category = new CategoryEntity();
                category.setName(row.getCell(1).getStringCellValue());
                category.setDescription(row.getCell(2).getStringCellValue());
                category.setImage(row.getCell(3).getStringCellValue());
                category.setCreateDate(excelDateParserService.parseDateCell(row.getCell(4)));
                category.setUpdateDate(excelDateParserService.parseDateCell(row.getCell(5)));
                category.setAvailable("Yes".equalsIgnoreCase(row.getCell(6).getStringCellValue()));

                categories.add(category);
            }

            categoryRepository.saveAll(categories);
        } catch (IOException | ParseException e) {
            throw new ExcelExportException("Failed to import categories from Excel");
        }
    }


}
