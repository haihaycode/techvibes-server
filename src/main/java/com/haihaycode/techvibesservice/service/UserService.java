package com.haihaycode.techvibesservice.service;

import com.haihaycode.techvibesservice.entity.RoleEntity;
import com.haihaycode.techvibesservice.entity.UserEntity;

import com.haihaycode.techvibesservice.exception.ExcelExportException;
import com.haihaycode.techvibesservice.exception.RoleNotFoundException;
import com.haihaycode.techvibesservice.exception.UserAlreadyExistsException;
import com.haihaycode.techvibesservice.repository.RoleRepository;
import com.haihaycode.techvibesservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public Optional<UserEntity> findByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email)));
    }

    public Page<UserEntity> findUsersByCriteria(String keyword, Boolean available, String roleName, Pageable pageable) {
        return userRepository.findUsersByCriteria(keyword, available, roleName, pageable);
    }
    public UserEntity getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
    }


    public UserEntity updateUser(Long userId, String fullName, Integer phone, String address, String email) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        userRepository.findByEmail(email)
                .ifPresent(existingUser -> {
                    if (!existingUser.getUserId().equals(userId)) {
                        throw new UserAlreadyExistsException("User already exists with email: " + email);
                    }
                });
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setAddress(address);
        user.setEmail(email);

        return userRepository.save(user);
    }

    public UserEntity updateUserAvailability(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        user.setAvailable(!user.getAvailable());
        return userRepository.save(user);
    }

    public void addUserRoles(Long userId, List<String> roleNames) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        Set<RoleEntity> roles = new HashSet<>(user.getRoles() != null ? user.getRoles() : new HashSet<>());

        for (String roleName : roleNames) {
            RoleEntity role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName));
            roles.add(role);
        }

        user.setRoles(roles);
        userRepository.save(user);
    }

    public void removeUserRoles(Long userId, List<String> roleNames) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        Set<RoleEntity> roles = user.getRoles();
        if (roles == null) {
            throw new IllegalArgumentException("User has no roles assigned.");
        }

        for (String roleName : roleNames) {
            RoleEntity role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));
            roles.remove(role);
        }
        user.setRoles(roles);
        userRepository.save(user);
    }




    public ByteArrayInputStream exportUsersToExcel() {
        List<UserEntity> users = userRepository.findAll();
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Users");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("User ID");
            headerRow.createCell(1).setCellValue("Email");
            headerRow.createCell(2).setCellValue("Full Name");
            headerRow.createCell(3).setCellValue("Phone");
            headerRow.createCell(4).setCellValue("Address");
            headerRow.createCell(5).setCellValue("Available");
            headerRow.createCell(6).setCellValue("Roles");

            // Create data rows
            int rowNum = 1;
            for (UserEntity user : users) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(user.getUserId());
                row.createCell(1).setCellValue(user.getEmail());
                row.createCell(2).setCellValue(user.getFullName());
                row.createCell(3).setCellValue(String.valueOf(user.getPhone() != null ? user.getPhone() : "N/A")); // Xử lý giá trị null
                row.createCell(4).setCellValue(user.getAddress());
                row.createCell(5).setCellValue(user.getAvailable() ? "Yes" : "No");

                Set<String> roleNames = user.getRoles().stream()
                        .map(RoleEntity::getName) // Giả định rằng RoleEntity có phương thức getName()
                        .collect(Collectors.toSet());
                row.createCell(6).setCellValue(String.join(", ", roleNames));

            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new ExcelExportException("Failed to export users to Excel");
        }
    }

    public ByteArrayInputStream exportUserToExcelById(Long userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException("User not found with ID: " + userId);
        }

        UserEntity user = userOptional.get();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("User");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("User ID");
            headerRow.createCell(1).setCellValue("Email");
            headerRow.createCell(2).setCellValue("Full Name");
            headerRow.createCell(3).setCellValue("Phone");
            headerRow.createCell(4).setCellValue("Address");
            headerRow.createCell(5).setCellValue("Available");
            headerRow.createCell(6).setCellValue("Roles");

            // Create data row
            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue(user.getUserId());
            row.createCell(1).setCellValue(user.getEmail());
            row.createCell(2).setCellValue(user.getFullName() != null ? user.getFullName() : "");
            row.createCell(3).setCellValue(String.valueOf(user.getPhone() != null ? user.getPhone() : "N/A"));
            row.createCell(4).setCellValue(user.getAddress() != null ? user.getAddress() : "");
            row.createCell(5).setCellValue(user.getAvailable() ? "Yes" : "No");
            row.createCell(6).setCellValue(user.getRoles().stream().map(RoleEntity::getName).collect(Collectors.joining(", ")));

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new ExcelExportException("Failed to export user to Excel");
        }
    }


}
