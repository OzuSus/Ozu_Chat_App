package com.ozu.chat.auth;

import com.ozu.chat.auth.dto.AuthResponse;
import com.ozu.chat.auth.dto.LoginRequest;
import com.ozu.chat.auth.dto.LogoutRequest;
import com.ozu.chat.auth.dto.RefreshTokenRequest;
import com.ozu.chat.auth.dto.RegisterRequest;
import com.ozu.chat.auth.dto.RegisterResponse;
import com.ozu.chat.auth.dto.VerifyEmailRequest;
import com.ozu.chat.common.ApiResponse;
import com.ozu.chat.common.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	ApiResponse<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
		return ApiResponse.ok("Account created. Please verify your email.", authService.register(request));
	}

	@PostMapping("/login")
	ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		return ApiResponse.ok("Login successful", authService.login(request));
	}

	@PostMapping("/refresh")
	ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
		return ApiResponse.ok("Token refreshed", authService.refresh(request.refreshToken()));
	}

	@PostMapping("/logout")
	ApiResponse<Void> logout(@Valid @RequestBody LogoutRequest request) {
		authService.logout(CurrentUser.get().id(), request.refreshToken());
		return ApiResponse.ok("Logged out");
	}

	@PostMapping("/verify-email")
	ApiResponse<Void> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
		authService.verifyEmail(request.token());
		return ApiResponse.ok("Email verified");
	}

	@GetMapping("/verify-email")
	ApiResponse<Void> verifyEmailLink(@RequestParam String token) {
		authService.verifyEmail(token);
		return ApiResponse.ok("Email verified");
	}
}
