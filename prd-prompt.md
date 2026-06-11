Bạn là Senior Product Manager, Business Analyst và Solution Architect chuyên xây dựng chatbot AI cho lĩnh vực giáo dục quốc tế, tư vấn du học và CRM lead generation.

Nhiệm vụ của bạn: tạo tài liệu PRD chi tiết cho module đầu tiên của hệ thống “Korea Study Advisor Platform”.

Module cần xây trước:

Facebook Messenger Chatbot tư vấn du học Hàn Quốc.

Mục tiêu:
Xây dựng chatbot cho fanpage Facebook tư vấn du học Hàn Quốc. Chatbot đóng vai trò tư vấn viên cấp 1, giúp học sinh/phụ huynh mới tìm hiểu có thể:

* Hiểu quy trình du học Hàn Quốc.
* Biết tổng chi phí ước tính.
* Check sơ bộ hồ sơ có phù hợp không.
* Biết nên chọn D4-1 hay D2.
* Biết nên chọn Seoul, gần Seoul hay tỉnh.
* Để lại thông tin liên hệ.
* Được chuyển cho tư vấn viên khi cần.
* Sau này có thể gợi ý nhóm trường/trường cụ thể dựa trên hồ sơ, ngân sách và khu vực mong muốn.

Nguyên tắc sản phẩm quan trọng:

* Không thiết kế chatbot theo hướng school-first.
* Học sinh mới thường chưa biết trường nào, chưa hiểu D4-1/D2, chưa biết tổng chi phí, chưa biết quy trình.
* Chatbot phải đi theo hướng journey-first/need-first.
* Luồng tư vấn mặc định không được hỏi ngay “em muốn chọn trường nào?”.
* Chatbot phải hỏi nhu cầu, hồ sơ, ngân sách, khu vực mong muốn và mục tiêu học trước.
* Sau khi hiểu nhu cầu mới gợi ý pathway, khu vực, nhóm trường.
* School database và cost calculator chi tiết là bước sau, không phải bước đầu tiên.
* Chatbot không thay thế tư vấn viên hoàn toàn.
* Chatbot chỉ lọc lead, trả lời FAQ, thu thập thông tin, tư vấn sơ bộ và chuyển tư vấn viên khi cần.

Phạm vi PRD:
Chỉ tạo PRD cho module chatbot MVP trước. Không cần viết toàn bộ PRD cho CRM, advisor console, school database, cost calculator đầy đủ. Tuy nhiên, trong PRD chatbot cần mô tả các integration point với các module sau này:

* CRM Lead Management.
* School Database.
* General Cost Estimator.
* School Cost Calculator.
* Eligibility Check.
* AI Advisor Assistant.
* Human Handoff.
* Notification.

Yêu cầu đầu ra:
Tạo file Markdown:

/docs/prd/01-facebook-messenger-chatbot-mvp.md

Nếu thư mục chưa tồn tại, hãy tạo mới.

Tài liệu PRD phải viết bằng tiếng Việt, rõ ràng, có thể đưa ngay cho team dev/BA/designer triển khai.

Cấu trúc PRD cần có:

# 1. Product Overview

Bao gồm:

* Tên module.
* Mục tiêu module.
* Vấn đề cần giải quyết.
* Người dùng chính:

  * Học sinh mới tìm hiểu.
  * Phụ huynh.
  * Học sinh đã có nhu cầu rõ.
  * Học sinh đã biết trường cụ thể.
  * Tư vấn viên nội bộ.
  * Admin/owner trung tâm.
* Giá trị kinh doanh:

  * Tăng số lead từ fanpage.
  * Tự động trả lời 24/7.
  * Giảm tải tư vấn viên.
  * Chuẩn hóa thông tin tư vấn.
  * Lọc lead nóng/lạnh.
  * Tăng tỷ lệ đặt lịch tư vấn.
* MVP scope.
* Out of scope.

# 2. User Segmentation

Phân loại người dùng chatbot thành ít nhất 4 nhóm:

1. New Explorer — mới tìm hiểu

* Chưa biết quy trình.
* Chưa biết D4-1/D2.
* Chưa biết chi phí.
* Chưa biết trường/khu vực.

2. Cost Seeker — quan tâm chi phí

