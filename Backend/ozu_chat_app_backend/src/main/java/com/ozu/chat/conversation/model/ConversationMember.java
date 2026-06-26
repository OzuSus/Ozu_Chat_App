package com.ozu.chat.conversation.model;

import java.time.Instant;

public class ConversationMember {

	private String userId;
	private ConversationRole role = ConversationRole.MEMBER;
	private Instant joinedAt = Instant.now();
	private Instant lastReadAt;
	private boolean muted;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public ConversationRole getRole() {
		return role;
	}

	public void setRole(ConversationRole role) {
		this.role = role;
	}

	public Instant getJoinedAt() {
		return joinedAt;
	}

	public void setJoinedAt(Instant joinedAt) {
		this.joinedAt = joinedAt;
	}

	public Instant getLastReadAt() {
		return lastReadAt;
	}

	public void setLastReadAt(Instant lastReadAt) {
		this.lastReadAt = lastReadAt;
	}

	public boolean isMuted() {
		return muted;
	}

	public void setMuted(boolean muted) {
		this.muted = muted;
	}
}
