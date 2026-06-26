package com.ozu.chat.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ozu.chat.config.AppProperties;
import com.ozu.chat.exception.BadRequestException;
import com.ozu.chat.user.model.User;
import com.ozu.chat.user.model.UserRole;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

	private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
	private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();
	private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
	};

	private final ObjectMapper objectMapper;
	private final AppProperties appProperties;

	public JwtService(ObjectMapper objectMapper, AppProperties appProperties) {
		this.objectMapper = objectMapper;
		this.appProperties = appProperties;
	}

	public TokenPair createTokenPair(User user) {
		Instant now = Instant.now();
		Instant accessExpiresAt = now.plus(appProperties.jwt().accessTtl());
		Instant refreshExpiresAt = now.plus(appProperties.jwt().refreshTtl());
		String accessToken = createToken(user, accessExpiresAt, appProperties.jwt().accessSecret(), "access");
		String refreshToken = createToken(user, refreshExpiresAt, appProperties.jwt().refreshSecret(), "refresh");
		return new TokenPair(accessToken, refreshToken, accessExpiresAt, refreshExpiresAt);
	}

	public JwtClaims verifyAccessToken(String token) {
		return verify(token, appProperties.jwt().accessSecret(), "access");
	}

	public JwtClaims verifyRefreshToken(String token) {
		return verify(token, appProperties.jwt().refreshSecret(), "refresh");
	}

	private String createToken(User user, Instant expiresAt, String secret, String tokenUse) {
		try {
			Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
			Map<String, Object> payload = new LinkedHashMap<>();
			payload.put("sub", user.getId());
			payload.put("email", user.getEmail());
			payload.put("username", user.getUsername());
			payload.put("roles", user.getRoles().stream().map(Enum::name).toList());
			payload.put("token_use", tokenUse);
			payload.put("iat", Instant.now().getEpochSecond());
			payload.put("exp", expiresAt.getEpochSecond());
			payload.put("jti", UUID.randomUUID().toString());

			String encodedHeader = encodeJson(header);
			String encodedPayload = encodeJson(payload);
			String unsignedToken = encodedHeader + "." + encodedPayload;
			return unsignedToken + "." + sign(unsignedToken, secret);
		} catch (Exception exception) {
			throw new IllegalStateException("Unable to create JWT", exception);
		}
	}

	private JwtClaims verify(String token, String secret, String expectedUse) {
		try {
			String[] parts = token.split("\\.");
			if (parts.length != 3) {
				throw new BadRequestException("Invalid token");
			}
			String unsignedToken = parts[0] + "." + parts[1];
			String expectedSignature = sign(unsignedToken, secret);
			if (!MessageDigest.isEqual(parts[2].getBytes(StandardCharsets.UTF_8), expectedSignature.getBytes(StandardCharsets.UTF_8))) {
				throw new BadRequestException("Invalid token signature");
			}

			Map<String, Object> payload = objectMapper.readValue(BASE64_URL_DECODER.decode(parts[1]), MAP_TYPE);
			String tokenUse = readString(payload, "token_use");
			if (!expectedUse.equals(tokenUse)) {
				throw new BadRequestException("Invalid token type");
			}

			Instant expiresAt = Instant.ofEpochSecond(readLong(payload, "exp"));
			if (expiresAt.isBefore(Instant.now())) {
				throw new BadRequestException("Token expired");
			}

			List<?> rawRoles = (List<?>) payload.getOrDefault("roles", List.of());
			Set<UserRole> roles = rawRoles.stream()
					.map(Object::toString)
					.map(UserRole::valueOf)
					.collect(Collectors.toUnmodifiableSet());

			return new JwtClaims(
					readString(payload, "sub"),
					readString(payload, "email"),
					readString(payload, "username"),
					roles,
					expiresAt);
		} catch (BadRequestException exception) {
			throw exception;
		} catch (Exception exception) {
			throw new BadRequestException("Invalid token");
		}
	}

	private String encodeJson(Map<String, Object> value) throws Exception {
		return BASE64_URL_ENCODER.encodeToString(objectMapper.writeValueAsBytes(value));
	}

	private String sign(String value, String secret) throws Exception {
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
		return BASE64_URL_ENCODER.encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
	}

	private String readString(Map<String, Object> payload, String key) {
		Object value = payload.get(key);
		if (value == null) {
			throw new BadRequestException("Invalid token payload");
		}
		return value.toString();
	}

	private long readLong(Map<String, Object> payload, String key) {
		Object value = payload.get(key);
		if (value instanceof Number number) {
			return number.longValue();
		}
		throw new BadRequestException("Invalid token payload");
	}

	public record TokenPair(
			String accessToken,
			String refreshToken,
			Instant accessTokenExpiresAt,
			Instant refreshTokenExpiresAt) {
	}
}
