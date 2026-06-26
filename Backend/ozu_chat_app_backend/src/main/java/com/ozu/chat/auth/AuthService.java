package com.ozu.chat.auth;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Locale;

import com.ozu.chat.auth.dto.AuthResponse;
import com.ozu.chat.auth.dto.LoginRequest;
import com.ozu.chat.auth.dto.RegisterRequest;
import com.ozu.chat.auth.dto.RegisterResponse;
import com.ozu.chat.auth.model.EmailVerificationToken;
import com.ozu.chat.auth.model.RefreshToken;
import com.ozu.chat.auth.repository.EmailVerificationTokenRepository;
import com.ozu.chat.auth.repository.RefreshTokenRepository;
import com.ozu.chat.config.AppProperties;
import com.ozu.chat.exception.BadRequestException;
import com.ozu.chat.exception.NotFoundException;
import com.ozu.chat.security.JwtClaims;
import com.ozu.chat.security.JwtService;
import com.ozu.chat.security.JwtService.TokenPair;
import com.ozu.chat.security.TokenHashService;
import com.ozu.chat.user.UserMapper;
import com.ozu.chat.user.model.User;
import com.ozu.chat.user.model.UserStatus;
import com.ozu.chat.user.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final EmailVerificationTokenRepository verificationTokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final TokenHashService tokenHashService;
	private final EmailService emailService;
	private final UserMapper userMapper;
	private final AppProperties appProperties;
	private final SecureRandom secureRandom = new SecureRandom();

	public AuthService(
			UserRepository userRepository,
			RefreshTokenRepository refreshTokenRepository,
			EmailVerificationTokenRepository verificationTokenRepository,
			PasswordEncoder passwordEncoder,
			JwtService jwtService,
			TokenHashService tokenHashService,
			EmailService emailService,
			UserMapper userMapper,
			AppProperties appProperties) {
		this.userRepository = userRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.verificationTokenRepository = verificationTokenRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.tokenHashService = tokenHashService;
		this.emailService = emailService;
		this.userMapper = userMapper;
		this.appProperties = appProperties;
	}

	public RegisterResponse register(RegisterRequest request) {
		String email = request.email().trim().toLowerCase(Locale.ROOT);
		String username = request.username().trim();
		if (userRepository.existsByEmailIgnoreCase(email)) {
			throw new BadRequestException("Email is already registered");
		}
		if (userRepository.existsByUsernameIgnoreCase(username)) {
			throw new BadRequestException("Username is already taken");
		}

		User user = new User();
		user.setEmail(email);
		user.setUsername(username);
		user.setPasswordHash(passwordEncoder.encode(request.password()));
		user.setDisplayName(request.displayName().trim());
		user.setLanguage(request.language() == null ? "vi" : request.language());
		user.setEmailVerified(false);
		user.setStatus(UserStatus.OFFLINE);
		User saved = userRepository.save(user);

		String rawToken = createVerificationToken(saved);
		emailService.sendVerificationEmail(saved, rawToken);
		return new RegisterResponse(userMapper.toProfile(saved), rawToken);
	}

	public AuthResponse login(LoginRequest request) {
		User user = findByEmailOrUsername(request.emailOrUsername());
		if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
			throw new BadCredentialsException("Invalid email/username or password");
		}
		if (!user.isEmailVerified()) {
			throw new BadCredentialsException("Please verify your email before login");
		}
		user.setStatus(UserStatus.ONLINE);
		userRepository.save(user);
		return issueAuthResponse(user);
	}

	public AuthResponse refresh(String refreshToken) {
		JwtClaims claims = jwtService.verifyRefreshToken(refreshToken);
		String tokenHash = tokenHashService.sha256(refreshToken);
		RefreshToken stored = refreshTokenRepository.findByTokenHash(tokenHash)
				.orElseThrow(() -> new BadRequestException("Refresh token is not recognized"));
		if (stored.getRevokedAt() != null || stored.getExpiresAt().isBefore(Instant.now())) {
			throw new BadRequestException("Refresh token is expired or revoked");
		}

		User user = userRepository.findById(claims.subject())
				.orElseThrow(() -> new NotFoundException("User not found"));
		stored.setRevokedAt(Instant.now());
		refreshTokenRepository.save(stored);
		return issueAuthResponse(user);
	}

	public void logout(String userId, String refreshToken) {
		String tokenHash = tokenHashService.sha256(refreshToken);
		refreshTokenRepository.findByTokenHash(tokenHash)
				.filter(token -> token.getUserId().equals(userId))
				.ifPresent(token -> {
					token.setRevokedAt(Instant.now());
					refreshTokenRepository.save(token);
				});
		userRepository.findById(userId).ifPresent(user -> {
			user.setStatus(UserStatus.OFFLINE);
			user.setLastSeenAt(Instant.now());
			userRepository.save(user);
		});
	}

	public void verifyEmail(String rawToken) {
		String tokenHash = tokenHashService.sha256(rawToken);
		EmailVerificationToken token = verificationTokenRepository.findByTokenHash(tokenHash)
				.orElseThrow(() -> new BadRequestException("Verification token is invalid"));
		if (token.getUsedAt() != null || token.getExpiresAt().isBefore(Instant.now())) {
			throw new BadRequestException("Verification token is expired or already used");
		}

		User user = userRepository.findById(token.getUserId())
				.orElseThrow(() -> new NotFoundException("User not found"));
		user.setEmailVerified(true);
		token.setUsedAt(Instant.now());
		userRepository.save(user);
		verificationTokenRepository.save(token);
	}

	private AuthResponse issueAuthResponse(User user) {
		TokenPair tokenPair = jwtService.createTokenPair(user);
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setUserId(user.getId());
		refreshToken.setTokenHash(tokenHashService.sha256(tokenPair.refreshToken()));
		refreshToken.setExpiresAt(tokenPair.refreshTokenExpiresAt());
		refreshTokenRepository.save(refreshToken);
		return new AuthResponse(
				tokenPair.accessToken(),
				tokenPair.refreshToken(),
				tokenPair.accessTokenExpiresAt(),
				tokenPair.refreshTokenExpiresAt(),
				userMapper.toProfile(user));
	}

	private User findByEmailOrUsername(String emailOrUsername) {
		String input = emailOrUsername.trim();
		return (input.contains("@")
				? userRepository.findByEmailIgnoreCase(input)
				: userRepository.findByUsernameIgnoreCase(input))
				.orElseThrow(() -> new BadCredentialsException("Invalid email/username or password"));
	}

	private String createVerificationToken(User user) {
		String rawToken = randomToken();
		EmailVerificationToken token = new EmailVerificationToken();
		token.setUserId(user.getId());
		token.setTokenHash(tokenHashService.sha256(rawToken));
		token.setExpiresAt(Instant.now().plus(appProperties.email().verificationTtl()));
		verificationTokenRepository.save(token);
		return rawToken;
	}

	private String randomToken() {
		byte[] bytes = new byte[48];
		secureRandom.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}
}
