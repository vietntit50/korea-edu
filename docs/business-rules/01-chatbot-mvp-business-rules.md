# Business Rules - Facebook Messenger Chatbot MVP

## 1. Rule Design Principles

- Rule engine phai ho tro **journey-first/need-first**: user moi duoc hoi nhu cau, ho so, ngan sach, muc tieu va khu vuc truoc khi goi y truong.
- LLM khong phai source of truth. LLM chi ho tro classify intent, extract slots, generate/format response theo du lieu da verified.
- Tat ca chi phi phai lay tu `CostRangeTemplate`/config, khong de LLM tu tinh hoac tu bia so.
- Tat ca thong tin truong/nganh/hoc bong/KTX/hoc phi cu the phai lay tu database verified. Neu khong co, bot phai noi can tu van vien xac nhan.
- Bot chi duoc dung cac cum: "phu hop so bo", "can xac nhan", "co rui ro can kiem tra them". Khong dung: "chac chan di duoc", "chac chan dau visa", "khong the di".
- SDT khong bat buoc. Chi bat buoc neu user muon duoc goi lai/dat lich qua dien thoai.
- Tone mac dinh: `trung tam/ban`.
- Moi rule co the versioned va audit khi admin thay doi.

## 2. Rule Categories

| Category | Purpose | Source of Truth | Output |
|---|---|---|---|
| Lead scoring | Phan loai chat/lead A/B/C/D | Rule config + profile slots | `lead_score`, `next_action` |
| Eligibility risk | Danh gia rui ro LOW/MEDIUM/HIGH | Rule config + profile slots | `risk_level`, `risk_reasons` |
| Cost estimator | Uoc tinh range chi phi | `CostRangeTemplate` | `cost_range`, `disclaimer_required` |
| Handoff | Quyet dinh chuyen tu van vien | Trigger matrix | `handoff_required`, `handoff_reason` |
| Lead creation | Quyet dinh tao/update lead | Lead trigger rules | `lead_status`, `lead_fields` |
| Disclaimer | Chen canh bao bat buoc | Disclaimer config | `disclaimer_text` |
| Guardrail | Chan unsafe/high-risk response | Guardrail policy | `blocked`, `safe_response`, `handoff_required` |
| Admin config | Cho phep business thay rule | `BotConfiguration` | Versioned config |

## 3. Lead Scoring Rules

### 3.1. Scoring Inputs

| Input | Type | Notes |
|---|---|---|
| `education_level` | enum | THPT/CD/DH/dang hoc/chua ro |
| `graduation_year` | number | Dung tinh gap year |
| `gpa` | decimal/string | Mac dinh thang 10 neu user Viet Nam; hoi xac nhan neu khong chac |
| `korean_level` | enum | none/basic/intermediate/advanced |
| `topik_level` | enum | none/TOPIK I/TOPIK II/cap cu the |
| `budget_vnd` | number/range | Sensitive financial |
| `desired_pathway` | enum | D4-1/D2/UNKNOWN |
| `desired_region` | enum | LOW_COST_PROVINCE/MAJOR_CITY/NEAR_SEOUL/SEOUL_TOP/UNKNOWN |
| `desired_intake` | string | Ky/nam du kien |
| `need_type` | enum | PROCESS/COST/ELIGIBILITY/SCHOOL/HANDOFF |
| `phone` | string | Optional; tang readiness nhung khong bat buoc |
| `risk_level` | enum | Tu eligibility risk rules |

### Rule ID: LEAD_SCORE_A_READY_HOT

- Category: Lead scoring.
- Priority: High.
- Description: User co nhu cau ro, ho so tuong doi day du, ngan sach phu hop, san sang de tu van vien tiep tuc.
- Input: `education_level`, `graduation_year`, `gpa`, `budget_vnd`, `desired_pathway`, `desired_region`, `need_type`, `risk_level`.
- Condition:
  - Co it nhat 5/8 slot chinh: hoc van, nam tot nghiep, GPA, ngan sach, pathway, region, intake, muc tieu.
  - `risk_level=LOW`.
  - `budget_vnd` nam trong hoac cao hon range toi thieu cua pathway/region mong muon.
  - User hoi chi phi cu the, check ho so, muon nop ho so, hoac muon tu van vien.
- Output: `lead_score=A`, `next_action=PRIORITY_HANDOFF`.
- Bot behavior: Noi "ho so co kha nang phu hop so bo, nen de tu van vien kiem tra chi tiet".
- Backend action: Update lead/profile; create handoff neu user dong y hoac da yeu cau.
- Handoff required: Recommended; required neu user muon tu van vien/bao gia.
- Disclaimer required: Eligibility/visa disclaimer neu co noi ve kha nang di/visa.
- Example: User tot nghiep 2025, GPA 7.5, ngan sach 350 trieu, muon D4-1 gan Seoul, muon di ky gan -> score A.
- Business owner confirmation needed: Yes, nguong "budget phu hop" theo cost range can phe duyet.

