package com.ozu.chat.attachment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import com.ozu.chat.attachment.dto.AttachmentDto;
import com.ozu.chat.attachment.model.Attachment;
import com.ozu.chat.attachment.model.AttachmentKind;
import com.ozu.chat.attachment.repository.AttachmentRepository;
import com.ozu.chat.config.AppProperties;
import com.ozu.chat.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AttachmentService {

	private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
			"png", "jpg", "jpeg", "webp", "gif", "mp4", "webm", "mov",
			"pdf", "doc", "docx", "xls", "xlsx", "zip", "txt", "csv");

	private final AttachmentRepository attachmentRepository;
	private final AttachmentMapper attachmentMapper;
	private final AppProperties appProperties;

	public AttachmentService(
			AttachmentRepository attachmentRepository,
			AttachmentMapper attachmentMapper,
			AppProperties appProperties) {
		this.attachmentRepository = attachmentRepository;
		this.attachmentMapper = attachmentMapper;
		this.appProperties = appProperties;
	}

	public AttachmentDto store(String ownerId, MultipartFile file, boolean avatar) {
		if (file == null || file.isEmpty()) {
			throw new BadRequestException("File is required");
		}
		String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
		String extension = extension(originalName);
		if (!ALLOWED_EXTENSIONS.contains(extension)) {
			throw new BadRequestException("File type is not allowed");
		}

		AttachmentKind kind = avatar ? AttachmentKind.AVATAR : kind(file.getContentType(), extension);
		String storageName = UUID.randomUUID() + "." + extension;
		Path uploadRoot = Path.of(appProperties.upload().directory()).toAbsolutePath().normalize();
		Path target = uploadRoot.resolve(storageName).normalize();
		if (!target.startsWith(uploadRoot)) {
			throw new BadRequestException("Invalid file name");
		}

		try {
			Files.createDirectories(uploadRoot);
			Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException exception) {
			throw new BadRequestException("Unable to store file");
		}

		Attachment attachment = new Attachment();
		attachment.setOwnerId(ownerId);
		attachment.setOriginalName(originalName);
		attachment.setStorageName(storageName);
		attachment.setContentType(file.getContentType());
		attachment.setSize(file.getSize());
		attachment.setKind(kind);
		attachment.setUrl("/uploads/" + storageName);
		return attachmentMapper.toDto(attachmentRepository.save(attachment));
	}

	private String extension(String fileName) {
		int dotIndex = fileName.lastIndexOf('.');
		if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
			throw new BadRequestException("File extension is required");
		}
		return fileName.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
	}

	private AttachmentKind kind(String contentType, String extension) {
		String type = contentType == null ? "" : contentType.toLowerCase(Locale.ROOT);
		if (type.startsWith("image/") && "gif".equals(extension)) {
			return AttachmentKind.GIF;
		}
		if (type.startsWith("image/")) {
			return AttachmentKind.IMAGE;
		}
		if (type.startsWith("video/")) {
			return AttachmentKind.VIDEO;
		}
		if ("pdf".equals(extension)) {
			return AttachmentKind.PDF;
		}
		if (Set.of("doc", "docx", "txt").contains(extension)) {
			return AttachmentKind.DOCUMENT;
		}
		if (Set.of("xls", "xlsx", "csv").contains(extension)) {
			return AttachmentKind.SPREADSHEET;
		}
		if ("zip".equals(extension)) {
			return AttachmentKind.ARCHIVE;
		}
		return AttachmentKind.FILE;
	}
}
