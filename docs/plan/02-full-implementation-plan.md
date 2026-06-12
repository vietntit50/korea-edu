# Full Implementation Plan - Facebook Messenger Chatbot MVP

**Vai trò:** Team Lead (`korea-edu-team`)  
**Mục tiêu:** Ship MVP native trong **30 ngày**, demo được từng slice, go-live an toàn về compliance.

Trạng thái: 2026-06-11.

## 1. Tổng quan

| Hạng mục | Nội dung |
|---|---|
| **Sản phẩm** | Chatbot Facebook Messenger tư vấn du học Hàn Quốc (cấp 1) |
| **Timeline** | 30 ngày build + 2–3 ngày UAT/go-live gate |
| **Stack** | Spring Boot, PostgreSQL, Redis (optional), LLM abstraction, Docker |
| **Team tối thiểu** | 1 Backend lead, 1 Backend dev, 1 BA/Content, 1 QA, DevOps part-time |
| **Release goal** | Bot journey-first, FAQ, cost range, eligibility, lead/handoff, config cơ bản |

### Nguyên tắc bất biến (mọi phase)

- Journey-first/need-first, không school-first với user mới.
- LLM không phải source of truth.
- Tone `trung tâm/bạn`.
- SĐT không bắt buộc — chỉ khi callback/dat lịch/handoff/báo giá chính thức.
- Disclaimer bắt buộc cho chi phí, visa, trường, học bổng, việc làm thêm.
- Guardrails: không cam kết visa, không bịa số liệu, không khuyên khai sai hồ sơ.

### Tài liệu nguồn

| Loại | Path |
|---|---|
| PRD | `docs/prd/01-facebook-messenger-chatbot-mvp.md` |
| Build slices | `docs/plan/01-chatbot-mvp-build-slices.md` |
| Backlog | `docs/backlog/01-chatbot-mvp-backlog.md` |
| Tech spec | `docs/tech/01-chatbot-mvp-technical-spec.md` |
| Conversation flows | `docs/design/01-chatbot-conversation-flows.md` |
| Business rules | `docs/business-rules/01-chatbot-mvp-business-rules.md` |
| Decisions | `docs/decisions/01-chatbot-mvp-open-questions.md` |

### Phạm vi

```text
MVP Must (30 ngày)
  Slice 1: Webhook + Greeting/Menu
  Slice 2: Intent Routing + FAQ
  Slice 3: Slot Filling + Profile Store
  Slice 4: General Cost Estimator
  Slice 5: Eligibility Check
  Slice 6: Lead Capture + Human Handoff
  Slice 7: Admin Config + Basic Analytics

Should-have (nếu còn thời gian)
  - School lookup 20-30 trường verified
  - Advisor notification (email/Slack)
  - Analytics dashboard UI

Out of scope
  - Full CRM, full school DB, OCR, payment, mobile app, advisor console
```

### Dependency graph (slices)

```text
Slice 1 → Slice 2 → Slice 3
                      ├→ Slice 4 ─┐
                      ├→ Slice 5 ─┼→ Slice 6
                      └───────────┘
Slice 1..6 → Slice 7
```

---

## 2. Phase 0 — Chuẩn bị (Trước Slice 1, ~2–3 ngày)

**Mục tiêu:** Gỡ blockers, repo sẵn sàng, business gate rõ.

### BA

- [ ] Rà soát FAQ MVP (quy trình, D4/D2, TOPIK, GPA, visa, documents, timeline).
- [ ] Xác nhận copy menu 7 mục + disclaimers (`docs/design/01-chatbot-conversation-flows.md`).
- [ ] Đóng gói cost range để owner phê duyệt (D-003 — hiện **Proposed**).
- [ ] Chốt ngưỡng GPA/gap year tạm cho eligibility (staging).

### Dev

- [ ] Scaffold Spring Boot + Flyway/Liquibase + Docker Compose (PostgreSQL).
- [ ] Cấu trúc module: `webhook`, `orchestrator`, `router`, `llm`, `domain`, `persistence`.
- [ ] Env template: Facebook token, LLM key, DB URL.
- [ ] CI cơ bản: build + unit test.

