package com.ozu.chat.conversation.dto;

import java.util.List;

import com.ozu.chat.conversation.model.ConversationType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateConversationRequest(
		@NotNull ConversationType type,
		@Size(min = 2, max = 80) String name,
		@NotEmpty List<String> memberIds) {
}
