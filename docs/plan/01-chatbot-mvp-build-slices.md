# MVP Build Slices Plan - Facebook Messenger Chatbot MVP

## 1. Build Strategy

Build MVP theo 7 vertical slices, moi slice co the demo va test doc lap o muc hop ly. Thu tu uu tien:

1. Lam duong ong Messenger + menu journey-first truoc.
2. Them routing/FAQ de bot co gia tri ngay ca khi LLM chua hoan hao.
3. Them slot/profile de ca nhan hoa flow.
4. Them cost estimator va eligibility checker bang rule/config.
5. Them lead/handoff de dong vong kinh doanh.
6. Them admin config/analytics de van hanh va cai tien.

Nguyen tac:

- Journey-first/need-first, khong school-first.
- LLM la ho tro, khong phai source of truth.
- Chi phi lay tu `CostRangeTemplate`, co disclaimer.
- SDT optional; phone chi required khi user muon callback.
- CRM deferred; MVP dung `LeadService` abstraction + database storage.

## 2. MVP Release Goal

Trong 30 ngay, release chatbot native co the:

- Nhan/gui message Facebook Messenger.
- Hien global menu 7 muc.
- Tra loi FAQ co ban.
- Hieu intent tu quick reply va message tu nhien.
- Thu thap profile/slots.
- Uoc tinh cost range theo pathway/khu vuc.
- Check eligibility so bo, lead score A/B/C/D va risk LOW/MEDIUM/HIGH.
- Tao lead partial/phone lead.
- Tao handoff request va notify advisor/default queue.
- Co config va analytics co ban.

## 3. Assumptions

- Backend: Spring Boot.
- DB: PostgreSQL.
- Redis optional cho rate limit/session cache.
- Admin UI co the deferred; config API + DB seed du cho MVP neu team nho.
- Facebook app/page token co truoc Slice 1 staging.
- LLM provider/API key co truoc Slice 2/3.
- Cost range proposed da co, can final approval truoc go-live.
- Notification channel chua chot: default la DB queue + email/internal notification neu co.
- PII retention final can chot truoc production.

## 4. Recommended Build Order

| Order | Slice | Demo Independent | Depends On | Main Value |
|---:|---|---|---|---|
| 1 | Messenger Webhook + Greeting/Menu | Yes | Facebook test app/page | User inbox bot va thay menu journey-first |
| 2 | Intent Routing + FAQ | Yes | Slice 1 | Bot tra loi cau hoi co ban |
| 3 | Slot Filling + Profile Store | Yes | Slice 2 | Bot nho thong tin user |
| 4 | General Cost Estimator | Yes | Slice 3 | Bot tra range chi phi co disclaimer |
| 5 | Eligibility Check | Yes | Slice 3 | Bot check ho so so bo |
| 6 | Lead Capture + Human Handoff | Yes | Slice 3, partial Slice 4/5 | Dong vong lead/advisor |
| 7 | Admin Config + Basic Analytics | Partly | Slice 1-6 | Van hanh/cai tien MVP |

## 5. Slice 1: Messenger Webhook + Greeting/Menu

### Objective

Nhan message Facebook Messenger, tao session, gui greeting va global menu journey-first.

### User Value

User inbox fanpage va ngay lap tuc biet bot co the ho tro quy trinh, chi phi, check ho so, khu vuc, D4/D2 va gap tu van vien.

### Scope

- Webhook verify/receive.
- Parse text/quick reply/postback co ban.
- De-duplicate message by `mid`.
- Persist session/message.
- Send text + quick replies.
- Greeting/menu 7 muc voi tone `trung tam/ban`.
- Structured logs co `trace_id`.

### Out of Scope

- LLM.
- FAQ routing phuc tap.
- Cost/eligibility/lead.
- Admin UI.

### Functional Requirements

- Webhook reject invalid verification/signature.
- Duplicate inbound message khong tao duplicate response.
- New session hien menu 7 muc.
- User click menu payload duoc log, co the reply placeholder theo selected intent.

### Technical Tasks

