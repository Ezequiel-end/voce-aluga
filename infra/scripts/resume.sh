#!/usr/bin/env bash
set -euo pipefail

REPO_DIR="$(cd "$(dirname "$0")/../.." && pwd)"
INFRA_DIR="$REPO_DIR/infra"
export PATH="${HOME}/.local/bin:${PATH}"

log() { echo "==> $*"; }

log "1/5 Docker compose"
docker compose -f "$INFRA_DIR/docker-compose.yml" up -d --build 2>&1 | tail -8

docker restart voce-aluga-ec2-api 2>/dev/null || true
sleep 12
docker exec voce-aluga-ec2-api bash -c '
  mkdir -p /home/ubuntu/.ssh
  cp /run/keys/authorized_keys /home/ubuntu/.ssh/authorized_keys
  chown -R ubuntu:ubuntu /home/ubuntu/.ssh
  chmod 700 /home/ubuntu/.ssh
  chmod 600 /home/ubuntu/.ssh/authorized_keys
  echo "Defaults:ubuntu !requiretty" > /etc/sudoers.d/ubuntu-voce-aluga
  echo "ubuntu ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers.d/ubuntu-voce-aluga
  chmod 440 /etc/sudoers.d/ubuntu-voce-aluga
' 2>/dev/null || true

log "Aguardar SSH na EC2 simulada"
for i in $(seq 1 24); do
  ssh -o BatchMode=yes -o IdentitiesOnly=yes -o ConnectTimeout=5 -o StrictHostKeyChecking=accept-new \
    -p 2224 -i "$INFRA_DIR/ansible/files/voce-aluga" ubuntu@127.0.0.1 \
    "sudo -n whoami" 2>/dev/null | grep -q root && {
    echo "EC2 SSH + sudo prontos."; break
  }
  sleep 5
done

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
  -e ansible_ssh_private_key_file=files/voce-aluga \
  -e registry_host=localhost:5000 -e docker_tag=latest) 2>&1 | tail -5

log "4/5 K8s + replicacao MySQL"
if kind get clusters 2>/dev/null | grep -qx voce-aluga; then
  bash "$INFRA_DIR/scripts/setup-kind.sh" 2>&1 | tail -3
  log "Aguardar cluster Kubernetes"
  for i in $(seq 1 24); do
    kubectl --context kind-voce-aluga get nodes >/dev/null 2>&1 && \
    kubectl --context kind-voce-aluga apply -f "$INFRA_DIR/kubernetes/namespace.yaml" --validate=false >/dev/null 2>&1 && {
      echo "Cluster K8s pronto."; break
    }
    sleep 5
  done
  kind load docker-image vocealuga/api-web:latest --name voce-aluga 2>/dev/null || true
  (cd "$INFRA_DIR/ansible" && ansible-playbook playbooks/deploy-k8s.yml \
    -e docker_image_web=vocealuga/api-web -e docker_tag=latest \
    -e kubectl_context_override=kind-voce-aluga) 2>&1 | tail -8
  bash "$INFRA_DIR/scripts/wait-replication.sh" 2>&1 | tail -5
fi

log "5/5 Validacao"
docker restart voce-aluga-gateway 2>/dev/null || true
sleep 3
bash "$INFRA_DIR/scripts/validate.sh"
