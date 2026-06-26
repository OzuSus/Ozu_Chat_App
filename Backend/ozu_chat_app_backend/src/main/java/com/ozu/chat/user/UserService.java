package com.ozu.chat.user;

import java.util.List;

import com.ozu.chat.attachment.AttachmentService;
import com.ozu.chat.attachment.dto.AttachmentDto;
import com.ozu.chat.exception.NotFoundException;
import com.ozu.chat.user.dto.ThemePreferenceRequest;
import com.ozu.chat.user.dto.UpdateProfileRequest;
import com.ozu.chat.user.dto.UserProfileDto;
import com.ozu.chat.user.dto.UserSummaryDto;
import com.ozu.chat.user.model.ThemePreference;
import com.ozu.chat.user.model.User;
import com.ozu.chat.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final AttachmentService attachmentService;

	public UserService(UserRepository userRepository, UserMapper userMapper, AttachmentService attachmentService) {
		this.userRepository = userRepository;
		this.userMapper = userMapper;
		this.attachmentService = attachmentService;
	}

	public UserProfileDto me(String userId) {
		return userMapper.toProfile(getById(userId));
	}

	public UserProfileDto updateProfile(String userId, UpdateProfileRequest request) {
		User user = getById(userId);
		if (request.displayName() != null) {
			user.setDisplayName(request.displayName().trim());
		}
		if (request.bio() != null) {
			user.setBio(request.bio().trim());
		}
		if (request.language() != null) {
			user.setLanguage(request.language());
		}
		if (request.theme() != null) {
			applyTheme(user.getTheme(), request.theme());
		}
		return userMapper.toProfile(userRepository.save(user));
	}

	public AttachmentDto uploadAvatar(String userId, MultipartFile file) {
		AttachmentDto attachment = attachmentService.store(userId, file, true);
		User user = getById(userId);
		user.setAvatarUrl(attachment.url());
		userRepository.save(user);
		return attachment;
	}

	public UserProfileDto removeAvatar(String userId) {
		User user = getById(userId);
		user.setAvatarUrl(null);
		return userMapper.toProfile(userRepository.save(user));
	}

	public List<UserSummaryDto> search(String query) {
		String normalized = query == null ? "" : query.trim();
		if (normalized.isBlank()) {
			return userRepository.findAll(PageRequest.of(0, 20))
					.stream()
					.map(userMapper::toSummary)
					.toList();
		}
		return userRepository.findAll(PageRequest.of(0, 80))
				.stream()
				.filter(user -> user.getUsername().toLowerCase().contains(normalized.toLowerCase())
						|| user.getDisplayName().toLowerCase().contains(normalized.toLowerCase()))
				.limit(20)
				.map(userMapper::toSummary)
				.toList();
	}

	private User getById(String userId) {
		return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
	}

	private void applyTheme(ThemePreference theme, ThemePreferenceRequest request) {
		if (request.mode() != null) {
			theme.setMode(request.mode());
		}
		if (request.primaryColor() != null) {
			theme.setPrimaryColor(request.primaryColor());
		}
		if (request.secondaryColor() != null) {
			theme.setSecondaryColor(request.secondaryColor());
		}
		if (request.accentColor() != null) {
			theme.setAccentColor(request.accentColor());
		}
		if (request.fontSize() != null) {
			theme.setFontSize(request.fontSize());
		}
		if (request.borderRadius() != null) {
			theme.setBorderRadius(request.borderRadius());
		}
	}
}
