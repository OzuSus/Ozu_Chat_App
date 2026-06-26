package com.ozu.chat.message.dto;

import java.time.Instant;

public record RealtimeEventDto<T>(
		String event,
		T payload,
		Instant timestamp) {

	public static <T> RealtimeEventDto<T> of(String event, T payload) {
		return new RealtimeEventDto<>(event, payload, Instant.now());
	}
}
