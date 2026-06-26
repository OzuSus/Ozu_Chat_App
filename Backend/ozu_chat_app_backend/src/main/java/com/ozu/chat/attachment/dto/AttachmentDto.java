package com.ozu.chat.attachment.dto;

import java.time.Instant;

import com.ozu.chat.attachment.model.AttachmentKind;

public record AttachmentDto(
		String id,
		String originalName,
		String contentType,
		long size,
		AttachmentKind kind,
		String url,
		Integer width,
		Integer height,
		Long durationMs,
		Instant createdAt) {
}
