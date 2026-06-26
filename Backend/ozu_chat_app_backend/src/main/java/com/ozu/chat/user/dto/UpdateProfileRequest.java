package com.ozu.chat.user.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
		@Size(min = 2, max = 60) String displayName,
		@Size(max = 220) String bio,
		@Pattern(regexp = "en|vi", message = "must be en or vi") String language,
		@Valid ThemePreferenceRequest theme) {
}
