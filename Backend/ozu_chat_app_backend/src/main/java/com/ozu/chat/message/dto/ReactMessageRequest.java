package com.ozu.chat.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReactMessageRequest(
		@NotBlank @Size(max = 16) String emoji) {
}
