#!/usr/bin/env bash
set -euo pipefail

REPO_DIR="$(cd "$(dirname "$0")/../.." && pwd)"
INFRA_DIR="$REPO_DIR/infra"
export PATH="${HOME}/.local/bin:${PATH}"

log() { echo "==> $*"; }

log "1/6 Corrigir stack base"
docker compose -f "$INFRA_DIR/docker-compose.yml" up -d localstack rds-mysql registry sonarqube ec2-api gateway 2>/dev/null || true
docker start voce-aluga-api-rest voce-aluga-api-web voce-aluga-gateway 2>/dev/null || true
sleep 8

log "2/6 Pipeline local"
bash "$INFRA_DIR/scripts/pipeline-local.sh"

log "3/6 Gateway K8s + Ingress"
kubectl apply -f "$INFRA_DIR/kubernetes/gateway-deployment.yaml"
kubectl apply -f "$INFRA_DIR/kubernetes/gateway-service.yaml"
kubectl apply -f "$INFRA_DIR/kubernetes/ingress.yaml"
kubectl rollout status deployment/gateway -n voce-aluga --timeout=120s

log "4/6 Ansible deploy-docker (EC2)"
ssh-keygen -f "${HOME}/.ssh/known_hosts" -R '[127.0.0.1]:2224' 2>/dev/null || true
docker compose -f "$INFRA_DIR/docker-compose.yml" up -d ec2-api registry 2>/dev/null || true
sleep 8
for i in $(seq 1 15); do
  ssh -o StrictHostKeyChecking=accept-new -o IdentitiesOnly=yes -o ConnectTimeout=3 -p 2224 \
    -i "$INFRA_DIR/ansible/files/voce-aluga" ubuntu@127.0.0.1 echo ok && break
  sleep 2
done
docker stop voce-aluga-api-rest 2>/dev/null || true
(cd "$INFRA_DIR/ansible" && ansible-playbook playbooks/deploy-docker.yml \
  -e registry_host=localhost:5000 -e docker_tag=latest)

log "5/6 Ansible deploy-k8s + replicacao MySQL"
kind load docker-image vocealuga/api-web:latest --name voce-aluga 2>/dev/null || true
(cd "$INFRA_DIR/ansible" && ansible-playbook playbooks/deploy-k8s.yml \
  -e docker_image_web=vocealuga/api-web -e docker_tag=latest)
kubectl delete job mysql-setup-replication -n voce-aluga --ignore-not-found
bash "$INFRA_DIR/scripts/wait-replication.sh"

log "6/6 Validacao final"
sleep 10
bash "$INFRA_DIR/scripts/validate.sh"
