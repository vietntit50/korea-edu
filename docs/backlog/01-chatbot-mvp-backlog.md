# Implementation Backlog - Facebook Messenger Chatbot MVP

## 1. Delivery Goal

Trong 30 ngay, build MVP native cho **Facebook Messenger Chatbot tu van du hoc Han Quoc** cua Korea Study Advisor Platform.

MVP can dat cac muc tieu:

- Nhan va tra loi message tu Facebook Messenger.
- Huong user theo journey-first/need-first, khong school-first voi user moi.
- Tu dong tra loi FAQ co ban ve quy trinh, chi phi, D4-1/D2, Seoul/tinh, TOPIK, GPA, visa disclaimer.
- Thu thap profile so bo theo tung intent ma khong bat buoc user de lai SDT.
- Uoc tinh chi phi tong quan theo pathway/khu vuc bang range configurable.
- Check eligibility so bo va gan lead score A/B/C/D, risk LOW/MEDIUM/HIGH.
- Tao lead khi co du dieu kien va handoff cho tu van vien khi can.
- Luu conversation, intent, slot, lead va handoff de phuc vu tu van vien va analytics.

## 2. MVP Assumptions

- Bot tone default: `trung tam/ban`.
- SDT khong bat buoc trong cac flow tu van co ban; chi can khi user muon goi lai, dat lich, bao gia chi tiet hoac handoff.
- Build native, khong dung ManyChat/n8n cho MVP.
- CRM implementation chua can chot; MVP phai co `LeadService` abstraction va co the luu lead vao database rieng truoc.
- Cost range ban dau dung range de xuat trong [decision log](/Users/tfs-mt-235/viet/korea/docs/decisions/01-chatbot-mvp-open-questions.md), can business owner phe duyet lan cuoi truoc go-live.
- School database full khong thuoc MVP core; basic lookup 20-30 truong la `Should have`.
- Tu van ngoai gio, SLA chinh thuc va PII retention can chot truoc production go-live.

## 3. Scope Summary

### 3.1. Must Have

- Facebook Messenger webhook verify/receive/send.
- Greeting/menu journey-first.
- Bot orchestrator va conversation state.
- Rule-based routing cho menu/quick replies/handoff/disclaimer.
- LLM intent classification va slot extraction cho message tu nhien.
- FAQ/knowledge base co ban.
- Slot filling va user profile store.
- General cost estimator theo range configurable.
- Eligibility check so bo.
- Lead capture optional.
- Human handoff request.
- Conversation logging.
- Admin config co ban cho FAQ/cost/disclaimer.
- Security, logging, monitoring co ban.
- QA cho core conversation flows.

### 3.2. Should Have

- Basic school lookup cho 20-30 truong neu co data verified.
- Basic region recommendation.
- Bot summary cho tu van vien.
- Lead scoring A/B/C/D.
- Notification cho tu van vien khi handoff.
- Analytics dashboard/API co ban.

### 3.3. Could Have

- Book consultation calendar.
- Tin nhan/PDF tu van tu dong.
- Multi-language support.
- AI recommendation nang cao.
- Zalo channel adapter.

### 3.4. Out of Scope

- Full advisor console.
- Full CRM pipeline.
- Full school database va school cost calculator chi tiet.
- Parent dashboard.
- CTV portal.
- OCR ho so.
- AI interview coach.
- Crawler tu dong.
- Payment/contract.
- Mobile app.

## 4. Epics

| Epic ID | Epic | Priority | Goal |
|---|---|---|---|
| E01 | Facebook Messenger Integration | Must | Ket noi Messenger native de nhan/gui message on dinh. |
| E02 | Bot Orchestration & Conversation State | Must | Dieu phoi flow, state, backend action va response. |
| E03 | Intent Routing & LLM Classification | Must | Phan loai nhu cau bang rule + LLM. |
| E04 | Slot Filling & User Profile Store | Must | Thu thap va luu thong tin ho so tung buoc. |
| E05 | FAQ & Knowledge Base | Must | Tra loi thong tin co ban co kiem soat. |
| E06 | General Cost Estimator | Must | Uoc tinh chi phi theo range, khong bao gia tuyet doi. |
| E07 | Eligibility Check | Must | Check phu hop so bo va risk. |
| E08 | Lead Capture & CRM Integration | Must | Tao/update lead bang abstraction, chua phu thuoc CRM cuoi. |
| E09 | Human Handoff & Notification | Must | Chuyen tu van vien voi summary va lead context. |
| E10 | Admin Configuration | Must | Cau hinh FAQ, cost range, disclaimer, handoff triggers co ban. |
| E11 | Analytics & Reporting | Should | Do conversation, lead, intent, fallback, handoff. |
| E12 | Security, Logging & Monitoring | Must | Dam bao webhook, PII, logs, alerts va audit. |
| E13 | QA & Acceptance Testing | Must | Test conversation flows, rules, API va regression. |

