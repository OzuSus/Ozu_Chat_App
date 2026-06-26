package com.ozu.chat.message;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ozu.chat.attachment.AttachmentMapper;
import com.ozu.chat.attachment.dto.AttachmentDto;
import com.ozu.chat.attachment.repository.AttachmentRepository;
import com.ozu.chat.message.dto.MessageDto;
import com.ozu.chat.message.dto.MessageReactionDto;
import com.ozu.chat.message.model.Message;
import com.ozu.chat.user.UserMapper;
import com.ozu.chat.user.dto.UserSummaryDto;
import com.ozu.chat.user.model.User;
import com.ozu.chat.user.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

	private final UserRepository userRepository;
	private final AttachmentRepository attachmentRepository;
	private final UserMapper userMapper;
	private final AttachmentMapper attachmentMapper;

	public MessageMapper(
			UserRepository userRepository,
			AttachmentRepository attachmentRepository,
			UserMapper userMapper,
			AttachmentMapper attachmentMapper) {
		this.userRepository = userRepository;
		this.attachmentRepository = attachmentRepository;
		this.userMapper = userMapper;
		this.attachmentMapper = attachmentMapper;
	}

	public MessageDto toDto(Message message) {
		UserSummaryDto sender = userRepository.findById(message.getSenderId())
				.map(userMapper::toSummary)
				.orElseGet(() -> deletedUser(message.getSenderId()));
		List<AttachmentDto> attachments = message.getAttachmentIds().isEmpty()
				? List.of()
				: attachmentRepository.findByIdIn(message.getAttachmentIds())
						.stream()
						.map(attachmentMapper::toDto)
						.toList();
		return toDto(message, sender, attachments);
	}

	public List<MessageDto> toDtos(List<Message> messages) {
		Map<String, UserSummaryDto> users = userRepository.findAllById(
						messages.stream().map(Message::getSenderId).collect(Collectors.toSet()))
				.stream()
				.collect(Collectors.toMap(User::getId, userMapper::toSummary));
		return messages.stream()
				.map(message -> toDto(
						message,
						users.getOrDefault(message.getSenderId(), deletedUser(message.getSenderId())),
						message.getAttachmentIds().isEmpty()
								? List.of()
								: attachmentRepository.findByIdIn(message.getAttachmentIds())
										.stream()
										.map(attachmentMapper::toDto)
										.toList()))
				.toList();
	}

	private MessageDto toDto(Message message, UserSummaryDto sender, List<AttachmentDto> attachments) {
		return new MessageDto(
				message.getId(),
				message.getConversationId(),
				sender,
				message.getType(),
				message.getStatus(),
				message.getContent(),
				attachments,
				message.getReplyToMessageId(),
				message.getReactions()
						.stream()
						.map(reaction -> new MessageReactionDto(reaction.getUserId(), reaction.getEmoji(), reaction.getCreatedAt()))
						.toList(),
				message.getEditedAt(),
				message.getDeletedAt(),
				message.getRecalledAt(),
				message.getCreatedAt(),
				message.getUpdatedAt());
	}

	private UserSummaryDto deletedUser(String userId) {
		return new UserSummaryDto(userId, "deleted-user", "Deleted user", null, null, null);
	}
}
