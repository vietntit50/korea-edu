package com.koreaedu.chatbot.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {

    boolean existsByFacebookMid(String facebookMid);

    Optional<MessageEntity> findByFacebookMid(String facebookMid);
}
