package com.koreaedu.chatbot.webhook;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.koreaedu.chatbot.persistence.MessageRepository;
import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FacebookWebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageRepository messageRepository;

    @Test
    void verifiesWebhookChallenge() throws Exception {
        mockMvc.perform(get("/webhooks/facebook")
                        .param("hub.mode", "subscribe")
                        .param("hub.verify_token", "test-verify-token")
                        .param("hub.challenge", "12345"))
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString()).isEqualTo("12345"));
    }

    @Test
    void rejectsInvalidSignature() throws Exception {
        String body =
                """
                {"object":"page","entry":[{"messaging":[{"sender":{"id":"PSID_1"},"message":{"mid":"m_bad","text":"hi"}}]}]}
                """;

        mockMvc.perform(post("/webhooks/facebook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Hub-Signature-256", "sha256=invalid")
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("invalid_signature"));
    }

    @Test
    void processesNewMessageAndDedupesByMid() throws Exception {
        String body =
                """
                {
                  "object": "page",
                  "entry": [{
                    "messaging": [{
                      "sender": { "id": "PSID_1" },
                      "message": { "mid": "m_new", "text": "Tu van du hoc Han" }
                    }]
                  }]
                }
                """;
        String signature = sign(body, "test-app-secret");

        mockMvc.perform(post("/webhooks/facebook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Hub-Signature-256", signature)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("received"));

        long afterFirst = messageRepository.count();
        assertThat(afterFirst).isGreaterThanOrEqualTo(2);

        mockMvc.perform(post("/webhooks/facebook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Hub-Signature-256", signature)
                        .content(body))
                .andExpect(status().isOk());

        assertThat(messageRepository.count()).isEqualTo(afterFirst);
    }

    private static String sign(String body, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] digest = mac.doFinal(body.getBytes(StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder("sha256=");
        for (byte value : digest) {
            builder.append(String.format("%02x", value));
        }
        return builder.toString();
    }
}