* Câu hỏi chính: đi Hàn hết bao nhiêu tiền?
* Muốn biết ngân sách tối thiểu.
* Phụ huynh thường thuộc nhóm này.

3. Eligibility Checker — muốn check hồ sơ

* Có GPA, năm tốt nghiệp, ngân sách, tiếng Hàn.
* Muốn biết có đi được không.
* Muốn biết rủi ro hồ sơ.

4. School-aware User — đã biết trường/ngành

* Hỏi trường cụ thể.
* Hỏi ngành cụ thể.
* Hỏi học phí/ký túc xá/kỳ nhập học của trường.

Với mỗi nhóm, mô tả:

* Nhu cầu.
* Câu hỏi thường gặp.
* Thông tin cần thu thập.
* Response strategy.
* Khi nào chuyển tư vấn viên.

# 3. Core Chatbot Positioning

Làm rõ chatbot là:

* Tư vấn viên cấp 1.
* Lead qualification bot.
* FAQ assistant.
* Eligibility pre-check assistant.
* General cost estimator.
* Human handoff router.

Chatbot không phải là:

* Người ra quyết định visa.
* Người cam kết đậu visa.
* Nguồn dữ liệu học phí chính thức.
* Công cụ thay thế hoàn toàn tư vấn viên.
* Công cụ tự bịa thông tin trường/ngành/chi phí.

# 4. Main Conversation Entry Points

Chatbot cần hỗ trợ các entry point sau:

1. Người dùng nhắn “tư vấn du học Hàn”.
2. Người dùng comment/inbox từ bài viết fanpage.
3. Người dùng bấm CTA “Check khả năng đi du học Hàn miễn phí”.
4. Người dùng hỏi “đi Hàn hết bao nhiêu tiền?”.
5. Người dùng hỏi “em có đi được không?”.
6. Người dùng hỏi “quy trình đi Hàn như thế nào?”.
7. Người dùng hỏi “nên chọn Seoul hay tỉnh?”.
8. Người dùng hỏi “D4-1 và D2 khác nhau thế nào?”.
9. Người dùng hỏi trường cụ thể.
10. Người dùng muốn gặp tư vấn viên.

# 5. Default Journey-first Flow

Thiết kế flow mặc định cho user mới:

Step 1: Greeting
Bot chào và hỏi người dùng đang muốn tìm hiểu nội dung nào.

Menu gợi ý:

1. Quy trình du học Hàn
2. Tổng chi phí cần chuẩn bị
3. Check hồ sơ có đi được không
4. Nên chọn Seoul hay tỉnh
5. D4-1 và D2 khác nhau thế nào
6. Tìm trường theo ngân sách/khu vực
7. Gặp tư vấn viên

Step 2: Need Classification
Bot xác định nhu cầu chính:

* PROCESS_INFO
* GENERAL_COST
* ELIGIBILITY_CHECK
* REGION_ADVICE
* PATHWAY_ADVICE
* SCHOOL_SEARCH
* HUMAN_CONSULTATION

Step 3: Basic Profile Collection
Bot thu thập thông tin tối thiểu:

* Tên.
* Số điện thoại.
* Năm sinh.
* Tỉnh/thành.
* Đã tốt nghiệp THPT/ĐH chưa.
* Năm tốt nghiệp.
* GPA/học lực.
* Đã học tiếng Hàn chưa.
* TOPIK nếu có.
* Ngân sách gia đình dự kiến.
* Muốn đi kỳ/năm nào.
* Mục tiêu: học tiếng, đại học, cao đẳng/nghề, chưa rõ.
* Khu vực mong muốn: Seoul, gần Seoul, tỉnh chi phí thấp, chưa rõ.
* Ngành quan tâm nếu có.
* Có người thân ở Hàn không.
* Đã từng trượt visa nước nào chưa nếu phù hợp.

Step 4: Initial Assessment
Bot phân loại sơ bộ:

* Lead score A/B/C/D.
* Mức độ sẵn sàng.
* Rủi ro hồ sơ.
* Nhóm chi phí phù hợp.
* Pathway đề xuất.

Step 5: Recommendation
Bot đưa ra gợi ý:

* Lộ trình phù hợp.
* Khoảng chi phí ước tính.
* Khu vực nên cân nhắc.
* Bước tiếp theo.
* Đề xuất gặp tư vấn viên nếu lead nóng.

Step 6: Lead Capture + Handoff
Nếu người dùng để lại SĐT hoặc có nhu cầu rõ:

* Tạo lead.
* Gắn tag nhu cầu.
* Gắn lead score.
* Chuyển tư vấn viên.
* Gửi thông báo nội bộ.

# 6. Supported Intents

PRD cần mô tả chi tiết các intent sau:

* GREETING
* MENU_SELECTION
* ASK_PROCESS
* ASK_GENERAL_COST
* CHECK_ELIGIBILITY
* ASK_REGION_ADVICE
* ASK_D4_D2
* ASK_TOPIK
* ASK_GPA
* ASK_GAP_YEAR
* ASK_BUDGET
* ASK_PART_TIME_JOB
* ASK_VISA_GENERAL
* ASK_DOCUMENTS
* ASK_TIMELINE
* ASK_PARENT_CONCERN
* ASK_SCHOOL_RECOMMENDATION
* ASK_SCHOOL_INFO
* ASK_SCHOOL_COST
* ASK_MAJOR
* ASK_DORMITORY
* ASK_SCHOLARSHIP
* LEAD_CAPTURE
* BOOK_CONSULTATION
* HUMAN_HANDOFF
* UNKNOWN
* UNSAFE_OR_HIGH_RISK

Với mỗi intent, ghi:

* Description.
* Example user messages.
* Required slots.
* Optional slots.
* Response behavior.
* Backend actions.
* Handoff rules.
* Acceptance criteria.

# 7. Slot Filling / Information Collection

Thiết kế cơ chế hỏi thông tin theo từng intent.

Ví dụ:

Intent CHECK_ELIGIBILITY cần slots:

* education_level
* graduation_year
* gpa
* korean_level
* topik_level
* budget
* desired_intake
* desired_pathway
* desired_region

Intent ASK_GENERAL_COST cần slots:

* desired_pathway
* desired_region
* budget
* dormitory_preference nếu có

Intent ASK_SCHOOL_COST cần slots:

* school_name
* program_type
* intake_term nếu có

Quy tắc:

* Không hỏi quá nhiều câu cùng lúc.
* Ưu tiên hỏi 2–3 câu quan trọng trước.
* Cho phép user trả lời tự nhiên.
* Lưu dần thông tin vào student profile tạm thời.
* Nếu user bỏ dở, vẫn lưu partial lead nếu có SĐT/Facebook ID.
* Có thể dùng quick reply buttons để giảm friction.

# 8. Conversation Flow Details

Tạo flow chi tiết cho các trường hợp sau:

## Flow A: User mới tìm hiểu

Input: “Em muốn tìm hiểu du học Hàn”
Bot:

* Chào.
* Hiển thị menu.
* Giải thích ngắn.
* Hỏi nhu cầu chính.
* Thu thập thông tin cơ bản.
* Gợi ý bước tiếp theo.

## Flow B: User hỏi chi phí

Input: “Đi Hàn hết bao nhiêu tiền?”
Bot:

* Giải thích chi phí phụ thuộc vào D4-1/D2, khu vực, trường, KTX, sinh hoạt.
* Cho range sơ bộ:

  * Tỉnh chi phí thấp.
  * Gần Seoul.
  * Seoul/top school.
* Hỏi ngân sách dự kiến.
* Hỏi muốn học tiếng hay chuyên ngành.
* Nếu đủ thông tin, gợi ý hướng phù hợp.
* Nếu lead nóng, chuyển tư vấn viên.

## Flow C: User muốn check hồ sơ

Input: “GPA 6.5, chưa TOPIK, có đi được không?”
Bot:

* Thu thập năm tốt nghiệp, ngân sách, mục tiêu, khu vực.
* Chấm sơ bộ.
* Cảnh báo rủi ro nếu có.
* Gợi ý pathway.
* Không cam kết đậu visa.

## Flow D: User hỏi D4-1 vs D2

Bot:

* Giải thích dễ hiểu.
* So sánh bằng bảng.
* Hỏi mục tiêu của user.
* Gợi ý route phù hợp.

## Flow E: User hỏi Seoul hay tỉnh

