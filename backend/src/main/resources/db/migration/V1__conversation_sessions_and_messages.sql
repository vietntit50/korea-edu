CREATE TABLE conversation_sessions (
    id UUID PRIMARY KEY,
    facebook_psid VARCHAR(128) NOT NULL,
    facebook_name VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    current_state VARCHAR(80) NOT NULL,
    current_intent VARCHAR(80),
    fallback_count INT NOT NULL DEFAULT 0,
    metadata_json JSONB,
    started_at TIMESTAMPTZ NOT NULL,
    last_message_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_conversation_sessions_facebook_psid ON conversation_sessions (facebook_psid);
CREATE INDEX idx_conversation_sessions_status ON conversation_sessions (status);
CREATE INDEX idx_conversation_sessions_current_state ON conversation_sessions (current_state);
CREATE INDEX idx_conversation_sessions_last_message_at ON conversation_sessions (last_message_at);

CREATE TABLE messages (
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES conversation_sessions (id),
    facebook_mid VARCHAR(128),
    direction VARCHAR(20) NOT NULL,
    sender_type VARCHAR(20) NOT NULL,
    message_type VARCHAR(30) NOT NULL,
    text TEXT,
    payload_json JSONB,
    trace_id VARCHAR(80),
    created_at TIMESTAMPTZ NOT NULL
);

CREATE UNIQUE INDEX uq_messages_facebook_mid ON messages (facebook_mid) WHERE facebook_mid IS NOT NULL;
CREATE INDEX idx_messages_session_id ON messages (session_id);
CREATE INDEX idx_messages_direction ON messages (direction);
CREATE INDEX idx_messages_trace_id ON messages (trace_id);
CREATE INDEX idx_messages_created_at ON messages (created_at);
