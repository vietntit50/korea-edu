# PRD: Facebook Messenger Chatbot MVP - Korea Study Advisor Platform

## 1. Product Overview

### 1.1. Ten module

Facebook Messenger Chatbot tu van du hoc Han Quoc.

### 1.2. Muc tieu module

Xay dung chatbot cho fanpage Facebook dong vai tro tu van vien cap 1, giup hoc sinh va phu huynh moi tim hieu du hoc Han Quoc co the nam duoc quy trinh, chi phi uoc tinh, pathway phu hop, rui ro ho so so bo va de lai thong tin de tu van vien tiep tuc cham soc.

Chatbot phai di theo huong journey-first/need-first. Bot khong hoi ngay "em muon chon truong nao?" voi user moi. Bot can hoi nhu cau, ho so, ngan sach, khu vuc mong muon va muc tieu hoc truoc, sau do moi goi y lo trinh, khu vuc va nhom truong phu hop.

### 1.3. Van de can giai quyet

- User moi thuong chua hieu D4-1/D2, quy trinh, tong chi phi va tieu chi chon khu vuc.
- Fanpage nhan nhieu cau hoi lap lai ve chi phi, visa, GPA, TOPIK, Seoul/tinh va truong cu the.
- Tu van vien mat thoi gian loc lead lan dau, trong khi lead nong can duoc phan hoi nhanh.
- Thong tin tu van de bi khong dong nhat neu phu thuoc hoan toan vao tung nhan su.
- Lead co the roi cuoc tro chuyen truoc khi de lai so dien thoai.

### 1.4. Nguoi dung chinh

| Nhom nguoi dung | Mo ta | Muc tieu khi dung bot |
|---|---|---|
| Hoc sinh moi tim hieu | Chua biet quy trinh, chi phi, D4-1/D2, truong/khu vuc | Hieu tong quan va biet nen bat dau tu dau |
| Phu huynh | Quan tam chi phi, an toan, visa, viec lam them | Biet ngan sach can chuan bi va rui ro can luu y |
| Hoc sinh da co nhu cau ro | Da biet muon di ky nao, co GPA/ngan sach/huong hoc | Check phu hop so bo va dat lich tu van |
| Hoc sinh da biet truong cu the | Hoi hoc phi, ky tuc xa, nganh, ky nhap hoc | Lay thong tin truong neu da verified hoac duoc chuyen tu van vien |
| Tu van vien noi bo | Nhan lead, xem tom tat hoi thoai, tiep tuc tu van | Tiet kiem thoi gian intake va uu tien lead nong |
| Admin/owner trung tam | Quan ly noi dung bot, rule, lead, analytics | Tang lead, kiem soat chat luong tu van, do hieu qua fanpage |

### 1.5. Gia tri kinh doanh

- Tang so lead tu fanpage va comment/inbox.
- Tu dong tra loi 24/7 cho cau hoi co ban.
- Giam tai cho tu van vien o vong tu van dau tien.
- Chuan hoa thong tin tu van ve quy trinh, chi phi, D4-1/D2, visa disclaimer.
- Loc lead nong/lanh bang lead score A/B/C/D.
- Tang ty le dat lich tu van va goi lai.
- Tao nen tang du lieu cho CRM, eligibility check, cost calculator va school recommendation sau nay.

### 1.6. MVP scope

MVP gom Facebook Messenger webhook, greeting/menu, flow journey-first, FAQ co ban, general cost estimator, eligibility check so bo, lead capture, human handoff, conversation logging, LLM intent classification, LLM slot extraction, admin config co ban va tao lead sang CRM/database don gian.

### 1.7. Out of scope

- Full CRM/advisor console.
- School database day du va cost calculator chi tiet theo tung truong.
- Tu dong de xuat truong chinh xac neu du lieu chua verified.
- OCR ho so, AI interview coach, crawler truong tu dong.
- Payment, hop dong, mobile app, parent dashboard, CTV portal.

## 2. User Segmentation

| Segment | Nhu cau | Cau hoi thuong gap | Thong tin can thu thap | Response strategy | Khi nao handoff |
|---|---|---|---|---|---|
| New Explorer | Hieu tong quan, biet bat dau tu dau | "Du hoc Han nhu the nao?", "Can chuan bi gi?", "D4-1 la gi?" | Ten, tinh/thanh, hoc van, nam tot nghiep, muc tieu hoc, ngan sach so bo, khu vuc mong muon | Giai thich ngan, dua menu journey-first, hoi 2-3 cau de dinh huong | User muon lo trinh rieng, de lai SDT, hoi case co rui ro |
| Cost Seeker | Biet tong tien can chuan bi | "Di Han het bao nhieu?", "Ngan sach 200 trieu du khong?" | Pathway, khu vuc, ngan sach, KTX/sinh hoat, ky du kien | Dua range uoc tinh theo nhom khu vuc, nhan manh bien dong, hoi ngan sach de goi y | Hoi bao gia chinh thuc, hoi truong cu the, ngan sach sat nguong |
| Eligibility Checker | Check ho so co phu hop khong | "GPA 6.5 di duoc khong?", "Tot nghiep 2021 co rui ro khong?" | GPA, nam tot nghiep, gap year, TOPIK/tieng Han, ngan sach, muc tieu, lich su visa | Cham so bo A/B/C/D, neu rui ro thi noi ro can tu van vien check | Ho so rui ro, gap year dai, tai chinh yeu, lich su truot visa |
| School-aware User | Hoi truong/nganh/chi phi cu the | "Dongguk het bao nhieu?", "Truong nao co nganh media?" | Ten truong, program type, nganh, ky nhap hoc, ngan sach | Tim DB neu co verified; neu khong, khong bia, chuyen tu van vien xac nhan | Hoi hoc phi/KTX/ky nhap hoc chinh thuc, du lieu chua verified |

## 3. Core Chatbot Positioning

Chatbot la:

- Tu van vien cap 1.
- Lead qualification bot.
- FAQ assistant.
- Eligibility pre-check assistant.
- General cost estimator.
- Human handoff router.

Chatbot khong phai la:

- Nguoi ra quyet dinh visa.
- Nguoi cam ket dau visa.
- Nguon du lieu hoc phi chinh thuc neu khong co database verified.
- Cong cu thay the hoan toan tu van vien.
- Cong cu tu bia thong tin truong, nganh, hoc bong, chi phi.

## 4. Main Conversation Entry Points

Bot phai ho tro cac entry point:

1. User nhan "tu van du hoc Han".
2. User comment/inbox tu bai viet fanpage.
3. User bam CTA "Check kha nang di du hoc Han mien phi".
4. User hoi "di Han het bao nhieu tien?".
5. User hoi "em co di duoc khong?".
6. User hoi "quy trinh di Han nhu the nao?".
7. User hoi "nen chon Seoul hay tinh?".
8. User hoi "D4-1 va D2 khac nhau the nao?".
9. User hoi truong cu the.
10. User muon gap tu van vien.

## 5. Default Journey-first Flow

### Step 1: Greeting

Bot chao than thien va hoi user muon tim hieu noi dung nao.

Menu goi y:

1. Quy trinh du hoc Han.
2. Tong chi phi can chuan bi.
3. Check ho so co di duoc khong.
4. Nen chon Seoul hay tinh.
5. D4-1 va D2 khac nhau the nao.
6. Tim truong theo ngan sach/khu vuc.
7. Gap tu van vien.

### Step 2: Need Classification

Bot phan loai nhu cau chinh thanh:

- `PROCESS_INFO`
- `GENERAL_COST`
- `ELIGIBILITY_CHECK`
- `REGION_ADVICE`
- `PATHWAY_ADVICE`
- `SCHOOL_SEARCH`
- `HUMAN_CONSULTATION`

### Step 3: Basic Profile Collection

Bot thu thap lan luot, khong hoi qua nhieu cau cung luc:

- Ten.
- So dien thoai.
- Nam sinh.
- Tinh/thanh.
- Da tot nghiep THPT/DH chua.
- Nam tot nghiep.
- GPA/hoc luc.
- Da hoc tieng Han chua.
- TOPIK neu co.
- Ngan sach gia dinh du kien.
- Muon di ky/nam nao.
- Muc tieu: hoc tieng, dai hoc, cao dang/nghe, chua ro.
- Khu vuc mong muon: Seoul, gan Seoul, tinh chi phi thap, chua ro.
- Nganh quan tam neu co.
- Co nguoi than o Han khong.
- Da tung truot visa nuoc nao chua neu phu hop.

### Step 4: Initial Assessment

Bot phan loai so bo:

- Lead score A/B/C/D.
- Muc do san sang.
- Rui ro ho so.
- Nhom chi phi phu hop.
- Pathway de xuat.

### Step 5: Recommendation

Bot dua ra:

- Lo trinh phu hop.
- Khoang chi phi uoc tinh.
- Khu vuc nen can nhac.
- Buoc tiep theo.
- De xuat gap tu van vien neu lead nong.

### Step 6: Lead Capture + Handoff

Neu user de lai SDT hoac co nhu cau ro:

- Tao lead.
- Gan tag nhu cau.
- Gan lead score.
- Chuyen tu van vien.
- Gui thong bao noi bo.

## 6. Supported Intents

| Intent | Description | Example user messages | Required slots | Optional slots | Response behavior | Backend actions | Handoff rules | Acceptance criteria |
|---|---|---|---|---|---|---|---|---|
| `GREETING` | Mo dau hoi thoai | "Hi", "Tu van du hoc Han" | `facebook_psid` | `facebook_name` | Chao, hien menu journey-first | Tao/lay session | Neu user yeu cau gap nguoi that | Bot hien dung menu 7 muc |
| `MENU_SELECTION` | User chon quick reply/menu | "2", "Chi phi", button cost | `selected_option` | `session_id` | Route sang intent tuong ung | Log selection | Theo intent dich | Route dung >= 95% menu click |
| `ASK_PROCESS` | Hoi quy trinh | "Quy trinh di Han nhu the nao?" | none | `desired_intake` | Giai thich cac buoc, hoi muc tieu | Log intent | User muon checklist rieng | Co CTA check ho so/dat lich |
| `ASK_GENERAL_COST` | Hoi tong chi phi | "Di Han het bao nhieu?" | none | `desired_pathway`, `desired_region`, `budget` | Dua range, hoi ngan sach/pathway | Goi cost estimator neu co slot | Hoi bao gia chinh thuc | Co disclaimer chi phi |
| `CHECK_ELIGIBILITY` | Check phu hop ho so | "GPA 6.5 co di duoc khong?" | `education_level`, `graduation_year`, `gpa` | `korean_level`, `budget`, `desired_region` | Hoi slot thieu, cham so bo | Goi eligibility checker | Risk high hoac user de SDT | Khong cam ket visa |
| `ASK_REGION_ADVICE` | Seoul/gang Seoul/tinh | "Nen chon Seoul hay tinh?" | none | `budget`, `desired_pathway` | So sanh uu/nhuoc, hoi ngan sach | Log profile | Can shortlist khu vuc | Goi y theo ngan sach |
| `ASK_D4_D2` | So sanh D4-1 va D2 | "D4 voi D2 khac gi nhau?" | none | `education_level`, `goal` | Bang so sanh de hieu | Log intent | User muon chon route | Hoi muc tieu hoc tiep |
| `ASK_TOPIK` | Hoi TOPIK/tieng Han | "Chua TOPIK di duoc khong?" | none | `desired_pathway` | Giai thich vai tro TOPIK theo route | Update profile | Ho so can danh gia | Phan biet D4-1/D2 |
| `ASK_GPA` | Hoi GPA/hoc luc | "GPA may thi di duoc?" | none | `gpa`, `school_level` | Noi GPA la mot yeu to, can xem tong ho so | Update slot | GPA thap/gap year dai | Khong ket luan tuyet doi |
| `ASK_GAP_YEAR` | Hoi nam trong/gap year | "Tot nghiep 2020 co di duoc khong?" | `graduation_year` | `activities_since_graduation` | Noi co the co rui ro, can check them | Eligibility risk flag | Gap year dai | Chuyen TVV neu rui ro |
| `ASK_BUDGET` | Hoi ngan sach | "200 trieu di duoc khong?" | `budget` | `desired_region`, `pathway` | Match ngan sach voi nhom khu vuc | Cost estimate | Ngan sach sat nguong | Co range va disclaimer |
| `ASK_PART_TIME_JOB` | Hoi viec lam them | "Sang do lam them du tra hoc phi khong?" | none | `pathway`, `region` | Noi quy dinh chung, khong khuyen khich phu thuoc lam them | Log risk | User co y dinh lam trai quy dinh | Co compliance warning |
| `ASK_VISA_GENERAL` | Hoi visa | "Bao dau visa khong?" | none | `profile` | Giai thich khong cam ket, yeu to anh huong | Log unsafe if guarantee asked | Luon de xuat TVV voi case visa | Co visa disclaimer |
| `ASK_DOCUMENTS` | Hoi ho so giay to | "Can giay to gi?" | none | `education_level`, `pathway` | Dua checklist tong quan | Log intent | User muon nop ho so | CTA dat lich |
| `ASK_TIMELINE` | Hoi thoi gian | "Chuan bi may thang?" | none | `desired_intake` | Dua timeline so bo | Log intent | Sap deadline | Chuyen TVV neu ky gan |
| `ASK_PARENT_CONCERN` | Phu huynh hoi an toan/chi phi | "Con toi di Han co an toan khong?" | none | `relationship`, `budget` | Ton trong, ro rang, tap trung chi phi/rui ro | Tag parent | Muon goi tu van | Co CTA goi lai |
| `ASK_SCHOOL_RECOMMENDATION` | Goi y nhom truong | "Nen hoc truong nao?" | `budget`, `desired_region`, `goal` | `gpa`, `major` | Khong school-first; hoi profile truoc | School service neu co | Can shortlist cu the | Chi goi y nhom neu chua du data |
| `ASK_SCHOOL_INFO` | Hoi thong tin truong | "Dongguk co nganh gi?" | `school_name` | `major`, `program_type` | Tra DB verified hoac noi can xac nhan | School lookup | Data missing/unverified | Khong bia du lieu |
| `ASK_SCHOOL_COST` | Hoi chi phi truong | "Dongguk het bao nhieu?" | `school_name` | `program_type`, `intake_term` | Hoi D4-1/D2 neu thieu, tra verified cost neu co | School cost lookup | Hoi bao gia chi tiet | Co disclaimer |
| `ASK_MAJOR` | Hoi nganh | "Nganh media o Han the nao?" | `major` | `school_name`, `education_level` | Giai thich tong quan, hoi muc tieu | KB/school lookup | Muon list truong | Can source truong verified |
| `ASK_DORMITORY` | Hoi KTX | "Co KTX khong?" | none | `school_name`, `region` | Giai thich tuy truong, hoi truong/pathway | School lookup | Can gia KTX chinh thuc | Disclaimer data |
| `ASK_SCHOLARSHIP` | Hoi hoc bong | "Co hoc bong khong?" | none | `gpa`, `topik`, `school_name` | Noi dieu kien tuy truong, can xac minh | School lookup | Hoi hoc bong cu the | Khong cam ket hoc bong |
| `LEAD_CAPTURE` | Thu thong tin lead | User gui SDT/ten | `phone` | `full_name`, `province` | Xac nhan da nhan, noi buoc tiep theo | Create/update lead | Neu phone hop le | Lead duoc tao/update |
| `BOOK_CONSULTATION` | Dat lich tu van | "Dat lich giup em" | `phone` | `preferred_time` | Hoi thoi gian goi, xac nhan | Create handoff/task | Luon handoff | Tao task cho TVV |
| `HUMAN_HANDOFF` | Gap tu van vien | "Cho em gap nguoi that" | none | `phone`, `question` | Thong bao chuyen TVV, neu ngoai gio noi SLA | Create handoff | Luon | Advisor nhan summary |
| `UNKNOWN` | Bot khong chac intent | Cau hoi mo/loi go | none | raw message | Hoi lai bang menu/ngan gon | Log fallback | Sau 2 lan khong hieu | Fallback < target threshold |
| `UNSAFE_OR_HIGH_RISK` | Yeu cau vi pham/compliance | "Lam gia ho so", "Bao dau visa" | none | raw message | Tu choi nhe, dua huong dung, handoff neu can | Risk log | Case visa/ho so gia | Khong khuyen khich sai quy dinh |

