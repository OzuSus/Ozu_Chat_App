package com.ozu.chat.attachment;

import com.ozu.chat.attachment.dto.AttachmentDto;
import com.ozu.chat.common.ApiResponse;
import com.ozu.chat.common.CurrentUser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {

	private final AttachmentService attachmentService;

	public AttachmentController(AttachmentService attachmentService) {
		this.attachmentService = attachmentService;
	}

	@PostMapping
	ApiResponse<AttachmentDto> upload(@RequestPart("file") MultipartFile file) {
		return ApiResponse.ok("File uploaded", attachmentService.store(CurrentUser.get().id(), file, false));
	}
}
