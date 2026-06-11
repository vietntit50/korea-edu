# Conversation Flows - Facebook Messenger Chatbot MVP

## 1. Conversation Design Principles

- Bot di theo huong **journey-first/need-first**: hoi nhu cau, ho so, ngan sach, muc tieu va khu vuc truoc khi goi y truong.
- Bot khong hoi user moi ngay "ban muon chon truong nao?".
- Bot la tu van vien cap 1, khong thay the tu van vien that.
- Bot tra loi ngan truoc, hoi tiep sau.
- Bot khong dua thong tin truong/hoc phi/hoc bong/visa neu chua co source verified.
- Chi phi luon la range uoc tinh, khong phai bao gia chinh thuc.
- SDT khong bat buoc. Chi hoi SDT khi user muon goi lai, dat lich, nhan bao gia chi tiet hoac handoff.
- Tone mac dinh da chot: **"trung tam/ban"**.

## 2. Global Bot Behavior

### 2.1. Tone of Voice

- Than thien, ro rang, khong qua hoc thuat.
- Xung ho mac dinh: `trung tam/ban`.
- Voi phu huynh, bot co the dung `trung tam/anh chi` neu user tu nhan la phu huynh.
- Khong tao ap luc chot sale.
- Khong noi "chac chan", "bao do", "dam bao visa".

Copy mau:

```text
Trung tam co the ho tro ban check so bo lo trinh, chi phi va rui ro ho so. De bat dau, ban muon tim hieu muc nao truoc?
```

### 2.2. Message Length

- Moi bot turn nen co 1 y chinh va 1 CTA.
- Neu noi dung dai, dung bullet hoac bang ngan.
- Khong gui qua 2 message lien tiep tru khi Messenger UI can tach bang/quick replies.

### 2.3. Quick Reply Rules

Quick replies nen dung cho:

- Menu 7 muc.
- Pathway: `D4-1 hoc tieng`, `D2 chuyen nganh`, `Chua ro`.
- Region: `Seoul`, `Gan Seoul/Gyeonggi`, `Tinh chi phi thap`, `Chua ro`.
- Budget range: `<200 trieu`, `200-300 trieu`, `300-450 trieu`, `>450 trieu`, `Chua ro`.
- CTA: `Check ho so`, `Uoc tinh chi phi`, `Gap tu van vien`.

Payload phai on dinh, vi du `MENU_GENERAL_COST`, khong phu thuoc label hien thi.

### 2.4. Slot Collection Rules

- Uu tien hoi 1-3 slot quan trong nhat.
- Chap nhan user tra loi tu nhien.
- Neu user dua nhieu thong tin trong mot message, LLM slot extraction tach va luu tung slot.
- Moi slot can luu `value`, `source`, `confidence`, `updated_at`.
- Neu slot confidence thap, bot hoi xac nhan.
- Neu user khong biet thong tin, luu `UNKNOWN` va tiep tuc flow.

### 2.5. Fallback Rules

- Lan 1 khong hieu: hoi lai bang 2-3 lua chon gan nhat.
- Lan 2 khong hieu: hien menu rut gon va de xuat gap tu van vien.
- Sau 2 lan lien tiep `UNKNOWN`, tao `HandoffRequest` neu user dong y hoac co lead context.

Fallback copy:

```text
Trung tam chua hieu ro y ban. Ban muon hoi ve chi phi, check ho so hay gap tu van vien?
```

### 2.6. Disclaimer Rules

Chi phi:

```text
Luu y: Chi phi tren la uoc tinh ban dau va co the thay doi theo truong, ky nhap hoc, ty gia, KTX, sinh hoat va chinh sach tung thoi diem. Tu van vien se kiem tra lai truoc khi gui phuong an chinh thuc.
```

Visa:

```text
Chatbot khong the cam ket ket qua visa. Ket qua phu thuoc vao ho so, tai chinh, hoc luc, phong van va quyet dinh cua co quan xet duyet.
```

Truong/nganh/hoc bong:

```text
Thong tin truong, nganh va hoc bong can duoc kiem tra lai tu nguon chinh thuc cua truong truoc khi tu van phuong an cuoi cung.
```

Viec lam them:

```text
Ban khong nen lap ke hoach tai chinh dua chu yeu vao viec lam them. Viec lam them can tuan thu quy dinh visa va quy dinh tai Han Quoc.
```

### 2.7. Handoff Rules

Handoff khi:

- User yeu cau gap tu van vien.
- User de lai SDT va muon duoc goi lai.
- User hoi bao gia/hoc phi chinh thuc.
- User hoi visa case rui ro.
- User co ho so phuc tap: gap year dai, GPA thap, ngan sach sat nguong, tung truot visa.
- User muon nop ho so.
- Bot khong hieu sau 2 lan.
- Du lieu truong/hoc bong/KTX chua verified.

Neu khong co SDT, van tao handoff theo PSID va hoi:

```text
Trung tam co the chuyen cau hoi nay cho tu van vien. Neu ban muon duoc goi lai, ban co the de lai so dien thoai; neu chua tien de lai SDT, tu van vien van co the tiep tuc nhan trong Messenger.
```

## 3. Global Menu

### 3.1. Greeting Message

```text
Chao ban, trung tam co the ho tro ban tim hieu du hoc Han Quoc theo tung buoc: quy trinh, chi phi, check ho so, chon khu vuc va lo trinh D4-1/D2.

Ban muon tim hieu muc nao truoc?
```

### 3.2. Menu Options

