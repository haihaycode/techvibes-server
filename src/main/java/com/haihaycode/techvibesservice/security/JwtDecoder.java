package com.haihaycode.techvibesservice.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtDecoder {

    /*
    * cụ thể : dịch token mà JWT đã mã hóa ở class::JwtIssuer ngược lại thành 1 đối tượng DecodedJWT
    */
    private final JwtProperties properties;
    public DecodedJWT decode(String token){
     return JWT.require(Algorithm.HMAC256(properties.getSecretKey()))
             .build()
             .verify(token);
    }
}
