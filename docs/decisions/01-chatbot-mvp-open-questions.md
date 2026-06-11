# Open Questions & Decision Log - Facebook Messenger Chatbot MVP

## 1. Muc tieu

Tai lieu nay ghi lai cac quyet dinh da chot sau PRD cho module **Facebook Messenger Chatbot MVP** cua Korea Study Advisor Platform.

Trang thai cap nhat: 2026-06-11.

## 2. Critical Decisions Da Chot

| ID | Cau hoi | Quyet dinh | Tac dong den MVP | Trang thai |
|---|---|---|---|---|
| D-001 | Bot xung "chi/em" hay "trung tam/ban"? | Dung "trung tam/ban" lam default. | Bot tone trung tinh, phu hop ca hoc sinh va phu huynh; tranh mac dinh gioi tinh/tuoi cua tu van vien. | Decided |
| D-002 | Co bat buoc lay SDT khong? | Khong bat buoc lay SDT. | User co the tiep tuc flow FAQ, chi phi, check so bo ma chua de lai SDT; lead co SDT moi du de goi lai. | Decided |
| D-003 | Cost range ban dau dung so nao? | Dung range uoc tinh theo pathway va khu vuc ben duoi, cau hinh duoc trong admin/config. | Bot co the tra loi cau hoi chi phi trong MVP, nhung luon kem disclaimer va khong coi la bao gia chinh thuc. | Proposed |
| D-004 | CRM ban dau dung DB rieng, Google Sheet hay he thong co san? | Chua can quan tam trong phase nay; deferred. | MVP van thiet ke abstraction `LeadService`, nhung implementation CRM co the dung storage don gian sau khi vao build. | Deferred |
| D-005 | MVP build native hay ManyChat/n8n truoc? | Build native. | Kien truc can co webhook, bot orchestrator, database/session, LLM abstraction va Messenger API ngay tu dau. | Decided |

## 3. Cost Range De Xuat Cho MVP

### 3.1. Nguyen tac su dung

- Day la **range uoc tinh ban dau** de chatbot tu van so bo, khong phai bao gia chinh thuc.
- Range nen duoc luu trong `CostRangeTemplate` va admin/config co the sua.
- Range can duoc review lai moi thang hoac khi ty gia/chinh sach truong thay doi.
- Bot phai noi ro chi phi phu thuoc truong, ky nhap hoc, ty gia, KTX, sinh hoat, hoc bong va phi dich vu neu co.
- Truoc khi gui phuong an/bang phi chinh thuc, tu van vien phai xac nhan lai.

### 3.2. Gia dinh nghia range

Range ben duoi nen duoc hieu la **ngan sach chuan bi tham khao cho nam dau/12 thang dau**, gom cac nhom chi phi chinh:

- Hoc phi.
- Phi nhap hoc/ho so co ban neu co.
- KTX/nha o.
- Sinh hoat phi.
- Visa/KVAC/ARC/bao hiem.
- Ve may bay.
- Du phong ty gia va phat sinh.

Range **chua co dinh phi dich vu trung tam**, vi business owner chua chot chinh sach phi.

### 3.3. Range VND de xuat

| Pathway | Khu vuc | Range de xuat | Ghi chu |
|---|---|---:|---|
| D4-1 hoc tieng | Tinh tiet kiem | 180-240 trieu VND | Phu hop user uu tien toi uu chi phi; can check truong va KTX cu the. |
| D4-1 hoc tieng | Thanh pho lon ngoai Seoul | 220-300 trieu VND | Vi du Busan/Daegu/Daejeon; chi phi sinh hoat cao hon tinh nho. |
| D4-1 hoc tieng | Gan Seoul/Gyeonggi | 240-340 trieu VND | Can bang giua chi phi va kha nang tiep can Seoul. |
| D4-1 hoc tieng | Seoul/top school | 300-450 trieu VND | Range cao do hoc phi/KTX/sinh hoat tai Seoul. |
| D2 chuyen nganh | Tinh tiet kiem | 230-320 trieu VND | Tuy bac hoc, nganh, truong cong/tu. |
| D2 chuyen nganh | Thanh pho lon ngoai Seoul | 280-380 trieu VND | Can check hoc phi theo nganh. |
| D2 chuyen nganh | Gan Seoul/Gyeonggi | 320-450 trieu VND | Phu hop ngan sach trung binh-kha. |
| D2 chuyen nganh | Seoul/top school | 400-600 trieu VND | Dung de canh bao ngan sach cao, khong phai bao gia. |
| Chua ro pathway | Chua ro khu vuc | 220-450 trieu VND | Bot nen hoi them D4-1/D2 va khu vuc thay vi ket luan. |

