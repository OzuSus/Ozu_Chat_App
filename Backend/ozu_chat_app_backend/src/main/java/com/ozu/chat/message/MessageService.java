package com.ozu.chat.message;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import com.ozu.chat.attachment.repository.AttachmentRepository;
import com.ozu.chat.common.PageResponse;
import com.ozu.chat.conversation.ConversationService;
import com.ozu.chat.conversation.model.Conversation;
import com.ozu.chat.conversation.repository.ConversationRepository;
import com.ozu.chat.exception.BadRequestException;
import com.ozu.chat.exception.ForbiddenException;
import com.ozu.chat.exception.NotFoundException;
import com.ozu.chat.message.dto.EditMessageRequest;
import com.ozu.chat.message.dto.MessageDto;
import com.ozu.chat.message.dto.ReactMessageRequest;
import com.ozu.chat.message.dto.SendMessageRequest;
import com.ozu.chat.message.model.Message;
import com.ozu.chat.message.model.MessageReaction;
import com.ozu.chat.message.model.MessageStatus;
import com.ozu.chat.message.model.MessageType;
import com.ozu.chat.message.repository.MessageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

	private final MessageRepository messageRepository;
	private final ConversationRepository conversationRepository;
	private final AttachmentRepository attachmentRepository;
	private final ConversationService conversationService;
	private final MessageMapper messageMapper;
	private final RealtimePublisher realtimePublisher;

	public MessageService(
			MessageRepository messageRepository,
			ConversationRepository conversationRepository,
			AttachmentRepository attachmentRepository,
			ConversationService conversationService,
			MessageMapper messageMapper,
			RealtimePublisher realtimePublisher) {
		this.messageRepository = messageRepository;
		this.conversationRepository = conversationRepository;
		this.attachmentRepository = attachmentRepository;
		this.conversationService = conversationService;
		this.messageMapper = messageMapper;
		this.realtimePublisher = realtimePublisher;
	}

	public PageResponse<MessageDto> list(String userId, String conversationId, int page, int size) {
		conversationService.ensureMember(conversationId, userId);
		int safePage = Math.max(page, 0);
		int safeSize = Math.min(Math.max(size, 1), 80);
		List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtDesc(
				conversationId,
				PageRequest.of(safePage, safeSize));
		List<MessageDto> items = messageMapper.toDtos(messages);
		return new PageResponse<>(items, safePage, safeSize, messages.size(), messages.size() == safeSize);
	}

	public MessageDto send(String userId, String conversationId, SendMessageRequest request) {
		Conversation conversation = conversationService.getMemberConversation(conversationId, userId);
		validateSendRequest(request);
		List<String> attachmentIds = request.attachmentIds() == null
				? List.of()
				: new ArrayList<>(new LinkedHashSet<>(request.attachmentIds()));
		if (!attachmentIds.isEmpty() && attachmentRepository.findByIdIn(attachmentIds).size() != attachmentIds.size()) {
			throw new BadRequestException("One or more attachments do not exist");
		}

		Message message = new Message();
		message.setConversationId(conversationId);
		message.setSenderId(userId);
		message.setType(request.type());
		message.setContent(request.content() == null ? "" : request.content().trim());
		message.setAttachmentIds(attachmentIds);
		message.setReplyToMessageId(request.replyToMessageId());
		message.setDeliveredTo(List.of(userId));
		message.setSeenBy(List.of(userId));
		Message saved = messageRepository.save(message);

		conversation.setLastMessageId(saved.getId());
		conversation.setLastMessageAt(saved.getCreatedAt() == null ? Instant.now() : saved.getCreatedAt());
		conversationRepository.save(conversation);

		MessageDto dto = messageMapper.toDto(saved);
		realtimePublisher.conversation(conversationId, "receive-message", dto);
		return dto;
	}

	public MessageDto edit(String userId, String messageId, EditMessageRequest request) {
		Message message = getOwnEditableMessage(userId, messageId);
		message.setContent(request.content().trim());
		message.setEditedAt(Instant.now());
		MessageDto dto = messageMapper.toDto(messageRepository.save(message));
		realtimePublisher.conversation(message.getConversationId(), "edit-message", dto);
		return dto;
	}

	public MessageDto recall(String userId, String messageId) {
		Message message = getOwnEditableMessage(userId, messageId);
		message.setStatus(MessageStatus.RECALLED);
		message.setContent("");
		message.setRecalledAt(Instant.now());
		MessageDto dto = messageMapper.toDto(messageRepository.save(message));
		realtimePublisher.conversation(message.getConversationId(), "recall-message", dto);
		return dto;
	}

	public MessageDto deleteForEveryone(String userId, String messageId) {
		Message message = getOwnEditableMessage(userId, messageId);
		message.setStatus(MessageStatus.DELETED);
		message.setContent("");
		message.setAttachmentIds(List.of());
		message.setDeletedAt(Instant.now());
		MessageDto dto = messageMapper.toDto(messageRepository.save(message));
		realtimePublisher.conversation(message.getConversationId(), "delete-message", dto);
		return dto;
	}

	public MessageDto react(String userId, String messageId, ReactMessageRequest request) {
		Message message = messageRepository.findById(messageId).orElseThrow(() -> new NotFoundException("Message not found"));
		conversationService.ensureMember(message.getConversationId(), userId);
		message.setReactions(message.getReactions()
				.stream()
				.filter(reaction -> !reaction.getUserId().equals(userId))
				.toList());
		MessageReaction reaction = new MessageReaction();
		reaction.setUserId(userId);
		reaction.setEmoji(request.emoji());
		List<MessageReaction> reactions = new ArrayList<>(message.getReactions());
		reactions.add(reaction);
		message.setReactions(reactions);
		MessageDto dto = messageMapper.toDto(messageRepository.save(message));
		realtimePublisher.conversation(message.getConversationId(), "reaction", dto);
		return dto;
	}

	public void typing(String userId, String conversationId, boolean typing) {
		conversationService.ensureMember(conversationId, userId);
		realtimePublisher.conversation(conversationId, typing ? "typing" : "stop-typing", userId);
	}

	private Message getOwnEditableMessage(String userId, String messageId) {
		Message message = messageRepository.findById(messageId).orElseThrow(() -> new NotFoundException("Message not found"));
		conversationService.ensureMember(message.getConversationId(), userId);
		if (!message.getSenderId().equals(userId)) {
			throw new ForbiddenException("Only sender can change this message");
		}
		return message;
	}

	private void validateSendRequest(SendMessageRequest request) {
		boolean hasContent = request.content() != null && !request.content().trim().isBlank();
		boolean hasAttachments = request.attachmentIds() != null && !request.attachmentIds().isEmpty();
		if (!hasContent && !hasAttachments && request.type() != MessageType.EMOJI) {
			throw new BadRequestException("Message content or attachment is required");
		}
		if (request.type() == MessageType.TEXT && !hasContent) {
			throw new BadRequestException("Text message requires content");
		}
		if (List.of(MessageType.IMAGE, MessageType.VIDEO, MessageType.GIF, MessageType.FILE).contains(request.type()) && !hasAttachments) {
			throw new BadRequestException("Attachment message requires at least one file");
		}
	}
}