| Task ID | Task | Owner Role | Estimate | Dependencies |
|---|---|---|---|---|
| S1-T1 | Create Spring Boot project/module structure if not exists | Backend developer | M | Repo setup |
| S1-T2 | Implement `POST /webhooks/facebook` and verification endpoint if needed | Backend developer | M | Facebook credentials |
| S1-T3 | Implement Facebook signature/token verification | Backend developer | M | S1-T2 |
| S1-T4 | Implement Messenger event parser and message de-duplication | Backend developer | M | S1-T2 |
| S1-T5 | Implement Messenger send client for text + quick replies | Backend developer | M | Page access token |
| S1-T6 | Create tables: `conversation_sessions`, `messages` | Backend developer | M | DB migration tool |
| S1-T7 | Implement greeting/menu response builder | Backend developer/Designer | S | Menu copy |
| S1-T8 | Add structured logging and trace id | Backend developer | S | S1-T2 |
| S1-T9 | Create webhook fixtures/tests | QA/Backend | M | S1-T2 |

### APIs Involved

- `POST /webhooks/facebook`
- Messenger Send API
- Optional internal `POST /bot/messages/process` stub

### Database Changes

- `conversation_sessions`
- `messages`

### Admin/Config Changes

- Seed `menu.items` config or hardcoded temporary menu copy with TODO to move to config in Slice 7.

### Acceptance Criteria

Given Facebook sends valid text message  
When webhook receives it  
Then backend verifies, persists inbound message, creates session and sends greeting menu.

Given Facebook retries same `mid`  
When webhook receives duplicate  
Then backend returns success and does not send duplicate menu.

Given user sees greeting  
Then menu has 7 journey-first options and does not ask school first.

### Test Cases

| Test ID | Scenario | Steps | Expected Result |
|---|---|---|---|
| S1-TC1 | Webhook verification success | Send valid verify request | Returns challenge/success |
| S1-TC2 | Invalid webhook signature | Send invalid request | Returns 401/ignored safely |
| S1-TC3 | New text message | Send fixture with text | Session + message created, menu response sent |
| S1-TC4 | Duplicate message | Send same `mid` twice | Only one outbound message |
| S1-TC5 | Quick reply payload parse | Send payload fixture | Payload parsed and logged |

### Demo Scenario

User sends: "Tu van du hoc Han"  
Bot replies with greeting and 7 menu options.

### Risks

- Facebook app setup delayed.
- Messenger API rate/permission issues.
- Local testing without public webhook URL.

### Done When

- Webhook and send API work in staging/test page.
- Menu copy approved.
- Logs show trace/session/message IDs.
- Tests for webhook fixtures pass.

## 6. Slice 2: Intent Routing + FAQ

### Objective

Route menu/quick replies and answer fixed FAQ. Add LLM classification for natural text where needed, with fallback.

### User Value

User co the hoi cac cau co ban ve quy trinh, D4/D2, TOPIK, GPA, visa, documents va timeline ma khong can tu van vien ngay.

### Scope

- Rule-based router.
- Supported intent enum.
- FAQ/KB table.
- LLM intent classification abstraction.
- Low-confidence fallback.
- Guardrail keywords co ban: visa guarantee, fake documents, illegal work.

### Out of Scope

- Slot extraction.
- Cost estimate numeric range.
- Eligibility scoring.
- Lead/handoff full creation.

### Functional Requirements

- Quick reply/menu route khong goi LLM.
- Free text route qua LLM neu rule khong match.
- FAQ answer dung approved content.
- Unknown intent fallback lan 1/2.
- Visa guarantee/fake document/illegal work route sang safe response.

### Technical Tasks

| Task ID | Task | Owner Role | Estimate | Dependencies |
|---|---|---|---|---|
| S2-T1 | Define intent enum and router precedence | Backend/BA | M | Slice 1 |
| S2-T2 | Implement rule-based router for menu/keywords | Backend developer | M | S2-T1 |
| S2-T3 | Create `faq_items`, `intent_logs` tables | Backend developer | M | DB |
| S2-T4 | Draft and seed MVP FAQ content | BA/Content | L | PRD |
| S2-T5 | Implement FAQ lookup service | Backend developer | M | S2-T3/S2-T4 |
| S2-T6 | Implement `LlmService.classifyIntent` abstraction | Backend developer | L | LLM key |
| S2-T7 | Add JSON schema validation and confidence thresholds | Backend developer | M | S2-T6 |
| S2-T8 | Implement fallback count/state | Backend developer | S | Slice 1 |
| S2-T9 | Create intent/FAQ tests | QA/BA | M | S2-T1-S2-T8 |

