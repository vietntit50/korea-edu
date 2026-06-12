# Facebook Webhook Staging Setup

Hướng dẫn kết nối fanpage/test page với backend local qua HTTPS tunnel (staging).

## 1. Yêu cầu

- Backend Slice 1 chạy tại `http://localhost:8080`
- PostgreSQL (`docker compose up -d`)
- Tài khoản [Meta for Developers](https://developers.facebook.com/)
- Fanpage test (hoặc fanpage staging) đã liên kết với app
- Tool tunnel: **cloudflared** (đã cài qua Homebrew)

## 2. Biến môi trường

Copy và điền secret từ Meta Developer Console:

```bash
cp .env.staging.example .env
```

| Biến | Lấy ở đâu |
|---|---|
| `FACEBOOK_VERIFY_TOKEN` | Tự đặt (chuỗi bí mật, khớp khi verify webhook) |
| `FACEBOOK_APP_SECRET` | App → Settings → Basic → App Secret |
| `FACEBOOK_PAGE_ACCESS_TOKEN` | Messenger → Settings → Access Tokens (Page token) |

`FACEBOOK_VERIFY_TOKEN` trong `.env` phải **trùng** với Verify Token nhập trên Meta khi add webhook.

## 3. Chạy stack staging local

```bash
# Terminal 1 — DB + backend
./scripts/staging-up.sh

# Terminal 2 — HTTPS tunnel (sau khi backend đã listen :8080)
./scripts/staging-tunnel.sh
```

Tunnel in ra URL dạng `https://xxxx.trycloudflare.com`. Webhook callback:

```text
https://<tunnel-host>/webhooks/facebook
```

## 4. Cấu hình Meta Developer Console

### 4.1. Tạo / chọn App

1. [developers.facebook.com](https://developers.facebook.com/) → **Create App** → type **Business** (hoặc app hiện có).
2. Add product **Messenger**.

### 4.2. Page access token

1. **Messenger → Settings**.
2. Chọn **Add or Remove Pages** → thêm fanpage test.
3. **Generate** Page Access Token → copy vào `FACEBOOK_PAGE_ACCESS_TOKEN` trong `.env`.

### 4.3. Webhook

1. **Messenger → Settings → Webhooks** → **Add Callback URL**.
2. **Callback URL:** `https://<tunnel-host>/webhooks/facebook`
3. **Verify Token:** giá trị `FACEBOOK_VERIFY_TOKEN` trong `.env`
4. Click **Verify and Save** — backend phải đang chạy và tunnel active.

### 4.4. Subscribe Page

1. Trong Webhooks, **Add Subscriptions** cho page test.
2. Bật tối thiểu:
   - `messages`
   - `messaging_postbacks` (quick reply / button)

### 4.5. App mode

- **Development mode:** chỉ admin/developer/tester của app nhắn được bot.
- Thêm Facebook account test: **App Roles → Testers**.

## 5. Kiểm tra

### 5.1. Verify token (local, không cần tunnel)

```bash
./scripts/verify-webhook-local.sh
```

### 5.2. Nhắn thử trên Messenger

1. Mở fanpage test → **Message**.
2. Gửi: `Tu van du hoc Han`
3. Kỳ vọng: greeting + menu 7 quick replies.

### 5.3. Log backend

Tìm log:

```text
Menu selection ...   # khi bấm quick reply
Duplicate Facebook message ignored   # khi Facebook retry
Invalid Facebook webhook signature   # nếu APP_SECRET sai
```

## 6. Troubleshooting

| Triệu chứng | Nguyên nhân thường gặp | Cách xử lý |
|---|---|---|
| Verify failed | Backend/tunnel chưa chạy hoặc verify token lệch | Chạy `staging-up` + `staging-tunnel`; đối chiếu token |
| 401 invalid_signature | `FACEBOOK_APP_SECRET` sai | Copy lại App Secret; restart backend |
| Webhook OK nhưng không reply | Thiếu page token hoặc `MESSENGER_SEND_ENABLED=false` | Điền `FACEBOOK_PAGE_ACCESS_TOKEN`; check `.env` |
| User không nhắn được | App Development mode | Thêm user vào Testers hoặc chuyển Live (không khuyến nghị sớm) |
| Tunnel URL đổi mỗi lần chạy | Cloudflare quick tunnel ephemeral | Cập nhật lại Callback URL trên Meta |

## 7. Checklist staging

- [ ] PostgreSQL chạy (`docker compose ps`)
- [ ] Backend chạy port 8080
- [ ] `.env` có verify token, app secret, page token
- [ ] Tunnel HTTPS active
- [ ] Meta webhook verified
- [ ] Page subscribed `messages` + `messaging_postbacks`
- [ ] Test message nhận menu 7 mục
