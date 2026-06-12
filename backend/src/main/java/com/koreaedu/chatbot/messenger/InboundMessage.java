package com.koreaedu.chatbot.messenger;

import com.koreaedu.chatbot.domain.MessageType;

public record InboundMessage(
        String psid,
        String mid,
        MessageType messageType,
        String text,
        String payload) {

    public boolean isQuickReplyOrPostback() {
        return messageType == MessageType.QUICK_REPLY || messageType == MessageType.BUTTON;
    }
}