## 5. Detailed Backlog

### Epic 1: Facebook Messenger Integration

| Story ID | User Story | Priority | Owner Role | Estimate | Dependencies | Acceptance Criteria |
|---|---|---|---|---|---|---|
| US-E01-01 | As a Facebook user, I want the fanpage chatbot to receive my Messenger message, so that I can start a consultation from inbox. | Must | Backend developer | M | Facebook app/page setup | Given Messenger sends a valid webhook event, When backend receives it, Then message is verified, parsed, de-duplicated and passed to bot processing. |
| US-E01-02 | As a Facebook user, I want the bot to reply in Messenger with text and quick replies, so that I can continue the journey without typing too much. | Must | Backend developer | M | US-E01-01 | Given bot produces response messages, When response is sent, Then Messenger receives text and quick reply payloads successfully. |
| US-E01-03 | As an operator, I want webhook failures to be logged and visible, so that integration issues are caught quickly. | Must | Backend developer/DevOps | S | US-E01-01 | Given Facebook webhook call fails validation or processing, When error occurs, Then structured log contains event id, reason, and no sensitive token is exposed. |

#### Tasks

| Task ID | Task | Role | Estimate | Dependencies | Notes |
|---|---|---|---|---|---|
| T-E01-01 | Create Messenger webhook endpoint `POST /webhooks/facebook`. | Backend developer | M | Facebook app credentials | Include GET verification if required by Facebook setup. |
| T-E01-02 | Implement signature/token verification and event parsing. | Backend developer | M | T-E01-01 | Reject invalid source. |
| T-E01-03 | Implement message de-duplication by message id. | Backend developer | S | T-E01-02 | Prevent duplicate bot replies. |
| T-E01-04 | Implement Messenger send API adapter for text, quick replies and buttons. | Backend developer | M | Page access token | Keep channel adapter isolated. |
| T-E01-05 | Add local/test webhook payload fixtures. | Tester/QA | S | T-E01-01 | Cover text, quick reply, unsupported event. |
| T-E01-06 | Configure webhook secrets via environment variables. | DevOps | S | T-E01-01 | No hardcoded tokens. |

### Epic 2: Bot Orchestration & Conversation State

| Story ID | User Story | Priority | Owner Role | Estimate | Dependencies | Acceptance Criteria |
|---|---|---|---|---|---|---|
| US-E02-01 | As a new user, I want a journey-first greeting menu, so that I can choose what I need before thinking about schools. | Must | Backend developer/Conversation designer | M | E01 | Given I send "tu van du hoc Han", When bot processes it, Then bot replies with 7 menu options from PRD using "trung tam/ban" tone. |
| US-E02-02 | As a user, I want the bot to remember the current conversation context, so that I do not need to repeat information each turn. | Must | Backend developer | L | E01 | Given I answer a follow-up question, When bot processes next message, Then previous intent, state and slots are available. |
| US-E02-03 | As a product owner, I want bot flows to have explicit states, so that behavior is predictable and testable. | Must | Backend developer/BA | M | US-E02-01 | Given a supported flow, When a transition happens, Then current state, next state and backend action are logged. |

#### Tasks

| Task ID | Task | Role | Estimate | Dependencies | Notes |
|---|---|---|---|---|---|
| T-E02-01 | Define conversation states for greeting, menu, slot collection, recommendation, lead capture, handoff and fallback. | BA/Backend developer | M | PRD | Keep states minimal for MVP. |
| T-E02-02 | Implement `BotOrchestrator` service. | Backend developer | L | E01 | Coordinates router, slots, services, response builder. |
| T-E02-03 | Implement `ConversationSession` persistence. | Backend developer | M | DB setup | Include `facebook_psid`, `status`, `current_intent`, timestamps. |
| T-E02-04 | Implement response builder for text + quick replies. | Backend developer | M | T-E02-02 | Centralize tone/disclaimer injection. |
| T-E02-05 | Write conversation state transition tests. | Tester/QA | M | T-E02-01 | Cover Flow A-G happy paths at state level. |

### Epic 3: Intent Routing & LLM Classification

