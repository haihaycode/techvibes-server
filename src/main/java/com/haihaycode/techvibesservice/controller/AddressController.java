package com.haihaycode.techvibesservice.controller;

import com.haihaycode.techvibesservice.entity.AddressEntity;
import com.haihaycode.techvibesservice.entity.UserEntity;
import com.haihaycode.techvibesservice.model.ResponseWrapper;
import com.haihaycode.techvibesservice.service.AddressService;
import com.haihaycode.techvibesservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/public")  
public class AddressController {

    @Autowired
    private AddressService addressService;
    @Autowired
    private UserService userService;


    // Lấy danh sách địa chỉ của người dùng hiện tại
    @GetMapping("/address")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<List<AddressEntity>>> getAddresses(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<UserEntity> user = userService.findByEmail(userDetails.getUsername());
        List<AddressEntity> addresses = addressService.getAddressesByUser(user.get());
        ResponseWrapper<List<AddressEntity>> response = new ResponseWrapper<>(
                HttpStatus.OK, "Danh sách địa chỉ", addresses);
        return ResponseEntity.ok(response);
    }
    // Xóa địa chỉ theo ID
    @DeleteMapping("/address/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<Void>> deleteAddress(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<UserEntity> user = userService.findByEmail(userDetails.getUsername());
        addressService.deleteAddressByIdAndUser(id, user.get());
        ResponseWrapper<Void> response = new ResponseWrapper<>(
                HttpStatus.NO_CONTENT, "Địa chỉ đã được xóa", null);
        return ResponseEntity.ok(response);
    }

    // Cập nhật địa chỉ theo ID
    @PutMapping("/address/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<AddressEntity>> updateAddress(@PathVariable Long id, @RequestBody AddressEntity address,
                                                                        @AuthenticationPrincipal UserDetails userDetails) {
        Optional<UserEntity> user = userService.findByEmail(userDetails.getUsername());
        Optional<AddressEntity> updatedAddress = addressService.updateAddressByIdAndUser(id, user.get(), address);
        if (updatedAddress.isPresent()) {
            ResponseWrapper<AddressEntity> response = new ResponseWrapper<>(
                    HttpStatus.OK, "Địa chỉ đã được cập nhật", updatedAddress.get());
            return ResponseEntity.ok(response);
        } else {
            ResponseWrapper<AddressEntity> response = new ResponseWrapper<>(
                    HttpStatus.NOT_FOUND, "Địa chỉ không tìm thấy", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // Cập nhật địa chỉ mặc định
    @PutMapping("/address/{id}/default")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<AddressEntity>> setDefaultAddress(@PathVariable Long id,
                                                                            @AuthenticationPrincipal UserDetails userDetails) {
        Optional<UserEntity> user = userService.findByEmail(userDetails.getUsername());
        Optional<AddressEntity> updatedAddress = addressService.setDefaultAddress(id, user.get());
        if (updatedAddress.isPresent()) {
            ResponseWrapper<AddressEntity> response = new ResponseWrapper<>(
                    HttpStatus.OK, "Địa chỉ mặc định đã được cập nhật", updatedAddress.get());
            return ResponseEntity.ok(response);
        } else {
            ResponseWrapper<AddressEntity> response = new ResponseWrapper<>(
                    HttpStatus.NOT_FOUND, "Địa chỉ không tìm thấy", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/address")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN','STAFF')")
    public ResponseEntity<ResponseWrapper<AddressEntity>> addAddress(@RequestBody AddressEntity address,
                                                                     @AuthenticationPrincipal UserDetails userDetails) {
        // Lấy thông tin người dùng từ UserDetails
        Optional<UserEntity> user = userService.findByEmail(userDetails.getUsername());
        // Liên kết địa chỉ với người dùng

        address.setUser(user.get());
        // Lưu địa chỉ mới
        AddressEntity newAddress = addressService.saveAddress(address);
        // Gói gọn phản hồi trong ResponseWrapper
        ResponseWrapper<AddressEntity> response = new ResponseWrapper<>(
                HttpStatus.CREATED, "Địa chỉ mới đã được thêm", newAddress);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