| Label | Payload | Intent |
|---|---|---|
| Quy trinh du hoc Han | `MENU_PROCESS` | `ASK_PROCESS` |
| Tong chi phi can chuan bi | `MENU_GENERAL_COST` | `ASK_GENERAL_COST` |
| Check ho so co di duoc khong | `MENU_ELIGIBILITY` | `CHECK_ELIGIBILITY` |
| Nen chon Seoul hay tinh | `MENU_REGION` | `ASK_REGION_ADVICE` |
| D4-1 va D2 khac nhau the nao | `MENU_D4_D2` | `ASK_D4_D2` |
| Tim truong theo ngan sach/khu vuc | `MENU_SCHOOL_SEARCH` | `ASK_SCHOOL_RECOMMENDATION` |
| Gap tu van vien | `MENU_HUMAN_HANDOFF` | `HUMAN_HANDOFF` |

## 4. Flow A: User moi tim hieu

### Flow Objective

Giup user moi hieu cach bat dau, khong day vao school-first, sau do huong sang quy trinh, chi phi hoac check ho so.

### Entry Triggers

- "Em/ban muon tim hieu du hoc Han"
- "Tu van du hoc Han"
- "Du hoc Han can gi"
- User comment/inbox tu fanpage ma chua co context.

### Required Slots

- `facebook_psid`
- `need_type`

### Optional Slots

- `education_level`
- `graduation_year`
- `desired_intake`
- `desired_pathway`
- `budget_vnd`
- `desired_region`

### Step-by-Step Script

| Step | User Input / Condition | Bot Message | Quick Replies / Buttons | Slots Updated | Backend Action | Next State |
|---|---|---|---|---|---|---|
| A1 | New session or greeting intent | Chao ban, trung tam co the ho tro ban tim hieu du hoc Han Quoc theo tung buoc: quy trinh, chi phi, check ho so, chon khu vuc va D4-1/D2. Ban muon tim hieu muc nao truoc? | 7 global menu options | `facebook_psid` | Create/get `ConversationSession`; log `GREETING` | `MENU_SHOWN` |
| A2 | User selects `MENU_PROCESS` | Quy trinh co ban gom: 1. Dinh huong muc tieu; 2. Check ho so; 3. Chon D4-1/D2 va khu vuc; 4. Chuan bi tai chinh/giay to; 5. Nop truong; 6. Xin visa; 7. Chuan bi bay. Ban da tot nghiep THPT/DH chua va du kien di nam nao? | `Da tot nghiep THPT`, `Dang hoc THPT`, `Da tot nghiep DH/CD`, `Chua ro` | `need_type=PROCESS_INFO` | Log `ASK_PROCESS` | `COLLECT_BASIC_PROFILE` |
| A3 | User answers education/intake | Cam on ban. De trung tam goi y buoc tiep theo sat hon, ban muon check ho so so bo hay uoc tinh chi phi truoc? | `Check ho so`, `Uoc tinh chi phi`, `Tim hieu D4-1/D2`, `Gap tu van vien` | `education_level`, `graduation_year` or `desired_intake` | Extract slots; update `UserProfile` | `NEXT_NEED_SELECTION` |
| A4 | User chooses check/cost/pathway | Route to selected flow. | Depends on selected flow | `need_type` | Route intent | Flow B/C/D |

### Fallback Handling

- Neu user noi qua chung: hien menu.
- Neu user hoi nhieu y, bot uu tien y co intent ro nhat va noi: "Trung tam se tra loi phan nay truoc, sau do minh quay lai phan con lai."

### Disclaimer Placement

Khong can disclaimer trong greeting. Neu user hoi visa/chi phi ngay trong flow, chen disclaimer theo topic.

### Lead Capture Moment

Khong hoi SDT o greeting. Chi hoi khi user muon tu van vien goi lai hoac sau khi da nhan duoc value.

### Human Handoff Trigger

- User chon `Gap tu van vien`.
- User noi "toi muon nop ho so", "goi lai cho toi".

### Exit Conditions

- User chon flow tiep theo.
- User duoc handoff.
- User im lang: session giu `MENU_SHOWN`.

### Edge Cases

- User la phu huynh: doi copy sang `anh/chi` va hoi thong tin cua con.
- User chi gui sticker/emoji: fallback lan 1.

### Acceptance Criteria

- Given user moi nhan "tu van du hoc Han", When bot tra loi, Then bot hien menu journey-first 7 muc.
- Given user chon quy trinh, Then bot khong hoi truong ngay ma hoi tinh trang hoc/du kien di.
- Given user chua de lai SDT, Then bot van tiep tuc flow binh thuong.

## 5. Flow B: User hoi chi phi

### Flow Objective

Tra loi cau hoi chi phi bang range uoc tinh theo pathway/khu vuc, thu thap ngan sach va huong user den check ho so/handoff neu can.

### Entry Triggers

- "Di Han het bao nhieu tien?"
- "Tong chi phi du hoc Han"
- "200 trieu di duoc khong?"
- User chon `MENU_GENERAL_COST`.

### Required Slots

- `facebook_psid`
- `need_type=GENERAL_COST`

### Optional Slots

- `budget_vnd`
- `desired_pathway`
- `desired_region`
- `dormitory_preference`
- `desired_intake`

### Step-by-Step Script