## 7. Slot Filling / Information Collection

### 7.1. Nguyen tac

- Khong hoi qua nhieu cau cung luc; uu tien 2-3 cau quan trong dau tien.
- Chap nhan user tra loi tu nhien, khong bat buoc dung form.
- Luu dan thong tin vao `UserProfile` tam thoi theo session.
- Neu user bo do, van luu partial lead neu co SDT hoac Facebook PSID.
- Dung quick reply buttons cho lua chon nhu pathway, khu vuc, ngan sach range, muc tieu hoc.
- Moi slot can co `source`, `confidence`, `updated_at` de audit LLM extraction.

### 7.2. Slot theo intent

| Intent | Slots uu tien | Cach hoi |
|---|---|---|
| `CHECK_ELIGIBILITY` | `education_level`, `graduation_year`, `gpa`, `korean_level`, `topik_level`, `budget`, `desired_intake`, `desired_pathway`, `desired_region` | Hoi truoc GPA/nam tot nghiep/ngan sach; sau do hoi tieng Han va khu vuc |
| `ASK_GENERAL_COST` | `desired_pathway`, `desired_region`, `budget`, `dormitory_preference` | Dua range chung truoc, hoi ngan sach va muc tieu hoc de tinh sat hon |
| `ASK_SCHOOL_COST` | `school_name`, `program_type`, `intake_term` | Xac nhan truong, hoi D4-1/D2 neu chua ro |
| `ASK_REGION_ADVICE` | `budget`, `desired_pathway`, `lifestyle_preference`, `family_in_korea` | Hoi ngan sach va uu tien: viec lam, chi phi, gan nguoi than, thanh pho lon |
| `LEAD_CAPTURE` | `full_name`, `phone`, `province`, `need_type` | Neu user gui SDT, chi hoi them ten/tinh khi can |

## 8. Conversation Flow Details

### Flow A: User moi tim hieu

Input: "Em muon tim hieu du hoc Han"

1. Bot chao va noi co the ho tro check lo trinh so bo.
2. Bot hien menu 7 muc journey-first.
3. Neu user chon quy trinh, bot giai thich cac buoc: dinh huong, check ho so, chon pathway/khu vuc, chuan bi tai chinh, nop truong, visa, bay.
4. Bot hoi: "Em da tot nghiep THPT/DH chua va du kien di nam nao?"
5. Bot luu slot, goi y buoc tiep theo: check ho so hoac uoc tinh chi phi.

### Flow B: User hoi chi phi

Input: "Di Han het bao nhieu tien?"

Bot phan hoi:

- Giai thich chi phi phu thuoc D4-1/D2, khu vuc, truong, KTX, sinh hoat, ty gia.
- Dua range so bo theo nhom:
  - Tinh tiet kiem: range thap nhat.
  - Gan Seoul/Gyeonggi: range trung binh.
  - Seoul/top school: range cao.
- Hoi: "Gia dinh minh du kien chuan bi khoang bao nhieu va em muon hoc tieng D4-1 hay vao chuyen nganh D2?"
- Neu co ngan sach/pathway, goi cost estimator.
- Neu user hoi bao gia chinh thuc, tao lead/handoff.

### Flow C: User muon check ho so

Input: "GPA 6.5, chua TOPIK, co di duoc khong?"

1. Bot noi co the check phu hop so bo, khong cam ket visa.
2. Bot hoi nam tot nghiep, ngan sach, muc tieu hoc, khu vuc mong muon.
3. Eligibility checker tra score/risk.
4. Bot noi: "Voi GPA 6.5 va chua TOPIK, em co the can nhac route D4-1 neu cac yeu to tai chinh va gap year phu hop. Can check them nam tot nghiep va ngan sach."
5. Neu risk medium/high, handoff cho tu van vien.

### Flow D: User hoi D4-1 vs D2

Bot dua bang:

| Tieu chi | D4-1 | D2 |
|---|---|---|
| Muc tieu | Hoc tieng Han tai truong | Hoc chuyen nganh cao dang/dai hoc/sau dai hoc |
| Phu hop voi | Chua du tieng Han, can lam quen moi truong | Da du dieu kien dau vao nganh/truong |
| TOPIK | Thuong chua bat buoc cao luc bat dau | Thuong can TOPIK/ngoai ngu theo truong |
| Chi phi | Tuy truong va khu vuc | Tuy hoc phi nganh/truong |
| Luu y | Can lo trinh len chuyen nganh | Can check dieu kien dau vao va ho so |

Sau bang, bot hoi muc tieu cua user de goi y route.

### Flow E: User hoi Seoul hay tinh

Bot so sanh:

| Khu vuc | Uu diem | Han che | Phu hop |
|---|---|---|---|
| Seoul | Nhieu truong, moi truong nang dong, brand manh | Chi phi cao, canh tranh cao | Ngan sach tot, muc tieu thanh pho lon |
| Gan Seoul/Gyeonggi | Can bang chi phi va ket noi | Tuy khu vuc, can xem truong | Muon gan Seoul nhung toi uu chi phi |
| Tinh chi phi thap | Chi phi de chiu, ap luc thap hon | Xa trung tam, it lua chon hon | Ngan sach can toi uu, uu tien an toan tai chinh |

