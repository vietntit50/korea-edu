# Prompt 02: Create Implementation Backlog

Bạn là Senior Product Manager, Business Analyst, Delivery Manager và Tech Lead cho sản phẩm **Korea Study Advisor Platform**.

## Bối Cảnh

Dự án đã có PRD module đầu tiên:

`/docs/prd/01-facebook-messenger-chatbot-mvp.md`

Module cần triển khai là **Facebook Messenger Chatbot MVP tư vấn du học Hàn Quốc**.

MVP phải tập trung vào:

- Facebook Messenger webhook.
- Greeting/menu.
- Journey-first flow.
- FAQ cơ bản.
- General cost estimator.
- Eligibility check sơ bộ.
- Lead capture.
- Human handoff.
- Conversation logging.
- LLM intent classification.
- LLM slot extraction.
- Admin config cơ bản.
- Tạo lead sang CRM hoặc database đơn giản.

## Nhiệm Vụ

Hãy biến PRD thành backlog triển khai MVP 30 ngày. Backlog phải đủ rõ để PM/BA/dev/designer/tester có thể dùng để lập sprint và bắt đầu làm việc.

Không viết lại PRD. Hãy chuyển PRD thành các **epic, user story và task cụ thể**.

## Input

- PRD chính: `/docs/prd/01-facebook-messenger-chatbot-mvp.md`
- Nếu đã có file open questions/decision log, hãy dùng để đánh dấu assumptions hoặc blockers.

## Yêu Cầu Output

Tạo tài liệu Markdown với cấu trúc sau:

```md
# Implementation Backlog - Facebook Messenger Chatbot MVP

## 1. Delivery Goal

## 2. MVP Assumptions

## 3. Scope Summary

### 3.1. Must Have
### 3.2. Should Have
### 3.3. Could Have
### 3.4. Out of Scope

## 4. Epics

## 5. Detailed Backlog

### Epic 1: Facebook Messenger Integration

| Story ID | User Story | Priority | Owner Role | Estimate | Dependencies | Acceptance Criteria |
|---|---|---|---|---|---|---|

#### Tasks

| Task ID | Task | Role | Estimate | Dependencies | Notes |
|---|---|---|---|---|---|

## 6. Sprint Suggestion for 30-Day MVP

## 7. Cross-Team Dependencies

## 8. Testing Backlog

## 9. Design Backlog

## 10. BA/Content Backlog

## 11. Risks & Mitigations

## 12. Definition of Ready

## 13. Definition of Done
```

## Backlog Requirements

Epics tối thiểu cần có:

1. Facebook Messenger Integration.
2. Bot Orchestration & Conversation State.
3. Intent Routing & LLM Classification.
4. Slot Filling & User Profile Store.
5. FAQ & Knowledge Base.
6. General Cost Estimator.
7. Eligibility Check.
8. Lead Capture & CRM Integration.
9. Human Handoff & Notification.
10. Admin Configuration.
11. Analytics & Reporting.
12. Security, Logging & Monitoring.
13. QA & Acceptance Testing.

Với mỗi user story cần có:

- `As a...`
- `I want...`
- `So that...`
- Priority: `Must`, `Should`, `Could`.
- Acceptance criteria theo Given/When/Then.
- Dependencies.
- Estimate tương đối: `S`, `M`, `L`.

Với task cần phân role:

- Backend developer.
- Frontend/admin developer nếu có.
- BA/content.
- Designer/conversation designer.
- Tester/QA.
- DevOps nếu cần.

## Tiêu Chí Chất Lượng

- Viết bằng tiếng Việt.
- Backlog phải bám sát PRD, không tự mở rộng sang phase sau.
- Phân biệt rõ MVP và post-MVP.
- Acceptance criteria phải test được.
- Các task phải đủ nhỏ để team thực thi.
- Luôn giữ nguyên tắc journey-first/need-first.
- Không ưu tiên school database trước flow, lead capture và handoff.

## File Output Gợi Ý

Lưu tài liệu được tạo ra tại:

`/docs/backlog/01-chatbot-mvp-backlog.md`
