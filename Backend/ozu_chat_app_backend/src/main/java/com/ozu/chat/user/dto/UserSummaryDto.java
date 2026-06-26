package com.ozu.chat.user.dto;

import java.time.Instant;

import com.ozu.chat.user.model.UserStatus;

public record UserSummaryDto(
		String id,
		String username,
		String displayName,
		String avatarUrl,
		UserStatus status,
		Instant lastSeenAt) {
}
