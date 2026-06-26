package com.ozu.chat.attachment.repository;

import java.util.List;

import com.ozu.chat.attachment.model.Attachment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AttachmentRepository extends MongoRepository<Attachment, String> {

	List<Attachment> findByIdIn(List<String> ids);
}
