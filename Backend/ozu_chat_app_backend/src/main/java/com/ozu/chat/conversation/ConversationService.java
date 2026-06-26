package com.ozu.chat.conversation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.ozu.chat.conversation.dto.ConversationDto;
import com.ozu.chat.conversation.dto.CreateConversationRequest;
import com.ozu.chat.conversation.model.Conversation;
import com.ozu.chat.conversation.model.ConversationMember;
import com.ozu.chat.conversation.model.ConversationRole;
import com.ozu.chat.conversation.model.ConversationType;
import com.ozu.chat.conversation.repository.ConversationRepository;
import com.ozu.chat.exception.BadRequestException;
import com.ozu.chat.exception.ForbiddenException;
import com.ozu.chat.exception.NotFoundException;
import com.ozu.chat.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ConversationService {

	private final ConversationRepository conversationRepository;
	private final UserRepository userRepository;
	private final ConversationMapper conversationMapper;

	public ConversationService(
			ConversationRepository conversationRepository,
			UserRepository userRepository,
			ConversationMapper conversationMapper) {
		this.conversationRepository = conversationRepository;
		this.userRepository = userRepository;
		this.conversationMapper = conversationMapper;
	}

	public List<ConversationDto> mine(String userId, int page, int size) {
		return conversationMapper.toDtos(conversationRepository.findByMemberIdsContainingOrderByLastMessageAtDesc(
				userId,
				PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50))));
	}

	public ConversationDto create(String creatorId, CreateConversationRequest request) {
		Set<String> memberIds = new LinkedHashSet<>(request.memberIds());
		memberIds.add(creatorId);
		if (request.type() == ConversationType.PRIVATE && memberIds.size() != 2) {
			throw new BadRequestException("Private conversation must have exactly two members");
		}
		if (request.type() == ConversationType.GROUP && memberIds.size() < 3) {
			throw new BadRequestException("Group conversation must have at least three members");
		}
		if (userRepository.findAllById(memberIds).size() != memberIds.size()) {
			throw new BadRequestException("One or more members do not exist");
		}
		if (request.type() == ConversationType.PRIVATE) {
			List<Conversation> existing = conversationRepository.findByTypeAndMemberIdsAll(
					ConversationType.PRIVATE,
					new ArrayList<>(memberIds));
			if (!existing.isEmpty()) {
				return conversationMapper.toDto(existing.get(0));
			}
		}

		Conversation conversation = new Conversation();
		conversation.setType(request.type());
		conversation.setName(request.type() == ConversationType.GROUP ? request.name() : null);
		conversation.setCreatedBy(creatorId);
		conversation.setMemberIds(new ArrayList<>(memberIds));
		conversation.setMembers(memberIds.stream()
				.map(memberId -> member(memberId, memberId.equals(creatorId) ? ConversationRole.OWNER : ConversationRole.MEMBER))
				.toList());
		conversation.setLastMessageAt(Instant.now());
		return conversationMapper.toDto(conversationRepository.save(conversation));
	}

	public Conversation getMemberConversation(String conversationId, String userId) {
		return conversationRepository.findByIdAndMemberIdsContaining(conversationId, userId)
				.orElseThrow(() -> new NotFoundException("Conversation not found"));
	}

	public void ensureMember(String conversationId, String userId) {
		if (conversationRepository.findByIdAndMemberIdsContaining(conversationId, userId).isEmpty()) {
			throw new ForbiddenException("You are not a member of this conversation");
		}
	}

	private ConversationMember member(String userId, ConversationRole role) {
		ConversationMember member = new ConversationMember();
		member.setUserId(userId);
		member.setRole(role);
		member.setJoinedAt(Instant.now());
		return member;
	}
}