Bot hoi ngan sach va muc tieu hoc de goi y.

### Flow F: User da biet truong

Input: "Truong Dongguk het bao nhieu?"

1. Bot extract `school_name=Dongguk`.
2. Bot hoi: "Em dang hoi he tieng D4-1 hay he chuyen nganh D2?"
3. Neu co DB verified, tra range hoc phi/KTX theo ky va disclaimer.
4. Neu chua co verified data, bot noi can tu van vien xac nhan voi nguon chinh thuc.
5. Neu user muon nhan bao gia chi tiet, bot thu SDT va tao lead.

### Flow G: Human handoff

Trigger:

- User de lai SDT.
- User hoi phi chinh thuc.
- User hoi visa case rui ro.
- User hoi ho so phuc tap.
- User muon nop ho so.
- Bot khong hieu sau 2 lan.
- User yeu cau gap nguoi that.

Bot can thong bao trang thai: da chuyen tu van vien, du kien phan hoi trong gio lam viec hoac SLA ngoai gio.

## 9. AI/LLM Usage Design

LLM duoc dung de:

- Hieu cau hoi tu nhien.
- Phan loai intent.
- Extract slots.
- Viet cau tra loi than thien theo policy.
- Tom tat hoi thoai cho tu van vien.
- Goi y cau hoi tiep theo.
- Giai thich thong tin dua tren knowledge base.

LLM khong duoc dung de:

- Tu bia hoc phi, truong, nganh, hoc bong, ky nhap hoc.
- Tu tinh tong chi phi neu khong co cost service/config.
- Cam ket dau visa.
- Dua loi khuyen phap ly tuyet doi.
- Tra loi thong tin chua co source.

Nguyen tac:

- LLM khong phai source of truth.
- Database verified va config noi bo moi la source of truth.
- Cost calculator backend moi la noi tinh chi phi.
- Voi du lieu chua verified, bot phai noi "can tu van vien xac nhan".

### 9.1. Prompt noi bo mau

**Intent classification prompt**

```text
Ban la intent classifier cho chatbot tu van du hoc Han. Hay tra ve JSON voi intent, confidence, language, detected_entities. Chi chon intent trong danh sach cho phep. Neu khong chac, dung UNKNOWN va neu co rui ro visa/ho so gia/bao dau, dung UNSAFE_OR_HIGH_RISK.
```

**Slot extraction prompt**

```text
Trich xuat thong tin ho so du hoc Han tu message va conversation context. Tra ve JSON gom slot_name, value, confidence, source_text. Khong suy dien du lieu khong co trong message.
```

**Response generation prompt**

```text
Viet cau tra loi ngan gon, than thien, bang tieng Viet. Chi dung facts tu knowledge_base, cost_result, eligibility_result hoac verified_school_data. Neu du lieu chua verified, noi can tu van vien xac nhan. Luon tranh cam ket visa va chi phi tuyet doi.
```

**Guardrail prompt**

```text
Kiem tra draft response. Neu co cam ket visa, bia so lieu, khuyen khai sai ho so, khuyen lam them trai quy dinh hoac dua thong tin truong chua co source, hay danh dau blocked=true va dua ly do.
```

## 10. Rule-based vs LLM Routing

| Layer | Dung cho | Ly do |
|---|---|---|
| Rule-based | Greeting, menu, quick replies, lead capture, book consultation, disclaimers, FAQ co dinh, handoff | Can on dinh, de audit, khong ton LLM |
| LLM | Cau hoi tu nhien, sai chinh ta, nhieu y, extract ten truong/nganh/khu vuc/ngan sach, tu van mem theo profile | Can hieu ngon ngu linh hoat |
| Database/cost service | Truong, nganh, hoc phi, KTX, tong chi phi theo truong, so sanh truong | Source of truth va co the audit |

Router uu tien rule-based neu user bam quick reply hoac match trigger ro. Neu khong, LLM classify intent. Truoc khi tra loi thong tin chi phi/truong/visa, response phai qua guardrail.

## 11. General Cost Estimator MVP

### 11.1. Output

Output la range, khong phai so co dinh. Moi response phai ghi "uoc tinh" va cac yeu to co the thay doi: truong, ky nhap hoc, ty gia, KTX, hoc bong, sinh hoat phi.

### 11.2. Nhom chi phi

| Nhom | Mo ta | Thanh phan can tinh |
|---|---|---|
| Tinh tiet kiem | Truong/khu vuc chi phi thap hon | Hoc phi, phi ho so/trung tam, KTX, sinh hoat, visa/KVAC, ve may bay, bao hiem, du phong |
| Gan Seoul/Gyeonggi | Can bang chi phi va kha nang tiep can Seoul | Cung thanh phan tren, range cao hon tinh |
| Thanh pho lon Busan/Daegu/Daejeon | Moi truong do thi, chi phi trung binh-cao | Cung thanh phan tren |
| Seoul/top school | Chi phi cao, can budget tot | Cung thanh phan tren, can disclaimer manh |

### 11.3. Quy tac

- Neu chua biet pathway, dua range rong va hoi D4-1/D2.
- Neu chua biet region, dua so sanh theo nhom region.
- Neu user dua ngan sach, bot danh gia "co the phu hop/hoi sat/can tinh lai", khong noi "du chac".
- Bao gia chinh thuc luon can tu van vien xac nhan.

## 12. Eligibility Check MVP

### 12.1. Input

- GPA.
- Nam tot nghiep.
- Gap year.
- TOPIK/tieng Han.
- Ngan sach.
- Muc tieu hoc.
- Khu vuc mong muon.
- Lich su visa neu co.

### 12.2. Output

- `lead_score`: A/B/C/D.
- `risk_level`: LOW/MEDIUM/HIGH.
- `suggested_pathway`.
- `suggested_region`.
- `missing_information`.
- `next_action`.

### 12.3. Rule so bo

| Score | Mo ta | Next action |
|---|---|---|
| A | Ho so ro, ngan sach phu hop, nhu cau gan | Chuyen tu van vien trong SLA uu tien |
| B | Co tiem nang, can bo sung tieng Han/tai chinh/thong tin | Thu them slot, hen tu van |
| C | Co rui ro, can tu van vien danh gia | Handoff kem risk warning |
| D | Chua phu hop hoac qua thieu thong tin | Nuoi duong bang content, khuyen bo sung dieu kien |

Bot khong duoc ket luan "chac chan di duoc", "chac chan dau visa", "khong the di". Chi dung "kha nang phu hop so bo", "can tu van vien xac nhan", "co mot so rui ro can kiem tra them".

## 13. Human Handoff Requirements

### 13.1. Trigger handoff

- User de lai SDT.
- User hoi chi phi/hoc phi chinh thuc.
- User hoi visa case rui ro.
- User co gap year dai, GPA thap, ngan sach sat nguong, lich su truot visa.
- User hoi ho so phuc tap.
- User muon nop ho so/dat lich.
- Bot khong hieu sau 2 lan lien tiep.
- User yeu cau gap nguoi that.

