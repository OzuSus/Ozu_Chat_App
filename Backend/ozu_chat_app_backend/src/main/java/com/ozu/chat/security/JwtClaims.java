package com.ozu.chat.security;

import java.time.Instant;
import java.util.Set;

import com.ozu.chat.user.model.UserRole;

public record JwtClaims(
		String subject,
		String email,
		String username,
		Set<UserRole> roles,
		Instant expiresAt) {
}
