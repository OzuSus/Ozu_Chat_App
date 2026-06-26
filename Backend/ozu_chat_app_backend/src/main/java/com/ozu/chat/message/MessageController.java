package com.ozu.chat.message;

import com.ozu.chat.common.ApiResponse;
import com.ozu.chat.common.CurrentUser;
import com.ozu.chat.common.PageResponse;
import com.ozu.chat.message.dto.EditMessageRequest;
import com.ozu.chat.message.dto.MessageDto;
import com.ozu.chat.message.dto.ReactMessageRequest;
import com.ozu.chat.message.dto.SendMessageRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MessageController {

	private final MessageService messageService;

	public MessageController(MessageService messageService) {
		this.messageService = messageService;
	}

	@GetMapping("/conversations/{conversationId}/messages")
	ApiResponse<PageResponse<MessageDto>> list(
			@PathVariable String conversationId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "40") int size) {
		return ApiResponse.ok("Messages", messageService.list(CurrentUser.get().id(), conversationId, page, size));
	}

	@PostMapping("/conversations/{conversationId}/messages")
	ApiResponse<MessageDto> send(@PathVariable String conversationId, @Valid @RequestBody SendMessageRequest request) {
		return ApiResponse.ok("Message sent", messageService.send(CurrentUser.get().id(), conversationId, request));
	}

	@PatchMapping("/messages/{messageId}")
	ApiResponse<MessageDto> edit(@PathVariable String messageId, @Valid @RequestBody EditMessageRequest request) {
		return ApiResponse.ok("Message edited", messageService.edit(CurrentUser.get().id(), messageId, request));
	}

	@PostMapping("/messages/{messageId}/recall")
	ApiResponse<MessageDto> recall(@PathVariable String messageId) {
		return ApiResponse.ok("Message recalled", messageService.recall(CurrentUser.get().id(), messageId));
	}

	@DeleteMapping("/messages/{messageId}")
	ApiResponse<MessageDto> deleteForEveryone(@PathVariable String messageId) {
		return ApiResponse.ok("Message deleted", messageService.deleteForEveryone(CurrentUser.get().id(), messageId));
	}

	@PostMapping("/messages/{messageId}/reactions")
	ApiResponse<MessageDto> react(@PathVariable String messageId, @Valid @RequestBody ReactMessageRequest request) {
		return ApiResponse.ok("Reaction updated", messageService.react(CurrentUser.get().id(), messageId, request));
	}

	@PostMapping("/conversations/{conversationId}/typing")
	ApiResponse<Void> typing(@PathVariable String conversationId) {
		messageService.typing(CurrentUser.get().id(), conversationId, true);
		return ApiResponse.ok("Typing");
	}

	@PostMapping("/conversations/{conversationId}/stop-typing")
	ApiResponse<Void> stopTyping(@PathVariable String conversationId) {
		messageService.typing(CurrentUser.get().id(), conversationId, false);
		return ApiResponse.ok("Stopped typing");
	}
}