### Rule ID: LEAD_SCORE_B_POTENTIAL

- Category: Lead scoring.
- Priority: Medium.
- Description: User co tiem nang nhung thieu tieng Han, thieu mot so thong tin hoac ngan sach can tinh lai.
- Input: Profile slots + `risk_level`.
- Condition:
  - Co it nhat 3/8 slot chinh.
  - `risk_level=LOW` hoac `MEDIUM`.
  - Ngan sach chua ro hoac hoi sat range toi thieu.
  - Chua ro pathway/region nhung user tiep tuc tuong tac.
- Output: `lead_score=B`, `next_action=COLLECT_MORE_OR_HANDOFF_OFFER`.
- Bot behavior: Hoi them 1-2 slot quan trong hoac de xuat tu van vien check.
- Backend action: Update partial lead/profile; handoff optional.
- Handoff required: Optional, recommended neu user co nhu cau gan.
- Disclaimer required: Theo topic.
- Example: GPA 6.8, tot nghiep 2024, chua TOPIK, ngan sach 250 trieu, chua ro region -> score B.
- Business owner confirmation needed: Yes.

### Rule ID: LEAD_SCORE_C_RISK_REVIEW

- Category: Lead scoring.
- Priority: High.
- Description: User co rui ro can tu van vien danh gia.
- Input: `risk_level`, `risk_reasons`, profile slots.
- Condition:
  - `risk_level=HIGH`; or
  - Gap year dai; or
  - GPA thap; or
  - Ngan sach duoi range toi thieu; or
  - Co lich su truot visa; or
  - User hoi case visa/ho so phuc tap.
- Output: `lead_score=C`, `next_action=HANDOFF_RECOMMENDED`.
- Bot behavior: Noi co mot so rui ro can kiem tra them, khong ket luan xau.
- Backend action: Save `risk_warning`; offer handoff.
- Handoff required: Recommended; required neu user muon ket luan ho so/visa.
- Disclaimer required: Visa/eligibility disclaimer.
- Example: Tot nghiep 2019, GPA 5.8, chua TOPIK, ngan sach 180 trieu, hoi "co dau visa khong" -> score C.
- Business owner confirmation needed: Yes, nguong GPA/gap year.

### Rule ID: LEAD_SCORE_D_NURTURE

- Category: Lead scoring.
- Priority: Medium.
- Description: User chua phu hop de tu van sau hoac qua thieu du lieu.
- Input: Profile slots, interaction behavior.
- Condition:
  - Thieu hau het slot chinh va user chua co nhu cau ro; or
  - Ngan sach qua thap so voi moi range MVP; or
  - User chi doc thong tin chung va khong muon tiep tuc.
- Output: `lead_score=D`, `next_action=NURTURE_CONTENT`.
- Bot behavior: Dua thong tin co ban, goi y quay lai khi co them GPA/ngan sach/muc tieu.
- Backend action: Save partial profile by PSID; no forced lead phone.
- Handoff required: No, unless user asks.
- Disclaimer required: Theo topic.
- Example: User chi hoi "du hoc Han la gi", khong cung cap profile, khong muon check -> score D/unknown nurture.
- Business owner confirmation needed: No for MVP behavior; yes if remarketing rules added.

## 4. Eligibility Risk Rules

### 4.1. MVP Assumptions Can Duyet

Cac nguong duoi day la assumption de dev co rule ban dau. Business owner can phe duyet/chinh lai truoc go-live:

- GPA `>= 7.0`: thuan loi hon.
- GPA `6.0-6.9`: can check them.
- GPA `< 6.0`: rui ro cao hon.
- Gap year `0-2 nam`: rui ro thap hon.
- Gap year `3-4 nam`: can giai trinh/kiem tra.
- Gap year `>=5 nam`: rui ro cao hon.
- Budget duoi range toi thieu cua pathway/region mong muon: rui ro tai chinh.

### Rule ID: RISK_LOW_CLEAR_PROFILE

- Category: Eligibility risk.
- Priority: Medium.
- Description: Ho so co du thong tin va khong co risk flag lon.
- Input: GPA, graduation_year, budget, topik/korean_level, desired_pathway, desired_region, visa_history.
- Condition:
  - GPA >= 7.0.
  - Gap year <= 2 nam.
  - Budget >= min range cua pathway/region mong muon.
  - Khong co lich su truot visa hoac user chua khai bao risk.
- Output: `risk_level=LOW`, `risk_reasons=[]`.
- Bot behavior: "Ho so co kha nang phu hop so bo, can tu van vien xac nhan".
- Backend action: Save assessment.
- Handoff required: Optional.
- Disclaimer required: Eligibility/visa if mentioned.
- Example: GPA 7.4, tot nghiep 2025, ngan sach 320 trieu, D4-1 gan Seoul.
- Business owner confirmation needed: Yes.