| Step | User Input / Condition | Bot Message | Quick Replies / Buttons | Slots Updated | Backend Action | Next State |
|---|---|---|---|---|---|---|
| B1 | User asks cost, no slots | Chi phi du hoc Han phu thuoc vao lo trinh D4-1/D2, khu vuc, truong, KTX, sinh hoat va ty gia. De uoc tinh sat hon, ban dang nghieng ve lo trinh nao? | `D4-1 hoc tieng`, `D2 chuyen nganh`, `Chua ro` | `need_type=GENERAL_COST` | Classify `ASK_GENERAL_COST`; log intent | `COLLECT_COST_PATHWAY` |
| B2 | User selects pathway or says unknown | Ban du kien muon hoc khu vuc nao? | `Seoul/top school`, `Gan Seoul/Gyeonggi`, `Tinh chi phi thap`, `Chua ro` | `desired_pathway` | Update profile | `COLLECT_COST_REGION` |
| B3 | User selects region | Ngan sach gia dinh du kien khoang bao nhieu? Neu chua ro, ban co the chon "chua ro". | `<200 trieu`, `200-300 trieu`, `300-450 trieu`, `>450 trieu`, `Chua ro` | `desired_region` | Update profile | `COLLECT_BUDGET` |
| B4 | Enough pathway/region, budget optional | Với thông tin hiện có, chi phí tham khảo cho nhóm này khoảng `{cost_range}` cho năm đầu/12 tháng đầu. Khoản này thường gồm học phí, KTX/nhà ở, sinh hoạt, visa/KVAC, vé máy bay, bảo hiểm và dự phòng. Lưu ý: Chi phí trên là ước tính ban đầu và có thể thay đổi theo trường, kỳ nhập học, tỷ giá, KTX, sinh hoạt và chính sách từng thời điểm. Tu vấn viên sẽ kiểm tra lại trước khi gửi phương án chính thức. | `Check ho so`, `Gap tu van vien`, `Xem Seoul hay tinh`, `Quay lai menu` | `budget_vnd` if provided | Call Cost Estimator; log estimate | `COST_RESULT_SHOWN` |
| B5 | User asks "bao gia chinh thuc" | Bang phi chinh thuc can tu van vien kiem tra theo truong, ky nhap hoc va ho so cua ban. Ban co muon trung tam chuyen tu van vien tiep tuc trong Messenger hoac goi lai khong? | `Tiep tuc Messenger`, `De lai SDT`, `Chua can` | `handoff_reason=OFFICIAL_COST` | Create handoff if accepted | `HANDOFF_OFFERED` |

### Fallback Handling

- Neu user khong biet pathway: bot dung range rong `Chua ro pathway/khu vuc` va hoi muc tieu hoc.
- Neu budget qua thap: bot khong noi "khong the di", ma noi "co the can toi uu khu vuc/lo trinh va can check ky hon".

### Disclaimer Placement

Bat buoc o B4 va moi lan tra range chi phi.

### Lead Capture Moment

- Sau B4 neu user chon `Gap tu van vien`.
- B5 neu user muon bao gia chinh thuc.
- SDT optional; neu khong co SDT, tao handoff theo PSID neu user dong y.

### Human Handoff Trigger

- User hoi bao gia chinh thuc.
- User budget sat nguong/qua thap va muon phuong an cu the.
- User hoi truong cu the trong flow chi phi.

### Exit Conditions

- Cost result shown.
- Chuyen sang Flow C/E/F/G.
- User quay lai menu.

### Edge Cases

- User noi "bao nhieu cung duoc": hoi khu vuc/muc tieu de phan nhom.
- User noi "muon re nhat": route region `Tinh chi phi thap`.
- User hoi phi trung tam: neu chua config, handoff.

### Acceptance Criteria

- Given user hoi chi phi, Then bot noi chi phi phu thuoc pathway/khu vuc/truong/KTX/sinh hoat.
- Given co pathway va region, Then bot goi Cost Estimator va tra range + disclaimer.
- Given user khong de SDT, Then bot van tra range uoc tinh.

## 6. Flow C: User muon check ho so

### Flow Objective

Thu thap thong tin toi thieu de check eligibility so bo, gan score/risk va de xuat next action khong cam ket visa.

### Entry Triggers

- "Em/ban co di duoc khong?"
- "GPA 6.5 chua TOPIK co di duoc khong?"
- User chon `MENU_ELIGIBILITY`.

### Required Slots

- `education_level`
- `graduation_year`
- `gpa`

### Optional Slots

- `korean_level`
- `topik_level`
- `budget_vnd`
- `desired_intake`
- `desired_pathway`
- `desired_region`
- `visa_history`

### Step-by-Step Script

| Step | User Input / Condition | Bot Message | Quick Replies / Buttons | Slots Updated | Backend Action | Next State |
|---|---|---|---|---|---|---|
| C1 | User asks eligibility, some slots may exist | Trung tam co the check phu hop so bo cho ban. Luu y chatbot khong the cam ket ket qua visa. Truoc tien, ban cho trung tam biet: ban da tot nghiep bac nao, nam tot nghiep va GPA/hoc luc gan dung? | `THPT`, `CD/DH`, `Dang hoc`, `Chua ro` | `need_type=ELIGIBILITY_CHECK` | Classify; extract any provided slots | `COLLECT_ELIGIBILITY_CORE` |
| C2 | User gives core info | Trung tam ghi nhan. Ban da hoc tieng Han/TOPIK chua va ngan sach gia dinh du kien khoang bao nhieu? | `Chua hoc`, `Dang hoc so cap`, `Co TOPIK`, `Chua ro ngan sach` | `education_level`, `graduation_year`, `gpa` | Slot extract; update profile | `COLLECT_ELIGIBILITY_SUPPORT` |
| C3 | User gives Korean/budget | Ban mong muon hoc tieng D4-1, vao chuyen nganh D2 hay chua ro? Khu vuc mong muon la Seoul, gan Seoul hay tinh chi phi thap? | Pathway + region quick replies | `korean_level`, `topik_level`, `budget_vnd` | Update profile | `COLLECT_GOAL_REGION` |
| C4 | Minimum slots available | Ket qua so bo: ho so cua ban dang o nhom `{lead_score}`, rui ro `{risk_level}`. Lo trinh goi y: `{suggested_pathway}`. Thong tin con thieu: `{missing_information}`. Day chi la danh gia ban dau; tu van vien can kiem tra ho so va tai chinh truoc khi ket luan. | `Gap tu van vien`, `Uoc tinh chi phi`, `Xem D4-1/D2`, `Quay lai menu` | `lead_score`, `risk_level`, `suggested_pathway` | Call Eligibility Checker; save assessment | `ELIGIBILITY_RESULT_SHOWN` |
| C5 | Risk medium/high or user asks next step | Ho so nay nen duoc tu van vien kiem tra ky hon. Ban muon trung tam chuyen tu van vien tiep tuc trong Messenger hay ban muon de lai SDT de duoc goi lai? | `Tiep tuc Messenger`, `De lai SDT`, `Chua can` | `handoff_reason=RISK_REVIEW` | Create handoff if accepted | `HANDOFF_OFFERED` |

