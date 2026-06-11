# Prompt 03: Design Conversation Flows

Bạn là Conversation Designer, Senior Product Manager và Business Analyst chuyên xây dựng chatbot AI cho giáo dục quốc tế và tư vấn du học.

## Bối Cảnh

Dự án đã có PRD module:

`/docs/prd/01-facebook-messenger-chatbot-mvp.md`

Module là **Facebook Messenger Chatbot MVP tư vấn du học Hàn Quốc**.

Nguyên tắc quan trọng:

- Chatbot phải đi theo hướng **journey-first/need-first**.
- Không hỏi user mới ngay: "Em muốn chọn trường nào?"
- Bot cần hỏi nhu cầu, hồ sơ, ngân sách, khu vực mong muốn và mục tiêu học trước.
- Chỉ sau khi hiểu nhu cầu mới gợi ý pathway, khu vực hoặc nhóm trường.
- Bot không thay thế tư vấn viên.
- Bot không tự bịa thông tin trường, học phí, visa hoặc học bổng.

## Nhiệm Vụ

Hãy thiết kế chi tiết các conversation flows cho chatbot MVP, đủ rõ để designer/dev có thể implement trên Facebook Messenger.

## Input

- PRD chính: `/docs/prd/01-facebook-messenger-chatbot-mvp.md`
- Tập trung vào section 5, 6, 7, 8, 15, 16, 23, 24, 25.

## Yêu Cầu Output

Tạo tài liệu Markdown với cấu trúc sau:

```md
# Conversation Flows - Facebook Messenger Chatbot MVP

## 1. Conversation Design Principles

## 2. Global Bot Behavior

### 2.1. Tone of Voice
### 2.2. Message Length
### 2.3. Quick Reply Rules
### 2.4. Slot Collection Rules
### 2.5. Fallback Rules
### 2.6. Disclaimer Rules
### 2.7. Handoff Rules

## 3. Global Menu

## 4. Flow A: User mới tìm hiểu

## 5. Flow B: User hỏi chi phí

## 6. Flow C: User muốn check hồ sơ

## 7. Flow D: User hỏi D4-1 vs D2

## 8. Flow E: User hỏi Seoul hay tỉnh

## 9. Flow F: User đã biết trường

## 10. Flow G: Human Handoff

## 11. Fallback & Recovery Flows

## 12. Edge Case Handling

## 13. Conversation State Map

## 14. Slot Collection Matrix

## 15. Copy Library

## 16. Implementation Notes for Dev Team
```

## Với Mỗi Flow Cần Có

Mỗi flow phải trình bày theo format:

```md
### Flow Objective

### Entry Triggers

### Required Slots

### Optional Slots

### Step-by-Step Script

| Step | User Input / Condition | Bot Message | Quick Replies / Buttons | Slots Updated | Backend Action | Next State |
|---|---|---|---|---|---|---|

### Fallback Handling

### Disclaimer Placement

### Lead Capture Moment

### Human Handoff Trigger

### Exit Conditions

### Edge Cases

### Acceptance Criteria
```

## Flow Bắt Buộc

Phải thiết kế đầy đủ:

1. Flow A: User mới tìm hiểu.
2. Flow B: User hỏi chi phí.
3. Flow C: User muốn check hồ sơ.
4. Flow D: User hỏi D4-1 vs D2.
5. Flow E: User hỏi Seoul hay tỉnh.
6. Flow F: User đã biết trường.
7. Flow G: Human handoff.

## Bot Copy Requirements

- Viết message bằng tiếng Việt.
- Tone thân thiện, dễ hiểu, giống tư vấn viên thật.
- Có thể dùng xưng hô mặc định "chị/em" nếu business chưa chốt, nhưng đánh dấu là assumption.
- Không spam nhiều tin liên tục.
- Mỗi lượt bot nên ngắn, rõ, có CTA phù hợp.
- Với nội dung phức tạp, dùng bullet/table.
- Với chi phí/visa/trường/học bổng/việc làm thêm phải có disclaimer phù hợp.

## State Transition Requirements

Mỗi flow cần chỉ rõ:

- `current_state`
- `intent`
- `missing_slots`
- `next_state`
- Khi nào lưu `UserProfile`.
- Khi nào tạo `Lead`.
- Khi nào tạo `HandoffRequest`.
- Khi nào gọi `LLM Service`.
- Khi nào gọi `Cost Estimator`.
- Khi nào gọi `Eligibility Checker`.
- Khi nào gọi `School Database`.

## Tiêu Chí Chất Lượng

- Đủ rõ để dev implement.
- Đủ tự nhiên để designer kiểm tra UX hội thoại.
- Không school-first với user mới.
- Không cam kết visa.
- Không đưa số chi phí tuyệt đối nếu không có source/config.
- Không tự bịa dữ liệu trường.
- Có fallback sau khi bot không hiểu.
- Có handoff cho case rủi ro hoặc user muốn gặp người thật.

## File Output Gợi Ý

Lưu tài liệu được tạo ra tại:

`/docs/design/01-chatbot-conversation-flows.md`
