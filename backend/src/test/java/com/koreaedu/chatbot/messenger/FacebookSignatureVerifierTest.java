package com.koreaedu.chatbot.messenger;

import static org.assertj.core.api.Assertions.assertThat;

import com.koreaedu.chatbot.config.FacebookProperties;
import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;

class FacebookSignatureVerifierTest {

    @Test
    void validatesMatchingSignature() throws Exception {
        byte[] body = "{\"object\":\"page\"}".getBytes(StandardCharsets.UTF_8);
        String secret = "test-app-secret";
        String signature = "sha256=" + hmac(secret, body);

        FacebookSignatureVerifier verifier =
                new FacebookSignatureVerifier(new FacebookProperties(secret, "token", "page", "v21.0", true));

        assertThat(verifier.isValid(signature, body)).isTrue();
    }

    @Test
    void rejectsInvalidSignature() {
        FacebookSignatureVerifier verifier =
                new FacebookSignatureVerifier(new FacebookProperties("secret", "token", "page", "v21.0", true));

        assertThat(verifier.isValid("sha256=deadbeef", "{}".getBytes(StandardCharsets.UTF_8)))
                .isFalse();
    }

    private static String hmac(String secret, byte[] payload) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] digest = mac.doFinal(payload);
        StringBuilder builder = new StringBuilder();
        for (byte value : digest) {
            builder.append(String.format("%02x", value));
        }
        return builder.toString();
    }
}
