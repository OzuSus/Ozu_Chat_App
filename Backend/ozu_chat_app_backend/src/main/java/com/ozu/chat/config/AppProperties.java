package com.ozu.chat.config;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ozu")
public record AppProperties(
		App app,
		Cors cors,
		Jwt jwt,
		Email email,
		Upload upload,
		RateLimit rateLimit) {

	public record App(String frontendUrl, String backendUrl) {
	}

	public record Cors(List<String> allowedOrigins) {
	}

	public record Jwt(
			String accessSecret,
			String refreshSecret,
			long accessTokenMinutes,
			long refreshTokenDays) {

		public Duration accessTtl() {
			return Duration.ofMinutes(accessTokenMinutes);
		}

		public Duration refreshTtl() {
			return Duration.ofDays(refreshTokenDays);
		}
	}

	public record Email(long verificationTokenMinutes) {

		public Duration verificationTtl() {
			return Duration.ofMinutes(verificationTokenMinutes);
		}
	}

	public record Upload(String directory) {
	}

	public record RateLimit(long requestsPerMinute) {
	}
}
