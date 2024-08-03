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
import com.haihaycode.techvibesservice.service.image.ImageService;
import com.haihaycode.techvibesservice.service.mail.EmailService;
import com.haihaycode.techvibesservice.service.mail.OtpService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class AuthService {
    private final JwtIssuer jwtIssuer;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final OtpService otpService;
    private final EmailService emailService;
    private final ImageService imageService;


    public LoginResponse attemptLogin(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new InvalidInputException("Email or password không được rỗng");
        }
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Người dùng không tồn tại"));
        if(!user.getAvailable()) {
            throw new UsernameNotFoundException("Người dùng không tồn tại");
        }
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
            throw new InvalidInputException("Tài khoản hoặc mật khẩu không đúng");
        }
    }

    public void registerNewUser(RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty() || request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new InvalidInputException("Tài khoản mật khẩu không được rỗng");
        }
        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new UserAlreadyExistsException("Tài khoản đã tồn tại ");
                });

        UserEntity newUser = new UserEntity();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setAvailable(true);
        newUser.setCreateDate(new Date());


        userRepository.save(newUser);
    }

    public InfoResponse getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidInputException("No authentication details found.");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserPrincipal userPrincipal)) {
            throw new InvalidInputException("Principal is not of type UserPrincipal");
        }

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
        if (!(principal instanceof UserPrincipal userPrincipal)) {
            throw new InvalidInputException("Principal is not of type UserPrincipal");
        }

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
            throw new InvalidInputException("Mât khẩu xác minh không trùng khớp.");
        }
        if (!otpService.validateOtp(request.getEmail(), request.getOtp())) {
            throw new InvalidInputException("OTP không hợp lệ .");
        }
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng có email : " + request.getEmail()));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        if (!otpService.validateOtp(request.getEmail(), request.getOtp())) {
            throw new InvalidInputException("OTP không hợp lệ  .");
        }
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản : " + request.getEmail()));

                RoleEntity role = roleRepository.findById(2L)//ROLE_USER IN DATABASE
                .orElseThrow(() -> new RoleNotFoundException("Đã có lỗi , vui lòng thử lại sau ( không tìm thấy quyền )  "));
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        userRepository.save(user);
    }


    public void updateAccount(UpdateUserRequest request, Optional<MultipartFile> file) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidInputException("No authentication details found.");
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserPrincipal userPrincipal)) {
            throw new InvalidInputException("Principal is not of type UserPrincipal");
        }
        UserEntity user = userRepository.findById(userPrincipal.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setFullName(request.getFullName());
        user.setAddress(request.getAddress());
        user.setPhone(request.getPhone());
        user.setUpdateDate(new Date());

        if (file.isPresent() && !file.get().isEmpty()) {
            try {
                imageService.deleteImage(user.getPhoto(), "image/directory/account/");
                user.setPhoto(imageService.saveImage(file.get(), "image/directory/account/"));
            } catch (IOException e) {
                throw new InvalidInputException(e.getMessage());
            }
        }
        userRepository.save(user);
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }

}
