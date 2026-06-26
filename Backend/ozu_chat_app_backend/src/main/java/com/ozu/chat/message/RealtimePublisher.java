package com.ozu.chat.message;

import com.ozu.chat.message.dto.RealtimeEventDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class RealtimePublisher {

	private final SimpMessagingTemplate messagingTemplate;

	public RealtimePublisher(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	public <T> void conversation(String conversationId, String event, T payload) {
		messagingTemplate.convertAndSend(
				"/topic/conversations/" + conversationId,
				RealtimeEventDto.of(event, payload));
	}
}