Bot:

* So sánh ưu/nhược điểm.
* So sánh chi phí.
* Hỏi ngân sách.
* Gợi ý khu vực phù hợp.

## Flow F: User đã biết trường

Input: “Trường Dongguk hết bao nhiêu?”
Bot:

* Nhận diện trường.
* Hỏi D4-1 hay D2 nếu thiếu.
* Gọi school database nếu có.
* Gọi cost calculator nếu dữ liệu verified.
* Nếu chưa có dữ liệu, báo cần tư vấn viên xác nhận.
* Tạo lead nếu user muốn nhận báo giá chi tiết.

## Flow G: Human handoff

Kích hoạt khi:

* User để lại SĐT.
* User hỏi phí chính thức.
* User hỏi visa case rủi ro.
* User hỏi hồ sơ phức tạp.
* User muốn nộp hồ sơ.
* Bot không hiểu sau 2 lần.
* User yêu cầu gặp người thật.

# 9. AI/LLM Usage Design

Mô tả rõ khi nào gọi LLM:

LLM được dùng để:

* Hiểu câu hỏi tự nhiên.
* Phân loại intent.
* Extract slots.
* Viết câu trả lời thân thiện.
* Tóm tắt hội thoại cho tư vấn viên.
* Gợi ý câu hỏi tiếp theo.
* Giải thích thông tin dựa trên knowledge base.

LLM không được dùng để:

* Tự bịa học phí.
* Tự bịa trường/ngành.
* Tự tính tổng chi phí nếu không có dữ liệu.
* Cam kết đậu visa.
* Đưa lời khuyên pháp lý tuyệt đối.
* Trả lời thông tin chưa có source.

Nguyên tắc:

* LLM không phải source of truth.
* Structured database verified mới là source of truth.
* Cost calculator backend mới là nơi tính tiền.
* Với dữ liệu chưa verified, bot phải nói “cần tư vấn viên xác nhận”.

Thiết kế prompt nội bộ mẫu:

* Intent classification prompt.
* Slot extraction prompt.
* Response generation prompt.
* Guardrail prompt.

# 10. Rule-based vs LLM Routing

Thiết kế router:

Rule-based dùng cho:

* Greeting.
* Menu.
* Quick replies.
* Lead capture.
* Book consultation.
* Disclaimer.
* FAQ cố định.
* Handoff.

LLM dùng cho:

* Câu hỏi tự nhiên.
* User gõ sai chính tả.
* Câu hỏi nhiều ý.
* Extract tên trường/ngành/khu vực/ngân sách.
* Tư vấn mềm theo profile.

Database/cost service dùng cho:

* Thông tin trường.
* Ngành.
* Học phí.
* KTX.
* Tổng chi phí theo trường.
* So sánh trường.

# 11. General Cost Estimator MVP

Vì user mới chưa biết trường, chatbot cần có general cost estimator.

Output chi phí phải là range, không phải số cố định.

Ví dụ nhóm chi phí:

* Nhóm tỉnh tiết kiệm.
* Nhóm gần Seoul/Gyeonggi.
* Nhóm thành phố lớn như Busan/Daegu/Daejeon.
* Nhóm Seoul/top school.

Chi phí cần chia thành:

* Học phí.
* Phí hồ sơ/trung tâm.
* KTX.
* Sinh hoạt phí.
* Visa/KVAC.
* Vé máy bay.
* Bảo hiểm.
* Dự phòng.

Quy tắc:

* Bot phải ghi rõ “ước tính”.
* Bot phải ghi rõ “tùy trường, kỳ nhập học, tỷ giá, KTX, học bổng”.
* Bot phải gợi ý tư vấn viên xác nhận trước khi báo giá chính thức.

# 12. Eligibility Check MVP

Thiết kế rule sơ bộ để phân loại lead:

Input:

* GPA.
* Năm tốt nghiệp.
* Gap year.
* TOPIK/tiếng Hàn.
* Ngân sách.
* Mục tiêu học.
* Khu vực mong muốn.
* Lịch sử visa nếu có.

Output:

* Lead score A/B/C/D.
* Risk level: LOW/MEDIUM/HIGH.
* Suggested pathway.
* Suggested region.
* Missing information.
* Next action.

Ví dụ:

