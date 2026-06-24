#!/usr/bin/env bash
set -euo pipefail

REPO_DIR="$(cd "$(dirname "$0")/../.." && pwd)"
INFRA_DIR="$REPO_DIR/infra"
export PATH="${HOME}/.local/bin:${PATH}"

log() { echo "==> PROVA 100%: $*"; }

log "1/4 Suite completa de testes Gradle"
docker run --rm -v "$REPO_DIR:/app" -w /app eclipse-temurin:21-jdk \
  ./gradlew test -x jacocoTestReport --no-daemon -q

log "2/4 Pipeline local ponta a ponta"
bash "$INFRA_DIR/scripts/pipeline-local.sh"

log "3/4 MySQL replicacao K8s"
bash "$INFRA_DIR/scripts/wait-replication.sh"

log "4/4 Validacao infraestrutura"
bash "$INFRA_DIR/scripts/validate.sh"

log "PROVA 100%: CONCLUIDA COM SUCESSO"
