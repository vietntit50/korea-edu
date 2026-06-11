# Technical Specification - Facebook Messenger Chatbot MVP

## 1. Technical Overview

Module **Facebook Messenger Chatbot MVP** la backend native cho fanpage tu van du hoc Han Quoc. He thong nhan message tu Facebook Messenger, dieu phoi conversation flow, phan loai intent, thu thap slots, tra loi FAQ, uoc tinh chi phi, check eligibility so bo, tao lead va handoff cho tu van vien.

Tech stack khuyen nghi:

- Backend: Spring Boot.
- Database: PostgreSQL.
- Cache/session: Redis optional cho session/cache/rate limit.
- LLM: provider abstraction.
- Admin: React simple UI hoac DB-backed config workflow trong MVP.
- Deployment: Docker.
- Observability: structured logs, metrics endpoint/dashboard, alert co ban.

Quyet dinh da chot:

- Bot tone: `trung tam/ban`.
- SDT khong bat buoc, chi can khi user muon callback/dat lich/handoff qua dien thoai.
- Build native, khong dung ManyChat/n8n.
- CRM deferred; implement `LeadService` abstraction va MVP database storage.
- Cost range dung config `CostRangeTemplate`, can business owner approve truoc production.

## 2. Architecture Goals & Constraints

### Goals

- Xu ly Messenger message on dinh, idempotent va co retry/fallback.
- Journey-first/need-first, khong school-first voi user moi.
- Rule-based truoc LLM cho menu, quick replies, handoff, disclaimer, guardrails.
- LLM co abstraction, timeout, JSON schema validation va prompt versioning.
- Structured data/config la source of truth; LLM khong tu tao chi phi/truong/visa.
- Luu du profile/slots/intent/log de tu van vien tiep tuc.
- Co admin config co ban cho FAQ, cost ranges, disclaimers, handoff triggers.

### Constraints

- MVP 30 ngay, tranh over-engineer.
- Full CRM, full school database, school-level cost calculator, advisor console day du la post-MVP.
- PII phai duoc mask trong logs.
- Cac rule GPA/gap year/cost range la MVP assumptions/proposed config, can phe duyet truoc go-live.

## 3. High-Level Architecture

```text
Facebook Messenger
  -> Webhook Controller
  -> Bot Orchestrator
  -> Intent Router
  -> LLM Service
  -> Slot Manager
  -> User Profile Store
  -> FAQ/Knowledge Base
  -> General Cost Estimator
  -> Eligibility Checker
  -> Lead Service/CRM
  -> Human Handoff Service
  -> Messenger Response API
```

### Logical Modules

| Module | Type | MVP Implementation |
|---|---|---|
| Channel Adapter | Spring service | Facebook Messenger webhook/send API |
| Bot Core | Spring service | Orchestrator, state machine, response builder |
| AI Layer | Spring service | LLM provider interface + prompt registry |
| Business Rules | Spring service/config | Cost, eligibility, lead scoring, handoff, guardrails |
| Data Layer | PostgreSQL/JPA | Session, messages, profile, lead, config, audit |
| Cache Layer | Redis optional | Rate limit, short-lived session cache |
| Admin | REST + optional React | Config API; UI can be deferred |

## 4. Component Responsibilities

| Component | Responsibilities | Must Not Do |
|---|---|---|
| Webhook Controller | Verify Facebook request, parse event, de-duplicate `mid`, enqueue/process message | Business reasoning, LLM calls directly |
| Messenger Client | Send text, quick replies, buttons, typing indicator | Build business copy |
| Bot Orchestrator | Load session/profile, call router/services, apply state transition, build response | Invent source data |
| Intent Router | Rule-based routing, LLM classification fallback, confidence handling | Create final answer without guardrails |
| LLM Service | Intent classify, slot extract, response rewrite, summary | Decide source of truth, compute cost |
| Slot Manager | Merge slots, validate confidence, track missing slots, update profile | Overwrite high-confidence data silently |
| FAQ/KB Service | Retrieve approved FAQ by intent/topic | Store unapproved/generated content as truth |
| Cost Estimator | Read `CostRangeTemplate`, return range and budget fit | Calculate numbers from LLM |
| Eligibility Checker | Rule-based score/risk/missing info | Promise visa/pass/fail |
| Lead Service | Create/update partial or phone lead behind abstraction | Depend on final CRM choice |
| Handoff Service | Create handoff, advisor payload, notification | Require phone for Messenger handoff |
| Config Service | Versioned config read/write, active config resolution | Change config without audit |
| Analytics Service | Aggregate conversation/lead/intent/fallback metrics | Expose PII in reports |

## 5. Runtime Data Flow

### 5.1. Inbound Message Flow

