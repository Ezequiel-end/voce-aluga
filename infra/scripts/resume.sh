#!/usr/bin/env bash
set -euo pipefail

REPO_DIR="$(cd "$(dirname "$0")/../.." && pwd)"
INFRA_DIR="$REPO_DIR/infra"
export PATH="${HOME}/.local/bin:${PATH}"

log() { echo "==> $*"; }

log "1/5 Docker compose"
docker compose -f "$INFRA_DIR/docker-compose.yml" up -d --build 2>&1 | tail -8

docker restart voce-aluga-ec2-api 2>/dev/null || true
sleep 8
docker exec voce-aluga-ec2-api bash -c '
  mkdir -p /home/ubuntu/.ssh
  cp /run/keys/authorized_keys /home/ubuntu/.ssh/authorized_keys
  chown -R ubuntu:ubuntu /home/ubuntu/.ssh
  chmod 700 /home/ubuntu/.ssh
  chmod 600 /home/ubuntu/.ssh/authorized_keys
' 2>/dev/null || true

log "2/5 Aguardar servicos (ate 4 min)"
for i in $(seq 1 48); do
  curl -sf http://localhost:8081/actuator/health >/dev/null && \
  curl -sf http://localhost:8080/cliente/login >/dev/null && \
  curl -sf http://localhost:5000/v2/ >/dev/null && \
  curl -sf http://localhost:4566/_localstack/health >/dev/null && {
    echo "Stack Docker pronta."; break
  }
  sleep 5
done

log "3/5 Registry + Ansible EC2"
bash "$INFRA_DIR/scripts/push-registry.sh" 2>&1 | tail -3
(cd "$INFRA_DIR/ansible" && ansible-playbook playbooks/deploy-docker.yml \
  -e registry_host=localhost:5000 -e docker_tag=latest) 2>&1 | tail -5

log "4/5 K8s + replicacao MySQL"
if kind get clusters 2>/dev/null | grep -qx voce-aluga; then
  kind load docker-image vocealuga/api-web:latest --name voce-aluga 2>/dev/null || true
  (cd "$INFRA_DIR/ansible" && ansible-playbook playbooks/deploy-k8s.yml \
    -e docker_image_web=vocealuga/api-web -e docker_tag=latest) 2>&1 | tail -5
  bash "$INFRA_DIR/scripts/wait-replication.sh" 2>&1 | tail -5
fi

log "5/5 Validacao"
bash "$INFRA_DIR/scripts/validate.sh"
