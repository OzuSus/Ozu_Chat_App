package com.ozu.chat.attachment;

import com.ozu.chat.attachment.dto.AttachmentDto;
import com.ozu.chat.attachment.model.Attachment;
import org.springframework.stereotype.Component;

@Component
public class AttachmentMapper {

	public AttachmentDto toDto(Attachment attachment) {
		return new AttachmentDto(
				attachment.getId(),
				attachment.getOriginalName(),
				attachment.getContentType(),
				attachment.getSize(),
				attachment.getKind(),
				attachment.getUrl(),
				attachment.getWidth(),
				attachment.getHeight(),
				attachment.getDurationMs(),
				attachment.getCreatedAt());
	}
}
