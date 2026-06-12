#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

PORT="${SERVER_PORT:-8080}"
VERIFY_TOKEN="${FACEBOOK_VERIFY_TOKEN:-}"

if [[ -f .env ]]; then
  set -a
  # shellcheck disable=SC1091
  source .env
  set +a
  PORT="${SERVER_PORT:-8080}"
  VERIFY_TOKEN="${FACEBOOK_VERIFY_TOKEN:-}"
fi

if [[ -z "$VERIFY_TOKEN" ]]; then
  VERIFY_TOKEN="change-me"
fi

CHALLENGE="staging-challenge-12345"
URL="http://localhost:${PORT}/webhooks/facebook?hub.mode=subscribe&hub.verify_token=${VERIFY_TOKEN}&hub.challenge=${CHALLENGE}"

echo "GET $URL"
RESPONSE="$(curl -s -w "\n%{http_code}" "$URL")"
CODE="$(echo "$RESPONSE" | tail -n 1)"
BODY="$(echo "$RESPONSE" | sed '$d')"

echo "HTTP $CODE"
echo "Body: $BODY"

if [[ "$CODE" == "200" && "$BODY" == "$CHALLENGE" ]]; then
  echo "OK — webhook verification endpoint works locally."
  exit 0
fi

echo "FAIL — ensure backend is running and FACEBOOK_VERIFY_TOKEN matches."
exit 1
