package com.ozu.chat.user;

import com.ozu.chat.user.dto.ThemePreferenceDto;
import com.ozu.chat.user.dto.UserProfileDto;
import com.ozu.chat.user.dto.UserSummaryDto;
import com.ozu.chat.user.model.ThemePreference;
import com.ozu.chat.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

	public UserProfileDto toProfile(User user) {
		return new UserProfileDto(
				user.getId(),
				user.getEmail(),
				user.getUsername(),
				user.getDisplayName(),
				user.getBio(),
				user.getAvatarUrl(),
				user.getLanguage(),
				toTheme(user.getTheme()),
				user.getStatus(),
				user.getLastSeenAt(),
				user.isEmailVerified(),
				user.getRoles(),
				user.getCreatedAt(),
				user.getUpdatedAt());
	}

	public UserSummaryDto toSummary(User user) {
		return new UserSummaryDto(
				user.getId(),
				user.getUsername(),
				user.getDisplayName(),
				user.getAvatarUrl(),
				user.getStatus(),
				user.getLastSeenAt());
	}

	public ThemePreferenceDto toTheme(ThemePreference theme) {
		return new ThemePreferenceDto(
				theme.getMode(),
				theme.getPrimaryColor(),
				theme.getSecondaryColor(),
				theme.getAccentColor(),
				theme.getFontSize(),
				theme.getBorderRadius());
	}
}