### APIs Involved

- `POST /bot/messages/process`
- `POST /llm/intent-classify`
- Messenger Send API

### Database Changes

- `faq_items`
- `intent_logs`
- Add `current_intent`, `fallback_count` handling in `conversation_sessions` if not already.

### Admin/Config Changes

- Seed FAQ content and fallback messages.
- Seed guardrail keyword lists.

### Acceptance Criteria

Given user clicks "D4-1 va D2 khac nhau the nao"  
When bot processes payload  
Then bot returns FAQ explanation without LLM call.

Given user types "di han can giay to gi"  
When no exact rule matches  
Then LLM classifies `ASK_DOCUMENTS` and FAQ answer is returned.

Given user asks "co bao do visa khong"  
Then bot refuses guarantee, shows visa disclaimer and offers risk check/handoff.

### Test Cases

| Test ID | Scenario | Steps | Expected Result |
|---|---|---|---|
| S2-TC1 | Menu route | Click each menu payload | Correct intent logged |
| S2-TC2 | FAQ exact match | Ask process/D4/TOPIK question | Approved answer returned |
| S2-TC3 | Free-text classification | Ask typo/mixed wording | Correct intent or fallback |
| S2-TC4 | Low confidence | Send ambiguous text | Clarification options shown |
| S2-TC5 | Visa guarantee guardrail | Ask "bao do visa?" | No guarantee, disclaimer shown |

### Demo Scenario

User asks:

1. "D4 voi D2 khac nhau the nao?"
2. "Bao do visa khong?"
3. "Quy trinh di Han nhu the nao?"

Bot routes correctly and shows safe FAQ answers.

### Risks

- FAQ content not approved in time.
- LLM unstable/slow.
- Too much reliance on LLM before eval set exists.

### Done When

- Top FAQ intents route correctly.
- Guardrail smoke tests pass.
- Fallback after 2 unknowns works.
- Intent logs are persisted.

## 7. Slice 3: Slot Filling + Profile Store

### Objective

Extract and store user profile slots from natural answers; ask missing slots without forcing SDT.

### User Value

Bot remembers GPA, nam tot nghiep, ngan sach, pathway/khu vuc va khong bat user lap lai.

### Scope

- `user_profiles`, `slot_values`.
- Slot schema and priority by intent.
- LLM slot extraction abstraction.
- Slot merge/confidence logic.
- Basic profile update from quick replies/free text.
- Partial profile by PSID.

### Out of Scope

- Final eligibility scoring.
- Lead creation as separate business entity.
- Admin slot config UI.

### Functional Requirements

- Extract GPA, graduation year, budget, Korean level, TOPIK, pathway, region, school name.
- Store source/confidence.
- Ask confirmation for low-confidence or conflicting slots.
- User can continue with `UNKNOWN` slot.
- No phone required.

### Technical Tasks

| Task ID | Task | Owner Role | Estimate | Dependencies |
|---|---|---|---|---|
| S3-T1 | Define slot schema and expected slots per intent | BA/Backend | M | Slice 2 |
| S3-T2 | Create `user_profiles`, `slot_values` tables | Backend developer | M | DB |
| S3-T3 | Implement `SlotManager` merge/conflict rules | Backend developer | L | S3-T1 |
| S3-T4 | Implement `LlmService.extractSlots` | Backend developer | L | Slice 2 LLM |
| S3-T5 | Implement slot prompts for cost/eligibility/region flows | Backend/Designer | M | Conversation flows |
| S3-T6 | Add PII masking for phone/budget/raw text logs | Backend developer | M | Security logging |
| S3-T7 | Create slot extraction eval/test set | QA/BA | M | S3-T1 |

### APIs Involved

