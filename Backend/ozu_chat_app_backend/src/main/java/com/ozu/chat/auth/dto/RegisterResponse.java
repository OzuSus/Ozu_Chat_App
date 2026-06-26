package com.ozu.chat.auth.dto;

import com.ozu.chat.user.dto.UserProfileDto;

public record RegisterResponse(
		UserProfileDto user,
		String devVerificationToken) {
}
