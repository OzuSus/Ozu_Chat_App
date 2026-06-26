package com.ozu.chat.conversation;

import java.util.List;

import com.ozu.chat.common.ApiResponse;
import com.ozu.chat.common.CurrentUser;
import com.ozu.chat.conversation.dto.ConversationDto;
import com.ozu.chat.conversation.dto.CreateConversationRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

	private final ConversationService conversationService;

	public ConversationController(ConversationService conversationService) {
		this.conversationService = conversationService;
	}

	@GetMapping
	ApiResponse<List<ConversationDto>> mine(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "30") int size) {
		return ApiResponse.ok("Conversations", conversationService.mine(CurrentUser.get().id(), page, size));
	}

	@PostMapping
	ApiResponse<ConversationDto> create(@Valid @RequestBody CreateConversationRequest request) {
		return ApiResponse.ok("Conversation created", conversationService.create(CurrentUser.get().id(), request));
	}
}
