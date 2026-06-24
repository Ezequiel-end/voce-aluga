#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"

docker compose -f "$ROOT_DIR/docker-compose.yml" up -d localstack rds-mysql ec2-api
"$ROOT_DIR/scripts/setup-kind.sh" || true

echo "Ambiente local iniciado."
echo "  Gateway:     http://localhost:8080"
echo "  API REST:    http://localhost:8081"
echo "  API Web:     http://localhost:8082"
echo "  LocalStack:  http://localhost:4566"
echo "  SonarQube:   http://localhost:9000"
