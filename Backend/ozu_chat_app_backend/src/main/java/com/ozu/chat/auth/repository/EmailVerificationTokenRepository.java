package com.ozu.chat.auth.repository;

import java.util.Optional;

import com.ozu.chat.auth.model.EmailVerificationToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmailVerificationTokenRepository extends MongoRepository<EmailVerificationToken, String> {

	Optional<EmailVerificationToken> findByTokenHash(String tokenHash);
}