### Rule ID: RISK_MEDIUM_NEEDS_REVIEW

- Category: Eligibility risk.
- Priority: High.
- Description: Ho so co mot so diem can kiem tra them.
- Input: Same as above.
- Condition: Any of:
  - GPA 6.0-6.9.
  - Gap year 3-4 nam.
  - Chua co tieng Han/TOPIK nhung muon D2.
  - Budget hoi sat min range.
  - Chua ro pathway hoac region.
- Output: `risk_level=MEDIUM`, `risk_reasons`.
- Bot behavior: Noi ro diem can bo sung/kiem tra.
- Backend action: Save risk reasons; recommend handoff if user wants specific conclusion.
- Handoff required: Recommended for detailed advice.
- Disclaimer required: Eligibility/visa.
- Example: GPA 6.5, tot nghiep 2022, chua TOPIK, muon D2.
- Business owner confirmation needed: Yes.

### Rule ID: RISK_HIGH_HUMAN_REVIEW

- Category: Eligibility risk.
- Priority: Critical.
- Description: Case rui ro cao can tu van vien danh gia.
- Input: Same as above.
- Condition: Any of:
  - GPA < 6.0.
  - Gap year >= 5 nam.
  - Budget duoi min range cua pathway/region mong muon.
  - Co lich su truot visa.
  - User hoi "bao do visa", "lam ho so sao cho qua", "khai khac duoc khong".
  - Ho so phuc tap khac do advisor/business cau hinh.
- Output: `risk_level=HIGH`, `next_action=HANDOFF_RECOMMENDED`, `risk_reasons`.
- Bot behavior: Khong ket luan "khong the di"; noi can kiem tra ky.
- Backend action: Create risk warning; offer handoff.
- Handoff required: Yes if user asks for conclusion/next step.
- Disclaimer required: Visa/eligibility.
- Example: Tot nghiep 2018, GPA 5.5, ngan sach 180 trieu, hoi co bao dau visa khong.
- Business owner confirmation needed: Yes.

### Rule ID: RISK_UNKNOWN_INSUFFICIENT_DATA

- Category: Eligibility risk.
- Priority: Medium.
- Description: Chua du du lieu de danh gia.
- Input: Available slots.
- Condition: Missing 2 or more core slots among education_level, graduation_year, gpa, budget.
- Output: `risk_level=UNKNOWN`, `missing_information`.
- Bot behavior: Hoi tiep 1-3 slot quan trong, khong cham ket luan.
- Backend action: Save partial profile.
- Handoff required: No, unless user wants human.
- Disclaimer required: No, unless visa/eligibility conclusion is discussed.
- Example: User chi noi "em co di duoc khong" -> ask for graduation year, GPA, budget.
- Business owner confirmation needed: No.

## 5. General Cost Estimator Rules

### 5.1. Cost Range Config Keys

| Config Key | Pathway | Region Group | Proposed Range |
|---|---|---|---:|
| `cost.d4_1.low_cost_province` | D4-1 | Tinh tiet kiem | 180-240 trieu VND |
| `cost.d4_1.major_city` | D4-1 | Thanh pho lon ngoai Seoul | 220-300 trieu VND |
| `cost.d4_1.near_seoul` | D4-1 | Gan Seoul/Gyeonggi | 240-340 trieu VND |
| `cost.d4_1.seoul_top` | D4-1 | Seoul/top school | 300-450 trieu VND |
| `cost.d2.low_cost_province` | D2 | Tinh tiet kiem | 230-320 trieu VND |
| `cost.d2.major_city` | D2 | Thanh pho lon ngoai Seoul | 280-380 trieu VND |
| `cost.d2.near_seoul` | D2 | Gan Seoul/Gyeonggi | 320-450 trieu VND |
| `cost.d2.seoul_top` | D2 | Seoul/top school | 400-600 trieu VND |
| `cost.unknown.unknown` | UNKNOWN | UNKNOWN | 220-450 trieu VND |

Trang thai: Proposed. Can business owner phe duyet truoc go-live.

### Rule ID: COST_ESTIMATE_KNOWN_PATHWAY_REGION

- Category: Cost estimator.
- Priority: Medium.
- Description: Tra range khi da biet pathway va region.
- Input: `desired_pathway`, `desired_region`, optional `budget_vnd`.
- Condition: Pathway in D4-1/D2 and region group known.
- Output: `cost_range`, `components`, `disclaimer_required=true`.
- Bot behavior: Tra range theo config, noi la uoc tinh nam dau/12 thang dau.
- Backend action: Query `CostRangeTemplate`; log estimate.
- Handoff required: No, unless user asks official quote.
- Disclaimer required: Yes.
- Example: D4-1 + near Seoul -> 240-340 trieu VND.
- Business owner confirmation needed: Yes.

