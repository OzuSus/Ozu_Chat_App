package com.ozu.chat.user.dto;

import com.ozu.chat.user.model.ThemeMode;

public record ThemePreferenceDto(
		ThemeMode mode,
		String primaryColor,
		String secondaryColor,
		String accentColor,
		int fontSize,
		int borderRadius) {
}
