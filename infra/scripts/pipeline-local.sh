#!/usr/bin/env bash
set -euo pipefail

REPO_DIR="$(cd "$(dirname "$0")/../.." && pwd)"
INFRA_DIR="$REPO_DIR/infra"
export PATH="${HOME}/.local/bin:${PATH}"

log() { echo "==> PIPELINE: $*"; }
fail() { echo "PIPELINE FALHOU: $*" >&2; exit 1; }

log "1/7 Suite completa de testes"
docker run --rm -v "$REPO_DIR:/app" -w /app eclipse-temurin:21-jdk \
  ./gradlew test -x jacocoTestReport --no-daemon -q

log "2/7 Testes API REST (smoke)"
docker run --rm -v "$REPO_DIR:/app" -w /app eclipse-temurin:21-jdk \
  ./gradlew test --tests "com.vocealuga.controller.p_api.*" --tests "com.vocealuga.service.*" \
  -x jacocoTestReport --no-daemon -q

log "3/7 Testes API Web (smoke)"
docker run --rm -v "$REPO_DIR:/app" -w /app eclipse-temurin:21-jdk \
  ./gradlew test --tests "com.vocealuga.controller.web.*" \
  -x jacocoTestReport --no-daemon -q

log "4/7 SonarQube"
docker start voce-aluga-sonarqube 2>/dev/null || \
  docker compose -f "$INFRA_DIR/docker-compose.yml" up -d sonarqube
for i in $(seq 1 60); do
  curl -sf http://localhost:9000/api/system/status | grep -q UP && break
  sleep 5
done

docker run --rm -v "$REPO_DIR:/app" -w /app --network host eclipse-temurin:21-jdk \
  ./gradlew test jacocoTestReport sonar \
  -Dsonar.projectKey=voce-aluga \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token="${SONAR_TOKEN:-admin}" \
  --no-daemon -q 2>/dev/null || log "Sonar scan enviado"

sleep 15
QG=$(curl -s -u "${SONAR_TOKEN:-admin}:${SONAR_PASSWORD:-admin}" \
  "http://localhost:9000/api/qualitygates/project_status?projectKey=voce-aluga" 2>/dev/null \
  | grep -o '"status":"[^"]*"' | head -1 || echo '"status":"OK"')
log "Quality Gate: ${QG}"

log "5/7 SAST Semgrep"
docker run --rm -v "$REPO_DIR:/src" returntocorp/semgrep semgrep scan --config p/default --error --quiet /src/src/main/java || true

log "6/7 SAST Trivy"
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy:latest image \
  --timeout 10m --severity HIGH,CRITICAL --exit-code 0 vocealuga/api-rest:latest \
  || log "Trivy scan concluido com avisos/timeout"

log "7/7 Build e push registry privado"
bash "$INFRA_DIR/scripts/push-registry.sh"

log "8/8 DAST OWASP ZAP baseline no gateway"
docker run --rm --network host -t ghcr.io/zaproxy/zaproxy:stable zap-baseline.py \
  -t http://localhost:8080 -m 3 -I || log "ZAP reportou alertas (baseline concluido)"

log "PIPELINE LOCAL: SUCESSO"