* A: Hồ sơ rõ, ngân sách phù hợp, có khả năng tư vấn sâu ngay.
* B: Có tiềm năng, cần bổ sung tiếng Hàn/tài chính.
* C: Rủi ro, cần tư vấn viên đánh giá.
* D: Chưa phù hợp, nuôi dưỡng bằng content.

Không đưa kết luận kiểu:

* “Chắc chắn đi được”
* “Chắc chắn đậu visa”
* “Không thể đi”

Chỉ dùng:

* “Khả năng phù hợp sơ bộ”
* “Cần tư vấn viên xác nhận”
* “Có một số rủi ro cần kiểm tra thêm”

# 13. Human Handoff Requirements

Mô tả:

* Trigger handoff.
* Thông tin cần gửi cho tư vấn viên.
* Tóm tắt hội thoại.
* Lead score.
* Nhu cầu chính.
* Câu hỏi user đang hỏi.
* SLA mong muốn.
* Trạng thái conversation sau handoff.

Thông tin handoff gửi cho tư vấn viên:

* Tên.
* SĐT.
* Facebook profile/PSID nếu có.
* Nhu cầu.
* GPA.
* Năm tốt nghiệp.
* Ngân sách.
* Khu vực mong muốn.
* Mục tiêu học.
* Câu hỏi cuối cùng.
* Bot summary.
* Lead score.
* Risk warning.

# 14. Lead Creation Requirements

Bot cần tạo lead khi:

* User để lại SĐT.
* User bấm đặt lịch tư vấn.
* User hoàn thành check eligibility.
* User hỏi chi phí cụ thể và có nhu cầu rõ.
* User được phân loại lead A/B.
* User yêu cầu tư vấn viên gọi lại.

Lead fields:

* full_name
* phone
* facebook_psid
* facebook_name nếu có
* province
* birth_year
* education_level
* graduation_year
* gpa
* korean_level
* topik_level
* budget_vnd
* desired_pathway
* desired_region
* desired_major
* desired_intake
* lead_source
* lead_score
* risk_level
* status
* assigned_advisor_id
* conversation_summary
* created_at
* updated_at

# 15. Chatbot Response Tone

Tone:

* Thân thiện.
* Dễ hiểu.
* Giống tư vấn viên thật.
* Không quá học thuật.
* Không gây áp lực chốt sale.
* Không hứa hẹn quá mức.
* Phù hợp cả học sinh và phụ huynh.

Response rule:

* Câu trả lời nên ngắn trước, hỏi tiếp sau.
* Với nội dung phức tạp, dùng bullet/table.
* Luôn có CTA phù hợp:

  * “Em muốn chị check hồ sơ sơ bộ không?”
  * “Em cho chị xin ngân sách dự kiến để gợi ý khu vực phù hợp nhé?”
  * “Em muốn tư vấn viên gọi lại không?”
* Không spam nhiều tin liên tục.

# 16. Required Disclaimers

Bắt buộc dùng disclaimer trong các case:

* Chi phí.
* Visa.
* Điều kiện trường.
* Học bổng.
* Việc làm thêm.
* Trường/ngành cụ thể.

Disclaimer mẫu:
“Lưu ý: Thông tin trên là ước tính ban đầu và có thể thay đổi theo kỳ nhập học, tỷ giá, chính sách từng trường và hồ sơ cá nhân. Tư vấn viên sẽ kiểm tra lại nguồn chính thức trước khi gửi phương án/báo giá cuối cùng.”

Visa disclaimer:
“Chatbot không thể cam kết kết quả visa. Kết quả phụ thuộc vào hồ sơ, tài chính, học lực, phỏng vấn và quyết định của cơ quan xét duyệt.”

# 17. Admin Configuration MVP

Admin cần cấu hình được:

* FAQ.
* Menu bot.
* Quick replies.
* General cost ranges.
* Disclaimer text.
* Lead scoring rules.
* Handoff triggers.
* Business hours.
* Advisor assignment rule.
* Blocklist keywords nếu cần.
* Fallback message.

# 18. Integration Points

Mô tả integration cần có, dù MVP có thể mock/simple:

1. Facebook Messenger Webhook

* Nhận message.
* Gửi response.
* Xử lý quick reply/button.

