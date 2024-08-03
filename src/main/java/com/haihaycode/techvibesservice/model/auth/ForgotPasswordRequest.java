package com.haihaycode.techvibesservice.model.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordRequest {
    @NotNull(message = "Email không được rỗng")
    private String email;
}
