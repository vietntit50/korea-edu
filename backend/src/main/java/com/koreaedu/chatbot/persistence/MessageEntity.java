package com.koreaedu.chatbot.persistence;

import com.koreaedu.chatbot.domain.MessageDirection;
import com.koreaedu.chatbot.domain.MessageType;
import com.koreaedu.chatbot.domain.SenderType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "messages")
public class MessageEntity {

    @Id
    private UUID id;

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Column(name = "facebook_mid", length = 128)
    private String facebookMid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MessageDirection direction;

    @Enumerated(EnumType.STRING)
    @Column(name = "sender_type", nullable = false, length = 20)
    private SenderType senderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false, length = 30)
    private MessageType messageType;

    @Column(columnDefinition = "text")
    private String text;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload_json", columnDefinition = "jsonb")
    private String payloadJson;

    @Column(name = "trace_id", length = 80)
    private String traceId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected MessageEntity() {
    }

    public static MessageEntity inbound(
            UUID sessionId,
            String facebookMid,
            MessageType messageType,
            String text,
            String payloadJson,
            String traceId,
            Instant now) {
        MessageEntity message = new MessageEntity();
        message.id = UUID.randomUUID();
        message.sessionId = sessionId;
        message.facebookMid = facebookMid;
        message.direction = MessageDirection.INBOUND;
        message.senderType = SenderType.USER;
        message.messageType = messageType;
        message.text = text;
        message.payloadJson = payloadJson;
        message.traceId = traceId;
        message.createdAt = now;
        return message;
    }

    public static MessageEntity outbound(
            UUID sessionId,
            MessageType messageType,
            String text,
            String payloadJson,
            String traceId,
            Instant now) {
        MessageEntity message = new MessageEntity();
        message.id = UUID.randomUUID();
        message.sessionId = sessionId;
        message.direction = MessageDirection.OUTBOUND;
        message.senderType = SenderType.BOT;
        message.messageType = messageType;
        message.text = text;
        message.payloadJson = payloadJson;
        message.traceId = traceId;
        message.createdAt = now;
        return message;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public String getFacebookMid() {
        return facebookMid;
    }
}
