#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
ENV_FILE="$ROOT/.env"

if [[ ! -f "$ENV_FILE" ]]; then
  echo "ERROR: Missing .env — run: cp .env.staging.example .env"
  exit 1
fi

missing=0
while IFS= read -r key; do
  line="$(grep -E "^${key}=" "$ENV_FILE" || true)"
  val="${line#*=}"
  if [[ -z "$val" ]]; then
    echo "ERROR: $key is empty in .env (save file in editor if you just pasted values)"
    missing=1
  else
    echo "OK: $key is set (length ${#val})"
  fi
done <<'KEYS'
FACEBOOK_VERIFY_TOKEN
FACEBOOK_APP_SECRET
FACEBOOK_PAGE_ACCESS_TOKEN
KEYS

if [[ "$missing" -ne 0 ]]; then
  exit 1
fi

echo "Staging env looks ready."
