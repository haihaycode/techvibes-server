package com.haihaycode.techvibesservice.security;

import com.haihaycode.techvibesservice.exception.InvalidTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtDecoder jwtDecoder;
    private final JwtToPrincipalConverter jwtToPrincipalConverter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            extractTokenFromRequest(request)
                    .map(jwtDecoder::decode)// một đối tượng đại diện cho dữ liệu bên trong JWT
                    .map(jwtToPrincipalConverter::convert)//một đối tượng chứa thông tin người dùng sau khi đã xác thực JWT.
                    .map(UserPrincipalAuthenticationToken::new)// sẽ tạo ra một UserPrincipalAuthenticationToken từ UserPrincipal
                    .ifPresent(authentication -> SecurityContextHolder.getContext().setAuthentication(authentication));//đại diện cho 1 đối tượng đã xt
        } catch (InvalidTokenException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            String errorMessage = String.format("{\"error\": \"%s\"}", "UNAUTHORIZED");
            response.getWriter().write(errorMessage);
            return;
        }
        filterChain.doFilter(request, response);
    }

    //hàm này lấy token ra từ header
    private Optional<String> extractTokenFromRequest(HttpServletRequest request) {
        var token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return Optional.of(token.substring(7));
        }
        return Optional.empty();
    }
}