| Story ID | User Story | Priority | Owner Role | Estimate | Dependencies | Acceptance Criteria |
|---|---|---|---|---|---|---|
| US-E03-01 | As a user, I want the bot to understand menu choices and quick replies reliably, so that common actions do not depend on LLM. | Must | Backend developer | M | E02 | Given I click a menu option, When bot processes payload, Then it routes to the matching intent without LLM call. |
| US-E03-02 | As a user, I want the bot to understand natural Vietnamese messages, so that I can ask questions freely. | Must | Backend developer | L | LLM provider/config | Given I ask "di Han het bao nhieu", When LLM classification succeeds, Then intent is `ASK_GENERAL_COST` with confidence logged. |
| US-E03-03 | As a compliance owner, I want risky requests detected first, so that the bot does not promise visa or encourage false documents. | Must | Backend developer/BA | M | Guardrail rules | Given user asks "bao dau visa khong", When routed, Then intent is `ASK_VISA_GENERAL` or `UNSAFE_OR_HIGH_RISK` and visa disclaimer is required. |

#### Tasks

| Task ID | Task | Role | Estimate | Dependencies | Notes |
|---|---|---|---|---|---|
| T-E03-01 | Implement rule-based router for greeting, menu, quick replies, lead capture and handoff keywords. | Backend developer | M | E02 | Rule layer runs before LLM. |
| T-E03-02 | Define supported intent enum from PRD. | Backend developer/BA | S | PRD section 6 | Include UNKNOWN and UNSAFE_OR_HIGH_RISK. |
| T-E03-03 | Implement LLM service abstraction for intent classification. | Backend developer | L | Provider credentials | Keep provider swappable. |
| T-E03-04 | Create intent classification prompt and JSON schema validation. | BA/Backend developer | M | T-E03-03 | Reject invalid JSON. |
| T-E03-05 | Add low-confidence fallback behavior. | Backend developer | S | T-E03-03 | Ask user to choose from 2-3 options. |
| T-E03-06 | Create intent test set for top intents. | Tester/QA/BA | M | PRD sample scripts | Include spelling errors and multi-intent messages. |

### Epic 4: Slot Filling & User Profile Store

| Story ID | User Story | Priority | Owner Role | Estimate | Dependencies | Acceptance Criteria |
|---|---|---|---|---|---|---|
| US-E04-01 | As a user, I want the bot to ask only a few important questions at a time, so that the conversation feels easy. | Must | Conversation designer/Backend developer | M | E02 | Given an intent requires multiple slots, When slots are missing, Then bot asks only 1-3 prioritized questions. |
| US-E04-02 | As a user, I want the bot to understand profile information in natural text, so that I can answer freely. | Must | Backend developer | L | E03 | Given I say "tot nghiep 2023, GPA 6.5, ngan sach 280 trieu", Then slots are extracted and stored with confidence. |
| US-E04-03 | As an advisor, I want partial profile data saved even if user does not leave phone, so that follow-up context is not lost. | Must | Backend developer | M | DB schema | Given user provides GPA and budget without phone, When session is saved, Then data is linked to PSID/user profile. |

#### Tasks

| Task ID | Task | Role | Estimate | Dependencies | Notes |
|---|---|---|---|---|---|
| T-E04-01 | Define slot schema and priority by intent. | BA/Backend developer | M | PRD section 7 | Include required/optional slots. |
| T-E04-02 | Implement `SlotManager` merge/update rules. | Backend developer | M | T-E04-01 | Store source, confidence, updated_at. |
| T-E04-03 | Implement LLM slot extraction service and validation. | Backend developer | L | E03 | No guessing values. |
| T-E04-04 | Implement `UserProfile` and `SlotValue` persistence. | Backend developer | M | DB setup | Mark PII fields. |
| T-E04-05 | Design slot collection copy for cost and eligibility flows. | Conversation designer/BA | M | T-E04-01 | Use "trung tam/ban" tone. |
| T-E04-06 | Test partial profile save and slot overwrite rules. | Tester/QA | M | T-E04-02 | Cover confidence/source. |

### Epic 5: FAQ & Knowledge Base

| Story ID | User Story | Priority | Owner Role | Estimate | Dependencies | Acceptance Criteria |
|---|---|---|---|---|---|---|
| US-E05-01 | As a user, I want clear answers about process, D4-1/D2, TOPIK, GPA, documents and timeline, so that I can understand the basics quickly. | Must | BA/Content | M | PRD | Given user asks a supported FAQ, When bot answers, Then answer is concise, approved and has CTA. |
| US-E05-02 | As an admin, I want FAQ content separated from code, so that the team can update answers safely. | Must | Backend developer/BA | M | Admin config | Given FAQ item is active, When matching intent occurs, Then bot uses configured answer. |
| US-E05-03 | As a product owner, I want unsupported or unverified school data to be blocked, so that bot does not invent information. | Must | Backend developer/BA | S | Guardrails | Given user asks school-specific info not in KB, Then bot says advisor needs to confirm. |

#### Tasks

