package com.koreaedu.chatbot.orchestrator;

import com.koreaedu.chatbot.persistence.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageDedupService {

    private final MessageRepository messageRepository;

    public MessageDedupService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Transactional(readOnly = true)
    public boolean isDuplicate(String facebookMid) {
        if (facebookMid == null || facebookMid.isBlank()) {
            return false;
        }
        return messageRepository.existsByFacebookMid(facebookMid);
    }
}
