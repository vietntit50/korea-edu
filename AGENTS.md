# Korea Edu — Agent Team

Team agents cho dự án **Facebook Messenger Chatbot MVP** (Korea Study Advisor Platform).

## Roster

| Agent | Skill | Vai trò chính |
|---|---|---|
| **BA** | `korea-edu-ba` | Phân tích nghiệp vụ, backlog, conversation flows, business rules, nội dung bot |
| **Dev** | `korea-edu-dev` | Implement backend Spring Boot, API, orchestrator, integrations theo tech spec |
| **QA** | `korea-edu-qa` | Test plan, acceptance criteria, regression guardrails, UAT scripts |
| **Team Lead** | `korea-edu-team` | Điều phối BA → Dev → QA, chọn agent phù hợp, theo dõi slice MVP |

## Cách dùng trong Cursor

### Gọi một agent cụ thể

Trong chat, nhắc role hoặc skill:

```
@korea-edu-ba review business rules cho flow chi phí
@korea-edu-dev implement Slice 1 webhook + greeting menu
@korea-edu-qa viết test cases cho Flow C eligibility
@korea-edu-team lên kế hoạch hoàn thành Slice 4 trong tuần này
```

### Luồng làm việc khuyến nghị

```text
BA (làm rõ yêu cầu / copy / rules)
  → Dev (implement theo spec + acceptance criteria)
    → QA (verify + regression guardrails)
      → BA sign-off copy/behavior (nếu user-facing)
```

### Build slices (thứ tự MVP)

Theo `docs/plan/01-chatbot-mvp-build-slices.md`:

1. Webhook + greeting/menu
2. Intent routing + FAQ
3. Slot filling + profile
4. Cost estimator
5. Eligibility check
6. Lead + handoff
7. Admin config + analytics

## Tài liệu nguồn (source of truth)

| Loại | Path |
|---|---|
| PRD | `docs/prd/01-facebook-messenger-chatbot-mvp.md` |
| Quyết định | `docs/decisions/01-chatbot-mvp-open-questions.md` |
| Conversation flows | `docs/design/01-chatbot-conversation-flows.md` |
| Business rules | `docs/business-rules/01-chatbot-mvp-business-rules.md` |
| Tech spec | `docs/tech/01-chatbot-mvp-technical-spec.md` |
| Backlog | `docs/backlog/01-chatbot-mvp-backlog.md` |
| Build plan | `docs/plan/01-chatbot-mvp-build-slices.md` |

## Nguyên tắc bất biến (mọi agent phải tuân)

1. **Journey-first** — không school-first với user mới
2. **LLM không phải source of truth** — chi phí/trường/visa từ config/DB verified
3. **Tone** — `trung tâm/bạn`; SĐT không bắt buộc
4. **Guardrails** — không cam kết visa, không bịa số liệu, không khuyên khai sai hồ sơ
5. **MVP 30 ngày** — không mở rộng full CRM, full school DB, OCR, payment

## Handoff giữa agents

| Từ | Sang | Khi nào |
|---|---|---|
| BA | Dev | AC rõ, copy/rules đã có hoặc referenced trong docs |
| Dev | QA | Code/API sẵn sàng test; ghi rõ slice + files thay đổi |
| QA | Dev | Bug hoặc AC fail — kèm steps reproduce |
| QA | BA | Copy/behavior sai so với flows — không phải bug code |
| Bất kỳ | Team Lead | Scope mơ hồ, conflict giữa docs, ưu tiên slice |
