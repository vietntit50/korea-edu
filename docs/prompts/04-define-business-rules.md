# Prompt 04: Define Business Rules

Bạn là Senior Business Analyst, Product Manager và Solution Architect cho hệ thống chatbot tư vấn du học Hàn Quốc.

## Bối Cảnh

Dự án đã có PRD:

`/docs/prd/01-facebook-messenger-chatbot-mvp.md`

Module cần triển khai là **Facebook Messenger Chatbot MVP**. Bot có nhiệm vụ tư vấn cấp 1, lọc lead, trả lời FAQ, check hồ sơ sơ bộ, uớc tính chi phí tổng quan và chuyển tư vấn viên khi cần.

## Nhiệm Vụ

Hãy định nghĩa bộ business rules MVP để team dev có thể implement rule engine/config ban đầu.

Không cần viết code. Hãy mô tả rule theo format rõ ràng, có input/output, priority, ví dụ và điểm cần business owner xác nhận.

## Input

- PRD chính: `/docs/prd/01-facebook-messenger-chatbot-mvp.md`
- Tập trung vào section 11, 12, 13, 14, 16, 17, 23, 24.

## Yêu Cầu Output

Tạo tài liệu Markdown với cấu trúc sau:

```md
# Business Rules - Facebook Messenger Chatbot MVP

## 1. Rule Design Principles

## 2. Rule Categories

## 3. Lead Scoring Rules

## 4. Eligibility Risk Rules

## 5. General Cost Estimator Rules

## 6. Human Handoff Rules

## 7. Lead Creation Rules

## 8. Disclaimer Rules

## 9. Unsafe & High-Risk Guardrails

## 10. Admin Configuration Rules

## 11. Rule Precedence

## 12. Example Rule Evaluations

## 13. Rules Requiring Business Owner Confirmation

## 14. MVP Configuration Checklist
```

## Rule Format

Mỗi rule nên trình bày theo format:

```md
### Rule ID: RULE_NAME

- Category:
- Priority:
- Description:
- Input:
- Condition:
- Output:
- Bot behavior:
- Backend action:
- Handoff required:
- Disclaimer required:
- Example:
- Business owner confirmation needed:
```

## Rules Bắt Buộc

### Lead scoring A/B/C/D

Định nghĩa tiêu chí so bộ cho:

- `A`: hồ sơ rõ, ngân sách phù hợp, nhu cầu rõ, có thể tư vấn sâu ngay.
- `B`: có tiềm năng, cần bổ sung tiếng Hàn/tài chính/thông tin.
- `C`: rủi ro, cần tư vấn viên đánh giá.
- `D`: chưa phù hợp hoặc thiếu thông tin, nuôi dưỡng bằng content.

### Eligibility risk LOW/MEDIUM/HIGH

Input cần xét:

- GPA.
- Năm tốt nghiệp.
- Gap year.
- TOPIK/tieng Hàn.
- Ngân sách.
- Mục tiêu học.
- Khu vực mong muốn.
- Lịch sử visa nếu có.

### Cost estimator rules

Rule theo:

- Pathway: D4-1, D2, chưa rõ.
- Region: tỉnh tiết kiệm, gần Seoul/Gyeonggi, thành phố lớn, Seoul/top school.
- Budget group.
- Dormitory/living cost nếu có.

Output phải là range, không phải số cố định.

### Handoff trigger

Bao gồm:

- User để lại SĐT.
- User hỏi phí chính thức.
- User hỏi visa rủi ro.
- User hỏi hồ sơ phức tạp.
- User muốn nộp hồ sơ.
- Bot không hiểu sau 2 lần.
- User yêu cầu gặp người thật.

### Disclaimer rules

Bắt buộc cho:

- Chi phí.
- Visa.
- Điều kiện trường.
- Học bổng.
- Việc làm thêm.
- Trường/ngành cụ thể.

### Unsafe/high-risk guardrails

Bao gồm:

- Không cam kết đậu visa.
- Không khuyến khích khai sai hồ sơ.
- Không khuyến khích làm thêm trái quy định.
- Không để LLM tự tạo số liệu.
- Không tư vấn trường/ngành nếu dữ liệu chưa verified.

## Rule Precedence

Hãy định nghĩa thứ tự ưu tiên khi nhiều rule cùng match. Ví dụ:

1. Unsafe/high-risk guardrail.
2. Human handoff required.
3. Lead capture.
4. Eligibility check.
5. Cost estimator.
6. School lookup.
7. FAQ/general answer.
8. Fallback.

## Tiêu Chí Chất Lượng

- Viết bằng tiếng Việt.
- Business rules phải đủ rõ để dev implement.
- Có ví dụ input/output.
- Đánh dấu assumption nếu chưa có số liệu chính thức.
- Không tự đưa ra range chi phí cụ thể nếu PRD chưa cung cấp số; hãy tạo placeholder/config key để business owner điền.
- Luôn nhấn mạnh LLM không phải source of truth.
- Luôn dùng "phù hợp sơ bộ", "cần xác nhận", không dùng "chắc chắn".

## File Output Gợi Ý

Lưu tài liệu được tạo ra tại:

`/docs/business-rules/01-chatbot-mvp-business-rules.md`
