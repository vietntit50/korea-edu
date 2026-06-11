# Prompt 05: Create Technical Specification

Bạn là Solution Architect, Senior Backend Engineer và Tech Lead cho sản phẩm **Korea Study Advisor Platform**.

## Bối Cảnh

Dự án đã có PRD:

`/docs/prd/01-facebook-messenger-chatbot-mvp.md`

Module cần triển khai là **Facebook Messenger Chatbot MVP tư vấn du học Hàn Quốc**.

Tech stack gợi ý trong PRD:

- Backend: Spring Boot.
- Database: PostgreSQL.
- Cache/session: Redis.
- LLM: abstraction layer để thay đổi provider.
- Admin simple UI: React hoặc cấu hình DB tạm thời.
- Deployment: Docker.
- Logging/monitoring cơ bản.

## Nhiệm Vụ

Hãy tạo technical specification chi tiết từ PRD để dev team có thể bắt đầu thiết kế và implement MVP.

Không cần viết code implementation. Hãy mô tả architecture, components, data model, API, state machine, integrations, security, observability và testing strategy.

## Input

- PRD chính: `/docs/prd/01-facebook-messenger-chatbot-mvp.md`
- Nếu có backlog/business rules/conversation flows, dùng làm context bổ sung.

## Yêu Cầu Output

Tạo tài liệu Markdown với cấu trúc sau:

```md
# Technical Specification - Facebook Messenger Chatbot MVP

## 1. Technical Overview

## 2. Architecture Goals & Constraints

## 3. High-Level Architecture

## 4. Component Responsibilities

## 5. Runtime Data Flow

## 6. Conversation State Machine

## 7. Database Schema

## 8. API Contracts

## 9. Facebook Messenger Webhook Handling

## 10. Bot Orchestration Design

## 11. Intent Router Design

## 12. LLM Integration Design

## 13. Prompt & Guardrail Design

## 14. Slot Manager & User Profile Store

## 15. Cost Estimator Service

## 16. Eligibility Checker Service

## 17. Lead/CRM Integration

## 18. Human Handoff & Notification

## 19. Admin Configuration

## 20. Analytics & Reporting

## 21. Logging, Monitoring & Alerting

## 22. Security & Privacy

## 23. Error Handling & Fallback

## 24. Deployment Suggestion

## 25. Testing Strategy

## 26. Open Technical Questions

## 27. Implementation Notes
```

## Chi Tiết Bắt Buộc

### Architecture

Mô tả kiến trúc:

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

### Database schema

Đề xuất schema cho:

- `ConversationSession`
- `Message`
- `UserProfile`
- `Lead`
- `IntentLog`
- `SlotValue`
- `FAQItem`
- `CostRangeTemplate`
- `EligibilityAssessment`
- `HandoffRequest`
- `AdvisorNotification`
- `BotConfiguration`
- `AuditLog`

Với mỗi table/entity cần có:

- Fields.
- Type gợi ý.
- Required/nullable.
- Index.
- Relationship.
- PII flag.
- Retention note.

### API contracts

Mô tả chi tiết:

- `POST /webhooks/facebook`
- `POST /bot/messages/process`
- `POST /llm/intent-classify`
- `POST /llm/slot-extract`
- `POST /cost/general-estimate`
- `POST /eligibility/check`
- `POST /leads`
- `PATCH /leads/{id}`
- `POST /handoff`
- `GET /admin/bot-config`
- `PUT /admin/bot-config`
- `GET /analytics/chatbot`

Với mỗi API cần có:

- Purpose.
- Request JSON.
- Response JSON.
- Error cases.
- Permission/auth.
- Idempotency nếu cần.

### LLM design

Mô tả:

- Khi nào gọi LLM.
- Khi nào không gọi LLM.
- Intent classification.
- Slot extraction.
- Response generation.
- Guardrail check.
- Prompt versioning.
- Cost control.
- Timeout/fallback.
- JSON schema validation.

### Security & privacy

Bắt buộc có:

- Verify Facebook webhook signature.
- Input validation.
- Secret qua environment variables.
- PII masking trong log.
- Role-based access cho admin/advisor.
- Audit log.
- Rate limiting.
- Data retention.

### Testing strategy

Bao gồm:

- Unit test.
- Integration test.
- Contract test.
- Conversation flow test.
- LLM prompt/eval test.
- Security test.
- Regression test cho business rules.

## Tiêu Chí Chất Lượng

- Viết bằng tiếng Việt.
- Đủ chi tiết để team backend bắt đầu implement.
- Không over-engineer quá MVP 30 ngày.
- Phân biệt rõ MVP và future integration.
- Không để LLM là source of truth.
- Có error handling thực tế cho Facebook, LLM, CRM, database.
- Có logging/monitoring đủ để debug production.

## File Output Gợi Ý

Lưu tài liệu được tạo ra tại:

`/docs/tech/01-chatbot-mvp-technical-spec.md`
