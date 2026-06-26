package com.ozu.chat.message.dto;

import java.util.List;

import com.ozu.chat.message.model.MessageType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SendMessageRequest(
		@NotNull MessageType type,
		@Size(max = 5000) String content,
		List<String> attachmentIds,
		String replyToMessageId) {
}
