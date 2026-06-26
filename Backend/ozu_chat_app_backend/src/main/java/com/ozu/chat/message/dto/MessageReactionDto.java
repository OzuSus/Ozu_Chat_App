package com.ozu.chat.message.dto;

import java.time.Instant;

public record MessageReactionDto(
		String userId,
		String emoji,
		Instant createdAt) {
}
