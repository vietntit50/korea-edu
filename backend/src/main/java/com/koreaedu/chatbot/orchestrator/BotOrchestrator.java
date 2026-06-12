package com.koreaedu.chatbot.orchestrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.koreaedu.chatbot.domain.ConversationState;
import com.koreaedu.chatbot.domain.MessageType;
import com.koreaedu.chatbot.messenger.GreetingMenuBuilder;
import com.koreaedu.chatbot.messenger.InboundMessage;
import com.koreaedu.chatbot.messenger.MenuOption;
import com.koreaedu.chatbot.messenger.MessengerClient;
import com.koreaedu.chatbot.messenger.OutboundMessage;
import com.koreaedu.chatbot.persistence.ConversationSessionEntity;
import com.koreaedu.chatbot.persistence.ConversationSessionRepository;
import com.koreaedu.chatbot.persistence.MessageEntity;
import com.koreaedu.chatbot.persistence.MessageRepository;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BotOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(BotOrchestrator.class);

    private final ConversationSessionRepository sessionRepository;
    private final MessageRepository messageRepository;
    private final GreetingMenuBuilder greetingMenuBuilder;
    private final MessengerClient messengerClient;
    private final ObjectMapper objectMapper;

    public BotOrchestrator(
            ConversationSessionRepository sessionRepository,
            MessageRepository messageRepository,
            GreetingMenuBuilder greetingMenuBuilder,
            MessengerClient messengerClient,
            ObjectMapper objectMapper) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.greetingMenuBuilder = greetingMenuBuilder;
        this.messengerClient = messengerClient;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ProcessResult processInbound(InboundMessage inbound, String traceId) {
        Instant now = Instant.now();
        ConversationSessionEntity session = sessionRepository
                .findFirstByFacebookPsidOrderByLastMessageAtDesc(inbound.psid())
                .orElseGet(() -> ConversationSessionEntity.create(inbound.psid(), now));

        String payloadJson = toPayloadJson(inbound);
        MessageEntity inboundEntity = MessageEntity.inbound(
                session.getId(),
                inbound.mid(),
                inbound.messageType(),
                inbound.text(),
                payloadJson,
                traceId,
                now);
        messageRepository.save(inboundEntity);
        session.touch(now);

        OutboundMessage outbound;
        String nextIntent = null;
        ConversationState nextState;

        if (inbound.isQuickReplyOrPostback() && inbound.payload() != null) {
            MenuOption selected = greetingMenuBuilder.menuOptions().stream()
                    .filter(option -> option.payload().equals(inbound.payload()))
                    .findFirst()
                    .orElse(null);
            if (selected != null) {
                nextIntent = selected.intent();
                session.setCurrentIntent(nextIntent);
                outbound = OutboundMessage.textOnly(greetingMenuBuilder.placeholderForPayload(selected.payload()));
                nextState = ConversationState.MENU_SHOWN;
                log.info(
                        "Menu selection psid={} payload={} intent={} sessionId={}",
                        inbound.psid(),
                        selected.payload(),
                        selected.intent(),
                        session.getId());
            } else {
                outbound = buildGreetingMenu();
                nextState = ConversationState.MENU_SHOWN;
            }
        } else {
            outbound = buildGreetingMenu();
            nextState = ConversationState.MENU_SHOWN;
        }

        session.showMenu(now);
        sessionRepository.save(session);

        MessageEntity outboundEntity = MessageEntity.outbound(
                session.getId(),
                outbound.quickReplies().isEmpty() ? MessageType.TEXT : MessageType.QUICK_REPLY,
                outbound.text(),
                null,
                traceId,
                now);
        messageRepository.save(outboundEntity);

        messengerClient.send(inbound.psid(), outbound);

        return new ProcessResult(
                session.getId(),
                nextIntent,
                nextState.name(),
                List.of(outbound));
    }

    private OutboundMessage buildGreetingMenu() {
        return messengerClient.toOutboundWithMenu(
                greetingMenuBuilder.greetingText(), greetingMenuBuilder.menuOptions());
    }

    private String toPayloadJson(InboundMessage inbound) {
        try {
            if (inbound.payload() == null) {
                return null;
            }
            return objectMapper.writeValueAsString(
                    java.util.Map.of("payload", inbound.payload(), "text", inbound.text()));
        } catch (Exception ex) {
            return null;
        }
    }

    public record ProcessResult(
            java.util.UUID sessionId, String intent, String state, List<OutboundMessage> messages) {
    }
}