### QA

- [ ] Test matrix khung Flow A–G (`docs/backlog/01-chatbot-mvp-backlog.md` §8).
- [ ] Webhook fixtures (text, quick reply, duplicate `mid`, invalid signature).
- [ ] Guardrail regression list (visa, giấy tờ giả, illegal work, unverified school).

### Ops / Business (blockers P0)

- [ ] Facebook App + Page access token + webhook URL staging.
- [ ] LLM provider + API key.
- [ ] Business hours / SLA copy (O-002) — default: ngoài giờ → phản hồi giờ làm việc tiếp theo.
- [ ] Kênh notify advisor (email/Slack/DB queue).

**Gate Phase 0:** Repo chạy local, DB migrate OK, Facebook test page kết nối được.

---

## 3. Phase 1–7 — Build theo Slices (Ngày 1–28)

### Slice 1 — Webhook + Greeting/Menu (Ngày 1–4)

**Epic:** E01, E02 (partial), E12 (logging)

| Role | Tasks |
|---|---|
| **BA** | Sign-off greeting + menu copy; payload quick reply (`MENU_PROCESS`, …). |
| **Dev** | `POST /webhooks/facebook`, verify signature, dedup `mid`, `conversation_sessions` + `messages`, Messenger send (text + quick replies), greeting builder, `trace_id`. |
| **QA** | S1-TC1–TC5: verify, invalid sig, new message, duplicate, quick reply parse. |

**Demo:** User gửi "Tư vấn du học Hàn" → menu 7 mục, không hỏi trường.

**DoD:** Webhook staging ổn định; duplicate không reply 2 lần; logs có session/trace.

---

### Slice 2 — Intent Routing + FAQ (Ngày 5–8)

**Epic:** E02, E03, E05, E12

| Role | Tasks |
|---|---|
| **BA** | Seed `faq_items` cho top intents; guardrail copy (visa, false docs, illegal work). |
| **Dev** | `BotOrchestrator` v1, state machine cơ bản, rule router (menu/keywords/handoff), intent enum, `LlmService.classifyIntent`, FAQ service, `intent_logs`, fallback count (FALLBACK_1/2). |
| **QA** | Menu route 7/7; FAQ D4/D2, quy trình; "bảo đậu visa" → guardrail; low confidence → clarification. |

**Demo:** 3 câu: D4/D2, quy trình, bảo đậu visa — route đúng, an toàn.

**DoD:** Quick reply không gọi LLM; intent logs persist; fallback sau 2 UNKNOWN.

---

### Slice 3 — Slot Filling + Profile Store (Ngày 9–12)

**Epic:** E04, E12 (PII masking)

| Role | Tasks |
|---|---|
| **BA** | Slot prompts Flow B/C/E; quick reply budget/pathway/region. |
| **Dev** | `user_profiles`, `slot_values`, `SlotManager`, `LlmService.extractSlots`, merge/conflict rules, PII mask helper. |
| **QA** | Multi-slot extract; conflict GPA; `chưa rõ`; no-phone continue; log không lộ phone/budget. |

**Demo:** "GPA 6.5, tốt nghiệp 2023, ngân sách 280 triệu" → slots lưu, bot hỏi pathway/region.

**DoD:** Profile persist theo PSID; SĐT không bắt buộc.

---

### Slice 4 — General Cost Estimator (Ngày 13–16)

**Epic:** E06, E10 (cost seed)

| Role | Tasks |
|---|---|
| **BA** | Finalize `cost_range_templates` seed; cost disclaimer; budget-fit messaging. |
| **Dev** | `cost_range_templates`, `CostEstimatorService`, `POST /cost/general-estimate`, Flow B orchestration, disclaimer injection, `approved` flag. |
| **QA** | Mọi pathway×region; unknown pathway; budget below range; missing config → no number + handoff offer. |

**Demo:** Flow B end-to-end → range + disclaimer.