| Task ID | Task | Role | Estimate | Dependencies | Notes |
|---|---|---|---|---|---|
| T-E05-01 | Draft MVP FAQ content for required intents. | BA/Content | L | PRD section 6 | Process, cost, D4/D2, TOPIK, GPA, visa, docs, timeline. |
| T-E05-02 | Review FAQ content for compliance/disclaimers. | BA/Owner | M | T-E05-01 | No visa guarantee. |
| T-E05-03 | Implement `FAQItem` persistence and retrieval. | Backend developer | M | DB setup | Intent + active flag. |
| T-E05-04 | Implement KB response lookup in orchestrator. | Backend developer | M | E02/E03 | Use before LLM generation when exact FAQ exists. |
| T-E05-05 | Create FAQ regression tests. | Tester/QA | M | T-E05-01 | Snapshot approved messages. |

### Epic 6: General Cost Estimator

| Story ID | User Story | Priority | Owner Role | Estimate | Dependencies | Acceptance Criteria |
|---|---|---|---|---|---|---|
| US-E06-01 | As a cost seeker, I want an estimated cost range by pathway and region, so that I know whether my family budget is realistic. | Must | Backend developer/BA | M | Decision D-003 | Given pathway and region are known, When estimator runs, Then bot returns configured VND range with disclaimer. |
| US-E06-02 | As a new user who does not know D4-1/D2, I want the bot to explain that cost depends on pathway and region, so that I can provide missing info. | Must | Conversation designer/Backend developer | M | E04 | Given pathway is unknown, When user asks cost, Then bot gives broad range and asks D4-1/D2 or learning goal. |
| US-E06-03 | As an admin, I want cost ranges configurable, so that business can update estimates without code changes. | Must | Backend developer/Admin | M | E10 | Given admin updates cost range config, When estimator runs, Then new range is used and audit is logged. |

#### Tasks

| Task ID | Task | Role | Estimate | Dependencies | Notes |
|---|---|---|---|---|---|
| T-E06-01 | Define `CostRangeTemplate` seed data from decision log. | BA/Backend developer | S | Decision log | Mark as proposed until owner approval. |
| T-E06-02 | Implement `POST /cost/general-estimate`. | Backend developer | M | T-E06-01 | Return range + components + disclaimer flag. |
| T-E06-03 | Implement cost response templates for known/unknown slots. | Conversation designer/Backend developer | M | E04 | Do not output exact official price. |
| T-E06-04 | Add cost disclaimer injection. | Backend developer | S | PRD section 16 | Always required for cost. |
| T-E06-05 | Test budget matching and missing slot prompts. | Tester/QA | M | T-E06-02 | Include low/unknown/high budget. |

### Epic 7: Eligibility Check

| Story ID | User Story | Priority | Owner Role | Estimate | Dependencies | Acceptance Criteria |
|---|---|---|---|---|---|---|
| US-E07-01 | As a student, I want to check if my profile is suitable, so that I understand my preliminary pathway and risk. | Must | Backend developer/BA | L | E04 | Given minimum eligibility slots are available, When checker runs, Then bot returns score/risk/pathway/missing info without visa guarantee. |
| US-E07-02 | As an advisor, I want risky cases flagged, so that I can prioritize human review. | Must | Backend developer | M | US-E07-01 | Given gap year or low GPA or visa history risk is detected, Then risk warning is stored and handoff is recommended. |
| US-E07-03 | As a product owner, I want eligibility rules configurable enough for MVP, so that business can tune scoring. | Should | Backend developer/BA | M | E10 | Given rule config changes, When checker runs, Then score output follows updated rule version. |

#### Tasks

| Task ID | Task | Role | Estimate | Dependencies | Notes |
|---|---|---|---|---|---|
| T-E07-01 | Define MVP eligibility scoring rules A/B/C/D. | BA/Product owner | M | PRD + decisions | Avoid "chac chan". |
| T-E07-02 | Define risk LOW/MEDIUM/HIGH conditions. | BA/Product owner | M | T-E07-01 | Include missing data behavior. |
| T-E07-03 | Implement `EligibilityChecker` service. | Backend developer | L | T-E07-01 | Pure rule-based first. |
| T-E07-04 | Implement `POST /eligibility/check`. | Backend developer | M | T-E07-03 | Return score/risk/missing info/next action. |
| T-E07-05 | Implement bot response copy for eligibility results. | Conversation designer/BA | M | T-E07-03 | Include visa disclaimer when relevant. |
| T-E07-06 | Create eligibility rule regression tests. | Tester/QA | M | T-E07-01 | Cover A/B/C/D and LOW/MEDIUM/HIGH. |

### Epic 8: Lead Capture & CRM Integration