1. Facebook sends webhook event.
2. `WebhookController` verifies token/signature and extracts messaging events.
3. `MessageDedupService` checks `message.mid`; duplicates return success without processing.
4. Raw inbound message is persisted as `Message`.
5. `BotOrchestrator.processInboundMessage()` loads or creates `ConversationSession` by PSID.
6. `IntentRouter` applies rule-based matching:
   - quick reply/menu payload,
   - phone capture,
   - handoff keywords,
   - unsafe/high-risk keywords,
   - fixed FAQ triggers.
7. If no strong rule match, `LlmService.classifyIntent()` runs with timeout and JSON validation.
8. `SlotManager` extracts/merges slots by expected intent.
9. Orchestrator evaluates business rules:
   - guardrail,
   - handoff,
   - eligibility,
   - cost,
   - school lookup,
   - FAQ.
10. Response builder injects disclaimers and CTA.
11. Outbound messages are persisted and sent through Messenger Client.
12. Intent/action/latency are logged.

### 5.2. Handoff Flow

1. Handoff trigger detected.
2. Bot offers Messenger handoff or phone callback.
3. If user chooses Messenger, create `HandoffRequest` with PSID/profile/summary.
4. If user provides phone, create/update `Lead` and create `HandoffRequest`.
5. `AdvisorNotification` is queued/sent.
6. Conversation status becomes `handoff_requested` or `assigned`.

## 6. Conversation State Machine

| State | Entry Intent | Actions | Exit State |
|---|---|---|---|
| `NEW_SESSION` | `GREETING` | Create session, show global menu | `MENU_SHOWN` |
| `MENU_SHOWN` | `MENU_SELECTION` | Route selected payload | Target flow |
| `COLLECT_BASIC_PROFILE` | `ASK_PROCESS` | Extract education/intake | `NEXT_NEED_SELECTION` |
| `COLLECT_COST_PATHWAY` | `ASK_GENERAL_COST` | Ask/update pathway | `COLLECT_COST_REGION` |
| `COLLECT_COST_REGION` | `ASK_GENERAL_COST` | Ask/update region | `COLLECT_BUDGET` |
| `COLLECT_BUDGET` | `ASK_GENERAL_COST` | Ask/update budget | `COST_RESULT_SHOWN` |
| `COST_RESULT_SHOWN` | `ASK_GENERAL_COST` | Show range + disclaimer + CTA | B/C/E/G/menu |
| `COLLECT_ELIGIBILITY_CORE` | `CHECK_ELIGIBILITY` | Ask education/year/GPA | `COLLECT_ELIGIBILITY_SUPPORT` |
| `COLLECT_ELIGIBILITY_SUPPORT` | `CHECK_ELIGIBILITY` | Ask Korean/budget | `COLLECT_GOAL_REGION` |
| `COLLECT_GOAL_REGION` | `CHECK_ELIGIBILITY` | Ask pathway/region | `ELIGIBILITY_RESULT_SHOWN` |
| `ELIGIBILITY_RESULT_SHOWN` | `CHECK_ELIGIBILITY` | Save assessment + CTA | B/D/G/menu |
| `PATHWAY_EXPLAINED` | `ASK_D4_D2` | Explain D4-1/D2 | `COLLECT_PATHWAY_PROFILE` |
| `REGION_RECOMMENDED` | `ASK_REGION_ADVICE` | Recommend region | B/C/G/menu |
| `SCHOOL_LOOKUP` | `ASK_SCHOOL_INFO/COST` | Call school DB | `SCHOOL_INFO_SHOWN` or `HANDOFF_OFFERED` |
| `HANDOFF_OFFERED` | `HUMAN_HANDOFF` | Ask channel/phone optional | `HANDOFF_CREATED` or menu |
| `HANDOFF_CREATED` | `HUMAN_HANDOFF` | Notify advisor | End/menu |
| `FALLBACK_1` | `UNKNOWN` | Ask clarification | previous/menu |
| `FALLBACK_2` | `UNKNOWN` | Offer handoff | `HANDOFF_OFFERED` |

State transition records must include `session_id`, `current_state`, `next_state`, `intent`, `action`, `trace_id`, `created_at`.

## 7. Database Schema

Recommended PostgreSQL naming uses snake_case. IDs can be UUID.

### 7.1. `conversation_sessions`

