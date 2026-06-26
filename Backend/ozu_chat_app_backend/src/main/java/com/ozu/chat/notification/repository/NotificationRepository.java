package com.ozu.chat.notification.repository;

import java.util.List;

import com.ozu.chat.notification.model.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<Notification, String> {

	long countByUserIdAndReadAtIsNull(String userId);

	List<Notification> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
}
