package com.koreaedu.chatbot.messenger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.koreaedu.chatbot.domain.MessageType;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MessengerWebhookParser {

    private final ObjectMapper objectMapper;

    public MessengerWebhookParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<InboundMessage> parse(String rawBody) {
        List<InboundMessage> messages = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(rawBody);
            if (!"page".equals(root.path("object").asText())) {
                return messages;
            }
            for (JsonNode entry : root.path("entry")) {
                for (JsonNode messaging : entry.path("messaging")) {
                    parseMessagingEvent(messaging).ifPresent(messages::add);
                }
            }
        } catch (Exception ignored) {
            return List.of();
        }
        return messages;
    }

    private java.util.Optional<InboundMessage> parseMessagingEvent(JsonNode messaging) {
        String psid = messaging.path("sender").path("id").asText(null);
        if (psid == null || psid.isBlank()) {
            return java.util.Optional.empty();
        }

        JsonNode messageNode = messaging.path("message");
        if (messageNode.isMissingNode() || messageNode.isNull()) {
            JsonNode postback = messaging.path("postback");
            if (!postback.isMissingNode()) {
                return java.util.Optional.of(new InboundMessage(
                        psid,
                        messaging.path("timestamp").asText("postback-" + System.nanoTime()),
                        MessageType.BUTTON,
                        postback.path("title").asText(null),
                        postback.path("payload").asText(null)));
            }
            return java.util.Optional.empty();
        }

        String mid = messageNode.path("mid").asText(null);
        String text = messageNode.has("text") ? messageNode.path("text").asText(null) : null;

        JsonNode quickReply = messageNode.path("quick_reply");
        if (!quickReply.isMissingNode()) {
            return java.util.Optional.of(new InboundMessage(
                    psid,
                    mid,
                    MessageType.QUICK_REPLY,
                    text,
                    quickReply.path("payload").asText(null)));
        }

        if (text != null && !text.isBlank()) {
            return java.util.Optional.of(new InboundMessage(psid, mid, MessageType.TEXT, text, null));
        }

        return java.util.Optional.empty();
    }
}