### Rule ID: COST_ESTIMATE_UNKNOWN_PATHWAY

- Category: Cost estimator.
- Priority: Medium.
- Description: Neu chua biet D4-1/D2, dung range rong va hoi pathway.
- Input: `desired_pathway=UNKNOWN`, `desired_region`.
- Condition: Pathway missing/unknown.
- Output: Broad range or prompt for pathway.
- Bot behavior: Noi chi phi phu thuoc D4-1/D2, hoi muc tieu hoc.
- Backend action: Do not calculate exact pathway; use `cost.unknown.unknown` if needed.
- Handoff required: No.
- Disclaimer required: Yes if range shown.
- Example: User hoi "di Han het bao nhieu" -> ask D4-1/D2.
- Business owner confirmation needed: No.

### Rule ID: COST_ESTIMATE_UNKNOWN_REGION

- Category: Cost estimator.
- Priority: Medium.
- Description: Neu chua biet khu vuc, dua so sanh group va hoi region.
- Input: `desired_pathway`, `desired_region=UNKNOWN`.
- Condition: Region missing/unknown.
- Output: Prompt for region; optionally show low/mid/high grouping.
- Bot behavior: Hoi Seoul, gan Seoul, tinh chi phi thap hay chua ro.
- Backend action: No exact region range unless using broad range.
- Handoff required: No.
- Disclaimer required: Yes if numbers shown.
- Example: User biet D4-1 nhung chua biet khu vuc.
- Business owner confirmation needed: No.

### Rule ID: COST_BUDGET_MATCH

- Category: Cost estimator.
- Priority: Medium.
- Description: Danh gia ngan sach user voi range da tinh.
- Input: `budget_vnd`, `cost_range.min`, `cost_range.max`.
- Condition:
  - Budget >= min and <= max: `BUDGET_WITHIN_RANGE`.
  - Budget < min: `BUDGET_BELOW_RANGE`.
  - Budget > max: `BUDGET_ABOVE_RANGE`.
- Output: `budget_fit`.
- Bot behavior:
  - Within: "co the nam trong nhom can xem them".
  - Below: "co the can toi uu khu vuc/lo trinh, can check ky".
  - Above: "co nhieu lua chon hon, van can check theo truong".
- Backend action: Save budget fit.
- Handoff required: Recommended if below range and user wants plan.
- Disclaimer required: Yes.
- Example: Budget 200M for D4-1 Seoul/top -> below range.
- Business owner confirmation needed: Yes, after cost range approved.

### Rule ID: COST_OFFICIAL_QUOTE_REQUEST

- Category: Cost estimator/handoff.
- Priority: High.
- Description: User hoi bao gia/hoc phi chinh thuc.
- Input: raw message, intent.
- Condition: Message includes "phi chinh thuc", "bao gia", "hoc phi truong X", "tong tien chot", "dong bao nhieu".
- Output: `handoff_required=true`, `handoff_reason=OFFICIAL_COST`.
- Bot behavior: Noi can tu van vien xac nhan theo truong/ky/ho so.
- Backend action: Offer handoff; create if accepted.
- Handoff required: Yes/recommended.
- Disclaimer required: Yes.
- Example: "Dongguk chot het bao nhieu tien?".
- Business owner confirmation needed: No.

## 6. Human Handoff Rules

### Rule ID: HANDOFF_USER_REQUESTED

- Category: Human handoff.
- Priority: Critical.
- Description: User yeu cau gap nguoi that.
- Input: intent/message.
- Condition: User clicks `Gap tu van vien` or says "gap tu van vien", "goi lai", "nguoi that".
- Output: `handoff_required=true`, `handoff_reason=USER_REQUESTED`.
- Bot behavior: Hoi tiep Messenger hay de lai SDT goi lai.
- Backend action: Create `HandoffRequest`; create/update lead if phone provided.
- Handoff required: Yes.
- Disclaimer required: No, unless previous topic requires.
- Example: "Cho minh gap tu van vien".
- Business owner confirmation needed: No.

### Rule ID: HANDOFF_PHONE_PROVIDED_CALLBACK

- Category: Human handoff/lead creation.
- Priority: High.
- Description: User de lai SDT va muon duoc lien he.
- Input: `phone`, context.
- Condition: Valid phone detected and user intent is callback/advisor/consultation.
- Output: `lead_status=new_or_updated`, `handoff_reason=CALLBACK_REQUESTED`.
- Bot behavior: Xac nhan da nhan SDT va noi tu van vien se phan hoi trong gio lam viec.
- Backend action: Create/update lead; create handoff; notify advisor.
- Handoff required: Yes.
- Disclaimer required: No, unless topic requires.
- Example: "Sdt minh 090..." sau khi bot hoi goi lai.
- Business owner confirmation needed: No.