- `POST /bot/messages/process`
- `POST /llm/slot-extract`

### Database Changes

- `user_profiles`
- `slot_values`
- Add profile/session relationship.

### Admin/Config Changes

- Seed slot prompt templates if configurable.

### Acceptance Criteria

Given user says "Tot nghiep 2023, GPA 6.5, ngan sach 280 trieu"  
When bot processes the message  
Then `graduation_year=2023`, `gpa=6.5`, `budget_vnd=280000000` are saved with confidence/source.

Given user declines SDT  
Then bot continues and saves profile by PSID.

Given existing GPA differs from new message  
Then bot asks confirmation before overwrite.

### Test Cases

| Test ID | Scenario | Steps | Expected Result |
|---|---|---|---|
| S3-TC1 | Multi-slot extraction | Send combined profile text | All slots saved |
| S3-TC2 | Budget quick reply | Click `200-300 trieu` | budget range/value stored |
| S3-TC3 | Unknown slot | User says "chua ro" | Slot saved as UNKNOWN and flow continues |
| S3-TC4 | Conflict | Existing GPA 7.0, user says GPA 6.5 | Confirmation requested |
| S3-TC5 | PII masking | Log phone/budget message | Logs mask sensitive fields |

### Demo Scenario

User: "GPA 6.5, tot nghiep 2023, chua TOPIK, ngan sach 280 trieu"  
Bot confirms/checks missing pathway and region; backend shows slots saved.

### Risks

- LLM extracts wrong numeric value.
- Slot overwrite corrupts profile.
- PII appears in logs.

### Done When

- Slot extraction/merge tests pass.
- Profile persists across messages.
- Phone remains optional.
- Sensitive fields masked in logs.

## 8. Slice 4: General Cost Estimator

### Objective

Tra range chi phi uoc tinh theo pathway/khu vuc/config va budget fit, kem disclaimer bat buoc.

### User Value

User va phu huynh co duoc muc ngan sach tham khao de quyet dinh buoc tiep theo.

### Scope

- `cost_range_templates`.
- `CostEstimatorService`.
- `POST /cost/general-estimate`.
- Cost flow B messages.
- Budget fit: below/within/above range.
- Cost disclaimer.

### Out of Scope

- School-level cost calculator.
- Official quote.
- Fees by exact school if unverified.

### Functional Requirements

- If pathway+region known, return configured range.
- If pathway/region unknown, ask missing slot or show broad range.
- If user asks official fee, offer handoff.
- LLM never calculates numbers.
- Production uses approved cost config.

### Technical Tasks

| Task ID | Task | Owner Role | Estimate | Dependencies |
|---|---|---|---|---|
| S4-T1 | Create `cost_range_templates` table | Backend developer | M | DB |
| S4-T2 | Seed proposed cost ranges from decision log | BA/Backend | S | Business approval pending |
| S4-T3 | Implement `CostEstimatorService` | Backend developer | M | S4-T1 |
| S4-T4 | Implement `POST /cost/general-estimate` | Backend developer | M | S4-T3 |
| S4-T5 | Integrate cost flow into orchestrator | Backend developer | M | Slice 3 |
| S4-T6 | Add cost disclaimer injection | Backend developer | S | Business rules |
| S4-T7 | Create cost tests for all pathway/region combos | QA | M | S4-T3 |

### APIs Involved

- `POST /cost/general-estimate`
- `POST /bot/messages/process`

### Database Changes

- `cost_range_templates`
- Optional `bot_configurations` if config service starts before Slice 7.

### Admin/Config Changes

- Cost range seed data.
- `approved` flag.
- Cost disclaimer text.

### Acceptance Criteria

Given user selects D4-1 and near Seoul  
When cost estimator runs  
Then bot returns 240-340 trieu VND range from config and cost disclaimer.

Given user does not know pathway  
Then bot asks D4-1/D2/chua ro and does not invent exact number.

Given cost config missing  
Then bot does not show number and offers advisor confirmation.

### Test Cases

