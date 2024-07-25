package com.haihaycode.techvibesservice.model.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyEmailRequest {
    private String email;
    private String otp;
}