**Gate business:** Cost range **approved** hoặc production block cost flow (staging only).

---

### Slice 5 — Eligibility Check (Ngày 17–20)

**Epic:** E07

| Role | Tasks |
|---|---|
| **BA** | Sign-off rules A/B/C/D, LOW/MEDIUM/HIGH thresholds. |
| **Dev** | `EligibilityCheckerService`, `eligibility_assessments`, `POST /eligibility/check`, Flow C orchestration, visa disclaimer. |
| **QA** | Score A/B/C/D matrix; HIGH → handoff offer; không có wording "chắc chắn đậu visa". |

**Demo:** Flow C → score B, risk MEDIUM, missing info, CTA handoff.

**Gate business:** GPA/gap year thresholds approved trước go-live.

---

### Slice 6 — Lead Capture + Human Handoff (Ngày 21–24)

**Epic:** E08, E09

| Role | Tasks |
|---|---|
| **BA** | Handoff trigger matrix; handoff copy (Messenger vs SĐT); business hours message. |
| **Dev** | `LeadService` interface + DB impl, `leads`, `handoff_requests`, `advisor_notifications`, phone validation, summary builder, `POST /leads`, `PATCH /leads/{id}`, `POST /handoff`, Flow G. |
| **QA** | No-phone handoff by PSID; phone lead create/update; duplicate; notification fail non-blocking; official quote → handoff. |

**Demo:** User risk HIGH hoặc hỏi báo giá chính thức → handoff với summary/profile.

**DoD:** Advisor payload đủ: PSID, need, slots, score, risk, summary.

---

### Slice 7 — Admin Config + Analytics (Ngày 25–28)

**Epic:** E10, E11, E12

| Role | Tasks |
|---|---|
| **BA** | Config key list; audit requirements; analytics metrics definition. |
| **Dev** | `bot_configurations`, `audit_logs`, `GET/PUT /admin/bot-config`, wire FAQ/cost/disclaimer to config, `GET /analytics/chatbot`, RBAC cơ bản. |
| **QA** | Config update + audit; unapproved cost not in prod; analytics counts; unauthorized admin → 403. |

**Demo:** Admin đổi fallback message → bot dùng copy mới; owner xem metrics test.

**Should-have (buffer):** Admin UI đơn giản nếu còn thời gian; nếu không → DB/API workflow.

---

## 4. Phase 8 — Hardening & Go-live (Ngày 29–32)

| Role | Tasks |
|---|---|
| **QA** | UAT Flow A–G scripted; guardrail regression full; security checklist (webhook, PII, rate limit). |
| **Dev** | Fix P0/P1; monitoring/alerts (webhook error, LLM timeout, lead save fail); production env + secrets. |
| **BA** | UAT sign-off copy/behavior; chốt SLA message (O-002, O-005). |
| **Business** | Approve cost range (D-003); PII retention (O-007); go/no-go. |

### Go-live checklist (bắt buộc)

- [ ] Flow A–G pass UAT.
- [ ] Guardrails P0 pass (visa, unverified school, fake docs).
- [ ] Cost range approved **hoặc** cost flow tắt production.
- [ ] Facebook production webhook + page token.
- [ ] P0/P1 closed hoặc owner accept.
- [ ] Monitoring + rollback plan.
- [ ] Advisor queue/notification hoạt động.

---

## 5. Ma trận Epic × Slice × Team

| Epic | Slice chính | BA | Dev | QA |
|---|---|---|---|---|
| E01 Messenger | 1 | Copy menu | Webhook, send API | Contract tests |
| E02 Orchestrator | 1–2, mở rộng 3–6 | States, flows | Orchestrator, state machine | Flow state tests |
| E03 Intent/LLM | 2–3 | Intent list, eval set | Router, LLM abstraction | Classification eval |
| E04 Slots/Profile | 3 | Slot prompts | SlotManager, profile | Extraction tests |
| E05 FAQ/KB | 2, 7 | FAQ content | FAQ service, config | FAQ regression |
| E06 Cost | 4, 7 | Range approval | CostEstimator | Range/disclaimer tests |
| E07 Eligibility | 5 | Rule thresholds | EligibilityChecker | Score/risk regression |
| E08 Lead | 6 | Lead triggers | LeadService | Lead/no-phone tests |
| E09 Handoff | 6 | Handoff copy, SLA | HandoffService, notify | Handoff triggers |
| E10 Admin | 7 | Config keys | Config API, audit | Config/analytics tests |
| E11 Analytics | 7 | Metrics definition | Analytics API | Count validation |
| E12 Security | 1–7 | PII policy | Auth, mask, rate limit | Security QA |
| E13 QA | 8 | UAT scripts | Fix bugs | Full regression |

