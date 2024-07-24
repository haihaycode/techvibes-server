package com.haihaycode.techvibesservice.model.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    Long userId;
    @NotBlank(message = "Full name is required")
    private String fullName;
    @Email(message = "Email should be valid")
    private String email;
    private String address;
    private String phone;
    private String photo;
}
