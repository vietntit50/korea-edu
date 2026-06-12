package com.koreaedu.chatbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "facebook")
public record FacebookProperties(
        String appSecret,
        String verifyToken,
        String pageAccessToken,
        String graphApiVersion,
        boolean signatureVerificationEnabled
) {
}
