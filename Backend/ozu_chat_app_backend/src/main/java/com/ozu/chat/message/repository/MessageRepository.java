package com.ozu.chat.message.repository;

import java.util.List;

import com.ozu.chat.message.model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, String> {

	List<Message> findByConversationIdOrderByCreatedAtDesc(String conversationId, Pageable pageable);

	List<Message> findByConversationIdAndContentContainingIgnoreCaseOrderByCreatedAtDesc(
			String conversationId,
			String content,
			Pageable pageable);
}