---

## 6. Milestones & Demos

| Tuần | Milestone | Demo |
|---|---|---|
| **W1** (D1–7) | Pipeline sống + bot trả lời cơ bản | Menu + FAQ D4/D2 + guardrail visa |
| **W2** (D8–14) | Bot nhớ profile + cost range | Flow B với disclaimer |
| **W3** (D15–21) | Eligibility + lead loop | Flow C + handoff no-phone |
| **W4** (D22–28) | Vận hành được | Admin config + analytics |
| **W5** (D29–32) | Go-live ready | UAT full + owner sign-off |

---

## 7. Phụ thuộc & Rủi ro

| Blocker | Ảnh hưởng | Mitigation | Owner |
|---|---|---|---|
| Facebook credentials chậm | Slice 1 delay | Fixtures + `POST /bot/messages/process` local | Ops |
| LLM key chậm | Slice 2–3 delay | Rule-only path trước | Tech |
| Cost range chưa approve | Không go-live cost | Staging flag `approved=false` | Business |
| Không có notify channel | Handoff chậm | DB queue + email default | Ops |
| LLM misclassify | UX kém | Eval set, confidence threshold, fallback | Dev + QA |
| Scope creep school DB | Delay MVP | Should-have sau Slice 7 | Team Lead |

---

## 8. Should-have (sau Must, nếu còn buffer)

Ưu tiên sau Slice 7, trước go-live nếu có thời gian:

1. **School lookup** 20–30 trường verified (Flow F) — cần BA cung cấp data.
2. **Advisor notification** Slack/email thay vì chỉ DB queue.
3. **Conversation summary** LLM-enhanced (structured slots vẫn là primary).
4. **Analytics dashboard** UI (API-only đủ cho MVP).

---

## 9. Post-MVP (không nằm trong 30 ngày)

- Full CRM / advisor console.
- School database đầy đủ + cost calculator theo trường.
- Zalo channel adapter.
- Book consultation calendar.
- Parent dashboard, OCR, payment, mobile app.

---

## 10. Lệnh gọi agent theo phase

| Phase | Gợi ý |
|---|---|
| Phase 0 | `@korea-edu-ba` chuẩn bị FAQ + cost package |
| Slice 1 | `@korea-edu-dev` implement webhook + menu |
| Slice 2–3 | `@korea-edu-dev` orchestrator + slots; `@korea-edu-qa` fixtures |
| Slice 4–5 | `@korea-edu-ba` approve rules; `@korea-edu-qa` regression |
| Slice 6 | `@korea-edu-dev` lead/handoff; `@korea-edu-ba` SLA copy |
| Go-live | `@korea-edu-qa` UAT checklist; `@korea-edu-team` go/no-go review |

---

## 11. Definition of Done — Toàn dự án

- [ ] MVP Must scope trong `docs/backlog/01-chatbot-mvp-backlog.md` §3.1 đạt.
- [ ] Flow A–G pass acceptance criteria trong `docs/design/01-chatbot-conversation-flows.md`.
- [ ] Business rules trong `docs/business-rules/01-chatbot-mvp-business-rules.md` không vi phạm guardrail.
- [ ] API/schema khớp `docs/tech/01-chatbot-mvp-technical-spec.md`.
- [ ] Cost/eligibility thresholds approved hoặc gated.
- [ ] BA + QA + Business owner sign-off UAT.
