package com.haihaycode.techvibesservice.controller.admin;

import com.haihaycode.techvibesservice.entity.RoleEntity;
import com.haihaycode.techvibesservice.entity.UserEntity;
import com.haihaycode.techvibesservice.model.ResponseWrapper;
import com.haihaycode.techvibesservice.model.auth.UpdateUserRequest;
import com.haihaycode.techvibesservice.service.RoleService;
import com.haihaycode.techvibesservice.service.UserService;
import com.haihaycode.techvibesservice.service.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AdminAccountController {

    @Autowired
    private UserService userService;
           @GetMapping("/users/list/k")
           @PreAuthorize("hasRole('ADMIN,STAFF')")
           public ResponseEntity<ResponseWrapper<List<UserEntity>>> getAllUserList(){
               ResponseWrapper<List<UserEntity>> response = new ResponseWrapper<>(HttpStatus.OK, "Users fetched successfully", userService.getAllUsers());
               return ResponseEntity.ok(response);
           }
            @GetMapping("/users")
            @PreAuthorize("hasRole('ADMIN')")
            public ResponseEntity<ResponseWrapper<Page<UserEntity>>> findUsersByCriteria(
                    @RequestParam(value = "keyword", required = false) String keyword,
                    @RequestParam(value = "available", required = false) Boolean available,
                    @RequestParam(value = "roleName", required = false) String roleName,
                    @RequestParam(defaultValue = "0") int page,
                    @RequestParam(defaultValue = "10") int limit,
                    @RequestParam(required = false) Optional<String> sort) {

                String sortField = "userId";
                Sort.Direction sortDirection = Sort.Direction.DESC;

                if (sort.isPresent()) {
                    String[] sortParams = sort.get().split(",");
                    sortField = sortParams[0];
                    if (sortParams.length > 1) {
                        sortDirection = Sort.Direction.fromString(sortParams[1]);
                    }
                }
                Pageable pageable = PageRequest.of(page, limit, Sort.by(new Sort.Order(sortDirection, sortField)));

                Page<UserEntity> users = userService.findUsersByCriteria(keyword, available, roleName, pageable);
                ResponseWrapper<Page<UserEntity>> response = new ResponseWrapper<>(HttpStatus.OK, "Users fetched successfully", users);
                return ResponseEntity.ok(response);
            }



    @GetMapping("/users/get/{userId}")
    @PreAuthorize("hasRole('ADMIN')") //or #userId == principal.userId"
    public ResponseEntity<ResponseWrapper<UserEntity>> getUserById(@PathVariable Long userId) {
        UserEntity user = userService.getUserById(userId);
        ResponseWrapper<UserEntity> response = new ResponseWrapper<>(HttpStatus.OK, "User fetched successfully", user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<UserEntity>> updateUser(
            @PathVariable Long userId,
            @RequestBody UpdateUserRequest updateRequest) {

        UserEntity updatedUser = userService.updateUser(userId, updateRequest.getFullName(),
                updateRequest.getPhone(), updateRequest.getAddress(), updateRequest.getEmail());

        ResponseWrapper<UserEntity> response = new ResponseWrapper<>(HttpStatus.OK, "Cập nhật tài khoản thành công", updatedUser);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/{userId}/availability")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<UserEntity>> updateUserAvailability(
            @PathVariable Long userId) {
        UserEntity updatedUser = userService.updateUserAvailability(userId);
        ResponseWrapper<UserEntity> response = new ResponseWrapper<>(HttpStatus.OK, "Cập nhật thành công", updatedUser);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{userId}/roles/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<Void>> addUserRoles(
            @PathVariable Long userId,
            @RequestBody List<String> roleNames) {

        userService.addUserRoles(userId, roleNames);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Cập nhật quyền thành công", null));

    }

//    @PatchMapping("/{userId}/roles/remove")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ResponseWrapper<Void>> removeUserRoles(
//            @PathVariable Long userId,
//            @RequestBody List<String> roleNames) {
//        userService.removeUserRoles(userId, roleNames);
//        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Roles removed successfully", null));
//    }
    @GetMapping("/users/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource> exportUsersToExcel() {
        ByteArrayInputStream inputStream = userService.exportUsersToExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=accounts.xlsx");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(inputStream));
    }

        @GetMapping("/users/export/{userId}")
        @PreAuthorize("permitAll()")
        public ResponseEntity<byte[]> exportUserToExcelById(@PathVariable Long userId) {
            ByteArrayInputStream inputStream = userService.exportUserToExcelById(userId);
    
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=account_" + userId + ".xlsx");
    
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(inputStream.readAllBytes());
        }


}