| Field | Type | Required | Index | PII | Notes |
|---|---|---:|---|---|---|
| `id` | UUID | Yes | PK | No | Session id |
| `facebook_psid` | varchar(128) | Yes | idx | Yes | Messenger user id |
| `facebook_name` | varchar(255) | No |  | Yes | If available |
| `status` | varchar(50) | Yes | idx | No | active/handoff_requested/closed |
| `current_state` | varchar(80) | Yes | idx | No | State machine |
| `current_intent` | varchar(80) | No | idx | No | Last intent |
| `fallback_count` | int | Yes |  | No | Reset on valid intent |
| `metadata_json` | jsonb | No |  | Maybe | Channel/source/campaign |
| `started_at` | timestamptz | Yes |  | No |  |
| `last_message_at` | timestamptz | Yes | idx | No |  |
| `created_at`, `updated_at` | timestamptz | Yes |  | No |  |

Relationship: has many `messages`, `slot_values`, `intent_logs`, `handoff_requests`.
Retention: raw sessions 12-24 months; anonymize PSID per policy.

### 7.2. `messages`

| Field | Type | Required | Index | PII | Notes |
|---|---|---:|---|---|---|
| `id` | UUID | Yes | PK | No |  |
| `session_id` | UUID | Yes | idx | No | FK |
| `facebook_mid` | varchar(128) | No | unique | No | For inbound dedup |
| `direction` | varchar(20) | Yes | idx | No | inbound/outbound |
| `sender_type` | varchar(20) | Yes |  | No | user/bot/advisor/system |
| `message_type` | varchar(30) | Yes |  | No | text/quick_reply/button/event |
| `text` | text | No |  | Yes | Mask in logs, not DB |
| `payload_json` | jsonb | No |  | Maybe | Raw parsed payload |
| `trace_id` | varchar(80) | No | idx | No |  |
| `created_at` | timestamptz | Yes | idx | No |  |

Retention: 12-24 months or anonymize.

### 7.3. `user_profiles`

| Field | Type | Required | Index | PII | Notes |
|---|---|---:|---|---|---|
| `id` | UUID | Yes | PK | No |  |
| `facebook_psid` | varchar(128) | Yes | unique | Yes |  |
| `full_name` | varchar(255) | No |  | Yes |  |
| `phone` | varchar(30) | No | idx | Yes | Optional |
| `province` | varchar(100) | No | idx | Yes |  |
| `birth_year` | int | No |  | Yes |  |
| `education_level` | varchar(50) | No | idx | No |  |
| `graduation_year` | int | No | idx | No |  |
| `gpa` | numeric(4,2) | No |  | No |  |
| `korean_level` | varchar(50) | No |  | No |  |
| `topik_level` | varchar(50) | No |  | No |  |
| `budget_vnd` | bigint | No |  | Sensitive |  |
| `desired_pathway` | varchar(50) | No | idx | No |  |
| `desired_region` | varchar(50) | No | idx | No |  |
| `desired_major` | varchar(255) | No |  | No |  |
| `desired_intake` | varchar(100) | No |  | No |  |
| `family_in_korea` | boolean | No |  | Maybe |  |
| `visa_history_flag` | boolean | No |  | Sensitive | Do not over-ask |
| `created_at`, `updated_at` | timestamptz | Yes |  | No |  |

Retention: per lead/CRM policy; PII deletion support required before production.

### 7.4. `leads`

| Field | Type | Required | Index | PII | Notes |
|---|---|---:|---|---|---|
| `id` | UUID | Yes | PK | No |  |
| `profile_id` | UUID | No | idx | No | FK |
| `session_id` | UUID | No | idx | No | FK |
| `full_name` | varchar(255) | No |  | Yes |  |
| `phone` | varchar(30) | No | idx | Yes | Conditional |
| `facebook_psid` | varchar(128) | Yes | idx | Yes |  |
| `need_type` | varchar(80) | No | idx | No |  |
| `lead_source` | varchar(80) | Yes | idx | No | messenger/comment/cta |
| `lead_score` | varchar(5) | No | idx | No | A/B/C/D |
| `risk_level` | varchar(20) | No | idx | No | LOW/MEDIUM/HIGH |
| `status` | varchar(50) | Yes | idx | No | new/qualified/handoff/contacted/closed |
| `assigned_advisor_id` | UUID | No | idx | No | Future |
| `conversation_summary` | text | No |  | Maybe | Mask if logged |
| `created_at`, `updated_at` | timestamptz | Yes | idx | No |  |

Duplicate handling: match phone if present, else PSID + active status.

### 7.5. Supporting Tables