### Rule ID: HANDOFF_RISK_REVIEW

- Category: Human handoff.
- Priority: High.
- Description: Ho so rui ro can tu van vien danh gia.
- Input: `risk_level`, `risk_reasons`.
- Condition: `risk_level=HIGH` or risk reasons include visa history, long gap year, low GPA, budget below range.
- Output: `handoff_recommended=true`, `handoff_reason=RISK_REVIEW`.
- Bot behavior: Noi can kiem tra ky, offer advisor.
- Backend action: Save risk warning; create handoff if accepted.
- Handoff required: Recommended; required if user asks conclusion.
- Disclaimer required: Visa/eligibility.
- Example: GPA 5.5, gap year 6 nam.
- Business owner confirmation needed: Yes for risk thresholds.

### Rule ID: HANDOFF_UNVERIFIED_SCHOOL_DATA

- Category: Human handoff.
- Priority: High.
- Description: User hoi data truong/nganh/phi/KTX/hoc bong chua verified.
- Input: `school_name`, lookup result.
- Condition: School DB no result, stale, or `verified=false`.
- Output: `handoff_recommended=true`, `handoff_reason=UNVERIFIED_SCHOOL_DATA`.
- Bot behavior: Noi can tu van vien xac nhan tu nguon chinh thuc, khong bia.
- Backend action: Log missing data; offer handoff.
- Handoff required: Recommended.
- Disclaimer required: School data disclaimer.
- Example: "Truong X co hoc bong bao nhieu?" but no verified record.
- Business owner confirmation needed: No.

### Rule ID: HANDOFF_UNKNOWN_AFTER_TWO

- Category: Human handoff.
- Priority: Medium.
- Description: Bot khong hieu sau 2 lan lien tiep.
- Input: `fallback_count`.
- Condition: `fallback_count >= 2`.
- Output: `handoff_recommended=true`, `handoff_reason=BOT_UNDERSTANDING_FAILED`.
- Bot behavior: Xin loi ngan, de xuat chuyen tu van vien hoac hien menu.
- Backend action: Log unknown messages; create handoff if user accepts.
- Handoff required: Optional.
- Disclaimer required: No.
- Example: User gui message khong lien quan/sai chinh ta qua nhieu.
- Business owner confirmation needed: No.

## 7. Lead Creation Rules

### Rule ID: LEAD_CREATE_PHONE

- Category: Lead creation.
- Priority: High.
- Description: Tao lead khi user cung cap phone hop le.
- Input: `phone`, `facebook_psid`, profile slots.
- Condition: Phone valid and user context indicates consultation/callback/quote/handoff.
- Output: `lead.created_or_updated=true`.
- Bot behavior: Xac nhan da nhan thong tin.
- Backend action: Create/update `Lead`; link session/profile.
- Handoff required: If callback/advisor requested.
- Disclaimer required: No.
- Example: User: "Sdt 090..." after cost result.
- Business owner confirmation needed: No.

### Rule ID: LEAD_CREATE_ELIGIBILITY_COMPLETED

- Category: Lead creation.
- Priority: Medium.
- Description: Tao partial lead/profile khi user hoan thanh eligibility check, ke ca khong co phone.
- Input: `facebook_psid`, eligibility assessment.
- Condition: Eligibility checker returns score/risk.
- Output: `lead_or_profile_status=qualified_partial`.
- Bot behavior: Dua CTA tiep theo, khong ep SDT.
- Backend action: Save profile, assessment; create partial lead if product wants reporting.
- Handoff required: Based on risk/score.
- Disclaimer required: Eligibility/visa.
- Example: User co GPA/nam/budget/pathway, khong de phone.
- Business owner confirmation needed: Yes, co tao `Lead` partial hay chi `UserProfile`.

### Rule ID: LEAD_CREATE_SCORE_A_B

- Category: Lead creation.
- Priority: Medium.
- Description: Tao/update lead khi score A/B.
- Input: `lead_score`, `facebook_psid`, profile slots.
- Condition: `lead_score in [A,B]`.
- Output: `lead_status=qualified`.
- Bot behavior: Offer advisor but do not force phone.
- Backend action: Save lead/profile; notify if A and handoff accepted.
- Handoff required: A recommended, B optional.
- Disclaimer required: Theo topic.
- Example: Score A after eligibility.
- Business owner confirmation needed: Yes, partial lead policy.

### Rule ID: LEAD_NO_PHONE_CONTINUE

