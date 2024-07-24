package com.haihaycode.techvibesservice.controller;


import com.haihaycode.techvibesservice.model.auth.*;
import com.haihaycode.techvibesservice.model.ResponseWrapper;
import com.haihaycode.techvibesservice.security.UserPrincipal;
import com.haihaycode.techvibesservice.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class authController {
    @Autowired
    public AuthService authService;

    @PostMapping("/auth/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<LoginResponse>> login(@RequestBody @Validated LoginRequest request) {
        ResponseWrapper<LoginResponse> response = new ResponseWrapper<>(HttpStatus.OK, "Login successful", authService.attemptLogin(request.getEmail(),request.getPassword()));
        return ResponseEntity.ok(response);
    }
    @PostMapping("/auth/register")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<Void>> register(@RequestBody @Validated RegisterRequest request) {
        authService.registerNewUser(request);
        ResponseWrapper<Void> response = new ResponseWrapper<>(HttpStatus.CREATED, "Register successful", null);
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("permitAll()")
    @PostMapping("/auth/logout")
    public ResponseEntity<ResponseWrapper<Void>> logout() {
        authService.logout();
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Logout successful", null));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN','STAFF')")
    @GetMapping("/auth/account")
    public ResponseEntity<ResponseWrapper<InfoResponse>> getCurrentUser() {
        InfoResponse infoResponse = authService.getCurrentUser();
        ResponseWrapper<InfoResponse> response = new ResponseWrapper<>(HttpStatus.OK, "User fetched successfully", infoResponse);
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasAnyRole('USER', 'ADMIN','STAFF')")
    @PostMapping("/auth/change-password")
    public ResponseEntity<ResponseWrapper<Void>> changePassword(@RequestBody @Validated ChangePasswordRequest request) {
        authService.changePassword(request);
        ResponseWrapper<Void> response = new ResponseWrapper<>(HttpStatus.OK, "Password changed successfully.", null);
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("permitAll()")
    @PostMapping("/auth/forgot-password")
    public ResponseEntity<ResponseWrapper<Void>> forgotPassword(@RequestBody @Validated ForgotPasswordRequest request) {
        authService.sendOtp(request.getEmail());
        ResponseWrapper<Void> response = new ResponseWrapper<>(HttpStatus.OK, "OPT send successfully.", null);
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("permitAll()")
    @PostMapping("/auth/reset-password")
    public ResponseEntity<ResponseWrapper<Void>> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        ResponseWrapper<Void> response = new ResponseWrapper<>(HttpStatus.OK, "Password has been reset successfully.", null);
        return ResponseEntity.ok(response);
    }






    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/auth/role")
    public String roleUserAndAdmin(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return "u are logged in username :" + userPrincipal.getUsername() + " , password  : " + userPrincipal.getUserId() + " , role : " + userPrincipal.getAuthorities();
    }
    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/auth/role/user")
    public String roleUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return "u are logged in username :" + userPrincipal.getUsername() + " , password  : " + userPrincipal.getUserId() + " , role : " + userPrincipal.getAuthorities();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/auth/role/admin")
    public String roleAdmin(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return "u are logg  ed in username :" + userPrincipal.getUsername() + " , password  : " + userPrincipal.getUserId() + " , role : " + userPrincipal.getAuthorities();
    }

    @PreAuthorize("hasRole('OTHER')")
    @GetMapping("/auth/role/other")
    public String roleOther(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return "u are logg  ed in username :" + userPrincipal.getUsername() + " , password  : " + userPrincipal.getUserId() + " , role : " + userPrincipal.getAuthorities();
    }

    @PreAuthorize("hasRole('STAFF')")
    @GetMapping("/auth/role/staff")
    public String roleStaff(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return "u are logg  ed in username :" + userPrincipal.getUsername() + " , password  : " + userPrincipal.getUserId() + " , role : " + userPrincipal.getAuthorities();
    }
}
