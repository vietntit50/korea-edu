package com.koreaedu.chatbot.messenger;

import com.koreaedu.chatbot.config.FacebookProperties;
import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;

@Component
public class FacebookSignatureVerifier {

    private final FacebookProperties facebookProperties;

    public FacebookSignatureVerifier(FacebookProperties facebookProperties) {
        this.facebookProperties = facebookProperties;
    }

    public boolean isValid(String signatureHeader, byte[] rawBody) {
        if (!facebookProperties.signatureVerificationEnabled()) {
            return true;
        }
        String appSecret = facebookProperties.appSecret();
        if (appSecret == null || appSecret.isBlank()) {
            return false;
        }
        if (signatureHeader == null || !signatureHeader.startsWith("sha256=")) {
            return false;
        }
        String expected = "sha256=" + hmacSha256Hex(appSecret, rawBody);
        return constantTimeEquals(expected, signatureHeader);
    }

    private static String hmacSha256Hex(String secret, byte[] payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(payload);
            StringBuilder builder = new StringBuilder(digest.length * 2);
            for (byte value : digest) {
                builder.append(String.format("%02x", value));
            }
            return builder.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to compute Facebook signature", ex);
        }
    }

    private static boolean constantTimeEquals(String left, String right) {
        if (left.length() != right.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < left.length(); i++) {
            result |= left.charAt(i) ^ right.charAt(i);
        }
        return result == 0;
    }
}