| Test ID | Scenario | Steps | Expected Result |
|---|---|---|---|
| S4-TC1 | Known pathway/region | D4-1 + NEAR_SEOUL | Correct range, config version |
| S4-TC2 | Unknown pathway | Ask general cost | Missing pathway prompt |
| S4-TC3 | Budget below range | 200M + Seoul/top | Budget below message |
| S4-TC4 | Missing config | Disable config | No number, handoff offer |
| S4-TC5 | Disclaimer | Any cost response | Disclaimer included |

### Demo Scenario

User: "Di Han het bao nhieu?"  
Bot asks pathway, region, budget, then returns range and disclaimer.

### Risks

- Cost range not approved before go-live.
- User interprets estimate as official quote.
- Config missing/incorrect.

### Done When

- All configured cost groups return expected ranges.
- Disclaimer always appears.
- Official quote request routes to handoff.
- Business owner has approved or staging-only flag remains.

## 9. Slice 5: Eligibility Check

### Objective

Check ho so so bo bang rule-based checker, output lead score A/B/C/D va risk LOW/MEDIUM/HIGH/UNKNOWN.

### User Value

User biet ho so co kha nang phu hop so bo, can bo sung gi va co nen gap tu van vien khong.

### Scope

- `eligibility_assessments`.
- `EligibilityCheckerService`.
- `POST /eligibility/check`.
- Lead scoring/risk rules MVP.
- Eligibility response copy + visa disclaimer.

### Out of Scope

- Visa decision.
- Official admission decision.
- OCR/document upload.

### Functional Requirements

- Evaluate GPA, gap year, budget, Korean/TOPIK, pathway, region, visa history if available.
- Return score/risk/missing info/next action.
- No pass/fail wording.
- Risk high recommends handoff.
- Missing core slots asks follow-up.

### Technical Tasks

| Task ID | Task | Owner Role | Estimate | Dependencies |
|---|---|---|---|---|
| S5-T1 | Finalize MVP risk/scoring thresholds | BA/Product owner | M | Business rules |
| S5-T2 | Create `eligibility_assessments` table | Backend developer | M | DB |
| S5-T3 | Implement `EligibilityCheckerService` | Backend developer | L | S5-T1 |
| S5-T4 | Implement `POST /eligibility/check` | Backend developer | M | S5-T3 |
| S5-T5 | Integrate eligibility flow into orchestrator | Backend developer | M | Slice 3 |
| S5-T6 | Add visa/eligibility disclaimer handling | Backend developer | S | Business rules |
| S5-T7 | Create rule regression tests A/B/C/D and LOW/MEDIUM/HIGH | QA/BA | M | S5-T3 |

### APIs Involved

- `POST /eligibility/check`
- `POST /bot/messages/process`

### Database Changes

- `eligibility_assessments`
- Update `user_profiles`/`leads` with score/risk later if lead exists.

### Admin/Config Changes

- Threshold config can be code constants in MVP, with `rules_version`.
- Mark thresholds as business-approved before go-live.

### Acceptance Criteria

Given user provides graduation year, GPA and budget  
When eligibility checker runs  
Then response includes `lead_score`, `risk_level`, `missing_information` and no visa guarantee.

Given risk high  
Then bot recommends human handoff.

Given missing GPA  
Then bot asks GPA/hoc luc instead of scoring final.

### Test Cases

| Test ID | Scenario | Steps | Expected Result |
|---|---|---|---|
| S5-TC1 | Score A | Clear profile, good budget | A/LOW |
| S5-TC2 | Score B | Medium GPA, missing region | B/MEDIUM |
| S5-TC3 | Score C | Low GPA/long gap | C/HIGH |
| S5-TC4 | Unknown | Missing core slots | UNKNOWN/missing info |
| S5-TC5 | Visa wording | Ask "co dau visa khong" | No guarantee, disclaimer |

### Demo Scenario

User: "GPA 6.5, tot nghiep 2023, chua TOPIK, ngan sach 280 trieu"  
Bot asks missing pathway/region and returns preliminary score/risk.

### Risks

- Business thresholds not approved.
- User treats score as official result.
- Risk rules too blunt.

### Done When

- Rule tests pass.
- Copy avoids "chac chan".
- High-risk cases offer handoff.
- Assessment snapshot saved.

