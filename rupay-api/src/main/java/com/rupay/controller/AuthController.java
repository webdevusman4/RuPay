package com.rupay.controller;

import com.rupay.dto.AuthDTO;
import com.rupay.dto.CommonDTO;
import com.rupay.model.User;
import com.rupay.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<CommonDTO.ApiResponse<AuthDTO.AuthResponse>> register(
            @Valid @RequestBody AuthDTO.RegisterRequest request) {
        try {
            AuthDTO.AuthResponse response = authService.register(request);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success("Registration successful", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<CommonDTO.ApiResponse<AuthDTO.AuthResponse>> login(
            @Valid @RequestBody AuthDTO.LoginRequest request) {
        try {
            AuthDTO.AuthResponse response = authService.login(request);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success("Login successful", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(CommonDTO.ApiResponse.error("Invalid email or password"));
        }
    }

    @PostMapping("/verify-pin")
    public ResponseEntity<CommonDTO.ApiResponse<Boolean>> verifyPin(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AuthDTO.VerifyPinRequest request) {
        boolean valid = authService.verifyPin(user, request.getPin());
        if (valid) {
            return ResponseEntity.ok(CommonDTO.ApiResponse.success("PIN verified", true));
        }
        return ResponseEntity.badRequest()
                .body(CommonDTO.ApiResponse.error("Invalid PIN"));
    }

    @GetMapping("/me")
    public ResponseEntity<CommonDTO.ApiResponse<CommonDTO.UserProfile>> getCurrentUser(
            @AuthenticationPrincipal User user) {
        CommonDTO.UserProfile profile = CommonDTO.UserProfile.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .pkrBalance(user.getPkrBalance())
                .isAdmin(user.isAdmin())
                .createdAt(user.getCreatedAt())
                .build();

        return ResponseEntity.ok(CommonDTO.ApiResponse.success(profile));
    }
}
