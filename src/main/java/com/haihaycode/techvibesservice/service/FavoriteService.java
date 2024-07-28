package com.haihaycode.techvibesservice.service;

import com.haihaycode.techvibesservice.entity.FavoriteEntity;
import com.haihaycode.techvibesservice.entity.ProductEntity;
import com.haihaycode.techvibesservice.entity.UserEntity;
import com.haihaycode.techvibesservice.exception.ExcelExportException;
import com.haihaycode.techvibesservice.exception.ResourceNotFoundException;
import com.haihaycode.techvibesservice.repository.FavoriteRepository;
import com.haihaycode.techvibesservice.repository.ProductRepository;
import com.haihaycode.techvibesservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public Page<FavoriteEntity> getFavoritesByCriteria(Long userId, Long productId, Date startDate, Date endDate, Pageable pageable) {
        return favoriteRepository.findFavoritesByCriteria(userId, productId, startDate, endDate, pageable);
    }


    public String favorite(Long userId, Long productId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        Optional<ProductEntity> productOptional = productRepository.findById(productId);

        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
        if (productOptional.isEmpty()) {
            throw new ResourceNotFoundException("Product not found with ID: " + productId);
        }
        Optional<FavoriteEntity> existingFavorite = favoriteRepository.findByUserIdAndProductId(userId, productId);
        if (existingFavorite.isPresent()) {
            favoriteRepository.delete(existingFavorite.get());
            return "unfavorited  successfully !";
        }else{
            FavoriteEntity favorite = new FavoriteEntity();
            favorite.setUser(userOptional.get());
            favorite.setProduct(productOptional.get());
            favorite.setCreateDate(new Date());
            favoriteRepository.save(favorite);
            return "favorited  successfully !";
        }
    }

    public Page<ProductEntity> getFavorite(Long userId, int page, int limit, Sort.Direction sortDirection, String sortField) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        Pageable pageable = PageRequest.of(page, limit, Sort.by(new Sort.Order(sortDirection, sortField)));
        return favoriteRepository.findByUserId(userId, pageable);
    }
    public ByteArrayInputStream exportFavoritesToExcel() {
        List<FavoriteEntity> favoriteEntityList = favoriteRepository.findAll();
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Favorites");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Time Created");
            headerRow.createCell(2).setCellValue("ID User");
            headerRow.createCell(3).setCellValue("Email");
            headerRow.createCell(4).setCellValue("FullName");
            headerRow.createCell(5).setCellValue("ID Product");
            headerRow.createCell(6).setCellValue("Product Name");
            headerRow.createCell(7).setCellValue("Category Product");


            // Create data rows
            int rowNum = 1;
            for (FavoriteEntity favorite : favoriteEntityList) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(favorite.getId());
                row.createCell(1).setCellValue(favorite.getCreateDate());
                row.createCell(2).setCellValue(favorite.getUser().getUserId());
                row.createCell(3).setCellValue(favorite.getUser().getEmail());
                row.createCell(4).setCellValue(favorite.getUser().getFullName());
                row.createCell(5).setCellValue(favorite.getProduct().getId());
                row.createCell(6).setCellValue(favorite.getProduct().getName());
                row.createCell(7).setCellValue(favorite.getProduct().getCategory().getName());

            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new ExcelExportException("Failed to export favorites to Excel");
        }
    }




}