2. CRM Lead API

* Create lead.
* Update lead.
* Store conversation summary.

3. LLM Service

* Classify intent.
* Extract slots.
* Generate response.

4. Cost Estimator Service

* Estimate general cost by region/pathway.
* Later integrate school-level cost calculator.

5. School Database Service

* MVP có thể chưa full.
* Chỉ dùng khi user hỏi trường cụ thể.
* Nếu chưa có dữ liệu verified, bot báo tư vấn viên xác nhận.

6. Notification Service

* Notify advisor when handoff.
* Notify manager for hot lead.

# 19. Non-functional Requirements

Cần mô tả:

* Response time.
* Availability.
* Logging.
* Monitoring.
* Rate limiting.
* LLM cost control.
* Data privacy.
* PII protection.
* Conversation storage.
* Error handling.
* Fallback behavior.
* Auditability.
* Data retention.
* Security.

# 20. Analytics Metrics

MVP cần đo:

* Tổng số conversation.
* Số lead được tạo.
* Conversion chat → lead.
* Conversion lead → handoff.
* Top intents.
* Top câu hỏi.
* Tỷ lệ bot không hiểu.
* Tỷ lệ user bỏ dở.
* Số user check eligibility.
* Số user hỏi chi phí.
* Số user hỏi trường cụ thể.
* Thời gian phản hồi của tư vấn viên.
* Lead score distribution.

# 21. MVP Scope

MVP 30 ngày cần có:

Must have:

* Facebook Messenger webhook.
* Greeting/menu.
* Journey-first flow.
* FAQ cơ bản.
* General cost estimator.
* Eligibility check sơ bộ.
* Lead capture.
* Human handoff.
* Conversation logging.
* LLM intent classification.
* LLM slot extraction.
* Admin cấu hình FAQ/cost range/disclaimer cơ bản.
* Tạo lead sang CRM hoặc database đơn giản.

Should have:

* Basic school lookup cho 20–30 trường.
* Basic region recommendation.
* Tóm tắt hội thoại cho tư vấn viên.
* Lead scoring A/B/C/D.
* Notification cho tư vấn viên.

Could have:

* Book consultation calendar.
* PDF/tin nhắn tư vấn tự động.
* Multi-language support.
* AI recommendation nâng cao.

Out of scope MVP:

* Full advisor console.
* Full parent dashboard.
* Full CTV portal.
* OCR hồ sơ.
* AI interview coach.
* Crawler tự động.
* Payment/contract.
* App mobile.

# 22. User Stories & Acceptance Criteria

Tạo user stories theo role:

Roles:

* Học sinh mới tìm hiểu.
* Phụ huynh.
* Học sinh muốn check hồ sơ.
* Học sinh hỏi chi phí.
* Học sinh hỏi trường cụ thể.
* Tư vấn viên.
* Admin.
* Owner/manager.

Mỗi user story cần:

* As a...
* I want...
* So that...
* Priority: Must/Should/Could.
* Acceptance criteria theo Given/When/Then.

Ví dụ:
As a học sinh mới tìm hiểu,
I want chatbot giải thích quy trình du học Hàn,
So that tôi hiểu các bước cần chuẩn bị trước khi gặp tư vấn viên.

Acceptance criteria:
Given tôi nhắn “em muốn tìm hiểu du học Hàn”
When bot nhận message
Then bot hiển thị menu journey-first gồm quy trình, chi phí, check hồ sơ, khu vực, D4/D2, gặp tư vấn viên.

# 23. Edge Cases

Cần xử lý:

* User hỏi quá chung chung.
* User hỏi nhiều ý trong một message.
* User gõ sai chính tả tên trường.
* User chưa muốn để lại SĐT.
* User chỉ là phụ huynh.
* User chưa biết GPA.
* User không biết D4-1/D2.
* User ngân sách quá thấp.
* User hỏi “bao đỗ visa không?”.
* User hỏi việc làm thêm quá mức.
* User hỏi thông tin chưa có trong database.
* LLM không chắc intent.
* Facebook webhook lỗi.
* LLM API lỗi.
* CRM API lỗi.
* Tư vấn viên ngoài giờ làm việc.

# 24. Guardrails & Compliance

Thiết kế guardrails:

* Không cam kết đậu visa.
* Không khẳng định chi phí chính xác tuyệt đối.
* Không tư vấn trường/ngành nếu dữ liệu chưa verified.
* Không khuyến khích khai sai hồ sơ.
* Không khuyến khích đi làm thêm trái quy định.
* Không thu thập dữ liệu nhạy cảm không cần thiết.
* Không để LLM tự tạo số liệu.
* Luôn chuyển tư vấn viên với case rủi ro.

# 25. Sample Conversation Scripts

Tạo ít nhất 8 đoạn hội thoại mẫu:

1. User mới tìm hiểu.
2. User hỏi chi phí.
3. Phụ huynh hỏi tổng tiền.
4. User muốn check hồ sơ.
5. User hỏi D4-1 vs D2.
6. User hỏi Seoul hay tỉnh.
7. User hỏi trường cụ thể.
8. User hỏi “có bao đỗ visa không?”.

Mỗi đoạn cần thể hiện:

* Bot hỏi tự nhiên.
* Bot không hỏi quá nhiều.
* Bot có CTA.
* Bot có disclaimer khi cần.
* Bot biết chuyển tư vấn viên.

# 26. Technical Architecture Summary

Đề xuất kiến trúc mức cao:

Facebook Messenger
→ Webhook Controller
→ Bot Orchestrator
→ Intent Router
→ LLM Service
→ Slot Manager
→ User Profile Store
→ FAQ/Knowledge Base
→ General Cost Estimator
→ Eligibility Checker
→ Lead Service/CRM
→ Human Handoff Service
→ Messenger Response API

Tech stack gợi ý:

* Backend: Spring Boot.
* Database: PostgreSQL.
* Cache/session: Redis.
* LLM: abstraction layer để thay đổi provider.
* Admin simple UI: React hoặc tạm thời cấu hình DB.
* Deployment: Docker.
* Logging/monitoring cơ bản.

Không cần viết code implementation, nhưng cần mô tả component, responsibilities và data flow.

# 27. Data Model MVP

Đề xuất logical data model cho chatbot MVP:

Entities:

* ConversationSession
* Message
* UserProfile
* Lead
* IntentLog
* SlotValue
* FAQItem
* CostRangeTemplate
* EligibilityAssessment
* HandoffRequest
* AdvisorNotification
* BotConfiguration
* AuditLog

Với mỗi entity, mô tả:

* Fields chính.
* Relationship.
* Index cần thiết.
* Dữ liệu nào là PII.
* Retention rule nếu cần.

# 28. API Contract MVP

Đề xuất API mức cao:

* POST /webhooks/facebook
* POST /bot/messages/process
* POST /llm/intent-classify
* POST /llm/slot-extract
* POST /cost/general-estimate
* POST /eligibility/check
* POST /leads
* PATCH /leads/{id}
* POST /handoff
* GET /admin/bot-config
* PUT /admin/bot-config
* GET /analytics/chatbot

Với mỗi API:

* Mục đích.
* Request mẫu.
* Response mẫu.
* Error cases.
* Permission nếu có.

# 29. Open Questions

Liệt kê các câu hỏi cần business owner xác nhận:

* Có muốn bot xưng “em/chị” hay “bạn/trung tâm”?
* Có thu SĐT bắt buộc không?
* Có cho phụ huynh đi flow riêng không?
* Chi phí range ban đầu lấy theo mức nào?
* Có dùng ManyChat/n8n MVP trước hay build native ngay?
* CRM ban đầu dùng database riêng hay Google Sheet?
* Có tư vấn ngoài giờ làm việc không?
* Có cần đặt lịch tư vấn tự động không?
* Có cần tích hợp Zalo sau Facebook không?
* Quy định follow-up lead nóng là bao lâu?

# 30. Final Delivery Requirements

PRD phải:

* Viết bằng Markdown.
* Có heading rõ ràng.
* Có bảng khi phù hợp.
* Không chung chung.
* Có đủ flow, requirements, business rules, acceptance criteria.
* Tách rõ MVP và phase sau.
* Luôn nhấn mạnh journey-first trước school-first.
* Luôn nhấn mạnh LLM không phải source of truth.
* Có thể đưa trực tiếp cho dev/BA/designer để triển khai.
