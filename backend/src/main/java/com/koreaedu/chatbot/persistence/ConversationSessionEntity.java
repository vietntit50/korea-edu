package com.koreaedu.chatbot.persistence;

import com.koreaedu.chatbot.domain.ConversationState;
import com.koreaedu.chatbot.domain.SessionStatus;
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
@Table(name = "conversation_sessions")
public class ConversationSessionEntity {

    @Id
    private UUID id;

    @Column(name = "facebook_psid", nullable = false, length = 128)
    private String facebookPsid;

    @Column(name = "facebook_name", length = 255)
    private String facebookName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private SessionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_state", nullable = false, length = 80)
    private ConversationState currentState;

    @Column(name = "current_intent", length = 80)
    private String currentIntent;

    @Column(name = "fallback_count", nullable = false)
    private int fallbackCount;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata_json", columnDefinition = "jsonb")
    private String metadataJson;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "last_message_at", nullable = false)
    private Instant lastMessageAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ConversationSessionEntity() {
    }

    public static ConversationSessionEntity create(String facebookPsid, Instant now) {
        ConversationSessionEntity session = new ConversationSessionEntity();
        session.id = UUID.randomUUID();
        session.facebookPsid = facebookPsid;
        session.status = SessionStatus.ACTIVE;
        session.currentState = ConversationState.NEW_SESSION;
        session.fallbackCount = 0;
        session.startedAt = now;
        session.lastMessageAt = now;
        session.createdAt = now;
        session.updatedAt = now;
        return session;
    }

    public UUID getId() {
        return id;
    }

    public String getFacebookPsid() {
        return facebookPsid;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public ConversationState getCurrentState() {
        return currentState;
    }

    public String getCurrentIntent() {
        return currentIntent;
    }

    public Instant getLastMessageAt() {
        return lastMessageAt;
    }

    public void touch(Instant now) {
        this.lastMessageAt = now;
        this.updatedAt = now;
    }

    public void showMenu(Instant now) {
        this.currentState = ConversationState.MENU_SHOWN;
        touch(now);
    }

    public void setCurrentIntent(String currentIntent) {
        this.currentIntent = currentIntent;
    }
}
