package com.ozu.chat.auth.repository;

import java.util.List;
import java.util.Optional;

import com.ozu.chat.auth.model.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

	Optional<RefreshToken> findByTokenHash(String tokenHash);

	List<RefreshToken> findByUserIdAndRevokedAtIsNull(String userId);
}
