package com.ozu.chat.user.dto;

import com.ozu.chat.user.model.ThemeMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record ThemePreferenceRequest(
		ThemeMode mode,
		@Pattern(regexp = "^#[0-9a-fA-F]{6}$") String primaryColor,
		@Pattern(regexp = "^#[0-9a-fA-F]{6}$") String secondaryColor,
		@Pattern(regexp = "^#[0-9a-fA-F]{6}$") String accentColor,
		@Min(13) @Max(20) Integer fontSize,
		@Min(8) @Max(28) Integer borderRadius) {
}
