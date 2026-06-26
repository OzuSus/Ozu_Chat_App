package com.ozu.chat.conversation.dto;

import java.time.Instant;
import java.util.List;

import com.ozu.chat.conversation.model.ConversationType;
import com.ozu.chat.message.dto.MessageDto;
import com.ozu.chat.user.dto.UserSummaryDto;

public record ConversationDto(
		String id,
		ConversationType type,
		String name,
		String avatarUrl,
		List<UserSummaryDto> members,
		String createdBy,
		MessageDto lastMessage,
		Instant lastMessageAt,
		Instant createdAt,
		Instant updatedAt) {
}