| Story ID | User Story | Priority | Owner Role | Estimate | Dependencies | Acceptance Criteria |
|---|---|---|---|---|---|---|
| US-E08-01 | As a user, I want to continue without leaving phone, so that I do not feel forced into sales. | Must | Backend developer/Conversation designer | M | E04 | Given user declines phone, When flow continues, Then bot proceeds with FAQ/check and stores partial profile by PSID. |
| US-E08-02 | As a user who wants follow-up, I want to leave my phone, so that an advisor can contact me. | Must | Backend developer | M | E04 | Given user sends a valid phone, When lead capture runs, Then lead is created/updated and bot confirms next step. |
| US-E08-03 | As a future CRM integrator, I want lead creation behind an abstraction, so that storage can change later. | Must | Backend developer | M | DB setup | Given lead service is called, When CRM is deferred, Then lead is saved in MVP database through same interface. |

#### Tasks

| Task ID | Task | Role | Estimate | Dependencies | Notes |
|---|---|---|---|---|---|
| T-E08-01 | Define lead creation triggers and optional phone behavior. | BA/Backend developer | S | Decision D-002 | Phone only required for callback/handoff. |
| T-E08-02 | Implement phone detection and validation. | Backend developer | S | E03 | VN phone normalization. |
| T-E08-03 | Implement `Lead` persistence and duplicate handling. | Backend developer | M | DB setup | Match by phone or PSID. |
| T-E08-04 | Implement `LeadService` abstraction. | Backend developer | M | T-E08-03 | Keep CRM replaceable. |
| T-E08-05 | Implement `POST /leads` and `PATCH /leads/{id}`. | Backend developer | M | T-E08-04 | Internal API. |
| T-E08-06 | Test partial lead, phone lead and duplicate update. | Tester/QA | M | T-E08-03 | Include no-phone scenario. |

### Epic 9: Human Handoff & Notification

| Story ID | User Story | Priority | Owner Role | Estimate | Dependencies | Acceptance Criteria |
|---|---|---|---|---|---|---|
| US-E09-01 | As a user, I want to ask for a real advisor, so that complex questions are handled by a person. | Must | Backend developer | M | E08 | Given user asks "gap tu van vien", When bot handles it, Then handoff request is created and bot explains expected response. |
| US-E09-02 | As an advisor, I want a conversation summary and profile fields, so that I can continue without asking everything again. | Must | Backend developer/LLM | M | E04/E08 | Given handoff is created, Then advisor payload includes name, phone if any, PSID, need, slots, last question, score and risk warning. |
| US-E09-03 | As a manager, I want hot leads to trigger notification, so that response time is shorter. | Should | Backend developer/DevOps | M | E08/E07 | Given lead score A or high intent, When handoff created, Then notification is sent or queued. |

#### Tasks

| Task ID | Task | Role | Estimate | Dependencies | Notes |
|---|---|---|---|---|---|
| T-E09-01 | Define handoff trigger matrix. | BA/Product owner | M | PRD section 13 | Include unknown after 2 attempts. |
| T-E09-02 | Implement `HandoffRequest` persistence. | Backend developer | M | DB setup/E08 | Status: requested/assigned/advisor_replied/closed. |
| T-E09-03 | Implement `POST /handoff`. | Backend developer | M | T-E09-02 | Internal API. |
| T-E09-04 | Implement conversation summary generation. | Backend developer/LLM | M | E03/E04 | Use structured slots first; LLM optional. |
| T-E09-05 | Implement advisor notification adapter. | Backend developer | M | T-E09-02 | MVP can be email/Slack/internal log depending available channel. |
| T-E09-06 | Write handoff tests for all triggers. | Tester/QA | M | T-E09-01 | Include no-phone user. |

### Epic 10: Admin Configuration

| Story ID | User Story | Priority | Owner Role | Estimate | Dependencies | Acceptance Criteria |
|---|---|---|---|---|---|---|
| US-E10-01 | As an admin, I want to configure FAQ, disclaimer and fallback text, so that content can change without deployment. | Must | Backend developer/Frontend developer | M | DB setup | Given config is updated, When bot responds, Then active config value is used. |
| US-E10-02 | As an admin, I want to configure cost ranges and handoff triggers, so that business rules can be tuned. | Must | Backend developer | M | E06/E09 | Given cost or trigger config changes, When service runs, Then config version is applied and audit logged. |
| US-E10-03 | As an owner, I want configuration changes audited, so that we know who changed bot behavior. | Must | Backend developer | S | Auth/admin setup | Given admin changes config, Then audit log records actor, key, old/new value summary and timestamp. |

#### Tasks

