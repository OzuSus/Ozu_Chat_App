package com.ozu.chat.message.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "messages")
@CompoundIndex(name = "conversation_created_idx", def = "{'conversationId': 1, 'createdAt': -1}")
public class Message {

	@Id
	private String id;

	@Indexed
	private String conversationId;

	@Indexed
	private String senderId;

	private MessageType type = MessageType.TEXT;
	private MessageStatus status = MessageStatus.SENT;
	private String content;
	private List<String> attachmentIds = new ArrayList<>();
	private String replyToMessageId;
	private List<MessageReaction> reactions = new ArrayList<>();
	private Instant editedAt;
	private Instant deletedAt;
	private Instant recalledAt;
	private List<String> deliveredTo = new ArrayList<>();
	private List<String> seenBy = new ArrayList<>();

	@CreatedDate
	private Instant createdAt;

	@LastModifiedDate
	private Instant updatedAt;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public MessageStatus getStatus() {
		return status;
	}

	public void setStatus(MessageStatus status) {
		this.status = status;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<String> getAttachmentIds() {
		return attachmentIds;
	}

	public void setAttachmentIds(List<String> attachmentIds) {
		this.attachmentIds = attachmentIds;
	}

	public String getReplyToMessageId() {
		return replyToMessageId;
	}

	public void setReplyToMessageId(String replyToMessageId) {
		this.replyToMessageId = replyToMessageId;
	}

	public List<MessageReaction> getReactions() {
		return reactions;
	}

	public void setReactions(List<MessageReaction> reactions) {
		this.reactions = reactions;
	}

	public Instant getEditedAt() {
		return editedAt;
	}

	public void setEditedAt(Instant editedAt) {
		this.editedAt = editedAt;
	}

	public Instant getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(Instant deletedAt) {
		this.deletedAt = deletedAt;
	}

	public Instant getRecalledAt() {
		return recalledAt;
	}

	public void setRecalledAt(Instant recalledAt) {
		this.recalledAt = recalledAt;
	}

	public List<String> getDeliveredTo() {
		return deliveredTo;
	}

	public void setDeliveredTo(List<String> deliveredTo) {
		this.deliveredTo = deliveredTo;
	}

	public List<String> getSeenBy() {
		return seenBy;
	}

	public void setSeenBy(List<String> seenBy) {
		this.seenBy = seenBy;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}
}