### Fallback Handling

- Neu user khong biet GPA: hoi hoc luc gan dung.
- Neu user chi noi "co di duoc khong" khong them thong tin: hoi 3 slot core.
- Neu user khong muon cung cap thong tin: dua checklist chung va quay lai menu.

### Disclaimer Placement

Visa disclaimer bat buoc o C1 hoac C4.

### Lead Capture Moment

- Sau C4 neu lead score A/B.
- Sau C5 neu risk medium/high hoac user muon tu van vien.
- SDT optional; PSID + summary van luu.

### Human Handoff Trigger

- `risk_level=HIGH`.
- Gap year dai, GPA thap, ngan sach sat nguong, tung truot visa.
- User muon nop ho so.

### Exit Conditions

- Eligibility result shown.
- Handoff created.
- User chon cost/pathway/menu.

### Edge Cases

- User noi "GPA 6.5" nhung khong noi thang diem: mac dinh thang 10 neu context Viet Nam, hoac hoi xac nhan neu khong chac.
- User hoi "bao do visa": route `ASK_VISA_GENERAL` va guardrail.

### Acceptance Criteria

- Given user cung cap GPA/nam tot nghiep, Then bot luu slots va hoi thong tin con thieu.
- Given du slot toi thieu, Then bot goi Eligibility Checker.
- Given output risk high, Then bot de xuat handoff.
- Bot khong bao gio noi "chac chan dau visa".

## 7. Flow D: User hoi D4-1 vs D2

### Flow Objective

Giai thich khac nhau giua D4-1 va D2 bang ngon ngu de hieu, sau do hoi muc tieu de goi y route.

### Entry Triggers

- "D4-1 va D2 khac nhau the nao?"
- "Nen di D4 hay D2?"
- User chon `MENU_D4_D2`.

### Required Slots

- None.

### Optional Slots

- `education_level`
- `goal`
- `korean_level`
- `topik_level`
- `desired_pathway`

### Step-by-Step Script

| Step | User Input / Condition | Bot Message | Quick Replies / Buttons | Slots Updated | Backend Action | Next State |
|---|---|---|---|---|---|---|
| D1 | User asks D4/D2 | D4-1 la lo trinh hoc tieng Han tai truong. D2 la lo trinh hoc chuyen nganh cao dang/dai hoc/sau dai hoc. Noi ngan gon: neu ban chua du tieng hoac chua san sang vao nganh, thuong can xem D4-1; neu da du dieu kien dau vao nganh/truong, co the xem D2. | `So sanh chi tiet`, `Goi y theo ho so`, `Check chi phi` | `need_type=PATHWAY_ADVICE` | Log `ASK_D4_D2` | `PATHWAY_EXPLAINED` |
| D2 | User asks comparison | Bang so sanh nhanh: D4-1: hoc tieng, phu hop nguoi can bo sung tieng Han. D2: hoc chuyen nganh, can check dieu kien dau vao va ngoai ngu theo truong. Ban dang muon hoc tieng truoc hay vao nganh luon? | `Hoc tieng truoc`, `Vao nganh luon`, `Chua ro` | none | Send comparison | `COLLECT_GOAL` |
| D3 | User gives goal | Trung tam ghi nhan. De goi y route sat hon, ban cho biet ban da tot nghiep bac nao va da co TOPIK/tieng Han chua? | `THPT`, `CD/DH`, `Chua hoc tieng`, `Co TOPIK` | `goal`, `desired_pathway` | Update profile | `COLLECT_PATHWAY_PROFILE` |
| D4 | Profile enough | Voi thong tin hien co, route so bo nen xem la `{suggested_pathway}`. Day la goi y ban dau; neu ban muon, trung tam co the check ho so day du hon. | `Check ho so`, `Uoc tinh chi phi`, `Gap tu van vien` | `suggested_pathway` | Optional eligibility pre-check if enough slots | `PATHWAY_RECOMMENDED` |

### Fallback Handling

Neu user khong hieu D4/D2, dung vi du:

```text
Hieu don gian: D4-1 giong buoc hoc tieng/chuan bi truoc; D2 la buoc hoc nganh chinh thuc.
```

### Disclaimer Placement

Neu noi den dieu kien dau vao D2, chen:

```text
Dieu kien cu the tuy truong/nganh va can kiem tra lai nguon chinh thuc.
```

### Lead Capture Moment

Khong hoi SDT mac dinh. Hoi neu user muon tu van route rieng.

### Human Handoff Trigger

- User muon chon route cu the theo ho so.
- User co case rui ro.

### Exit Conditions

- Route de xuat.
- Chuyen Flow C/B/G.

### Edge Cases