| Table | Key Fields | Required Indexes | PII | Notes |
|---|---|---|---|---|
| `intent_logs` | `id`, `session_id`, `message_id`, `intent`, `confidence`, `source`, `model_version`, `raw_text`, `created_at` | `intent`, `session_id`, `created_at` | raw_text maybe | Store only needed raw text; mask in logs. |
| `slot_values` | `id`, `session_id`, `profile_id`, `slot_name`, `slot_value_json`, `confidence`, `source`, `source_message_id`, `updated_at` | `session_id+slot_name`, `profile_id` | depends | Preserve source/confidence. |
| `faq_items` | `id`, `intent`, `question`, `answer`, `tags`, `is_active`, `version`, `updated_by`, `updated_at` | `intent`, `is_active`, `tags` | No | Approved KB. |
| `cost_range_templates` | `id`, `pathway`, `region_group`, `min_vnd`, `max_vnd`, `components_json`, `approved`, `version`, `effective_from` | `pathway+region_group`, `approved` | No | Source of truth for general cost. |
| `eligibility_assessments` | `id`, `lead_id`, `profile_id`, `session_id`, `lead_score`, `risk_level`, `suggested_pathway`, `suggested_region`, `missing_info_json`, `rules_triggered_json`, `created_at` | `lead_id`, `profile_id`, `risk_level`, `lead_score` | Maybe | Rule output snapshot. |
| `handoff_requests` | `id`, `lead_id`, `session_id`, `facebook_psid`, `reason`, `status`, `assigned_advisor_id`, `summary`, `risk_warning`, `sla_due_at`, `created_at` | `status`, `assigned_advisor_id`, `created_at` | Yes | Phone optional. |
| `advisor_notifications` | `id`, `handoff_id`, `channel`, `recipient`, `status`, `payload_json`, `sent_at`, `error_message`, `created_at` | `handoff_id`, `status` | Maybe | Payload should be minimal/masked. |
| `bot_configurations` | `id`, `config_key`, `value_json`, `version`, `is_active`, `updated_by`, `updated_at` | `config_key+is_active`, `version` | Usually no | Versioned config. |
| `audit_logs` | `id`, `actor_id`, `actor_role`, `action`, `entity_type`, `entity_id`, `changes_json`, `created_at` | `entity_type+entity_id`, `actor_id`, `created_at` | Maybe | Mask PII diffs if possible. |

## 8. API Contracts

All internal APIs should return a standard error envelope:

```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Human readable safe message",
    "trace_id": "tr_123"
  }
}
```

### 8.1. `POST /webhooks/facebook`

Purpose: Receive Messenger webhook events.

Permission/auth: Facebook webhook verification/signature; no public business action without verification.

Idempotency: Use `message.mid` unique key.

Request: Facebook webhook payload.

Response:

```json
{ "status": "received" }
```

Error cases:

- `401 INVALID_SIGNATURE`
- `400 UNSUPPORTED_EVENT`
- `409 DUPLICATE_MESSAGE` internally logged but response can still be 200 to Facebook
- `500 PROCESSING_ERROR` only if request cannot be accepted

### 8.2. `POST /bot/messages/process`

Purpose: Process normalized inbound message; useful for tests/internal channel adapters.

Permission/auth: Internal service token.

Request:

```json
{
  "psid": "123456",
  "text": "GPA 6.5, chua TOPIK, co di duoc khong?",
  "source": "messenger",
  "payload": null,
  "trace_id": "tr_001"
}
```

Response:

```json
{
  "session_id": "6a4d...",
  "intent": "CHECK_ELIGIBILITY",
  "state": "COLLECT_ELIGIBILITY_SUPPORT",
  "messages": [
    {
      "type": "text",
      "text": "Trung tam co the check phu hop so bo cho ban...",
      "quick_replies": ["THPT", "CD/DH", "Dang hoc", "Chua ro"]
    }
  ]
}
```

Error cases: invalid PSID, processing timeout, session lock failed.

### 8.3. `POST /llm/intent-classify`

Purpose: Internal wrapper for intent classification.

Permission/auth: Internal only.

Request:

```json
{
  "text": "Dongguk het bao nhieu?",
  "context": {
    "current_state": "MENU_SHOWN",
    "last_intent": null
  },
  "allowed_intents": ["ASK_GENERAL_COST", "ASK_SCHOOL_COST", "UNKNOWN"]
}
```

Response:

```json
{
  "intent": "ASK_SCHOOL_COST",
  "confidence": 0.91,
  "entities": {
    "school_name": "Dongguk"
  },
  "model_version": "intent-v1"
}
```

Error cases: provider timeout, invalid JSON, confidence below threshold.

### 8.4. `POST /llm/slot-extract`

Purpose: Extract structured slots from user text.

Request:

```json
{
  "text": "Tot nghiep 2023, GPA 6.5, ngan sach 280 trieu",
  "expected_slots": ["graduation_year", "gpa", "budget_vnd"],
  "current_profile": {}
}
```

Response:

```json
{
  "slots": [
    { "name": "graduation_year", "value": 2023, "confidence": 0.98, "source_text": "Tot nghiep 2023" },
    { "name": "gpa", "value": 6.5, "confidence": 0.97, "source_text": "GPA 6.5" },
    { "name": "budget_vnd", "value": 280000000, "confidence": 0.94, "source_text": "280 trieu" }
  ],
  "model_version": "slot-v1"
}
```

Error cases: invalid JSON, unsupported slot, low confidence.

### 8.5. `POST /cost/general-estimate`

Purpose: Return general cost range from approved config.

Request:

```json
{
  "desired_pathway": "D4-1",
  "desired_region": "NEAR_SEOUL",
  "budget_vnd": 280000000
}
```

Response:

```json
{
  "currency": "VND",
  "estimate_type": "RANGE",
  "min_vnd": 240000000,
  "max_vnd": 340000000,
  "budget_fit": "WITHIN_RANGE",
  "components": ["tuition", "housing", "living", "visa", "flight", "insurance", "buffer"],
  "disclaimer_required": true,
  "config_version": 1
}
```

Error cases: missing approved config, unsupported pathway/region.

### 8.6. `POST /eligibility/check`

Purpose: Rule-based preliminary profile assessment.

Request:

```json
{
  "education_level": "THPT",
  "graduation_year": 2023,
  "gpa": 6.5,
  "korean_level": "NONE",
  "budget_vnd": 280000000,
  "desired_pathway": "UNKNOWN",
  "desired_region": "UNKNOWN",
  "visa_history_flag": null
}
```

Response:

```json
{
  "lead_score": "B",
  "risk_level": "MEDIUM",
  "risk_reasons": ["GPA_MEDIUM", "PATHWAY_UNKNOWN"],
  "suggested_pathway": "D4-1",
  "suggested_region": null,
  "missing_information": ["desired_region"],
  "next_action": "COLLECT_MORE_OR_HANDOFF_OFFER",
  "rules_version": 1
}
```

Error cases: insufficient minimum data, rule config missing.

### 8.7. `POST /leads`

Purpose: Create lead or partial lead.

Request:

```json
{
  "facebook_psid": "123456",
  "phone": "0900000000",
  "full_name": "Nguyen Van A",
  "need_type": "GENERAL_COST",
  "lead_source": "messenger",
  "profile": {
    "gpa": 6.5,
    "budget_vnd": 280000000
  }
}
```

Response:

```json
{
  "lead_id": "9df0...",
  "status": "new",
  "dedup_action": "created"
}
```

Error cases: invalid phone, duplicate merge conflict, database unavailable.

### 8.8. `PATCH /leads/{id}`

Purpose: Update lead score, risk, status, advisor assignment or profile fields.

Request:

```json
{
  "lead_score": "B",
  "risk_level": "MEDIUM",
  "status": "qualified",
  "conversation_summary": "User hoi chi phi D4-1..."
}
```

Response:

```json
{ "lead_id": "9df0...", "updated": true }
```

Error cases: lead not found, invalid transition, unauthorized.

### 8.9. `POST /handoff`

Purpose: Create handoff request for advisor.

Request:

```json
{
  "session_id": "6a4d...",
  "lead_id": "9df0...",
  "facebook_psid": "123456",
  "reason": "RISK_REVIEW",
  "summary": "User GPA 6.5, tot nghiep 2023, ngan sach 280 trieu.",
  "risk_warning": "MEDIUM"
}
```

Response:

```json
{
  "handoff_id": "ho_123",
  "status": "requested",
  "assigned_advisor_id": null,
  "notification_status": "queued"
}
```

Error cases: missing session, notification failure, invalid reason.

### 8.10. `GET /admin/bot-config`

Purpose: Read active bot config.

Query params: `key`, `include_inactive=false`.

Response:

```json
{
  "items": [
    {
      "key": "fallback.message",
      "value": "Trung tam chua hieu ro y ban...",
      "version": 3,
      "is_active": true
    }
  ]
}
```

Permission: admin/owner.

### 8.11. `PUT /admin/bot-config`

Purpose: Update config with audit.

Request:

```json
{
  "key": "cost.d4_1.near_seoul",
  "value": {
    "min_vnd": 240000000,
    "max_vnd": 340000000,
    "approved": false
  },
  "change_reason": "Update proposed MVP range"
}
```

Response:

```json
{ "key": "cost.d4_1.near_seoul", "version": 4, "updated": true }
```

Error cases: invalid schema, unauthorized, approved-version conflict.

### 8.12. `GET /analytics/chatbot`

Purpose: Return MVP analytics metrics.

Query params: `from`, `to`, `source`, `group_by`.

Response:

```json
{
  "total_conversations": 1200,
  "leads_created": 240,
  "chat_to_lead_conversion": 0.2,
  "handoffs": 130,
  "fallback_rate": 0.08,
  "top_intents": [
    { "intent": "ASK_GENERAL_COST", "count": 420 }
  ],
  "lead_score_distribution": {
    "A": 40,
    "B": 110,
    "C": 60,
    "D": 30
  }
}
```

Permission: admin/owner/manager.

## 9. Facebook Messenger Webhook Handling

### Requirements

- Support webhook verification during app setup.
- Verify request authenticity using Facebook app secret/signature where configured.
- Parse text, quick reply, postback/button, delivery/read events if needed.
- Ignore unsupported events safely.
- De-duplicate inbound messages by `message.mid`.
- Return 200 quickly after accepting event; if processing may be slow, enqueue async.

### Processing Mode

MVP can start synchronous if latency stays acceptable. If p95 exceeds target or Facebook retries become noisy, move processing to queue:

```text
Webhook -> persist raw event -> enqueue -> worker -> bot process -> send response
```

### Messenger Response Types

- Text.
- Quick replies.
- Buttons/postbacks for handoff/menu.
- Typing indicator optional.

## 10. Bot Orchestration Design

### Orchestrator Responsibilities

1. Load active session/profile.
2. Persist inbound message.
3. Resolve current state.
4. Route intent.
5. Extract/merge slots.
6. Evaluate business rules by precedence.
7. Call domain services.
8. Build response with CTA/disclaimers.
9. Persist outbound message and transition.
10. Send via Messenger Client.

### Response Builder

Response builder input:

- `intent`
- `state`
- `profile`
- `service_result`
- `disclaimer_flags`
- `quick_reply_set`
- `handoff_offer`

Output:

- One or more Messenger messages.
- Quick replies/buttons.
- `next_state`.
- Events to log.

## 11. Intent Router Design

### Rule-Based First

Rule-based router handles:

- Greeting.
- Menu payload.
- Quick replies.
- Phone capture.
- Handoff keywords.
- Unsafe/high-risk keywords.
- Fixed FAQ triggers.
- Disclaimer-only cases.

### LLM Classification

LLM classification runs when:

- No rule match.
- User asks natural free-text question.
- Message has typo/multiple concepts.
- School/major/region/budget entity extraction is needed.

Confidence thresholds:

- `>=0.80`: accept intent.
- `0.60-0.79`: ask confirmation if high-impact.
- `<0.60`: `UNKNOWN` fallback.

Unsafe guardrail can override confidence threshold.

## 12. LLM Integration Design

### Interface

`LlmService` should expose:

- `classifyIntent(text, context): IntentResult`
- `extractSlots(text, expectedSlots, context): SlotExtractionResult`
- `generateResponse(templateContext): DraftResponse`
- `summarizeConversation(sessionId): SummaryResult`
- `checkGuardrail(draft, context): GuardrailResult`

### When Not To Call LLM

- Quick reply/menu route.
- Cost calculation.
- Eligibility scoring.
- Phone validation.
- Static FAQ exact answer.
- Webhook verification.
- Admin config validation.

### Reliability Controls

- Timeout: 3-5 seconds per LLM call for MVP.
- Retry: at most 1 retry for transient provider errors.
- JSON schema validation on classification and extraction.
- Prompt version stored in `intent_logs`.
- Token cap per prompt.
- Cache stable FAQ responses if response generation is used.
- Fallback to menu/FAQ if LLM fails.

## 13. Prompt & Guardrail Design

### Intent Classification Prompt

Use allowed intent list only. Output JSON:

```json
{
  "intent": "ASK_GENERAL_COST",
  "confidence": 0.92,
  "entities": {},
  "unsafe_flags": []
}
```

### Slot Extraction Prompt

Rules:

- Extract only values present in message/context.
- Do not infer missing GPA, budget, school, TOPIK.
- Return confidence and source text.

### Response Generation Prompt

Rules:

- Use only approved KB, cost result, eligibility result or verified school data.
- Use tone `trung tam/ban`.
- Keep answer short and CTA-driven.
- Include disclaimer flags supplied by backend.

### Guardrail Check

Guardrail runs on:

- LLM-generated response.
- Any response containing cost/school/visa/hoc bong/viec lam them.

Blocked if:

- Visa guarantee.
- False document advice.
- Illegal work advice.
- Unverified numeric cost/school data.
- School recommendation without minimum profile.

## 14. Slot Manager & User Profile Store

### Slot Merge Rules

- High-confidence new slot can update empty value.
- If existing value differs, ask confirmation unless user explicitly corrected.
- Store `source_message_id`, `confidence`, `source`, `updated_at`.
- Sensitive slots like budget/visa history must not be printed in logs.

