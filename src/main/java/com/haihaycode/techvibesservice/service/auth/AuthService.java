package com.haihaycode.techvibesservice.service.auth;

import com.haihaycode.techvibesservice.entity.RoleEntity;
import com.haihaycode.techvibesservice.entity.UserEntity;
import com.haihaycode.techvibesservice.exception.InvalidInputException;
import com.haihaycode.techvibesservice.exception.RoleNotFoundException;
import com.haihaycode.techvibesservice.exception.UserAlreadyExistsException;
import com.haihaycode.techvibesservice.model.auth.*;
import com.haihaycode.techvibesservice.repository.RoleRepository;
import com.haihaycode.techvibesservice.repository.UserRepository;
import com.haihaycode.techvibesservice.security.JwtIssuer;
import com.haihaycode.techvibesservice.security.UserPrincipal;
import com.haihaycode.techvibesservice.service.UserService;
import com.haihaycode.techvibesservice.service.mail.EmailService;
import com.haihaycode.techvibesservice.service.mail.OtpService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class AuthService {
    private final JwtIssuer jwtIssuer;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final OtpService otpService;
    private final EmailService emailService;


    public LoginResponse attemptLogin(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new InvalidInputException("Email or password cannot be empty");
        }
        var user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            var principal = (UserPrincipal) authentication.getPrincipal();

            var roles = principal.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            String token = jwtIssuer.issuer(principal.getUserId(), principal.getEmail(), roles);
            return LoginResponse.builder()
                    .accessToken(token)
                    .build();
        } catch (AuthenticationException e) {
            throw new InvalidInputException("Invalid email or password");
        }
    }

    public void registerNewUser(RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty() || request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new InvalidInputException("Email or password cannot be empty");
        }
        userService.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new UserAlreadyExistsException("User already exists with email : " + request.getEmail());
                });

        UserEntity newUser = new UserEntity();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setAvailable(true);
        newUser.setCreateDate(new Date());

        RoleEntity role = roleRepository.findById(1L)
                .orElseThrow(() ->  new RoleNotFoundException("Role not found ")); // Thay thế RuntimeException bằng ngoại lệ tùy chỉnh nếu có
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(role);
        newUser.setRoles(roles);
        userRepository.save(newUser);
    }

    public InfoResponse getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidInputException("No authentication details found.");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserPrincipal)) {
            throw new InvalidInputException("Principal is not of type UserPrincipal");
        }

        UserPrincipal userPrincipal = (UserPrincipal) principal;

        UserEntity user = userRepository.findById(userPrincipal.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());

        return InfoResponse.builder()
                .userId(user.getUserId())
                .createDate(user.getCreateDate())
                .updateDate(user.getUpdateDate())
                .address(user.getAddress())
                .phone(user.getPhone())
                .photo(user.getPhoto())
                .fullName(user.getFullName())
                .roles(roles)
                .email(user.getEmail())
                .build();
    }

    public void changePassword(ChangePasswordRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidInputException("No authentication details found.");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserPrincipal)) {
            throw new InvalidInputException("Principal is not of type UserPrincipal");
        }

        UserPrincipal userPrincipal = (UserPrincipal) principal;

        UserEntity user = userRepository.findById(userPrincipal.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidInputException("Current password is incorrect.");
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new InvalidInputException("New passwords do not match.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void sendOtp(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        String otp = otpService.generateOtp(email);
        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidInputException("Passwords do not match.");
        }
        if (!otpService.validateOtp(request.getEmail(), request.getOtp())) {
            throw new InvalidInputException("Invalid OTP.");
        }
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }

}