- User da co TOPIK cao va hoi D4-1: bot noi co the xem D2 neu dap ung dieu kien nganh/truong, can check them.
- User chua tot nghiep: bot noi can theo doi thoi diem tot nghiep/ky nhap hoc.

### Acceptance Criteria

- Given user hoi D4/D2, Then bot giai thich khac nhau de hieu.
- Given user chua ro muc tieu, Then bot hoi muc tieu thay vi goi y truong.

## 8. Flow E: User hoi Seoul hay tinh

### Flow Objective

So sanh Seoul, gan Seoul/Gyeonggi va tinh chi phi thap theo chi phi, moi truong va phu hop ngan sach.

### Entry Triggers

- "Nen chon Seoul hay tinh?"
- "Hoc o dau re?"
- "Gan Seoul co tot khong?"
- User chon `MENU_REGION`.

### Required Slots

- None.

### Optional Slots

- `budget_vnd`
- `desired_pathway`
- `lifestyle_preference`
- `family_in_korea`
- `desired_region`

### Step-by-Step Script

| Step | User Input / Condition | Bot Message | Quick Replies / Buttons | Slots Updated | Backend Action | Next State |
|---|---|---|---|---|---|---|
| E1 | User asks region advice | Co 3 huong de can nhac: Seoul/top school chi phi cao hon; gan Seoul/Gyeonggi can bang hon; tinh chi phi thap giup toi uu ngan sach. Ban uu tien chi phi, moi truong thanh pho lon hay gan nguoi than o Han? | `Toi uu chi phi`, `Thanh pho lon`, `Gan Seoul`, `Gan nguoi than` | `need_type=REGION_ADVICE` | Log `ASK_REGION_ADVICE` | `COLLECT_REGION_PRIORITY` |
| E2 | User gives priority | Ngan sach gia dinh du kien khoang bao nhieu? | `<200 trieu`, `200-300 trieu`, `300-450 trieu`, `>450 trieu`, `Chua ro` | `lifestyle_preference` | Update profile | `COLLECT_REGION_BUDGET` |
| E3 | User gives budget | Ban dang nghieng ve D4-1 hoc tieng, D2 chuyen nganh hay chua ro? | `D4-1`, `D2`, `Chua ro` | `budget_vnd` | Update profile | `COLLECT_REGION_PATHWAY` |
| E4 | Enough slots | Goi y so bo: voi `{budget}` va `{pathway}`, ban nen can nhac `{suggested_region}`. Neu muon, trung tam co the uoc tinh chi phi theo nhom khu vuc nay hoac check ho so truoc. | `Uoc tinh chi phi`, `Check ho so`, `Gap tu van vien` | `desired_region` | Optional Cost Estimator | `REGION_RECOMMENDED` |

### Fallback Handling

- Neu user khong co ngan sach: dua so sanh tong quan va hoi pathway.
- Neu user hoi "o dau tot nhat": noi "tot nhat tuy muc tieu/ngan sach/ho so", khong goi y truong ngay.

### Disclaimer Placement

Neu de cap chi phi, dung cost disclaimer ngan.

### Lead Capture Moment

Sau E4 neu user muon shortlist khu vuc/truong hoac goi lai.

### Human Handoff Trigger

- User co nguoi than/khu vuc cu the va muon phuong an rieng.
- User muon shortlist truong.

### Exit Conditions

- Region recommendation shown.
- Chuyen Flow B/C/F/G.

### Edge Cases

- User ngan sach qua thap nhung muon Seoul: bot noi Seoul co the tao ap luc chi phi cao, nen check phuong an gan Seoul/tinh.
- User co nguoi than o Han: luu `family_in_korea` va uu tien region gan nguoi than neu hop ly.

### Acceptance Criteria

- Given user hoi Seoul hay tinh, Then bot so sanh uu/nhuoc diem.
- Given user co budget, Then bot goi y khu vuc so bo theo budget/pathway.

## 9. Flow F: User da biet truong

### Flow Objective

Xu ly user school-aware ma khong de flow mac dinh thanh school-first. Neu co verified data thi tra loi; neu khong, chuyen tu van vien xac nhan.

### Entry Triggers

- "Truong Dongguk het bao nhieu?"
- "Korea University co nganh media khong?"
- "KTX truong X bao nhieu?"
- User chon `MENU_SCHOOL_SEARCH` va da nhap ten truong.

### Required Slots

- `school_name`

### Optional Slots

- `program_type`
- `major`
- `intake_term`
- `desired_pathway`
- `budget_vnd`

### Step-by-Step Script

| Step | User Input / Condition | Bot Message | Quick Replies / Buttons | Slots Updated | Backend Action | Next State |
|---|---|---|---|---|---|---|
| F1 | User asks school-specific question | Trung tam ghi nhan ban dang hoi ve `{school_name}`. Ban dang quan tam he hoc tieng D4-1 hay he chuyen nganh D2? | `D4-1 hoc tieng`, `D2 chuyen nganh`, `Chua ro` | `school_name` | LLM extracts school; log `ASK_SCHOOL_INFO/COST` | `COLLECT_SCHOOL_PROGRAM` |
| F2 | User gives program | Trung tam se kiem tra theo du lieu verified. Neu du lieu cua truong/ky moi nhat chua co trong he thong, trung tam se chuyen tu van vien xac nhan tu nguon chinh thuc. | none | `program_type` | Call School Database | `SCHOOL_LOOKUP` |
| F3a | Verified data found | Thong tin tham khao cho `{school_name}` - `{program_type}`: `{verified_summary}`. Luu y: Thong tin truong/nganh/chi phi can duoc kiem tra lai tu nguon chinh thuc truoc khi tu van phuong an cuoi cung. | `Nhan tu van chi tiet`, `Check ho so`, `Uoc tinh tong chi phi` | none | Return verified data; log source | `SCHOOL_INFO_SHOWN` |
| F3b | No verified data | Hien tai trung tam chua co du lieu verified moi nhat cho cau hoi nay. De tranh gui sai hoc phi/ky nhap hoc, trung tam nen chuyen tu van vien kiem tra lai tu nguon chinh thuc. Ban muon tiep tuc trong Messenger hay de lai SDT de duoc goi lai? | `Tiep tuc Messenger`, `De lai SDT`, `Chua can` | `handoff_reason=UNVERIFIED_SCHOOL_DATA` | Offer handoff | `HANDOFF_OFFERED` |
| F4 | User asks recommendation without profile | Truoc khi goi y truong, trung tam can hieu ngan sach, muc tieu hoc va khu vuc mong muon cua ban. Ban muon hoc tieng D4-1, D2 hay chua ro? | Pathway quick replies | `need_type=SCHOOL_SEARCH` | Route to profile collection | `COLLECT_SCHOOL_SEARCH_PROFILE` |