### 13.2. Thong tin gui cho tu van vien

- Ten.
- SDT.
- Facebook profile/PSID neu co.
- Nhu cau chinh.
- GPA.
- Nam tot nghiep.
- Ngan sach.
- Khu vuc mong muon.
- Muc tieu hoc.
- Cau hoi cuoi cung.
- Bot summary.
- Lead score.
- Risk warning.
- SLA mong muon.

### 13.3. Trang thai conversation sau handoff

- `handoff_requested`: da tao yeu cau.
- `assigned`: da gan tu van vien.
- `advisor_replied`: tu van vien da phan hoi.
- `closed_by_bot`: user khong tiep tuc sau fallback/ngoai gio.

## 14. Lead Creation Requirements

Bot tao lead khi:

- User de lai SDT.
- User bam dat lich tu van.
- User hoan thanh check eligibility.
- User hoi chi phi cu the va co nhu cau ro.
- User duoc phan loai lead A/B.
- User yeu cau tu van vien goi lai.

Lead fields:

| Field | Required | Ghi chu |
|---|---:|---|
| `full_name` | No | Hoi neu chua co |
| `phone` | Conditional | Bat buoc de tu van vien goi |
| `facebook_psid` | Yes | Dinh danh Messenger |
| `facebook_name` | No | Neu Facebook cung cap |
| `province` | No | Ho tro phan vung lead |
| `birth_year` | No | PII |
| `education_level` | No | THPT/DH/CĐ |
| `graduation_year` | No | Dung cho risk |
| `gpa` | No | Dung cho eligibility |
| `korean_level` | No | Chua hoc/so cap/trung cap |
| `topik_level` | No | TOPIK I/II/cap |
| `budget_vnd` | No | Sensitive financial |
| `desired_pathway` | No | D4-1/D2/chua ro |
| `desired_region` | No | Seoul/gan Seoul/tinh/chua ro |
| `desired_major` | No | Neu co |
| `desired_intake` | No | Ky/nam |
| `lead_source` | Yes | Fanpage/comment/CTA |
| `lead_score` | No | A/B/C/D |
| `risk_level` | No | LOW/MEDIUM/HIGH |
| `status` | Yes | new/qualified/handoff/contacted/closed |
| `assigned_advisor_id` | No | Sau khi gan |
| `conversation_summary` | No | Tom tat cho TVV |
| `created_at` | Yes | System |
| `updated_at` | Yes | System |

## 15. Chatbot Response Tone

Tone:

- Than thien, de hieu, giong tu van vien that.
- Khong qua hoc thuat.
- Khong gay ap luc chot sale.
- Khong hua hen qua muc.
- Phu hop ca hoc sinh va phu huynh.

Response rules:

- Tra loi ngan truoc, hoi tiep sau.
- Noi dung phuc tap dung bullet/table.
- Luon co CTA phu hop.
- Khong spam nhieu tin lien tuc.
- Khong hoi thong tin nhay cam neu chua can.

CTA mau:

- "Em muon chi check ho so so bo khong?"
- "Em cho chi xin ngan sach du kien de goi y khu vuc phu hop nhe?"
- "Em muon tu van vien goi lai khong?"

## 16. Required Disclaimers

Bat buoc dung disclaimer trong cac case:

- Chi phi.
- Visa.
- Dieu kien truong.
- Hoc bong.
- Viec lam them.
- Truong/nganh cu the.

Disclaimer mau:

> Luu y: Thong tin tren la uoc tinh ban dau va co the thay doi theo ky nhap hoc, ty gia, chinh sach tung truong va ho so ca nhan. Tu van vien se kiem tra lai nguon chinh thuc truoc khi gui phuong an/bao gia cuoi cung.

Visa disclaimer:

> Chatbot khong the cam ket ket qua visa. Ket qua phu thuoc vao ho so, tai chinh, hoc luc, phong van va quyet dinh cua co quan xet duyet.

## 17. Admin Configuration MVP

Admin can cau hinh duoc:

- FAQ.
- Menu bot.
- Quick replies.
- General cost ranges.
- Disclaimer text.
- Lead scoring rules.
- Handoff triggers.
- Business hours.
- Advisor assignment rule.
- Blocklist keywords neu can.
- Fallback message.

MVP co the dung admin simple UI hoac bang cau hinh trong database. Moi thay doi config can co audit log.

## 18. Integration Points

| Integration | MVP behavior | Phase sau |
|---|---|---|
| Facebook Messenger Webhook | Nhan message, quick reply, button; gui response | Comment-to-inbox automation, template message |
| CRM Lead API | Create/update lead, luu summary | Full lead pipeline, assignment, SLA |
| LLM Service | Classify intent, extract slots, generate guarded response | Multi-provider, eval, prompt versioning |
| Cost Estimator Service | Estimate general cost theo region/pathway | School-level cost calculator |
| School Database Service | Lookup truong khi user hoi cu the; co the mock/simple | Database verified day du ve truong/nganh/hoc phi/KTX |
| Notification Service | Notify advisor khi handoff, notify manager hot lead | Zalo/Slack/email/app notification |

## 19. Non-functional Requirements

- Response time: p95 duoi 3 giay cho FAQ/rule-based, p95 duoi 8 giay cho LLM/cost lookup.
- Availability: 99% trong gio campaign; graceful degradation neu LLM loi.
- Logging: luu message, intent, slot, action, error, latency.
- Monitoring: dashboard conversation volume, error rate, LLM latency, webhook failures.
- Rate limiting: theo PSID/IP/webhook source de tranh spam.
- LLM cost control: cache FAQ, rule-based truoc LLM, max token, prompt versioning.
- Data privacy: chi thu PII can thiet, thong bao muc dich khi xin SDT.
- PII protection: mask phone trong log ky thuat, role-based access.
- Conversation storage: lien ket session-message-lead.
- Error handling: fallback than thien va retry co gioi han.
- Auditability: luu source cua slot va response data.
- Data retention: conversation raw co the giu 12-24 thang tuy policy; PII xoa/anonymize theo yeu cau.
- Security: verify Facebook webhook signature, validate input, secrets qua env vars, khong hardcode token.

## 20. Analytics Metrics

MVP can do:

- Tong so conversation.
- So lead duoc tao.
- Conversion chat -> lead.
- Conversion lead -> handoff.
- Top intents.
- Top cau hoi.
- Ty le bot khong hieu.
- Ty le user bo do.
- So user check eligibility.
- So user hoi chi phi.
- So user hoi truong cu the.
- Thoi gian phan hoi cua tu van vien.
- Lead score distribution.
- Cost per lead neu co du lieu ad/campaign.

## 21. MVP Scope

### Must have trong 30 ngay

- Facebook Messenger webhook.
- Greeting/menu.
- Journey-first flow.
- FAQ co ban.
- General cost estimator.
- Eligibility check so bo.
- Lead capture.
- Human handoff.
- Conversation logging.
- LLM intent classification.
- LLM slot extraction.
- Admin cau hinh FAQ/cost range/disclaimer co ban.
- Tao lead sang CRM hoac database don gian.

### Should have