## 10. Slice 6: Lead Capture + Human Handoff

### Objective

Tao/update lead va handoff request cho tu van vien, khong bat buoc SDT neu user chi muon tiep tuc Messenger.

### User Value

User co the duoc tu van vien tiep tuc ho tro khi can; tu van vien nhan summary/profile thay vi hoi lai tu dau.

### Scope

- `leads`, `handoff_requests`, `advisor_notifications`.
- `LeadService` abstraction + DB implementation.
- `HandoffService`.
- `POST /leads`, `PATCH /leads/{id}`, `POST /handoff`.
- Phone detection/validation.
- Conversation summary.
- Notification adapter default.

### Out of Scope

- Full CRM pipeline.
- Full advisor console.
- Calendar booking.

### Functional Requirements

- Valid phone creates/updates lead.
- No phone still allows Messenger handoff by PSID.
- Handoff payload includes profile slots, last question, score/risk, summary.
- Notification failure does not block user response.
- Duplicate lead handling by phone or PSID.

### Technical Tasks

| Task ID | Task | Owner Role | Estimate | Dependencies |
|---|---|---|---|---|
| S6-T1 | Create `leads`, `handoff_requests`, `advisor_notifications` tables | Backend developer | M | DB |
| S6-T2 | Implement phone normalization/validation | Backend developer | S | Slice 3 |
| S6-T3 | Implement `LeadService` interface + DB implementation | Backend developer | M | S6-T1 |
| S6-T4 | Implement lead APIs | Backend developer | M | S6-T3 |
| S6-T5 | Implement `HandoffService` and handoff API | Backend developer | M | S6-T1 |
| S6-T6 | Implement conversation summary builder | Backend/LLM | M | Slice 2/3 |
| S6-T7 | Implement notification adapter | Backend/DevOps | M | Channel decision |
| S6-T8 | Integrate handoff rules into orchestrator | Backend developer | M | Slice 4/5 |
| S6-T9 | Create lead/handoff tests | QA | M | S6-T3-S6-T8 |

### APIs Involved

- `POST /leads`
- `PATCH /leads/{id}`
- `POST /handoff`
- `POST /bot/messages/process`

### Database Changes

- `leads`
- `handoff_requests`
- `advisor_notifications`
- Add lead/profile references where needed.

### Admin/Config Changes

- Handoff triggers.
- Business hours.
- Advisor assignment default.
- Notification channel config.

### Acceptance Criteria

Given user asks for advisor without phone  
When handoff is created  
Then `HandoffRequest` is created by PSID and bot does not require SDT.

Given user provides valid phone for callback  
Then lead is created/updated and handoff is created.

Given notification fails  
Then handoff remains saved and failure is logged/alerted.

### Test Cases

| Test ID | Scenario | Steps | Expected Result |
|---|---|---|---|
| S6-TC1 | Messenger handoff no phone | Click `Tiep tuc Messenger` | Handoff by PSID |
| S6-TC2 | Phone callback | Send valid phone | Lead + handoff |
| S6-TC3 | Invalid phone | Send bad phone | Ask retry or Messenger option |
| S6-TC4 | Duplicate phone | Send existing phone | Lead updated, no duplicate |
| S6-TC5 | Risk handoff | Eligibility high risk | Handoff recommended |
| S6-TC6 | Notification failure | Simulate adapter error | Handoff saved, error logged |

### Demo Scenario

User asks official cost or risk case. Bot offers Messenger/phone handoff. User chooses phone. Lead and handoff appear in DB/default advisor queue.

### Risks

- No chosen advisor notification channel.
- CRM future integration assumptions leak into MVP.
- Phone optional behavior accidentally becomes required.

### Done When

- Lead and handoff APIs work.
- No-phone handoff works.
- Advisor payload includes summary/profile/risk.
- Notification failure is non-blocking.

## 11. Slice 7: Admin Config + Basic Analytics

### Objective

Cho phep cau hinh FAQ/cost/disclaimer/rules co ban va xem metrics MVP.

### User Value

Admin/owner co the dieu chinh noi dung/range/disclaimer va do chatbot tao bao nhieu conversation/lead/handoff.

