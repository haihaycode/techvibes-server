package com.haihaycode.techvibesservice.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtIssuer {


    /*
      * return JWT.create(): Bắt đầu xây dựng JWT.
      .withSubject(String.valueOf(userId)): Đặt userId của người dùng làm subject của JWT.
      .withExpiresAt(Instant.now().plus(Duration.of(1, ChronoUnit.DAYS))): Đặt thời gian hết hạn cho JWT là 1 ngày kể từ thời điểm hiện tại.
      .withClaim("e", email): Thêm claim email vào JWT với key là e.
      .withClaim("a", roles): Thêm claim roles vào JWT với key là a.
      .sign(Algorithm.HMAC256(properties.getSecretKey())): Ký JWT bằng thuật toán HMAC256 và secret key lấy từ JwtProperties.
      *
      Đoạn mã này tạo ra một JWT chứa thông tin userId, email và roles,
      với thời hạn sử dụng là 1 ngày. JWT này được mã hóa bằng
      thuật toán HMAC256 sử dụng secret key từ JwtProperties.
      *
      * kiểm tra JWT trên jwt.io
      *
      *  Đảm bảo tính toàn vẹn của JWT
JWT là một chuỗi ký tự bao gồm ba phần: header, payload và signature. Phần signature được tạo ra bằng cách kết hợp header và payload với một khóa bí mật (secret key) và sử dụng một thuật toán mã hóa như HMAC256.
Khóa bí mật này giúp bảo đảm rằng bất kỳ sự thay đổi nào đối với nội dung của JWT (header hoặc payload) sau khi token đã được tạo ra sẽ làm cho signature không còn hợp lệ. Điều này ngăn chặn các cuộc tấn công thay đổi dữ liệu bên trong JWT.
    * */

    private final JwtProperties properties;

    public String issuer(long userId, String email, List<String> roles) {
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withExpiresAt(Instant.now().plus(Duration.of(1, ChronoUnit.DAYS)))
                .withClaim("e", email)
                .withClaim("a", roles)
                .sign(Algorithm.HMAC256(properties.getSecretKey()));
    }
}
