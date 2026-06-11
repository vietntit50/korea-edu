# Prompt 01: Clarify Open Questions

Bạn là Senior Product Manager, Business Analyst và Solution Architect cho sản phẩm **Korea Study Advisor Platform**.

## Bối Cảnh

Dự án đã có PRD module đầu tiên:

`/docs/prd/01-facebook-messenger-chatbot-mvp.md`

Module cần triển khai trước là **Facebook Messenger Chatbot MVP tư vấn du học Hàn Quốc**. Chatbot phải đi theo hướng **journey-first/need-first**, không hỏi trường ngay từ đầu với user mới.

## Nhiệm Vụ

Hãy đọc kỹ PRD và tạo tài liệu chốt các câu hỏi mở với business owner trước khi team bắt đầu build MVP.

Tài liệu đầu ra phải giúp business owner, PM, BA, designer và tech lead biết:

- Câu hỏi nào còn chưa chốt.
- Vì sao câu hỏi đó quan trọng.
- Nếu chưa chốt thì ảnh hưởng gì đến scope, UX, vận hành hoặc kỹ thuật.
- Đề xuất mặc định nên chọn là gì để MVP có thể chạy nhanh.
- Ai là người cần ra quyết định.

## Input

- PRD chính: `/docs/prd/01-facebook-messenger-chatbot-mvp.md`
- Ưu tiên lấy câu hỏi từ section **29. Open Questions**.
- Ngoài section 29, hãy rà thêm toàn bộ PRD để phát hiện câu hỏi ngầm chưa được chốt.

## Yêu Cầu Output

Tạo tài liệu Markdown với cấu trúc sau:

```md
# Open Questions & Decision Log - Facebook Messenger Chatbot MVP

## 1. Mục tiêu tài liệu

## 2. Critical Decisions cần chốt trước khi build MVP

## 3. Open Questions theo nhóm

### 3.1. Business Questions

| ID | Câu hỏi | Vì sao cần chốt | Tác động nếu chưa chốt | Đề xuất mặc định | Người quyết định | Priority |
|---|---|---|---|---|---|---|

### 3.2. Product Questions

### 3.3. Operation Questions

### 3.4. Technical Questions

### 3.5. Data & Privacy Questions

### 3.6. CRM & Lead Management Questions

### 3.7. Chatbot Tone & Conversation Questions

## 4. Decision Log

| ID | Decision | Owner | Decision Date | Final Choice | Notes | Status |
|---|---|---|---|---|---|---|

## 5. Recommended Default Decisions for MVP

## 6. Risks if Decisions Are Delayed

## 7. Next Steps
```

## Quy Tắc Phân Loại

Priority dùng:

- `P0`: phải chốt trước khi build.
- `P1`: nên chốt trong sprint đầu.
- `P2`: có thể chốt trong quá trình triển khai.

Status dùng:

- `Open`
- `Proposed`
- `Decided`
- `Blocked`

## Tiêu Chí Chất Lượng

- Viết bằng tiếng Việt.
- Không viết chung chung.
- Mỗi câu hỏi phải có tác động cụ thể.
- Đề xuất mặc định phải thực tế cho MVP 30 ngày.
- Luôn nhấn mạnh nguyên tắc journey-first trước school-first.
- Luôn nhấn mạnh LLM không phải source of truth.
- Không mở rộng scope sang full CRM, full school database hoặc cost calculator chi tiết.

## File Output Gợi Ý

Lưu tài liệu được tạo ra tại:

`/docs/decisions/01-chatbot-mvp-open-questions.md`
