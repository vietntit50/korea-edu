#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

if [[ ! -f .env ]]; then
  echo "Missing .env — run: cp .env.staging.example .env"
  echo "Then fill FACEBOOK_APP_SECRET and FACEBOOK_PAGE_ACCESS_TOKEN"
  exit 1
fi

if ! "$ROOT/scripts/check-staging-env.sh"; then
  echo ""
  echo "Tip: press Cmd+S to save .env in the editor, then run again."
  exit 1
fi

set -a
# shellcheck disable=SC1091
source .env
set +a

echo "==> Starting PostgreSQL..."
docker compose up -d postgres

echo "==> Waiting for PostgreSQL..."
for i in {1..30}; do
  if docker compose exec -T postgres pg_isready -U "${DATABASE_USERNAME:-chatbot}" -d korea_edu_chatbot >/dev/null 2>&1; then
    break
  fi
  sleep 1
done

echo "==> Starting Spring Boot (port ${SERVER_PORT:-8080})..."
cd backend
export DATABASE_URL DATABASE_USERNAME DATABASE_PASSWORD SERVER_PORT
export FACEBOOK_APP_SECRET FACEBOOK_VERIFY_TOKEN FACEBOOK_PAGE_ACCESS_TOKEN
export FACEBOOK_GRAPH_API_VERSION FACEBOOK_SIGNATURE_VERIFICATION_ENABLED MESSENGER_SEND_ENABLED

mvn -q spring-boot:run
