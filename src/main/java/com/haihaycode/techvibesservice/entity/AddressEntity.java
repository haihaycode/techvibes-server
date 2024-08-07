package com.haihaycode.techvibesservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AddressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;
    private String phone;
    private String email;
    private String name;

    private Boolean defaultAddress = false; // Địa chỉ mặc định

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private UserEntity user; // Liên kết với người dùng

    // Bạn có thể thêm các getter và setter nếu không sử dụng Lombok
}