### Fallback Handling

- Neu ten truong sai chinh ta: hoi xac nhan 1-2 ket qua gan dung.
- Neu user hoi nganh ma khong noi truong: hoi major + region/budget, khong bia list truong.

### Disclaimer Placement

Bat buoc khi tra school info, cost, KTX, hoc bong.

### Lead Capture Moment

- Khi user muon bao gia chi tiet.
- Khi DB khong co verified data va user muon tu van vien xac nhan.

### Human Handoff Trigger

- Data missing/unverified.
- User hoi hoc phi/KTX/hoc bong/ky nhap hoc chinh thuc.
- User muon nop ho so truong cu the.

### Exit Conditions

- Verified info shown.
- Handoff created.
- Route sang cost/eligibility.

### Edge Cases

- User hoi "truong nao tot nhat": route `ASK_SCHOOL_RECOMMENDATION`, hoi profile truoc.
- User noi chi ten truong: hoi user muon biet hoc phi, nganh, KTX hay dieu kien.

### Acceptance Criteria

- Given user hoi truong cu the, Then bot extract/confirm school name.
- Given data chua verified, Then bot khong bia va de xuat tu van vien xac nhan.
- Given user moi hoi "nen hoc truong nao", Then bot hoi nhu cau/profile truoc.

## 10. Flow G: Human Handoff

### Flow Objective

Chuyen user sang tu van vien dung luc, kem summary va context, khong lam user cam thay bi ep de lai SDT.

### Entry Triggers

- User chon `Gap tu van vien`.
- User de lai SDT va muon goi lai.
- Bot trigger handoff do risk/unverified data/official fee/unknown after 2 attempts.

### Required Slots

- `facebook_psid`
- `handoff_reason`

### Optional Slots

- `phone`
- `full_name`
- `need_type`
- `lead_score`
- `risk_level`
- `conversation_summary`
- `preferred_time`

### Step-by-Step Script

| Step | User Input / Condition | Bot Message | Quick Replies / Buttons | Slots Updated | Backend Action | Next State |
|---|---|---|---|---|---|---|
| G1 | Handoff triggered, no phone | Trung tam co the chuyen cau hoi nay cho tu van vien. Ban muon tiep tuc nhan trong Messenger hay de lai SDT de duoc goi lai? | `Tiep tuc Messenger`, `De lai SDT`, `Chua can` | `handoff_reason` | Prepare handoff context | `HANDOFF_CHANNEL_CHOICE` |
| G2a | User chooses Messenger | Da duoc. Trung tam se chuyen thong tin hoi thoai nay cho tu van vien va tiep tuc phan hoi ban trong Messenger. | `Quay lai menu` | none | Create `HandoffRequest` by PSID | `HANDOFF_CREATED` |
| G2b | User chooses phone | Ban gui giup trung tam so dien thoai va thoi gian tien nghe may neu co. | none | none | Await phone | `COLLECT_PHONE_FOR_HANDOFF` |
| G3 | User sends phone | Trung tam da nhan thong tin. Tu van vien se kiem tra lai ho so/cau hoi cua ban va phan hoi trong gio lam viec. | `Quay lai menu` | `phone`, `preferred_time` | Create/update Lead; create HandoffRequest; notify advisor | `HANDOFF_CREATED` |
| G4 | Outside business hours | Trung tam da ghi nhan yeu cau. Neu hien ngoai gio lam viec, tu van vien se phan hoi vao khung gio lam viec tiep theo. | `Quay lai menu` | none | Store SLA note | `HANDOFF_CREATED` |

### Fallback Handling

- Phone invalid: "So dien thoai co ve chua dung. Ban co the gui lai hoac chon tiep tuc trong Messenger."
- User khong muon handoff: quay lai menu, khong tao lead phone.

### Disclaimer Placement

Neu handoff do visa/cost/school, giu disclaimer tu flow truoc trong summary/context.

### Lead Capture Moment

- Neu user gui phone, tao/update `Lead`.
- Neu user khong gui phone nhung dong y Messenger handoff, tao `HandoffRequest` theo PSID va partial profile.

### Human Handoff Trigger

Flow nay chinh la handoff endpoint.

### Exit Conditions

- Handoff created.
- User declines.
- User quay lai menu.

### Edge Cases

- User gui SDT ngay tu dau: tao lead, hoi nhu cau chinh neu chua co.
- User yeu cau xoa thong tin: chuyen policy/manual support, khong tiep tuc thu data.

### Acceptance Criteria

- Given user muon gap tu van vien, Then bot khong bat buoc SDT.
- Given user de lai phone hop le, Then lead + handoff duoc tao.
- Given handoff tao, Then advisor payload co summary/profile/risk neu co.