### Scope

- `bot_configurations`, `audit_logs`.
- Config APIs.
- Basic analytics API.
- Optional simple admin UI.
- Metrics aggregation from existing tables.

### Out of Scope

- Full advisor console.
- Advanced BI dashboard.
- Complex approval workflow.

### Functional Requirements

- Admin can get/update config with version and audit.
- Cost config has approved flag.
- Analytics returns total conversations, leads, handoffs, top intents, fallback rate, lead score distribution.
- PII not exposed in analytics.

### Technical Tasks

| Task ID | Task | Owner Role | Estimate | Dependencies |
|---|---|---|---|---|
| S7-T1 | Create `bot_configurations`, `audit_logs` tables | Backend developer | M | DB |
| S7-T2 | Implement config schema registry/validation | Backend developer | M | Config keys |
| S7-T3 | Implement admin config APIs | Backend developer | M | S7-T1 |
| S7-T4 | Wire FAQ/cost/disclaimer reads to config service | Backend developer | M | Slices 2/4 |
| S7-T5 | Implement `GET /analytics/chatbot` | Backend developer | M | Prior slices |
| S7-T6 | Add RBAC/basic admin auth | Backend/DevOps | M | Auth decision |
| S7-T7 | Optional simple admin UI for config/analytics | Frontend developer | L | S7-T3/S7-T5 |
| S7-T8 | Add audit tests and analytics tests | QA | M | S7-T3/S7-T5 |

### APIs Involved

- `GET /admin/bot-config`
- `PUT /admin/bot-config`
- `GET /analytics/chatbot`

### Database Changes

- `bot_configurations`
- `audit_logs`

### Admin/Config Changes

- Move seeded FAQ/cost/disclaimer/fallback to config.
- Admin roles.

### Acceptance Criteria

Given admin updates fallback message  
When bot later hits fallback  
Then active config value is used and audit log is created.

Given admin updates cost range without approval  
Then production cost estimator does not use unapproved value.

Given owner requests analytics  
Then API returns conversation/lead/handoff/top intent/fallback metrics without PII.

### Test Cases

| Test ID | Scenario | Steps | Expected Result |
|---|---|---|---|
| S7-TC1 | Read config | GET config | Active values returned |
| S7-TC2 | Update config | PUT fallback message | New version + audit log |
| S7-TC3 | Invalid config | PUT invalid JSON | Validation error |
| S7-TC4 | Cost approval | Update unapproved cost | Not used in production |
| S7-TC5 | Analytics | Seed events/leads | Correct counts |
| S7-TC6 | Unauthorized admin | Call without role | 401/403 |

### Demo Scenario

Admin updates fallback copy and cost disclaimer, then runs a fallback chat and sees updated copy. Owner views analytics for test date range.

### Risks

- Admin UI expands scope.
- Config changes break bot behavior.
- Analytics queries slow if traffic grows.

### Done When

- Config APIs and audit work.
- Critical config reads are wired into bot.
- Analytics API returns MVP metrics.
- PII is not exposed.

## 12. Cross-Slice Dependencies

| Dependency | Blocks | Mitigation |
|---|---|---|
| Facebook credentials | Slice 1 staging | Use fixture/internal process API locally |
| LLM provider key | Slice 2/3 | Rule-based fallback and mock LLM |
| DB migration setup | All slices | Establish in Slice 1 |
| Cost range approval | Slice 4 production | Use staging-only proposed values until approved |
| Eligibility threshold approval | Slice 5 production | Mark as assumptions, gate go-live |
| Notification channel | Slice 6 | Default DB queue/email |
| Admin auth decision | Slice 7 | Use basic RBAC/internal auth for MVP |
| PII retention policy | Production go-live | Temporary default plus owner approval |

## 13. Technical Risks

| Risk | Impact | Mitigation |
|---|---|---|
| Messenger webhook retries duplicate events | Duplicate replies | De-duplicate by `mid`, idempotent processing |
| LLM latency or invalid JSON | Bad UX/failures | Timeout, schema validation, fallback, eval tests |
| Bot orchestrator grows too complex | Hard to test | Keep domain services separate; state transition tests |
| PII leaks into logs | Privacy risk | Masking helper, log review tests |
| Config missing/bad | Unsafe answer | Approved config, validation, fallback safe |
| Notification failure | Handoff lost | Persist handoff first, notification retry/error alert |
| Cost/eligibility rules wrong | Business risk | Business approval gate, regression tests |