### 3.4. Co so tham chieu

- Study in Korea, cong thong tin cua chinh phu Han Quoc, cong bo hoc phi trung binh chuong trinh degree: trung binh toan quoc khoang KRW 6.82M/nam, khu vuc thu do khoang KRW 7.65M/nam, ngoai thu do khoang KRW 6.32M/nam.
- Mot so chuong trinh tieng Han dai hoc dang cong khai hoc phi 10 tuan trong khoang KRW 1.3M-1.8M/ky, vi du Kyungpook National University va Korea University Korean Language Center.
- Ty gia tham chieu ngay 2026-06-11 vao khoang 1 KRW = 17.2 VND theo cac bang ty gia cong khai. Khi tinh bao gia thuc te can lay ty gia moi nhat.

Nguon:

- https://www.studyinkorea.go.kr/ko/plan/abroadExpenses.do
- https://en.knu.ac.kr/admission/korean01.htm
- https://klceng.korea.ac.kr/klceng/course/regular_intro.do
- https://www.forbes.com/advisor/money-transfer/currency-converter/krw-vnd/

## 4. Cac Cau Hoi Con Deferred

| ID | Cau hoi | De xuat xu ly |
|---|---|---|
| O-001 | Co can flow rieng cho phu huynh ngay tu greeting khong? | Chua bat buoc trong MVP; bot co the detect phu huynh qua message va doi tone. |
| O-002 | Tu van vien co cham soc ngoai gio lam viec khong? | Can chot truoc khi go-live de hien dung SLA. Default: ngoai gio ghi nhan va phan hoi vao gio lam viec tiep theo. |
| O-003 | Co can dat lich tu van tu dong trong MVP khong? | De `Could have`; MVP chi can thu preferred time va handoff. |
| O-004 | Co can tich hop Zalo sau Facebook khong? | Post-MVP. Khong anh huong build native Messenger neu tach channel adapter tot. |
| O-005 | SLA follow-up lead nong la bao lau? | De xuat default: lead A trong 15 phut gio lam viec, lead B trong 2 gio lam viec. |
| O-006 | Danh sach 20-30 truong basic lookup do ai cung cap? | Deferred den khi lam `Should have`; MVP khong phu thuoc school lookup day du. |
| O-007 | Quy dinh retention/xoa du lieu PII theo policy nao? | Can chot truoc go-live production; default tam thoi 12 thang cho conversation raw, theo policy CRM cho lead. |

## 5. Decision Log

| ID | Decision | Owner | Decision Date | Final Choice | Notes | Status |
|---|---|---|---|---|---|---|
| D-001 | Bot tone | Business owner | 2026-06-11 | "trung tam/ban" | Ap dung cho bot copy mac dinh. | Decided |
| D-002 | SDT required | Business owner | 2026-06-11 | Khong bat buoc | Lead co the partial theo PSID; SDT chi can cho goi lai/handoff. | Decided |
| D-003 | Cost range | Product/Business owner | 2026-06-11 | Dung range de xuat tam thoi | Can phe duyet lan cuoi truoc go-live. | Proposed |
| D-004 | CRM | Business/Tech | 2026-06-11 | Deferred | Thiet ke abstraction, chua chot implementation. | Deferred |
| D-005 | Build approach | Business/Tech | 2026-06-11 | Native | Khong dung ManyChat/n8n cho MVP. | Decided |

## 6. Next Steps

1. Cap nhat conversation flows sang tone "trung tam/ban".
2. Dua cost range vao business rules va `CostRangeTemplate` MVP.
3. Giu lead capture optional, chi bat buoc SDT khi user muon tu van vien goi lai/dat lich.
4. Thiet ke native architecture theo webhook + bot orchestrator + service abstraction.
5. Truoc go-live, business owner phe duyet lai cost range va SLA handoff.
