# Korea Edu Chatbot Backend

Facebook Messenger Chatbot MVP — Slice 1: webhook, session/message persistence, greeting menu.

## Prerequisites

- Java 21
- Maven 3.9+
- Docker (for PostgreSQL)

## Quick start

```bash
# Start PostgreSQL
docker compose up -d

# From repo root, copy env and set Facebook tokens when ready
cp .env.example .env

# Run backend
cd backend
mvn spring-boot:run
```

## Slice 1 endpoints

| Method | Path | Purpose |
|---|---|---|
| GET | `/webhooks/facebook` | Facebook webhook verification |
| POST | `/webhooks/facebook` | Receive Messenger events |
| POST | `/bot/messages/process` | Internal/local message processing |

## Local test without Facebook

```bash
curl -X POST http://localhost:8080/bot/messages/process \
  -H "Content-Type: application/json" \
  -d '{"psid":"local-psid-1","text":"Tu van du hoc Han"}'
```

## Tests

```bash
cd backend
mvn test
```

## Facebook webhook staging

Xem [`docs/ops/01-facebook-webhook-staging-setup.md`](../docs/ops/01-facebook-webhook-staging-setup.md) và [`docs/ops/STAGING_CURRENT.md`](../docs/ops/STAGING_CURRENT.md).

```bash
cp .env.staging.example .env   # điền APP_SECRET + PAGE_TOKEN
./scripts/staging-up.sh          # terminal 1
./scripts/staging-tunnel.sh      # terminal 2
```

## Slice 1 done criteria

- Webhook verify + signature check
- Dedup by `message.mid`
- Greeting + 7 menu quick replies (`trung tam/ban` tone)
- `conversation_sessions` + `messages` persisted
- Structured logs with `trace_id` in MDC
