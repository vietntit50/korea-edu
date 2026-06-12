package com.koreaedu.chatbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "messenger")
public record MessengerProperties(boolean sendEnabled) {
}
