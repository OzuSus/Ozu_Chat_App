package com.ozu.chat.user;

import java.util.List;

import com.ozu.chat.attachment.dto.AttachmentDto;
import com.ozu.chat.common.ApiResponse;
import com.ozu.chat.common.CurrentUser;
import com.ozu.chat.user.dto.UpdateProfileRequest;
import com.ozu.chat.user.dto.UserProfileDto;
import com.ozu.chat.user.dto.UserSummaryDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/me")
	ApiResponse<UserProfileDto> me() {
		return ApiResponse.ok("Current user", userService.me(CurrentUser.get().id()));
	}

	@PatchMapping("/me")
	ApiResponse<UserProfileDto> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
		return ApiResponse.ok("Profile updated", userService.updateProfile(CurrentUser.get().id(), request));
	}

	@PostMapping("/me/avatar")
	ApiResponse<AttachmentDto> uploadAvatar(@RequestPart("file") MultipartFile file) {
		return ApiResponse.ok("Avatar uploaded", userService.uploadAvatar(CurrentUser.get().id(), file));
	}

	@DeleteMapping("/me/avatar")
	ApiResponse<UserProfileDto> removeAvatar() {
		return ApiResponse.ok("Avatar removed", userService.removeAvatar(CurrentUser.get().id()));
	}

	@GetMapping("/search")
	ApiResponse<List<UserSummaryDto>> search(@RequestParam(required = false) String q) {
		return ApiResponse.ok("Users", userService.search(q));
	}
}
