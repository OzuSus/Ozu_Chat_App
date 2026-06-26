package com.ozu.chat.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
		@NotBlank @Email @Size(max = 120) String email,
		@NotBlank @Pattern(regexp = "^[a-zA-Z0-9_]{3,30}$", message = "must be 3-30 letters, numbers, or underscores") String username,
		@NotBlank @Size(min = 8, max = 72) String password,
		@NotBlank @Size(min = 2, max = 60) String displayName,
		@Pattern(regexp = "en|vi", message = "must be en or vi") String language) {
}
