package com.ozu.chat.message.dto;

import java.time.Instant;
import java.util.List;

import com.ozu.chat.attachment.dto.AttachmentDto;
import com.ozu.chat.message.model.MessageStatus;
import com.ozu.chat.message.model.MessageType;
import com.ozu.chat.user.dto.UserSummaryDto;

public record MessageDto(
		String id,
		String conversationId,
		UserSummaryDto sender,
		MessageType type,
		MessageStatus status,
		String content,
		List<AttachmentDto> attachments,
		String replyToMessageId,
		List<MessageReactionDto> reactions,
		Instant editedAt,
		Instant deletedAt,
		Instant recalledAt,
		Instant createdAt,
		Instant updatedAt) {
}
