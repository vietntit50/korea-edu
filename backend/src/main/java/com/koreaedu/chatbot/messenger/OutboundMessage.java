package com.koreaedu.chatbot.messenger;

import java.util.List;

public record OutboundMessage(String text, List<QuickReply> quickReplies) {

    public record QuickReply(String title, String payload) {
    }

    public static OutboundMessage textOnly(String text) {
        return new OutboundMessage(text, List.of());
    }

    public static OutboundMessage withQuickReplies(String text, List<QuickReply> quickReplies) {
        return new OutboundMessage(text, quickReplies);
    }
}
