# Prompt 06: Plan MVP Build Slices

Bạn là Delivery Lead, Tech Lead và Senior Product Manager cho sản phẩm **Korea Study Advisor Platform**.

## Bối Cảnh

Dự án đã có PRD:

`/docs/prd/01-facebook-messenger-chatbot-mvp.md`

Module cần triển khai là **Facebook Messenger Chatbot MVP tư vấn du học Hàn Quốc**.

Mục tiêu là build MVP theo từng lát cắt nhỏ, có thể demo, test và release dần, thay vì build toàn bộ một lần.

## Nhiệm Vụ

Hãy lập kế hoạch build MVP theo slices. Mỗi slice phải tạo được giá trị cụ thể, có scope rõ, acceptance criteria, test cases và demo scenario.

## Input

- PRD chính: `/docs/prd/01-facebook-messenger-chatbot-mvp.md`
- Nếu có backlog, technical spec, business rules hoặc conversation flows, dùng làm context bổ sung.

## Yêu Cầu Output

Tạo tài liệu Markdown với cấu trúc sau:

```md
# MVP Build Slices Plan - Facebook Messenger Chatbot MVP

## 1. Build Strategy

## 2. MVP Release Goal

## 3. Assumptions

## 4. Recommended Build Order

## 5. Slice 1: Messenger Webhook + Greeting/Menu

## 6. Slice 2: Intent Routing + FAQ

## 7. Slice 3: Slot Filling + Profile Store

## 8. Slice 4: General Cost Estimator

## 9. Slice 5: Eligibility Check

## 10. Slice 6: Lead Capture + Human Handoff

## 11. Slice 7: Admin Config + Basic Analytics

## 12. Cross-Slice Dependencies

## 13. Technical Risks

## 14. Product/Operation Risks

## 15. Testing Plan by Slice

## 16. Demo Plan by Slice

## 17. Definition of Done

## 18. 30-Day Timeline Recommendation
```

## Slice Format

Với mỗi slice, trình bày theo format:

```md
### Objective

### User Value

### Scope

### Out of Scope

### Functional Requirements

### Technical Tasks

| Task ID | Task | Owner Role | Estimate | Dependencies |
|---|---|---|---|---|

### APIs Involved

### Database Changes

### Admin/Config Changes

### Acceptance Criteria

Given/When/Then...

### Test Cases

| Test ID | Scenario | Steps | Expected Result |
|---|---|---|---|

### Demo Scenario

### Risks

### Done When
```

## Slices Bắt Buộc

### Slice 1: Messenger webhook + greeting/menu

Mục tiêu: nhận/gửi message cơ bản, hiển thị menu journey-first.

### Slice 2: intent routing + FAQ

Mục tiêu: route menu/quick reply/rule-based FAQ, fallback cơ bản, bắt đầu LLM classification nếu cần.

### Slice 3: slot filling + profile store

Mục tiêu: extract/lưu profile tạm thời, hỏi slot thiếu, lưu partial lead theo PSID.

### Slice 4: general cost estimator

Mục tiêu: trả range chi phí theo pathway/khu vực/config, có disclaimer.

### Slice 5: eligibility check

Mục tiêu: check hồ sơ sơ bộ, lead score A/B/C/D, risk LOW/MEDIUM/HIGH.

### Slice 6: lead capture + human handoff

Mục tiêu: tạo lead, update CRM/database, tạo handoff request, gửi notification.

### Slice 7: admin config + analytics cơ bản

Mục tiêu: cấu hình FAQ/cost range/disclaimer/rule cơ bản và xem metrics MVP.

## Planning Requirements

- Đưa ra thứ tự build khuyến nghị.
- Chỉ rõ slice nào có thể demo độc lập.
- Chỉ rõ dependency giữa slices.
- Chỉ rõ rủi ro kỹ thuật và cách giảm rủi ro.
- Có timeline 30 ngày nếu team nhỏ.
- Không mở rộng sang full CRM, full school database, OCR, payment, mobile app.

## Definition of Done Bắt Buộc

Mỗi slice chỉ được coi là done khi:

- Có acceptance criteria đạt.
- Có test cases pass.
- Có logging tối thiểu.
- Có fallback/error handling.
- Không vi phạm guardrails.
- Có demo scenario chạy được.
- Có tài liệu cập nhật nếu API/schema/rule thay đổi.

## Tiêu Chí Chất Lượng

- Viết bằng tiếng Việt.
- Kế hoạch thực tế cho MVP 30 ngày.
- Ưu tiên lát cắt tạo giá trị sớm.
- Không build school database trước các năng lực cốt lõi.
- Luôn giữ journey-first.
- Luôn giữ LLM là hỗ trợ, không phải source of truth.

## File Output Gợi Ý

Lưu tài liệu được tạo ra tại:

`/docs/plan/01-chatbot-mvp-build-slices.md`
