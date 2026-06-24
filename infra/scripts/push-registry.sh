#!/usr/bin/env bash
set -euo pipefail

REPO_DIR="$(cd "$(dirname "$0")/../.." && pwd)"
REGISTRY="${REGISTRY_HOST:-localhost:5000}"

log() { echo "==> $*"; }

if ! docker image inspect vocealuga/api-rest:latest >/dev/null 2>&1; then
  log "Build vocealuga/api-rest:latest"
  docker build -f "$REPO_DIR/infra/apis/api-1/Docker/Dockerfile" -t vocealuga/api-rest:latest "$REPO_DIR"
fi

if ! docker image inspect vocealuga/api-web:latest >/dev/null 2>&1; then
  log "Build vocealuga/api-web:latest"
  docker build -f "$REPO_DIR/infra/apis/api-2/Docker/Dockerfile" -t vocealuga/api-web:latest "$REPO_DIR"
fi

log "Push para registry privado $REGISTRY"
docker tag vocealuga/api-rest:latest "${REGISTRY}/vocealuga/api-rest:latest"
docker tag vocealuga/api-web:latest "${REGISTRY}/vocealuga/api-web:latest"
docker push "${REGISTRY}/vocealuga/api-rest:latest"
docker push "${REGISTRY}/vocealuga/api-web:latest"
log "Imagens publicadas no registry privado"
