#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

echo "==> Aguardando LocalStack..."
for i in $(seq 1 30); do
  if curl -sf http://localhost:4566/_localstack/health >/dev/null; then
    echo "LocalStack pronto."
    break
  fi
  sleep 2
done

echo "==> Aplicando Terraform..."
cd terraform
terraform init -input=false
terraform apply -auto-approve -input=false
cd ..

echo "==> Deploy concluido. Gateway: http://localhost:8080"