- Basic school lookup cho 20-30 truong.
- Basic region recommendation.
- Tom tat hoi thoai cho tu van vien.
- Lead scoring A/B/C/D.
- Notification cho tu van vien.

### Could have

- Book consultation calendar.
- PDF/tin nhan tu van tu dong.
- Multi-language support.
- AI recommendation nang cao.

### Out of scope MVP

- Full advisor console.
- Full parent dashboard.
- Full CTV portal.
- OCR ho so.
- AI interview coach.
- Crawler tu dong.
- Payment/contract.
- Mobile app.

## 22. User Stories & Acceptance Criteria

| Role | User story | Priority | Acceptance criteria |
|---|---|---|---|
| Hoc sinh moi tim hieu | As a hoc sinh moi tim hieu, I want chatbot giai thich quy trinh du hoc Han, so that toi hieu cac buoc can chuan bi truoc khi gap tu van vien. | Must | Given toi nhan "em muon tim hieu du hoc Han", When bot nhan message, Then bot hien menu journey-first gom quy trinh, chi phi, check ho so, khu vuc, D4/D2, gap tu van vien. |
| Phu huynh | As a phu huynh, I want biet tong ngan sach can chuan bi, so that gia dinh co the quyet dinh co nen di tiep khong. | Must | Given toi hoi tong tien, When bot tra loi, Then bot dua range theo khu vuc/pathway va co disclaimer chi phi. |
| Hoc sinh check ho so | As a hoc sinh muon check ho so, I want nhap GPA/nam tot nghiep/ngan sach, so that toi biet kha nang phu hop so bo. | Must | Given toi cung cap GPA va nam tot nghiep, When bot du slot toi thieu, Then bot tra lead score/risk level va khong cam ket visa. |
| Hoc sinh hoi chi phi | As a hoc sinh hoi chi phi, I want bot hoi them ngan sach va muc tieu hoc, so that goi y khu vuc phu hop hon. | Must | Given toi hoi "200 trieu di duoc khong", When bot xu ly, Then bot khong ket luan tuyet doi va goi y check them pathway/khu vuc. |
| Hoc sinh hoi truong cu the | As a school-aware user, I want hoi hoc phi truong cu the, so that toi biet co can gap tu van vien khong. | Should | Given toi hoi "Dongguk het bao nhieu", When DB chua co verified data, Then bot noi can tu van vien xac nhan va de nghi de lai SDT. |
| Tu van vien | As a tu van vien, I want nhan tom tat hoi thoai va lead score, so that toi goi lai dung nhu cau. | Must | Given user duoc handoff, When task tao, Then advisor nhan ten/SDT/nhu cau/profile/summary/risk warning. |
| Admin | As an admin, I want cau hinh FAQ, cost range va disclaimer, so that bot cap nhat duoc noi dung ma khong can deploy. | Must | Given admin sua config, When luu thanh cong, Then bot dung config moi va audit log duoc tao. |
| Owner/manager | As an owner, I want xem analytics chatbot, so that toi biet fanpage tao bao nhieu lead va intent nao pho bien. | Should | Given co conversation, When xem dashboard, Then thay tong chat, lead, conversion, top intents, fallback rate. |

## 23. Edge Cases

| Edge case | Expected behavior |
|---|---|
| User hoi qua chung chung | Hien menu va hoi nhu cau chinh |
| User hoi nhieu y mot message | LLM tach intents, bot uu tien y co rui ro/lead value cao, sau do hoi tiep |
| User go sai ten truong | Fuzzy match neu confidence cao; neu thap hoi xac nhan |
| User chua muon de lai SDT | Van tra loi co ban, luu partial profile theo PSID |
| User la phu huynh | Chuyen tone phu huynh, hoi thong tin con, tap trung chi phi/an toan |
| User chua biet GPA | Hoi hoc luc gan dung hoac de trong slot |
| User khong biet D4-1/D2 | Giai thich ngan va hoi muc tieu hoc |
| User ngan sach qua thap | Noi can kiem tra phuong an tiet kiem, khong ep sale, co the nuoi duong content |
| User hoi "bao do visa khong?" | Tu choi cam ket, dua visa disclaimer, de xuat TVV check case |
| User hoi viec lam them qua muc | Nhac quy dinh, khong khuyen phu thuoc lam them/trai phep |
| User hoi thong tin chua co DB | Noi can tu van vien xac nhan, khong bia |
| LLM khong chac intent | Hoi lai bang 2-3 lua chon |
| Facebook webhook loi | Retry, log error, alert neu vuot threshold |
| LLM API loi | Fallback rule-based/FAQ va xin loi ngan gon |
| CRM API loi | Queue retry, khong lam mat lead, notify admin neu fail |
| Tu van vien ngoai gio | Thong bao gio lam viec va SLA phan hoi |

## 24. Guardrails & Compliance

- Khong cam ket dau visa.
- Khong khang dinh chi phi chinh xac tuyet doi.
- Khong tu van truong/nganh neu du lieu chua verified.
- Khong khuyen khich khai sai ho so.
- Khong khuyen khich di lam them trai quy dinh.
- Khong thu thap du lieu nhay cam khong can thiet.
- Khong de LLM tu tao so lieu.
- Luon chuyen tu van vien voi case rui ro.
- Response co lien quan visa/chi phi/truong phai qua guardrail check.

## 25. Sample Conversation Scripts

### 25.1. User moi tim hieu

User: Em muon tim hieu du hoc Han.  
Bot: Chao em, chi co the giup em nam quy trinh, uoc tinh chi phi va check ho so so bo. Em muon tim hieu muc nao truoc?  
Bot: 1. Quy trinh 2. Chi phi 3. Check ho so 4. Seoul hay tinh 5. D4-1/D2 6. Tim truong 7. Gap tu van vien  
User: Quy trinh.  
Bot: Du hoc Han thuong gom: check ho so, chon lo trinh D4-1/D2, chuan bi tai chinh, nop truong, xin visa va chuan bi bay. Em da tot nghiep THPT/DH chua va du kien di nam nao?

### 25.2. User hoi chi phi

User: Di Han het bao nhieu tien?  
Bot: Chi phi phu thuoc vao lo trinh D4-1/D2, khu vuc, truong, KTX va sinh hoat. Uoc tinh ban dau thuong chia theo nhom: tinh tiet kiem, gan Seoul/Gyeonggi, va Seoul/top school. Em du kien ngan sach gia dinh khoang bao nhieu de chi goi y nhom phu hop hon?  
User: Khoang 250 trieu.  
Bot: Voi ngan sach nay, minh nen xem ky hon cac nhom tinh tiet kiem hoac gan Seoul tuy lo trinh. Luu y day la uoc tinh ban dau; tu van vien can check lai theo truong, ky nhap hoc va ty gia truoc khi bao gia cuoi cung.

### 25.3. Phu huynh hoi tong tien

User: Con toi di Han tong het bao nhieu?  
Bot: Da, tong chi phi se phu thuoc chau hoc tieng hay vao chuyen nganh, khu vuc Seoul hay tinh, KTX va sinh hoat. De uoc tinh sat hon, anh/chi cho em biet chau da tot nghiep lop 12 chua va gia dinh du kien ngan sach khoang bao nhieu?  
User: Chau vua tot nghiep, ngan sach 300 trieu.  
Bot: Em se ghi nhan. Muc nay co the xem duoc mot so huong, nhung can check them GPA, tieng Han va khu vuc mong muon. Anh/chi muon tu van vien goi lai de tinh phuong an chi tiet khong?