### Slot Priority

| Intent | Core Slots |
|---|---|
| `CHECK_ELIGIBILITY` | `education_level`, `graduation_year`, `gpa`, then `budget_vnd`, `korean_level`, `desired_pathway`, `desired_region` |
| `ASK_GENERAL_COST` | `desired_pathway`, `desired_region`, `budget_vnd` |
| `ASK_REGION_ADVICE` | `budget_vnd`, `desired_pathway`, `lifestyle_preference` |
| `ASK_SCHOOL_COST` | `school_name`, `program_type`, optional `intake_term` |
| `HUMAN_HANDOFF` | `facebook_psid`, optional `phone`, `preferred_time` |

## 15. Cost Estimator Service

### Responsibilities

- Read approved `CostRangeTemplate`.
- Return range by pathway/region.
- Evaluate budget fit.
- Require cost disclaimer.
- Return config version for audit.

### MVP Region Groups

- `LOW_COST_PROVINCE`
- `MAJOR_CITY`
- `NEAR_SEOUL`
- `SEOUL_TOP`
- `UNKNOWN`

### Error Behavior

- If approved config missing, do not invent number.
- Bot response: "Trung tam can tu van vien xac nhan lai range chi phi truoc khi gui ban."
- Offer handoff.

## 16. Eligibility Checker Service

### Responsibilities

- Apply rule-based risk and lead scoring.
- Return risk reasons and missing information.
- Snapshot rule version.
- Never produce pass/fail or visa guarantee.

### Outputs

- `lead_score`: A/B/C/D/UNKNOWN.
- `risk_level`: LOW/MEDIUM/HIGH/UNKNOWN.
- `suggested_pathway`.
- `suggested_region`.
- `missing_information`.
- `next_action`.
- `rules_triggered`.

### Rule Versioning

Eligibility rules should be loaded from config or versioned code constants. MVP can start with code constants + `rules_version`, then move to DB config.

## 17. Lead/CRM Integration

### MVP Approach

CRM choice is deferred. Implement:

```text
LeadService interface
  -> DatabaseLeadService implementation for MVP
  -> FutureCrmLeadService later
```

### Lead Creation Modes

- Phone lead: user provides phone and wants callback/handoff.
- Partial lead/profile: user completes eligibility or qualifies A/B but has no phone.
- Handoff-only PSID: user wants Messenger handoff without phone.

### Dedup Rules

1. If phone present, match active lead by normalized phone.
2. Else match by `facebook_psid` and active lead/session.
3. If duplicate found, update profile/summary instead of creating new.

## 18. Human Handoff & Notification

### Handoff Payload

Must include:

- `handoff_id`
- `reason`
- `facebook_psid`
- `phone` if available
- `need_type`
- `last_user_question`
- `conversation_summary`
- `profile_slots`
- `lead_score`
- `risk_level`
- `risk_warning`
- `created_at`

### Assignment

MVP options:

- Default advisor queue.
- Round-robin.
- Manual admin view.

If assignment rules are not ready, create `status=requested` and notify manager/default channel.

### Notification Channels

MVP can start with:

- Email.
- Slack/internal webhook if available.
- Admin queue only.

Do not block user response if notification fails; persist notification failure and alert.

## 19. Admin Configuration

### Config Keys

- `menu.items`
- `faq.*`
- `quick_replies.*`
- `fallback.message`
- `disclaimer.cost`
- `disclaimer.visa`
- `disclaimer.school`
- `cost.*`
- `lead_scoring.thresholds`
- `eligibility.thresholds`
- `handoff.triggers`
- `business_hours`
- `advisor.assignment_rule`
- `guardrail.blocklist`

### Update Requirements

- JSON schema validation per key.
- Versioned config.
- Audit log with actor/change reason.
- Active approved version for production-sensitive keys like cost.

## 20. Analytics & Reporting

### Metrics

- Total conversations.
- Leads created.
- Chat-to-lead conversion.
- Lead-to-handoff conversion.
- Top intents.
- Fallback rate.
- Eligibility checks.
- Cost inquiries.
- School-specific inquiries.
- Advisor response time if available.
- Lead score distribution.

### Implementation

MVP can aggregate from database tables on request. If traffic grows, add scheduled rollup table.

## 21. Logging, Monitoring & Alerting

### Structured Log Fields

- `trace_id`
- `session_id`
- `facebook_psid_hash` not raw PSID
- `message_id`
- `intent`
- `state`
- `backend_action`
- `latency_ms`
- `error_code`
- `llm_model_version`
- `config_version`

### Metrics/Alerts

- Webhook error rate.
- Messenger send failure rate.
- LLM timeout/error rate.
- Bot fallback rate.
- Handoff creation failure.
- Lead save failure.
- Cost config missing.
- P95 processing latency.