## 14. Product/Operation Risks

| Risk | Impact | Mitigation |
|---|---|---|
| User expects official quote | Trust/compliance risk | Always disclaimer, official quote -> handoff |
| SDT optional reduces callback leads | Lower phone conversion | Soft CTA after value, Messenger handoff by PSID |
| Advisor SLA not defined | User frustration | Default outside-hours message, chot SLA before go-live |
| FAQ/content not approved | Inconsistent answers | BA/content review before Slice 2 done |
| Scope creep into school DB | MVP delay | School DB only Should-have seed data, not core blocker |
| Parent tone needs refinement | UX mismatch | Detect phu huynh from message, adjust copy later |

## 15. Testing Plan by Slice

| Slice | Required Tests |
|---|---|
| Slice 1 | Webhook verification, invalid signature, duplicate message, greeting/menu, send API adapter |
| Slice 2 | Menu routing, FAQ responses, LLM classification, low confidence, guardrails |
| Slice 3 | Slot extraction, slot merge/conflict, profile persistence, PII masking |
| Slice 4 | Cost ranges, missing pathway/region, budget fit, missing config, disclaimer |
| Slice 5 | Lead score A/B/C/D, risk LOW/MEDIUM/HIGH/UNKNOWN, missing data, visa wording |
| Slice 6 | Phone validation, partial lead, duplicate lead, no-phone handoff, notification failure |
| Slice 7 | Config validation/versioning/audit, analytics counts, admin auth |

## 16. Demo Plan by Slice

| Slice | Demo Script |
|---|---|
| Slice 1 | User sends "tu van du hoc Han" and sees 7-item menu. |
| Slice 2 | User asks D4/D2, quy trinh, and visa guarantee; bot answers safely. |
| Slice 3 | User sends GPA/year/budget in one message; profile slots appear saved. |
| Slice 4 | User asks cost, selects D4-1/near Seoul/budget; bot returns range + disclaimer. |
| Slice 5 | User checks profile; bot returns score/risk and missing info without visa promise. |
| Slice 6 | User requests advisor; creates handoff with/without phone and advisor payload. |
| Slice 7 | Admin changes fallback/config; owner views analytics. |

## 17. Definition of Done

Moi slice chi done khi:

- Acceptance criteria dat.
- Required tests pass.
- Core logs co `trace_id`, session, intent/action where relevant.
- Fallback/error handling ton tai.
- Guardrails khong bi vi pham.
- Demo scenario chay duoc tren local/staging.
- API/schema/config thay doi da cap nhat docs.
- PII khong lo trong logs.
- Product/BA sign-off copy/rules cho user-facing flows.

## 18. 30-Day Timeline Recommendation

| Days | Slice/Focus | Output |
|---|---|---|
| 1-4 | Slice 1 | Webhook, session/message persistence, greeting/menu staging demo |
| 5-8 | Slice 2 | Rule routing, FAQ, LLM classify, fallback/guardrail demo |
| 9-12 | Slice 3 | Slot extraction/profile store, PII masking, profile demo |
| 13-16 | Slice 4 | Cost estimator, cost config seed, disclaimer, cost demo |
| 17-20 | Slice 5 | Eligibility checker, score/risk, rule regression demo |
| 21-24 | Slice 6 | Lead service, handoff, notification, advisor payload demo |
| 25-28 | Slice 7 | Config API, audit, analytics API, admin/report demo |
| 29-30 | Hardening/UAT | Flow A-G UAT, security/privacy checklist, go-live readiness |

Recommended small team allocation:

- 1 backend lead owns architecture/orchestrator.
- 1 backend developer owns integrations/services.
- 1 BA/conversation designer owns FAQ/copy/rules.
- 1 QA owns fixtures, flow tests and regression.
- DevOps support part-time for env/secrets/deploy.