| Task ID | Task | Role | Estimate | Dependencies | Notes |
|---|---|---|---|---|---|
| T-E10-01 | Define MVP config keys and JSON schema. | BA/Backend developer | M | PRD section 17 | FAQ, menu, cost, disclaimer, triggers, business hours. |
| T-E10-02 | Implement `BotConfiguration` persistence. | Backend developer | M | DB setup | Versioned. |
| T-E10-03 | Implement `GET /admin/bot-config` and `PUT /admin/bot-config`. | Backend developer | M | T-E10-02 | Admin permission. |
| T-E10-04 | Implement simple admin UI or DB-backed admin workflow. | Frontend/admin developer | L | T-E10-03 | If UI is too much, document DB config process for MVP. |
| T-E10-05 | Implement audit log for config changes. | Backend developer | S | T-E10-03 | Avoid raw secrets. |
| T-E10-06 | Test config versioning and rollback/manual correction process. | Tester/QA | M | T-E10-02 | At least update-read-use cycle. |

### Epic 11: Analytics & Reporting

| Story ID | User Story | Priority | Owner Role | Estimate | Dependencies | Acceptance Criteria |
|---|---|---|---|---|---|---|
| US-E11-01 | As an owner, I want to see total conversations, leads and handoffs, so that I can measure MVP value. | Should | Backend developer | M | E02/E08/E09 | Given conversations exist, When analytics API is called, Then totals for date range are returned. |
| US-E11-02 | As a PM, I want top intents and fallback rate, so that I know where bot content must improve. | Should | Backend developer/BA | M | E03 | Given intent logs exist, When analytics runs, Then top intents and UNKNOWN rate are returned. |
| US-E11-03 | As a manager, I want lead score distribution, so that I can judge lead quality from fanpage. | Should | Backend developer | S | E07/E08 | Given leads have score, Then distribution A/B/C/D is visible. |

#### Tasks

| Task ID | Task | Role | Estimate | Dependencies | Notes |
|---|---|---|---|---|---|
| T-E11-01 | Define MVP analytics metrics and date filters. | BA/Product owner | S | PRD section 20 | Keep simple. |
| T-E11-02 | Implement `IntentLog` and event aggregation. | Backend developer | M | E03 | Intent, confidence, model version. |
| T-E11-03 | Implement `GET /analytics/chatbot`. | Backend developer | M | T-E11-02/E08 | Admin/manager permission. |
| T-E11-04 | Create basic analytics dashboard or report endpoint output. | Frontend/admin developer | M | T-E11-03 | Could be API-only for MVP. |
| T-E11-05 | Test analytics counts with fixture data. | Tester/QA | S | T-E11-03 | Validate conversion calculations. |

### Epic 12: Security, Logging & Monitoring

| Story ID | User Story | Priority | Owner Role | Estimate | Dependencies | Acceptance Criteria |
|---|---|---|---|---|---|---|
| US-E12-01 | As an operator, I want secure webhook and secret handling, so that tokens and user data are protected. | Must | Backend developer/DevOps | M | E01 | Given invalid webhook signature/token, Then request is rejected and no bot action is executed. |
| US-E12-02 | As a developer, I want structured logs for message processing, so that production issues can be debugged. | Must | Backend developer | M | E02 | Given a message is processed, Then logs include trace id, session id, intent, action, latency and sanitized error if any. |
| US-E12-03 | As a compliance owner, I want PII handled carefully, so that phone/profile data is not leaked in logs. | Must | Backend developer/QA | M | E04/E08 | Given logs are emitted, Then phone is masked and raw PII is not printed in error traces. |
| US-E12-04 | As an operator, I want fallback behavior when LLM/CRM/Facebook APIs fail, so that user experience degrades gracefully. | Must | Backend developer | M | E03/E08/E01 | Given dependency timeout, Then bot uses fallback or queues retry and logs alertable event. |

#### Tasks

| Task ID | Task | Role | Estimate | Dependencies | Notes |
|---|---|---|---|---|---|
| T-E12-01 | Define security checklist for MVP. | Tech Lead/Backend developer | S | PRD section 19/24 | Include secrets, signature, rate limit, PII. |
| T-E12-02 | Implement structured logging and trace id. | Backend developer | M | E01/E02 | Across webhook to response. |
| T-E12-03 | Implement PII masking helper. | Backend developer | S | E08 | Phone/name masking. |
| T-E12-04 | Implement rate limiting/spam guard per PSID. | Backend developer | M | E01 | Reasonable default limits. |
| T-E12-05 | Implement timeout/retry policy for LLM, Messenger and lead persistence. | Backend developer | M | E03/E08 | Avoid infinite retries. |
| T-E12-06 | Configure basic monitoring/alerts for webhook error rate and LLM failures. | DevOps | M | Deployment env | Can start with logs + dashboard. |
| T-E12-07 | Run security/privacy QA checklist. | Tester/QA | M | T-E12-01 | Before go-live. |

### Epic 13: QA & Acceptance Testing

