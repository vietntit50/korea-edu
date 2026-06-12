package com.koreaedu.chatbot.messenger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.koreaedu.chatbot.config.FacebookProperties;
import com.koreaedu.chatbot.config.MessengerProperties;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class MessengerClient {

    private static final Logger log = LoggerFactory.getLogger(MessengerClient.class);

    private final RestClient restClient;
    private final FacebookProperties facebookProperties;
    private final MessengerProperties messengerProperties;
    private final ObjectMapper objectMapper;

    public MessengerClient(
            RestClient restClient,
            FacebookProperties facebookProperties,
            MessengerProperties messengerProperties,
            ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.facebookProperties = facebookProperties;
        this.messengerProperties = messengerProperties;
        this.objectMapper = objectMapper;
    }

    public void send(String psid, OutboundMessage message) {
        if (!messengerProperties.sendEnabled()) {
            log.info("Messenger send disabled; skipped outbound message for psid={}", psid);
            return;
        }
        String pageAccessToken = facebookProperties.pageAccessToken();
        if (pageAccessToken == null || pageAccessToken.isBlank()) {
            log.warn("FACEBOOK_PAGE_ACCESS_TOKEN missing; skipped outbound message for psid={}", psid);
            return;
        }

        ObjectNode body = objectMapper.createObjectNode();
        ObjectNode recipient = body.putObject("recipient");
        recipient.put("id", psid);
        ObjectNode messageNode = body.putObject("message");
        messageNode.put("text", message.text());

        if (message.quickReplies() != null && !message.quickReplies().isEmpty()) {
            ArrayNode quickReplies = messageNode.putArray("quick_replies");
            for (OutboundMessage.QuickReply quickReply : message.quickReplies()) {
                ObjectNode item = quickReplies.addObject();
                item.put("content_type", "text");
                item.put("title", quickReply.title());
                item.put("payload", quickReply.payload());
            }
        }

        String url = "https://graph.facebook.com/"
                + facebookProperties.graphApiVersion()
                + "/me/messages?access_token="
                + pageAccessToken;

        try {
            restClient.post().uri(url).body(body).retrieve().toBodilessEntity();
            log.info("Messenger message sent to psid={}", psid);
        } catch (Exception ex) {
            log.error("Messenger send failed for psid={}: {}", psid, ex.getMessage());
            throw ex;
        }
    }

    public OutboundMessage toOutboundWithMenu(String text, List<MenuOption> menuOptions) {
        List<OutboundMessage.QuickReply> quickReplies = menuOptions.stream()
                .map(option -> new OutboundMessage.QuickReply(option.label(), option.payload()))
                .toList();
        return OutboundMessage.withQuickReplies(text, quickReplies);
    }
}
