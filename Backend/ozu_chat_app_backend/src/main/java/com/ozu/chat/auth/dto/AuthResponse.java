package com.ozu.chat.auth.dto;

import java.time.Instant;

import com.ozu.chat.user.dto.UserProfileDto;

public record AuthResponse(
		String accessToken,
		String refreshToken,
		Instant accessTokenExpiresAt,
		Instant refreshTokenExpiresAt,
		UserProfileDto user) {
}