- Category: Lead creation.
- Priority: High.
- Description: User khong de phone van duoc tiep tuc.
- Input: User refusal/no phone.
- Condition: User says "chua can", "khong muon de SDT", no phone.
- Output: Continue flow; save PSID profile.
- Bot behavior: "Ban khong can de lai SDT neu chua san sang."
- Backend action: Save partial profile/session only.
- Handoff required: No, unless user wants Messenger handoff.
- Disclaimer required: No.
- Example: User asks cost then declines phone.
- Business owner confirmation needed: No; decided.

## 8. Disclaimer Rules

### Rule ID: DISCLAIMER_COST

- Category: Disclaimer.
- Priority: High.
- Description: Bat buoc khi noi ve chi phi.
- Input: intent/response topic.
- Condition: Intent in `ASK_GENERAL_COST`, `ASK_SCHOOL_COST`, `ASK_BUDGET`, or response includes cost range.
- Output: Add cost disclaimer.
- Bot behavior: Chen disclaimer sau range, co the rut gon neu trong cung session da noi gan day.
- Backend action: Mark `disclaimer_shown.cost=true`.
- Handoff required: No.
- Disclaimer required: Yes.
- Example: "Chi phi uoc tinh 240-340 trieu... Luu y..."
- Business owner confirmation needed: No.

### Rule ID: DISCLAIMER_VISA

- Category: Disclaimer.
- Priority: Critical.
- Description: Bat buoc khi noi ve visa/kha nang dau.
- Input: intent/response topic.
- Condition: Intent `ASK_VISA_GENERAL`, eligibility result mentions visa, user asks "bao do".
- Output: Add visa disclaimer.
- Bot behavior: Khong cam ket ket qua visa.
- Backend action: Log disclaimer shown.
- Handoff required: Recommended if case risk.
- Disclaimer required: Yes.
- Example: "Chatbot khong the cam ket ket qua visa..."
- Business owner confirmation needed: No.

### Rule ID: DISCLAIMER_SCHOOL_VERIFIED_DATA

- Category: Disclaimer.
- Priority: High.
- Description: Bat buoc khi noi ve truong/nganh/hoc bong/KTX.
- Input: school response.
- Condition: Intent in school/major/dorm/scholarship.
- Output: Add school data disclaimer.
- Bot behavior: Noi can kiem tra nguon chinh thuc.
- Backend action: Log source/version if available.
- Handoff required: If no verified data.
- Disclaimer required: Yes.
- Example: "Thong tin truong can duoc kiem tra lai..."
- Business owner confirmation needed: No.

### Rule ID: DISCLAIMER_PART_TIME_WORK

- Category: Disclaimer.
- Priority: High.
- Description: Bat buoc khi user hoi viec lam them.
- Input: intent `ASK_PART_TIME_JOB`.
- Condition: Any part-time work advice.
- Output: Add work compliance disclaimer.
- Bot behavior: Khong khuyen phu thuoc vao lam them hoac lam trai quy dinh.
- Backend action: Log compliance topic.
- Handoff required: If user intends illegal work.
- Disclaimer required: Yes.
- Example: "Viec lam them can tuan thu quy dinh visa..."
- Business owner confirmation needed: No.

## 9. Unsafe & High-Risk Guardrails

### Rule ID: GUARDRAIL_NO_VISA_GUARANTEE

- Category: Guardrail.
- Priority: Critical.
- Description: Chan moi cau tra loi cam ket dau visa.
- Input: user message, draft response.
- Condition: User asks guarantee or draft contains "bao do", "chac chan dau", "dam bao visa".
- Output: `blocked=true` for unsafe draft; safe response with visa disclaimer.
- Bot behavior: Tu choi cam ket, offer risk check/handoff.
- Backend action: Log `UNSAFE_OR_HIGH_RISK`.
- Handoff required: Recommended.
- Disclaimer required: Visa.
- Example: User: "Co bao do visa khong?" -> safe response.
- Business owner confirmation needed: No.

### Rule ID: GUARDRAIL_NO_FALSE_DOCUMENTS

- Category: Guardrail.
- Priority: Critical.
- Description: Khong ho tro khai sai/lam gia ho so.
- Input: user message.
- Condition: Mentions fake documents, false statements, hiding visa history, changing GPA.
- Output: Safe refusal + compliant alternative.
- Bot behavior: Noi trung tam khong ho tro khai sai, co the tu van cach bo sung ho so dung quy dinh.
- Backend action: Log high-risk; handoff optional to senior advisor if needed.
- Handoff required: Optional/high-risk.
- Disclaimer required: Compliance.
- Example: "Lam giay to tai chinh gia duoc khong?"
- Business owner confirmation needed: No.

### Rule ID: GUARDRAIL_NO_ILLEGAL_WORK

