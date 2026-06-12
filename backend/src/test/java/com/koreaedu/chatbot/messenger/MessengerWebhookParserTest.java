package com.koreaedu.chatbot.messenger;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.koreaedu.chatbot.domain.MessageType;
import org.junit.jupiter.api.Test;

class MessengerWebhookParserTest {

    private final MessengerWebhookParser parser = new MessengerWebhookParser(new ObjectMapper());

    @Test
    void parsesTextMessage() {
        String body =
                """
                {
                  "object": "page",
                  "entry": [{
                    "messaging": [{
                      "sender": { "id": "PSID_1" },
                      "message": { "mid": "m_1", "text": "Tu van du hoc Han" }
                    }]
                  }]
                }
                """;

        var messages = parser.parse(body);

        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).psid()).isEqualTo("PSID_1");
        assertThat(messages.get(0).mid()).isEqualTo("m_1");
        assertThat(messages.get(0).messageType()).isEqualTo(MessageType.TEXT);
        assertThat(messages.get(0).text()).isEqualTo("Tu van du hoc Han");
    }

    @Test
    void parsesQuickReplyPayload() {
        String body =
                """
                {
                  "object": "page",
                  "entry": [{
                    "messaging": [{
                      "sender": { "id": "PSID_1" },
                      "message": {
                        "mid": "m_2",
                        "text": "Quy trinh",
                        "quick_reply": { "payload": "MENU_PROCESS" }
                      }
                    }]
                  }]
                }
                """;

        var messages = parser.parse(body);

        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).messageType()).isEqualTo(MessageType.QUICK_REPLY);
        assertThat(messages.get(0).payload()).isEqualTo("MENU_PROCESS");
    }
}
