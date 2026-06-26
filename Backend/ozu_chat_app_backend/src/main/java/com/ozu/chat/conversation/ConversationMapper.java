package com.ozu.chat.conversation;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ozu.chat.conversation.dto.ConversationDto;
import com.ozu.chat.conversation.model.Conversation;
import com.ozu.chat.message.MessageMapper;
import com.ozu.chat.message.dto.MessageDto;
import com.ozu.chat.message.repository.MessageRepository;
import com.ozu.chat.user.UserMapper;
import com.ozu.chat.user.dto.UserSummaryDto;
import com.ozu.chat.user.model.User;
import com.ozu.chat.user.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class ConversationMapper {

	private final UserRepository userRepository;
	private final MessageRepository messageRepository;
	private final UserMapper userMapper;
	private final MessageMapper messageMapper;

	public ConversationMapper(
			UserRepository userRepository,
			MessageRepository messageRepository,
			UserMapper userMapper,
			MessageMapper messageMapper) {
		this.userRepository = userRepository;
		this.messageRepository = messageRepository;
		this.userMapper = userMapper;
		this.messageMapper = messageMapper;
	}

	public ConversationDto toDto(Conversation conversation) {
		Map<String, UserSummaryDto> users = userRepository.findAllById(conversation.getMemberIds())
				.stream()
				.collect(Collectors.toMap(User::getId, userMapper::toSummary));
		List<UserSummaryDto> members = conversation.getMemberIds()
				.stream()
				.map(memberId -> users.getOrDefault(memberId, deletedUser(memberId)))
				.toList();
		MessageDto lastMessage = conversation.getLastMessageId() == null
				? null
				: messageRepository.findById(conversation.getLastMessageId()).map(messageMapper::toDto).orElse(null);
		return new ConversationDto(
				conversation.getId(),
				conversation.getType(),
				conversation.getName(),
				conversation.getAvatarUrl(),
				members,
				conversation.getCreatedBy(),
				lastMessage,
				conversation.getLastMessageAt(),
				conversation.getCreatedAt(),
				conversation.getUpdatedAt());
	}

	public List<ConversationDto> toDtos(List<Conversation> conversations) {
		List<String> allMemberIds = conversations.stream()
				.flatMap(conversation -> conversation.getMemberIds().stream())
				.distinct()
				.toList();
		Map<String, UserSummaryDto> users = userRepository.findAllById(allMemberIds)
				.stream()
				.collect(Collectors.toMap(User::getId, userMapper::toSummary));
		Map<String, MessageDto> lastMessages = messageRepository.findAllById(conversations.stream()
						.map(Conversation::getLastMessageId)
						.filter(id -> id != null && !id.isBlank())
						.toList())
				.stream()
				.map(messageMapper::toDto)
				.collect(Collectors.toMap(MessageDto::id, Function.identity()));
		return conversations.stream()
				.map(conversation -> new ConversationDto(
						conversation.getId(),
						conversation.getType(),
						conversation.getName(),
						conversation.getAvatarUrl(),
						conversation.getMemberIds()
								.stream()
								.map(memberId -> users.getOrDefault(memberId, deletedUser(memberId)))
								.toList(),
						conversation.getCreatedBy(),
						lastMessages.get(conversation.getLastMessageId()),
						conversation.getLastMessageAt(),
						conversation.getCreatedAt(),
						conversation.getUpdatedAt()))
				.toList();
	}

	private UserSummaryDto deletedUser(String userId) {
		return new UserSummaryDto(userId, "deleted-user", "Deleted user", null, null, null);
	}
}