## 11. Fallback & Recovery Flows

| Case | Bot Behavior | Backend Action | Next State |
|---|---|---|---|
| `UNKNOWN` lan 1 | Hoi lai bang 3 lua chon: chi phi, check ho so, gap tu van vien | Increment fallback count | `FALLBACK_1` |
| `UNKNOWN` lan 2 | Hien menu rut gon va de xuat handoff | Increment fallback count; log low confidence | `FALLBACK_2` |
| LLM timeout | Dung rule/FAQ fallback: "Trung tam dang can kiem tra lai cau hoi nay..." | Log `LLM_TIMEOUT` | Previous safe state |
| Cost service error | Khong tra number; noi can tu van vien xac nhan | Log error; offer handoff | `HANDOFF_OFFERED` |
| Eligibility service error | Noi chua check duoc tu dong, hoi user co muon TVV check | Log error; offer handoff | `HANDOFF_OFFERED` |
| School DB missing | Khong bia; offer advisor verification | Log missing data | `HANDOFF_OFFERED` |
| CRM/Lead save fail | Thong bao da ghi nhan tren Messenger, queue retry noi bo | Queue retry; alert if repeated | `HANDOFF_PENDING_RETRY` |

## 12. Edge Case Handling

| Edge Case | Handling |
|---|---|
| User hoi qua chung chung | Hien global menu. |
| User hoi nhieu y | Uu tien unsafe/high-risk, sau do intent co lead value cao; noi se xu ly tung phan. |
| User go sai ten truong | Fuzzy match, hoi xac nhan neu confidence thap. |
| User chua muon de lai SDT | Tiep tuc flow; luu partial profile theo PSID. |
| User la phu huynh | Doi tone sang `anh/chi`, hoi thong tin cua con. |
| User chua biet GPA | Hoi hoc luc gan dung hoac cho phep `Chua ro`. |
| User khong biet D4-1/D2 | Giai thich ngan va hoi muc tieu hoc. |
| User ngan sach qua thap | Noi can toi uu khu vuc/lo trinh, khong ket luan "khong the di". |
| User hoi "bao do visa khong?" | Visa disclaimer, khong cam ket, offer eligibility/handoff. |
| User hoi lam them qua muc | Nhac quy dinh, khong khuyen phu thuoc/lam trai quy dinh. |
| User hoi du lieu chua co | Noi can tu van vien xac nhan, khong bia. |
| Tu van vien ngoai gio | Thong bao phan hoi vao gio lam viec tiep theo. |

## 13. Conversation State Map

| State | Description | Entry Intent | Main Actions | Exit |
|---|---|---|---|---|
| `NEW_SESSION` | Session moi | `GREETING` | Create/get session | `MENU_SHOWN` |
| `MENU_SHOWN` | Menu journey-first da hien | `MENU_SELECTION` | Wait quick reply/free text | Target flow state |
| `COLLECT_BASIC_PROFILE` | Thu thong tin nen | `ASK_PROCESS` | Slot extraction/profile update | `NEXT_NEED_SELECTION` |
| `COLLECT_COST_PATHWAY` | Hoi D4-1/D2 | `ASK_GENERAL_COST` | Update pathway | `COLLECT_COST_REGION` |
| `COLLECT_COST_REGION` | Hoi khu vuc | `ASK_GENERAL_COST` | Update region | `COLLECT_BUDGET` |
| `COLLECT_BUDGET` | Hoi ngan sach | `ASK_GENERAL_COST` | Update budget | `COST_RESULT_SHOWN` |
| `COST_RESULT_SHOWN` | Da tra range chi phi | `ASK_GENERAL_COST` | CTA next | Flow C/E/G/menu |
| `COLLECT_ELIGIBILITY_CORE` | Hoi hoc van/nam/GPA | `CHECK_ELIGIBILITY` | Slot extraction | `COLLECT_ELIGIBILITY_SUPPORT` |
| `COLLECT_ELIGIBILITY_SUPPORT` | Hoi tieng Han/ngan sach | `CHECK_ELIGIBILITY` | Slot extraction | `COLLECT_GOAL_REGION` |
| `ELIGIBILITY_RESULT_SHOWN` | Da cham score/risk | `CHECK_ELIGIBILITY` | Save assessment | Flow B/D/G/menu |
| `PATHWAY_EXPLAINED` | Da giai thich D4/D2 | `ASK_D4_D2` | CTA profile/check | `COLLECT_PATHWAY_PROFILE` |
| `REGION_RECOMMENDED` | Da goi y khu vuc | `ASK_REGION_ADVICE` | CTA cost/check | Flow B/C/G/menu |
| `SCHOOL_LOOKUP` | Dang lookup truong | `ASK_SCHOOL_INFO/COST` | Call DB | `SCHOOL_INFO_SHOWN` or `HANDOFF_OFFERED` |
| `HANDOFF_OFFERED` | De xuat handoff | `HUMAN_HANDOFF` | Ask channel/phone optional | `HANDOFF_CREATED` or menu |
| `HANDOFF_CREATED` | Da tao handoff | `HUMAN_HANDOFF` | Notify advisor | End/menu |
| `FALLBACK_1` | Khong hieu lan 1 | `UNKNOWN` | Ask clarification | Previous/menu |
| `FALLBACK_2` | Khong hieu lan 2 | `UNKNOWN` | Offer handoff | `HANDOFF_OFFERED` |

## 14. Slot Collection Matrix

