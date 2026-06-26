package com.ozu.chat.conversation.repository;

import java.util.List;
import java.util.Optional;

import com.ozu.chat.conversation.model.Conversation;
import com.ozu.chat.conversation.model.ConversationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ConversationRepository extends MongoRepository<Conversation, String> {

	List<Conversation> findByMemberIdsContainingOrderByLastMessageAtDesc(String userId, Pageable pageable);

	@Query("{ 'type': ?0, 'memberIds': { $all: ?1 } }")
	List<Conversation> findByTypeAndMemberIdsAll(ConversationType type, List<String> memberIds);

	Optional<Conversation> findByIdAndMemberIdsContaining(String id, String userId);
}