### 25.4. User check ho so

User: GPA 6.5, chua TOPIK, co di duoc khong?  
Bot: Co the check phu hop so bo cho em, nhung chatbot khong cam ket ket qua visa. Em cho chi biet them nam tot nghiep, ngan sach du kien va em muon hoc tieng D4-1 hay vao chuyen nganh D2?  
User: Tot nghiep 2023, ngan sach 280 trieu, chua ro.  
Bot: So bo thi em co the can nhac route D4-1 neu cac phan tai chinh va ke hoach hoc tap phu hop. Ho so can check ky hon vi chua co TOPIK va pathway chua ro. Em muon tu van vien goi lai check chi tiet khong?

### 25.5. User hoi D4-1 vs D2

User: D4-1 va D2 khac gi nhau?  
Bot: D4-1 la visa hoc tieng Han tai truong, phu hop khi em chua du tieng hoac muon lam quen moi truong truoc. D2 la visa hoc chuyen nganh cao dang/dai hoc/sau dai hoc, thuong can dap ung dieu kien dau vao cua truong/nganh. Muc tieu cua em la hoc tieng truoc hay vao nganh luon?

### 25.6. User hoi Seoul hay tinh

User: Nen chon Seoul hay tinh?  
Bot: Seoul nhieu lua chon va moi truong nang dong nhung chi phi cao. Gan Seoul/Gyeonggi can bang hon. Tinh chi phi thap hon va phu hop neu gia dinh muon toi uu ngan sach. Em du kien ngan sach bao nhieu va uu tien chi phi hay moi truong thanh pho lon?

### 25.7. User hoi truong cu the

User: Truong Dongguk het bao nhieu?  
Bot: Em dang hoi he hoc tieng D4-1 hay he chuyen nganh D2 cua Dongguk a? Chi can thong tin nay de check dung muc phi.  
User: D4-1.  
Bot: Neu du lieu verified cua ky moi nhat chua co trong he thong, chi se chuyen tu van vien check lai nguon chinh thuc truoc khi bao gia. Em de lai SDT duoc khong?

### 25.8. User hoi bao do visa

User: Trung tam co bao do visa khong?  
Bot: Chatbot khong the cam ket ket qua visa. Ket qua phu thuoc vao ho so, tai chinh, hoc luc, phong van va quyet dinh cua co quan xet duyet. Neu em gui GPA, nam tot nghiep, ngan sach va lich su visa neu co, chi co the check rui ro so bo va chuyen tu van vien danh gia ky hon.

## 26. Technical Architecture Summary

### 26.1. High-level architecture

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

### 26.2. Components

| Component | Responsibilities |
|---|---|
| Webhook Controller | Verify Facebook signature, parse events, de-duplicate messages |
| Bot Orchestrator | Dieu phoi conversation state, goi router/service, build response |
| Intent Router | Rule-based routing truoc, LLM routing sau |
| LLM Service | Classify intent, extract slots, generate guarded response |
| Slot Manager | Merge slots, confidence, missing slots, profile update |
| User Profile Store | Luu profile tam thoi va lien ket PSID |
| FAQ/Knowledge Base | Noi dung verified cho FAQ va policy |
| General Cost Estimator | Tra range chi phi theo pathway/khu vuc/config |
| Eligibility Checker | Cham score/risk so bo theo rule |
| Lead Service/CRM | Tao/update lead, status, assignment |
| Human Handoff Service | Tao request, gan tu van vien, gui summary |
| Messenger Response API | Gui text, quick reply, button, typing indicator |

### 26.3. Tech stack goi y

- Backend: Spring Boot.
- Database: PostgreSQL.
- Cache/session: Redis.
- LLM: abstraction layer de thay doi provider.
- Admin simple UI: React hoac tam thoi cau hinh DB.
- Deployment: Docker.
- Logging/monitoring: structured logs, metrics dashboard, alert co ban.

## 27. Data Model MVP

| Entity | Fields chinh | Relationship | Index | PII | Retention |
|---|---|---|---|---|---|
| `ConversationSession` | `id`, `facebook_psid`, `status`, `current_intent`, `started_at`, `last_message_at` | Has many messages, slots, intent logs | `facebook_psid`, `status`, `last_message_at` | PSID | 12-24 thang |
| `Message` | `id`, `session_id`, `sender_type`, `text`, `payload`, `created_at` | Belongs to session | `session_id`, `created_at` | Co the co PII trong text | 12-24 thang/anonymize |
| `UserProfile` | `id`, `facebook_psid`, `name`, `phone`, `province`, `education_level`, `gpa`, `budget_vnd` | Has leads/sessions | `facebook_psid`, `phone` | Yes | Theo policy CRM |
| `Lead` | Fields tai section 14 | Belongs to profile/session | `phone`, `status`, `lead_score`, `created_at` | Yes | Theo policy kinh doanh |
| `IntentLog` | `id`, `session_id`, `intent`, `confidence`, `raw_text`, `model_version` | Belongs to session | `intent`, `created_at` | Raw text co the co PII | 12 thang |
| `SlotValue` | `id`, `session_id`, `slot_name`, `value`, `confidence`, `source` | Belongs to session/profile | `session_id`, `slot_name` | Tuy slot | Theo profile |
| `FAQItem` | `id`, `question`, `answer`, `intent`, `is_active`, `updated_at` | Used by KB | `intent`, `is_active` | No | Den khi thay doi |
| `CostRangeTemplate` | `id`, `region_group`, `pathway`, `min_vnd`, `max_vnd`, `components`, `effective_from` | Used by estimator | `region_group`, `pathway` | No | Versioned |
| `EligibilityAssessment` | `id`, `lead_id`, `score`, `risk_level`, `rules_triggered`, `missing_info` | Belongs to lead | `lead_id`, `score`, `risk_level` | Co profile data | Theo lead |
| `HandoffRequest` | `id`, `lead_id`, `session_id`, `reason`, `status`, `assigned_advisor_id`, `sla_due_at` | Belongs to lead/session | `status`, `assigned_advisor_id` | Yes | Theo lead |
| `AdvisorNotification` | `id`, `handoff_id`, `channel`, `status`, `sent_at`, `error` | Belongs to handoff | `handoff_id`, `status` | Co the co PII | 6-12 thang |
| `BotConfiguration` | `id`, `key`, `value_json`, `version`, `updated_by`, `updated_at` | Used globally | `key`, `version` | Usually no | Versioned |
| `AuditLog` | `id`, `actor`, `action`, `entity_type`, `entity_id`, `changes`, `created_at` | Global | `entity_type`, `entity_id`, `created_at` | Co the co PII | 12-24 thang |

## 28. API Contract MVP

### `POST /webhooks/facebook`

Muc dich: Nhan event tu Facebook Messenger.

Request mau:

```json
{
  "object": "page",
  "entry": [
    {
      "messaging": [
        {
          "sender": { "id": "PSID" },
          "message": { "mid": "m_1", "text": "Di Han het bao nhieu?" }
        }
      ]
    }
  ]
}
```