| Slot | Use In Flows | Priority | Collection Prompt | Quick Replies | Notes |
|---|---|---|---|---|---|
| `full_name` | G | Optional | Ban cho trung tam xin ten de tu van vien tien xung ho nhe? | none | Khong bat buoc. |
| `phone` | G, lead capture | Conditional | Ban gui giup trung tam so dien thoai neu muon duoc goi lai. | none | Chi bat buoc cho callback. |
| `education_level` | A, C, D | High | Ban da tot nghiep bac nao? | THPT, CD/DH, Dang hoc, Chua ro | Core eligibility. |
| `graduation_year` | A, C | High | Ban tot nghiep nam nao? | none | Dung tinh gap year. |
| `gpa` | C | High | GPA/hoc luc gan dung cua ban la bao nhieu? | Chua ro | Chap nhan hoc luc neu khong biet GPA. |
| `korean_level` | C, D | Medium | Ban da hoc tieng Han/TOPIK chua? | Chua hoc, Dang hoc, Co TOPIK | TOPIK optional. |
| `topik_level` | C, D | Medium | Neu co TOPIK, ban dang co cap may? | TOPIK I, TOPIK II, Chua ro | Khong bat buoc D4-1. |
| `budget_vnd` | B, C, E | High | Ngan sach gia dinh du kien khoang bao nhieu? | <200, 200-300, 300-450, >450, Chua ro | Sensitive financial. |
| `desired_pathway` | B, C, D, E, F | High | Ban nghieng ve D4-1, D2 hay chua ro? | D4-1, D2, Chua ro | If unknown, guide. |
| `desired_region` | B, C, E | Medium | Ban muon khu vuc nao? | Seoul, Gan Seoul, Tinh, Chua ro | Used for cost. |
| `desired_intake` | A, C | Low | Ban du kien di ky/nam nao? | Gan nhat, Nam sau, Chua ro | Timeline/handoff. |
| `desired_major` | F | Low | Ban quan tam nganh nao neu co? | none | Only if school/major flow. |
| `school_name` | F | High for school flow | Ban dang hoi truong nao? | none | Confirm if fuzzy. |
| `program_type` | F | High for school cost | Ban hoi D4-1 hay D2? | D4-1, D2, Chua ro | Required before school cost. |
| `visa_history` | C, G | Conditional | Ban co tung truot visa nuoc nao khong? Neu khong tien chia se co the bo qua. | Co, Khong, Bo qua | Ask only if risk context. |

## 15. Copy Library

### 15.1. CTA Copy

- "Ban muon trung tam check ho so so bo khong?"
- "Ban cho trung tam biet ngan sach du kien de goi y khu vuc phu hop hon nhe?"
- "Ban muon trung tam chuyen tu van vien tiep tuc trong Messenger khong?"
- "Neu ban muon duoc goi lai, ban co the de lai SDT; neu chua tien de lai thi minh van tiep tuc trong Messenger."

### 15.2. Menu Copy

```text
Ban muon tim hieu muc nao truoc?
1. Quy trinh du hoc Han
2. Tong chi phi can chuan bi
3. Check ho so co di duoc khong
4. Nen chon Seoul hay tinh
5. D4-1 va D2 khac nhau the nao
6. Tim truong theo ngan sach/khu vuc
7. Gap tu van vien
```

### 15.3. Unknown Copy

```text
Trung tam chua hieu ro y ban. Ban muon hoi ve chi phi, check ho so hay gap tu van vien?
```

### 15.4. Low Confidence Confirmation

```text
Trung tam hieu la ban dang muon hoi ve `{detected_topic}`. Co dung khong?
```

Quick replies: `Dung roi`, `Khong, hoi muc khac`, `Gap tu van vien`.

### 15.5. Phone Optional Copy

```text
Ban khong can de lai SDT neu chua san sang. Trung tam van co the ho tro ban tim hieu thong tin co ban trong Messenger.
```

### 15.6. Advisor Handoff Copy

```text
Trung tam se chuyen tom tat hoi thoai nay cho tu van vien de ban khong phai nhac lai tu dau.
```

### 15.7. Guardrail Copy

Visa guarantee:

```text
Trung tam khong the cam ket dau visa. Ket qua phu thuoc vao ho so, tai chinh, hoc luc, phong van va quyet dinh cua co quan xet duyet. Trung tam co the ho tro ban check rui ro so bo truoc.
```

False document:

```text
Trung tam khong ho tro khai sai ho so hoac lam giay to khong dung thuc te. Neu ho so co diem yeu, trung tam co the tu van cach bo sung/chuan bi dung quy dinh.
```

Illegal work:

```text
Ban khong nen lap ke hoach du hoc dua vao viec lam them trai quy dinh. Viec lam them can tuan thu dieu kien visa va quy dinh tai Han Quoc.
```

## 16. Implementation Notes for Dev Team

- Rule-based router phai xu ly greeting, menu, quick replies, lead capture, handoff va unsafe keywords truoc LLM.
- LLM chi dung cho intent classification, slot extraction, response rewrite co guardrail va conversation summary.
- Moi response ve cost/truong/visa/hoc bong/viec lam them can qua guardrail/disclaimer rule.
- `Cost Estimator` chi tra range tu `CostRangeTemplate`, khong de LLM tinh tien.
- `School Database` chi tra data co `verified=true` va source/version. Neu khong co data, bot phai offer handoff.
- `LeadService` phai ho tro partial lead/profile theo PSID va lead co phone.
- Phone optional: khong block flow neu khong co `phone`.
- State transitions can duoc log voi `session_id`, `intent`, `current_state`, `next_state`, `backend_action`, `latency`.
- Fallback count nen theo session va reset khi user chon intent hop le.
- Copy trong tai lieu nay la baseline; nen dua vao config/KB de BA co the sua ma khong deploy.