- Category: Guardrail.
- Priority: Critical.
- Description: Khong khuyen lam them trai quy dinh.
- Input: user message/draft response.
- Condition: User asks how to work illegally, over permitted hours, fund whole tuition by work.
- Output: Safe response + work disclaimer.
- Bot behavior: Khuyen lap ngan sach an toan, tuan thu quy dinh.
- Backend action: Log compliance topic.
- Handoff required: Optional if user needs financial planning.
- Disclaimer required: Part-time work.
- Example: "Sang do di lam full-time de tra hoc phi duoc khong?"
- Business owner confirmation needed: No.

### Rule ID: GUARDRAIL_NO_UNVERIFIED_NUMBERS

- Category: Guardrail.
- Priority: Critical.
- Description: Khong de LLM tu tao so lieu chi phi/hoc phi/hoc bong.
- Input: draft response, data sources.
- Condition: Draft has numeric cost/scholarship/school fee not backed by config/verified DB.
- Output: Block draft; replace with "can tu van vien xac nhan" or configured range.
- Bot behavior: Chuyen sang safe answer.
- Backend action: Log guardrail block.
- Handoff required: Recommended for official data request.
- Disclaimer required: Cost/school.
- Example: LLM draft: "Dongguk 3,000 USD/ky" without source -> blocked.
- Business owner confirmation needed: No.

### Rule ID: GUARDRAIL_NO_SCHOOL_ADVICE_WITHOUT_PROFILE

- Category: Guardrail.
- Priority: High.
- Description: Khong goi y truong cho user moi khi chua co profile toi thieu.
- Input: intent `ASK_SCHOOL_RECOMMENDATION`, profile slots.
- Condition: Missing budget, region, goal/pathway.
- Output: Ask profile first.
- Bot behavior: "Truoc khi goi y truong, trung tam can hieu ngan sach, muc tieu va khu vuc mong muon."
- Backend action: Route to slot collection.
- Handoff required: No.
- Disclaimer required: No unless school info discussed.
- Example: "Nen hoc truong nao?" -> ask pathway/budget/region.
- Business owner confirmation needed: No.

## 10. Admin Configuration Rules

### Rule ID: CONFIG_VERSIONED_UPDATE

- Category: Admin config.
- Priority: High.
- Description: Moi thay doi config phai versioned va audit.
- Input: config key/value, actor.
- Condition: Admin updates FAQ, cost range, disclaimer, scoring rule, handoff trigger, business hours.
- Output: New config version.
- Bot behavior: Uses active config version.
- Backend action: Write `BotConfiguration` and `AuditLog`.
- Handoff required: No.
- Disclaimer required: No.
- Example: Admin updates `cost.d4_1.near_seoul`.
- Business owner confirmation needed: No.

### Rule ID: CONFIG_COST_APPROVAL_REQUIRED

- Category: Admin config.
- Priority: High.
- Description: Cost range can be edited but go-live needs approved flag.
- Input: cost range config.
- Condition: `approved=false`.
- Output: Allow staging/test; block production use or show internal warning.
- Bot behavior: In production, use last approved version.
- Backend action: Enforce approved version.
- Handoff required: No.
- Disclaimer required: Cost.
- Example: Proposed range updated but not approved.
- Business owner confirmation needed: Yes.

### Rule ID: CONFIG_BUSINESS_HOURS_DEFAULT

- Category: Admin config.
- Priority: Medium.
- Description: Neu chua chot ngoai gio, dung default safe SLA.
- Input: business hours config.
- Condition: No confirmed business hours.
- Output: `outside_hours_message=tu van vien se phan hoi vao gio lam viec tiep theo`.
- Bot behavior: Khong hua phan hoi ngay ngoai gio.
- Backend action: Use default message.
- Handoff required: No.
- Disclaimer required: No.
- Example: Handoff at 22:00.
- Business owner confirmation needed: Yes before go-live.

## 11. Rule Precedence

Khi nhieu rule cung match, xu ly theo thu tu:

1. Unsafe/high-risk guardrail.
2. Human handoff required.
3. Lead capture/phone detection.
4. Eligibility check/risk scoring.
5. Cost estimator.
6. School lookup.
7. FAQ/general answer.
8. Slot collection/follow-up.
9. Fallback.

Chi tiet:

- Guardrail luon co quyen block draft response.
- Handoff khong bat buoc SDT, tru khi user muon goi lai bang dien thoai.
- Lead capture khong duoc lam mat context intent hien tai.
- Cost/School response phai qua disclaimer rule truoc khi gui.
- LLM response generation chi chay sau khi rule/data source da xac dinh noi dung an toan.

## 12. Example Rule Evaluations