| Story ID | User Story | Priority | Owner Role | Estimate | Dependencies | Acceptance Criteria |
|---|---|---|---|---|---|---|
| US-E13-01 | As a product owner, I want all seven main flows tested, so that MVP behavior matches PRD. | Must | Tester/QA | L | E02-E09 | Given Flow A-G test cases, When QA runs them, Then expected bot messages/actions pass. |
| US-E13-02 | As a tester, I want regression cases for guardrails, so that future prompt/rule changes do not introduce unsafe answers. | Must | Tester/QA/BA | M | E03/E05/E07 | Given risky prompts, Then bot never promises visa, official cost or unverified school data. |
| US-E13-03 | As a delivery lead, I want release readiness criteria, so that go-live is not based on vibes and crossed fingers. | Must | QA/Tech Lead | M | All Must epics | Given release checklist, Then all P0/P1 bugs are closed or accepted by owner. |

#### Tasks

| Task ID | Task | Role | Estimate | Dependencies | Notes |
|---|---|---|---|---|---|
| T-E13-01 | Create test matrix for Flow A-G. | Tester/QA/BA | M | PRD section 8/25 | Include happy path and edge cases. |
| T-E13-02 | Create API integration tests for webhook, bot process, cost, eligibility, leads, handoff. | Tester/QA/Backend developer | L | APIs implemented | Can use fixtures. |
| T-E13-03 | Create LLM intent/slot eval set. | Tester/QA/BA | M | E03/E04 | Include typo/mixed intent. |
| T-E13-04 | Create guardrail regression test set. | Tester/QA/BA | M | E12 | Visa, false documents, illegal work, unverified data. |
| T-E13-05 | Run UAT with business owner on scripted Messenger scenarios. | QA/Product owner | M | Feature complete | Capture copy/content changes. |
| T-E13-06 | Prepare go-live checklist. | QA/Tech Lead/DevOps | S | All Must epics | Include env vars, tokens, rollback, monitoring. |

## 6. Sprint Suggestion for 30-Day MVP

| Timebox | Focus | Target Output |
|---|---|---|
| Days 1-5 | Foundation | Messenger webhook, DB baseline, session/message persistence, journey-first greeting/menu. |
| Days 6-10 | Routing + FAQ | Rule router, LLM intent classification, FAQ KB, fallback, initial conversation tests. |
| Days 11-15 | Slots + Profile | Slot schema, LLM slot extraction, profile store, partial profile behavior, cost/eligibility slot prompts. |
| Days 16-20 | Cost + Eligibility | Cost estimator, eligibility checker, disclaimers, lead score/risk rules, regression tests. |
| Days 21-25 | Lead + Handoff + Admin Config | LeadService, handoff request, advisor summary/notification, admin config for FAQ/cost/disclaimer. |
| Days 26-30 | Analytics + Hardening + UAT | Analytics API, monitoring, security/privacy checks, Flow A-G QA, business UAT, go-live readiness. |

## 7. Cross-Team Dependencies

| Dependency | Owner | Needed By | Risk if Delayed | Proposed Default |
|---|---|---|---|---|
| Facebook app/page access token and webhook setup | Business/Tech | Day 1 | Cannot test Messenger integration | Prepare app credentials before sprint starts. |
| Final approval for cost ranges | Business owner | Day 16 | Bot cost answers may be blocked from go-live | Use proposed range in config as staging only. |
| FAQ approved content | BA/Business owner | Day 8 | Bot answers may be inconsistent | Start with PRD-based FAQ and review. |
| LLM provider/API key | Tech owner | Day 6 | Natural-language routing delayed | Keep rule-based flow working without LLM. |
| Advisor notification channel | Operations | Day 21 | Handoff visible only in DB/log | Default to email/internal dashboard if no chat tool. |
| SLA/business hours | Operations | Before go-live | Bot may promise wrong callback timing | Default: business hours response, no outside-hours guarantee. |
| PII retention policy | Business/Legal/Tech | Before go-live | Production privacy risk | Temporary default: raw conversation 12 months, lead per CRM policy. |

## 8. Testing Backlog

| Test Area | Priority | Owner | Coverage |
|---|---|---|---|
| Messenger webhook contract tests | Must | QA/Backend | Verification, text event, quick reply, duplicate, unsupported event. |
| Conversation Flow A-G tests | Must | QA/BA | New explorer, cost, eligibility, D4/D2, region, school-aware, handoff. |
| Intent classification tests | Must | QA/BA | Supported intents, typos, mixed messages, low confidence. |
| Slot extraction tests | Must | QA/BA | GPA, graduation year, budget, TOPIK, region, school name. |
| Cost estimator tests | Must | QA | Pathway/region range, unknown slots, disclaimer required. |
| Eligibility rule tests | Must | QA | Score A/B/C/D, risk LOW/MEDIUM/HIGH, missing info. |
| Lead capture tests | Must | QA | No phone, valid phone, duplicate lead, update lead. |
| Handoff tests | Must | QA | User request, risk trigger, official fee request, fallback after 2 unknowns. |
| Guardrail tests | Must | QA/BA | Visa guarantee, fake documents, illegal work, unverified school data. |
| Security/privacy tests | Must | QA/Tech Lead | Webhook auth, PII masking, rate limit, secret handling. |
| Analytics tests | Should | QA | Conversation count, lead conversion, top intents, fallback rate. |

