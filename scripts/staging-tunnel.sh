#!/usr/bin/env bash
set -euo pipefail

PORT="${SERVER_PORT:-8080}"

if ! command -v cloudflared >/dev/null 2>&1; then
  echo "cloudflared not found. Install: brew install cloudflared"
  exit 1
fi

echo "==> Starting HTTPS tunnel to http://localhost:${PORT}"
echo "    Webhook URL for Meta: https://<printed-host>/webhooks/facebook"
echo ""

cloudflared tunnel --url "http://localhost:${PORT}"