## 22. Security & Privacy

### Required Controls

- Verify Facebook webhook signature/token.
- Secrets via environment variables or secret manager.
- Input validation for all public/internal APIs.
- Rate limiting by PSID and IP where applicable.
- PII masking in logs.
- Role-based access for admin/advisor/owner endpoints.
- Audit logs for config and lead changes.
- Data retention policy before production.
- Avoid collecting visa history unless relevant to risk review.

### PII Fields

- Facebook PSID.
- Facebook name.
- Full name.
- Phone.
- Province.
- Birth year.
- Budget.
- Visa history.
- Raw message text may contain PII.

## 23. Error Handling & Fallback

| Failure | User Behavior | Backend Behavior |
|---|---|---|
| Invalid webhook | No user response | Reject/log safely |
| Duplicate message | No duplicate bot response | Return success to Facebook |
| LLM timeout | Ask menu/fallback | Log, retry once if safe |
| LLM invalid JSON | Fallback confirmation | Log prompt/model version |
| Cost config missing | Do not show number, offer advisor | Alert config issue |
| Eligibility rule error | Offer advisor/manual check | Log and fail safe |
| Lead save failure | Tell user info is noted in Messenger if true; retry queue | Queue retry/alert |
| Messenger send failure | No user-visible response possible | Retry based on API policy |
| School DB missing | Say advisor must confirm | Log missing verified data |

## 24. Deployment Suggestion

### MVP Deployment

```text
Dockerized Spring Boot app
PostgreSQL
Redis optional
External LLM provider
Facebook Messenger API
Admin UI optional
```

### Environments

- `local`: fixtures/mock Messenger, mock LLM optional.
- `staging`: real Facebook test page/app, test LLM key, staging DB.
- `production`: real fanpage, production secrets, monitoring enabled.

### Configuration

Use env vars for:

- Facebook app secret/page token.
- LLM API key/provider.
- Database URL.
- Redis URL.
- Admin auth secret/OIDC config.
- Notification channel credentials.

## 25. Testing Strategy

| Test Type | Scope | Examples |
|---|---|---|
| Unit tests | Routers, rule engine, cost, eligibility, slot merge | Cost budget fit, risk thresholds, guardrail detection |
| Integration tests | DB + services + APIs | Bot process -> cost -> response, lead create, handoff |
| Contract tests | Messenger webhook and internal APIs | Payload parsing, response schema, error envelope |
| Conversation flow tests | Flow A-G | New user, cost, eligibility, D4/D2, region, school, handoff |
| LLM eval tests | Intent/slot prompts | Typos, mixed intent, GPA/budget extraction |
| Security tests | Auth, PII, rate limit | Invalid webhook, masked logs, admin unauthorized |
| Regression tests | Business rules/guardrails | Visa guarantee, false documents, illegal work, unverified numbers |
| UAT | Business owner scripts | Messenger scenario walkthrough |

Release gate:

- All Must-have flow tests pass.
- No P0/P1 security/privacy issue.
- Cost config approved or cost flow blocked from production.
- Guardrail regression tests pass.

## 26. Open Technical Questions

| Question | Impact | Proposed Default |
|---|---|---|
| Admin UI now or DB-backed config first? | Delivery scope | DB-backed config + minimal API first; UI if time permits |
| Notification channel? | Handoff operations | Default advisor queue/email; Slack/webhook if available |
| Queue required from day 1? | Reliability/complexity | Start sync with idempotency; add queue if latency/retry requires |
| Auth provider for admin/advisor? | Security | Basic role-based internal auth for MVP, OIDC later |
| School lookup included in MVP? | Scope | Only verified seed data if BA provides; otherwise safe handoff |
| PII retention exact policy? | Production compliance | Temporary default: raw conversation 12 months, lead by CRM policy |
| LLM provider/model? | Cost/reliability | Use provider abstraction; config-driven model |

## 27. Implementation Notes

- Implement vertical slices in this order: webhook/menu, router/FAQ, slots/profile, cost, eligibility, lead/handoff, admin/analytics.
- Keep `BotOrchestrator` thin enough to delegate domain logic to services.
- Avoid hardcoding business copy in Java classes; use config/FAQ templates where practical.
- Persist raw inbound/outbound messages before and after processing for audit/debug.
- Do not allow LLM output to bypass guardrail/disclaimer checks.
- Add `trace_id` from webhook through all service calls.
- Phone optional must be enforced in UX and backend validation.
- Keep CRM and school database behind interfaces so post-MVP integrations do not rewrite conversation logic.
- Document every config key with owner, schema, default and approval requirement.