## 9. Design Backlog

| Item | Priority | Owner | Output |
|---|---|---|---|
| Journey-first global menu copy | Must | Conversation designer | 7 menu options with quick reply labels. |
| Flow A-G conversation scripts | Must | Conversation designer/BA | Step-by-step copy for Messenger. |
| Tone guide for "trung tam/ban" | Must | Conversation designer | Copy rules and examples. |
| Fallback and recovery copy | Must | Conversation designer | Unknown, low confidence, API error, outside hours. |
| Disclaimer placement | Must | BA/Conversation designer | Cost, visa, school, scholarship, part-time work. |
| Lead capture prompts | Must | Conversation designer | Optional phone UX and callback CTA. |
| Handoff confirmation copy | Must | Conversation designer/Ops | In-hours and out-of-hours variants. |
| Admin config UI wireframe | Should | Designer/Frontend | Simple form/list for FAQ/cost/disclaimer/config. |
| Analytics dashboard wireframe | Should | Designer/Product | Basic metrics view. |

## 10. BA/Content Backlog

| Item | Priority | Owner | Output |
|---|---|---|---|
| FAQ content for process, cost, D4/D2, TOPIK, GPA, visa, documents, timeline | Must | BA/Content | Approved FAQItem list. |
| Cost range approval package | Must | BA/Product owner | Final `CostRangeTemplate` values and disclaimer. |
| Eligibility scoring rule definition | Must | BA/Product owner | A/B/C/D and LOW/MEDIUM/HIGH rules. |
| Handoff trigger matrix | Must | BA/Ops | Trigger, reason code, advisor payload. |
| Lead field mapping | Must | BA/Backend | Required/optional fields and PII flags. |
| Guardrail policy text | Must | BA/Product owner | Unsafe topics and allowed response pattern. |
| School lookup data for 20-30 schools | Should | BA/Content | Verified school seed data, if included. |
| Business hours and SLA | Must before go-live | Ops/Business owner | Config values and copy. |
| PII retention rule | Must before go-live | Business/Legal/Tech | Retention and deletion policy. |

## 11. Risks & Mitigations

| Risk | Impact | Mitigation |
|---|---|---|
| LLM misclassifies intent or extracts wrong slot | Wrong advice or poor UX | Rule-based routing for menu/high-risk cases, confidence threshold, fallback, eval set. |
| Bot gives unverified cost/school/visa information | Compliance and trust risk | Database/config as source of truth, mandatory disclaimers, guardrail tests. |
| CRM decision deferred | Lead integration uncertainty | Use `LeadService` abstraction and MVP DB storage. |
| SDT optional reduces callable leads | Fewer direct follow-ups | Store partial PSID profile, use soft CTA after value is delivered. |
| Cost ranges not approved before go-live | Cost flow cannot launch safely | Treat proposed range as staging config; require business approval gate. |
| Facebook app setup delayed | Cannot test real channel | Use webhook fixtures and local bot process API until credentials ready. |
| No advisor notification channel | Handoff response delayed | Start with DB queue/admin list, add email/chat notification as Should. |
| PII logging leak | Privacy risk | Mask PII, restrict logs, add QA privacy checklist. |
| Scope creep into full school database | MVP delay | Keep school lookup as Should, not blocker for core journey. |

## 12. Definition of Ready

A story is ready when:

- User value is clear.
- Priority is set to Must/Should/Could.
- Acceptance criteria are written in Given/When/Then.
- Dependencies are identified.
- Required business decision or assumption is documented.
- Required copy/content is available or task exists to produce it.
- Data fields and PII impact are understood if relevant.
- Test approach is clear enough for QA.

## 13. Definition of Done

A story is done when:

- Implementation meets acceptance criteria.
- Unit/integration/conversation tests pass for the story scope.
- Logs and error handling are implemented for production debugging.
- PII is masked in logs where relevant.
- Guardrails are respected: no visa guarantee, no fake documents, no unverified official data.
- Bot copy uses `trung tâm/bạn` tone unless explicitly overridden.
- Configurable values are not hardcoded when admin/config is required.
- Documentation is updated if API, schema, prompt, rule or config changes.
- Product owner or BA signs off conversation behavior for user-facing flows.