| Example | Input | Matched Rules | Output |
|---|---|---|---|
| User hoi chi phi chung | "Di Han het bao nhieu?" | `COST_ESTIMATE_UNKNOWN_PATHWAY`, `DISCLAIMER_COST` | Hoi D4-1/D2, co the dua range rong neu can. |
| User co budget D4-1 gan Seoul | D4-1, near Seoul, budget 280M | `COST_ESTIMATE_KNOWN_PATHWAY_REGION`, `COST_BUDGET_MATCH`, `DISCLAIMER_COST` | Range 240-340M, budget within range, disclaimer. |
| User hoi "200 trieu di Seoul du khong?" | budget 200M, Seoul/top | `COST_BUDGET_MATCH`, `HANDOFF_RISK_REVIEW` optional | Noi co the sat/thap so voi Seoul, de xuat xem tinh/gan Seoul va check TVV. |
| GPA 6.5, tot nghiep 2023, chua TOPIK | GPA 6.5, gap 3 nam if current 2026, no TOPIK | `RISK_MEDIUM_NEEDS_REVIEW`, `LEAD_SCORE_B_POTENTIAL` | Risk MEDIUM, score B, hoi budget/pathway/region. |
| Tot nghiep 2018, GPA 5.5 | gap >=5, GPA <6 | `RISK_HIGH_HUMAN_REVIEW`, `LEAD_SCORE_C_RISK_REVIEW` | Risk HIGH, score C, offer handoff. |
| User hoi "bao do visa khong?" | visa guarantee | `GUARDRAIL_NO_VISA_GUARANTEE`, `DISCLAIMER_VISA`, `HANDOFF_RISK_REVIEW` | Safe refusal, visa disclaimer, offer risk check. |
| User hoi truong cu the khong co DB | school_name present, no verified data | `HANDOFF_UNVERIFIED_SCHOOL_DATA`, `DISCLAIMER_SCHOOL_VERIFIED_DATA` | Noi can TVV xac nhan, offer handoff. |
| User de lai SDT | valid phone | `LEAD_CREATE_PHONE`, maybe `HANDOFF_PHONE_PROVIDED_CALLBACK` | Create/update lead, handoff if callback intent. |
| User khong muon de SDT | no phone/refusal | `LEAD_NO_PHONE_CONTINUE` | Continue in Messenger, save partial profile. |

## 13. Rules Requiring Business Owner Confirmation

| Item | Why Needed | Proposed Default | Required Before |
|---|---|---|---|
| Cost ranges | Bot se noi con so uoc tinh voi user | Use decision log proposed ranges | Production go-live |
| GPA thresholds | Anh huong lead score/risk | >=7 low, 6-6.9 medium, <6 high | Eligibility go-live |
| Gap year thresholds | Anh huong risk warning | 0-2 low, 3-4 medium, >=5 high | Eligibility go-live |
| Budget fit behavior | Anh huong goi y khu vuc/pathway | Compare with min/max cost range | Cost flow go-live |
| Partial lead policy | Co tao `Lead` khi khong co phone hay chi luu `UserProfile` | Save `UserProfile`; create partial lead after eligibility completed | Lead reporting go-live |
| Business hours/SLA | Bot noi khi nao tu van vien phan hoi | Ngoai gio: phan hoi vao gio lam viec tiep theo; A in 15 min, B in 2h during hours | Handoff go-live |
| Advisor assignment | Ai nhan hot lead | Round-robin or default advisor queue | Handoff go-live |
| PII retention | Luu/xoa data dung policy | Raw conversation 12 thang, lead theo CRM policy | Production go-live |

## 14. MVP Configuration Checklist

### Cost Config

- [ ] `cost.d4_1.low_cost_province`
- [ ] `cost.d4_1.major_city`
- [ ] `cost.d4_1.near_seoul`
- [ ] `cost.d4_1.seoul_top`
- [ ] `cost.d2.low_cost_province`
- [ ] `cost.d2.major_city`
- [ ] `cost.d2.near_seoul`
- [ ] `cost.d2.seoul_top`
- [ ] `cost.unknown.unknown`
- [ ] Cost disclaimer text.
- [ ] Approved version flag.

### Eligibility Config

- [ ] GPA low/medium/high thresholds.
- [ ] Gap year thresholds.
- [ ] Budget fit thresholds.
- [ ] TOPIK/pathway compatibility rules.
- [ ] Visa history risk flag.
- [ ] Missing information behavior.

### Lead/Handoff Config

- [ ] Lead score A/B/C/D definitions.
- [ ] Handoff trigger matrix.
- [ ] Business hours.
- [ ] Advisor assignment rule.
- [ ] Notification channel.
- [ ] Optional phone copy.

### Guardrail Config

- [ ] Visa guarantee blocklist.
- [ ] False document blocklist.
- [ ] Illegal work blocklist.
- [ ] Unverified number guardrail.
- [ ] School data verified-source rule.

### Admin/Content Config

- [ ] FAQ list.
- [ ] Menu labels and payloads.
- [ ] Quick replies.
- [ ] Fallback messages.
- [ ] Disclaimer messages.
- [ ] Audit log enabled.