Response:

```json
{ "status": "received" }
```

Errors: invalid signature, duplicate message, unsupported event. Permission: Facebook app secret verification.

### `POST /bot/messages/process`

Muc dich: Xu ly mot message da parse.

Request:

```json
{ "psid": "PSID", "text": "GPA 6.5 co di duoc khong?", "source": "messenger" }
```

Response:

```json
{
  "session_id": "sess_123",
  "intent": "CHECK_ELIGIBILITY",
  "messages": [{ "type": "text", "text": "Co the check so bo..." }]
}
```

Errors: session unavailable, processing timeout. Permission: internal service.

### `POST /llm/intent-classify`

Muc dich: Phan loai intent tu text/context.

Request:

```json
{ "text": "Dongguk het bao nhieu?", "context": { "last_intent": null } }
```

Response:

```json
{ "intent": "ASK_SCHOOL_COST", "confidence": 0.91, "entities": { "school_name": "Dongguk" } }
```

Errors: provider timeout, low confidence. Permission: internal.

### `POST /llm/slot-extract`

Muc dich: Extract slot tu message.

Request:

```json
{ "text": "Tot nghiep 2023, GPA 6.5, ngan sach 280 trieu", "expected_slots": ["graduation_year", "gpa", "budget_vnd"] }
```

Response:

```json
{
  "slots": [
    { "name": "graduation_year", "value": 2023, "confidence": 0.98 },
    { "name": "gpa", "value": 6.5, "confidence": 0.97 },
    { "name": "budget_vnd", "value": 280000000, "confidence": 0.94 }
  ]
}
```

Errors: invalid JSON, low confidence. Permission: internal.

### `POST /cost/general-estimate`

Muc dich: Tinh range chi phi tong quan.

Request:

```json
{ "desired_pathway": "D4-1", "desired_region": "NEAR_SEOUL", "budget_vnd": 280000000 }
```

Response:

```json
{
  "currency": "VND",
  "estimate_type": "RANGE",
  "components": [
    { "name": "tuition", "min": 0, "max": 0 },
    { "name": "dormitory", "min": 0, "max": 0 }
  ],
  "disclaimer_required": true
}
```

Errors: missing config, unsupported pathway/region. Permission: internal.

### `POST /eligibility/check`

Muc dich: Check phu hop so bo.

Request:

```json
{
  "graduation_year": 2023,
  "gpa": 6.5,
  "korean_level": "NONE",
  "budget_vnd": 280000000,
  "desired_pathway": "UNKNOWN"
}
```

Response:

```json
{
  "lead_score": "B",
  "risk_level": "MEDIUM",
  "suggested_pathway": "D4-1",
  "missing_information": ["desired_region"],
  "next_action": "HANDOFF_RECOMMENDED"
}
```

Errors: insufficient data, rule config missing. Permission: internal.

### `POST /leads`

Muc dich: Tao lead.

Request:

```json
{ "phone": "0900000000", "facebook_psid": "PSID", "need_type": "GENERAL_COST", "lead_source": "messenger" }
```

Response:

```json
{ "lead_id": "lead_123", "status": "new" }
```

Errors: invalid phone, duplicate lead, CRM unavailable. Permission: bot service/admin.

### `PATCH /leads/{id}`

Muc dich: Cap nhat lead/profile.

Request:

```json
{ "gpa": 6.5, "lead_score": "B", "risk_level": "MEDIUM" }
```

Response:

```json
{ "lead_id": "lead_123", "updated": true }
```

Errors: lead not found, invalid field. Permission: bot service/advisor/admin.

### `POST /handoff`

Muc dich: Tao yeu cau chuyen tu van vien.

Request:

```json
{ "lead_id": "lead_123", "reason": "USER_REQUESTED", "summary": "User hoi chi phi va de lai SDT." }
```

Response:

```json
{ "handoff_id": "ho_123", "status": "assigned", "assigned_advisor_id": "adv_1" }
```

Errors: no advisor available, lead missing. Permission: bot service.

### `GET /admin/bot-config`

Muc dich: Lay cau hinh bot.

Response:

```json
{ "faq_version": 3, "cost_ranges_version": 2, "business_hours": "08:30-18:00" }
```

Errors: unauthorized. Permission: admin/owner.

### `PUT /admin/bot-config`

Muc dich: Cap nhat cau hinh bot.

Request:

```json
{ "key": "fallback_message", "value": "Chi chua hieu y em, em chon giup chi mot muc nhe." }
```

Response:

```json
{ "updated": true, "version": 4 }
```

Errors: invalid config, unauthorized. Permission: admin/owner.

### `GET /analytics/chatbot`

Muc dich: Lay metrics chatbot.

Response:

```json
{
  "total_conversations": 1200,
  "leads_created": 240,
  "chat_to_lead_conversion": 0.2,
  "top_intents": [{ "intent": "ASK_GENERAL_COST", "count": 420 }]
}
```

Errors: invalid date range, unauthorized. Permission: admin/owner/manager.

## 29. Open Questions

- Bot nen xung "em/chi" hay "ban/trung tam" theo default? Da chot: dung "trung tam/ban".
- Co bat buoc thu SDT o flow nao khong, hay luon cho tiep tuc khong SDT? Da chot: khong bat buoc thu SDT; chi can SDT khi user muon goi lai/dat lich/handoff.
- Co can flow rieng cho phu huynh ngay tu greeting khong? Deferred: MVP co the detect phu huynh qua message va doi tone.
- Cost range ban dau lay theo muc nao va ai phe duyet? Da de xuat range MVP tai `/docs/decisions/01-chatbot-mvp-open-questions.md`; business owner can phe duyet truoc go-live.
- MVP nen dung ManyChat/n8n truoc hay build native ngay? Da chot: build native.
- CRM ban dau dung database rieng, Google Sheet hay CRM hien co? Deferred: chua can quan tam trong phase nay; khi build giu abstraction `LeadService`.
- Tu van vien co cham soc ngoai gio lam viec khong? Open: can chot truoc go-live de hien dung SLA.
- Co can dat lich tu van tu dong trong MVP khong? Deferred: MVP chi can thu preferred time va handoff.
- Co can tich hop Zalo sau Facebook trong phase 2 khong? Deferred: post-MVP.
- SLA follow-up lead nong la bao lau? Open: de xuat default lead A trong 15 phut gio lam viec, lead B trong 2 gio lam viec.
- Danh sach 20-30 truong basic lookup do ai cung cap va cap nhat? Deferred: khong bat buoc cho MVP core.
- Quy dinh retention/xoa du lieu PII theo policy nao? Open: can chot truoc go-live production.

## 30. Final Delivery Requirements

Tai lieu nay duoc viet cho team dev/BA/designer trien khai MVP. Khi implement, can giu cac nguyen tac:

- Journey-first truoc school-first.
- Bot khong thay the tu van vien.
- LLM khong phai source of truth.
- Chi phi, truong, visa va hoc bong can co disclaimer/source.
- MVP tach ro phan co the mock/config voi phan phase sau.
- Moi flow quan trong can co logging, handoff va acceptance criteria.
