package com.haihaycode.techvibesservice.service;

import com.haihaycode.techvibesservice.entity.CategoryEntity;
import com.haihaycode.techvibesservice.entity.ProductEntity;
import com.haihaycode.techvibesservice.exception.ExcelExportException;
import com.haihaycode.techvibesservice.exception.InvalidInputException;
import com.haihaycode.techvibesservice.exception.ResourceNotFoundException;
import com.haihaycode.techvibesservice.model.ProductRequest;
import com.haihaycode.techvibesservice.repository.CategoryRepository;
import com.haihaycode.techvibesservice.repository.ProductRepository;
import com.haihaycode.techvibesservice.service.ExportService.ExcelDateParserService;
import com.haihaycode.techvibesservice.service.image.ImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;
    private final ExcelDateParserService excelDateParserService;

    public Page<ProductEntity> findProductsByCriteria(
            Long minPrice,
            Long maxPrice,
            Date startDate,
            Date endDate,
            Long categoryId,
            String keyword,
            Boolean available,
            Boolean availableCategory,
            Pageable pageable
    ) {
        return productRepository.findProductsByCriteria(
                minPrice,
                maxPrice,
                startDate,
                endDate,
                categoryId,
                keyword,
                available,
                availableCategory,
                pageable
        );
    }

    public ProductEntity getProductById(Long productId) {
        return productRepository.findProductById(productId,null,null)//sữa lại nếu đó là nguoi dùng
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
    }

    public ProductEntity createProduct(ProductRequest request,Optional<MultipartFile> file){
        CategoryEntity category = categoryRepository.findById(request.getCategoryId()).orElseThrow(
                ()-> new ResourceNotFoundException("Category not found ")
        );
        var productEntity= new ProductEntity();
        productEntity.setName(request.getName());
        productEntity.setDescription(request.getDescription());
        productEntity.setPrice(request.getPrice());
        productEntity.setAvailable(true);
        productEntity.setCreateDate(new Date());
        productEntity.setDiscount(request.getDiscount());
        productEntity.setQuantity(request.getQuantity());
        productEntity.setDescriptionSort(request.getDescriptionSort());
        productEntity.setCategory(category);
        if (file.isPresent() && !file.get().isEmpty()) {
            try {
                productEntity.setImage(imageService.saveImage(file.get(), "image/directory/product/"));
            } catch (IOException e) {
                throw new InvalidInputException(e.getMessage());
            }
        }
        return productRepository.save(productEntity);
    }


    public ProductEntity updateProduct(Long id, ProductRequest request,Optional<MultipartFile> file){
        var productEntity = productRepository.findById(request.getCategoryId()).orElseThrow(
                ()-> new ResourceNotFoundException("Product not found ")
        );
        CategoryEntity category = categoryRepository.findById(request.getCategoryId()).orElseThrow(
                ()-> new ResourceNotFoundException("Category not found ")
        );

        productEntity.setName(request.getName());
        productEntity.setDescription(request.getDescription());
        productEntity.setPrice(request.getPrice());
        productEntity.setDiscount(request.getDiscount());
        productEntity.setQuantity(request.getQuantity());
        productEntity.setUpdateDate(new Date());
        productEntity.setDescriptionSort(request.getDescriptionSort());
        productEntity.setCategory(category);
        if (file.isPresent() && !file.get().isEmpty()) {
            try {
                imageService.deleteImage(productEntity.getImage(), "image/directory/product/");
                productEntity.setImage(imageService.saveImage(file.get(), "image/directory/product/"));
            } catch (IOException e) {
                throw new InvalidInputException(e.getMessage());
            }
        }
        return productRepository.save(productEntity);
    }

    public ProductEntity availableProduct(Long id) {
        var productEntity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productEntity.setAvailable(!productEntity.getAvailable());
        return productRepository.save(productEntity);
    }



    public ByteArrayInputStream exportProductsToExcel() {
        List<ProductEntity> products = productRepository.findAll();
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Products");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Product ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Image");
            headerRow.createCell(3).setCellValue("Price");
            headerRow.createCell(4).setCellValue("Description");
            headerRow.createCell(5).setCellValue("Description Sort");
            headerRow.createCell(6).setCellValue("Create Date");
            headerRow.createCell(7).setCellValue("Update Date");
            headerRow.createCell(8).setCellValue("Available");
            headerRow.createCell(9).setCellValue("Category ID");
            headerRow.createCell(10).setCellValue("Quantity");
            headerRow.createCell(11).setCellValue("Discount");

            // Create data rows
            int rowNum = 1;
            for (ProductEntity product : products) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(product.getId());
                row.createCell(1).setCellValue(product.getName());
                row.createCell(2).setCellValue(product.getImage());
                row.createCell(3).setCellValue(product.getPrice());

                // Handle long description
                String description = product.getDescription();
                if (description != null && description.length() > 32767) {
                    description = description.substring(0, 32767);
                }
                row.createCell(4).setCellValue(description);

                // Handle long descriptionSort
                String descriptionSort = product.getDescriptionSort();
                if (descriptionSort != null && descriptionSort.length() > 32767) {
                    descriptionSort = descriptionSort.substring(0, 32767);
                }
                row.createCell(5).setCellValue(descriptionSort);

                row.createCell(6).setCellValue(product.getCreateDate() != null ? product.getCreateDate().toString() : null);
                row.createCell(7).setCellValue(product.getUpdateDate() != null ? product.getUpdateDate().toString() : null);
                row.createCell(8).setCellValue(product.getAvailable() ? "Yes" : "No");
                row.createCell(9).setCellValue(product.getCategory() != null ? product.getCategory().getId() : null);
                row.createCell(10).setCellValue(product.getQuantity() != null ? product.getQuantity() : 0);
                row.createCell(11).setCellValue(product.getDiscount() != null ? product.getDiscount() : 0);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new ExcelExportException("Failed to export products to Excel");
        }
    }


    public ByteArrayInputStream exportProductByIdToExcel(Long productId) {
        // Tìm sản phẩm theo ID
        Optional<ProductEntity> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            throw new ResourceNotFoundException("Product not found with ID: " + productId);
        }

        ProductEntity product = productOptional.get();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Product");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Product ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Image");
            headerRow.createCell(3).setCellValue("Price");
            headerRow.createCell(4).setCellValue("Description");
            headerRow.createCell(5).setCellValue("Description Sort");
            headerRow.createCell(6).setCellValue("Create Date");
            headerRow.createCell(7).setCellValue("Update Date");
            headerRow.createCell(8).setCellValue("Available");
            headerRow.createCell(9).setCellValue("Category ID");
            headerRow.createCell(10).setCellValue("Quantity");
            headerRow.createCell(11).setCellValue("Discount");

            // Create data row
            Row row = sheet.createRow(1);

            row.createCell(0).setCellValue(product.getId());
            row.createCell(1).setCellValue(product.getName());
            row.createCell(2).setCellValue(product.getImage());
            row.createCell(3).setCellValue(product.getPrice());

            // Handle long description
            String description = product.getDescription();
            if (description != null && description.length() > 32767) {
                description = description.substring(0, 32767);
            }
            row.createCell(4).setCellValue(description);

            // Handle long descriptionSort
            String descriptionSort = product.getDescriptionSort();
            if (descriptionSort != null && descriptionSort.length() > 32767) {
                descriptionSort = descriptionSort.substring(0, 32767);
            }
            row.createCell(5).setCellValue(descriptionSort);

            row.createCell(6).setCellValue(product.getCreateDate() != null ? product.getCreateDate().toString() : null);
            row.createCell(7).setCellValue(product.getUpdateDate() != null ? product.getUpdateDate().toString() : null);
            row.createCell(8).setCellValue(product.getAvailable() ? "Yes" : "No");
            row.createCell(9).setCellValue(product.getCategory() != null ? product.getCategory().getId() : null);
            row.createCell(10).setCellValue(product.getQuantity() != null ? product.getQuantity() : 0);
            row.createCell(11).setCellValue(product.getDiscount() != null ? product.getDiscount() : 0);

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new ExcelExportException("Failed to export product to Excel");
        }
    }

    public void importProductsFromExcel(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ExcelExportException("Failed to import Products from Excel");
        }

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<ProductEntity> products = new ArrayList<>();

            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }

                ProductEntity product = new ProductEntity();
                product.setName(row.getCell(1).getStringCellValue());
                product.setImage(row.getCell(2).getStringCellValue());
                product.setPrice((long) row.getCell(3).getNumericCellValue());

                String description = row.getCell(4).getStringCellValue();
                if (description.length() > 32767) {
                    description = description.substring(0, 32767);
                }
                product.setDescription(description);

                String descriptionSort = row.getCell(5).getStringCellValue();
                if (descriptionSort.length() > 32767) {
                    descriptionSort = descriptionSort.substring(0, 32767);
                }
                product.setDescriptionSort(descriptionSort);

                product.setCreateDate(excelDateParserService.parseDateCell(row.getCell(6)));
                product.setUpdateDate(excelDateParserService.parseDateCell(row.getCell(7)));
                product.setAvailable("Yes".equalsIgnoreCase(row.getCell(8).getStringCellValue()));

                Long categoryId = ((Double) row.getCell(9).getNumericCellValue()).longValue();

                Optional<CategoryEntity> categoryOptional = categoryRepository.findById(categoryId);
                if (categoryOptional.isPresent()) {
                    product.setCategory(categoryOptional.get());
                } else {
                    throw new ResourceNotFoundException("Category not found with ID: " + categoryId);
                }
                product.setQuantity( (long)row.getCell(10).getNumericCellValue());
                product.setDiscount( (long)row.getCell(11).getNumericCellValue());

                products.add(product);
            }

            productRepository.saveAll(products);
        } catch (IOException | ParseException e) {
            throw new ExcelExportException("Failed to import products from Excel");
        }
    }





}
