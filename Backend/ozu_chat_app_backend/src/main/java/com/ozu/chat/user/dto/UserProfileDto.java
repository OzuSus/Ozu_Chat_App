package com.ozu.chat.user.dto;

import java.time.Instant;
import java.util.Set;

import com.ozu.chat.user.model.UserRole;
import com.ozu.chat.user.model.UserStatus;

public record UserProfileDto(
		String id,
		String email,
		String username,
		String displayName,
		String bio,
		String avatarUrl,
		String language,
		ThemePreferenceDto theme,
		UserStatus status,
		Instant lastSeenAt,
		boolean emailVerified,
		Set<UserRole> roles,
		Instant createdAt,
		Instant updatedAt) {
}
