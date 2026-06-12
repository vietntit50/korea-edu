package com.koreaedu.chatbot.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationSessionRepository extends JpaRepository<ConversationSessionEntity, UUID> {

    Optional<ConversationSessionEntity> findFirstByFacebookPsidOrderByLastMessageAtDesc(String facebookPsid);
}
