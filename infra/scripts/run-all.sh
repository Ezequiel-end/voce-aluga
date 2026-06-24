#!/usr/bin/env bash
set -euo pipefail

REPO_DIR="$(cd "$(dirname "$0")/../.." && pwd)"
INFRA_DIR="$REPO_DIR/infra"
export PATH="${HOME}/.local/bin:${PATH}"

log() { echo "==> $*"; }

cd "$REPO_DIR"

log "Testes Gradle via Docker"
docker run --rm -v "$REPO_DIR:/app" -w /app eclipse-temurin:21-jdk \
  ./gradlew test --tests "com.vocealuga.controller.p_api.ReservaControllerTest" \
  --tests "com.vocealuga.controller.web.ClienteLoginControllerTest" -x jacocoTestReport --no-daemon

log "Build imagens Docker"
docker build -f infra/apis/api-1/Docker/Dockerfile -t vocealuga/api-rest:latest "$REPO_DIR"
docker build -f infra/apis/api-2/Docker/Dockerfile -t vocealuga/api-web:latest "$REPO_DIR"

log "Subir LocalStack e MySQL"
docker compose -f "$INFRA_DIR/docker-compose.yml" up -d localstack rds-mysql
sleep 15

log "Criar cluster kind (pre-requisito do Terraform K8s provider)"
bash "$INFRA_DIR/scripts/setup-kind.sh"

log "Terraform apply"
cd "$INFRA_DIR/terraform"
terraform init -input=false
terraform apply -auto-approve -input=false
cd "$INFRA_DIR"

log "Subir EC2 simulada, APIs e Gateway"
docker compose -f "$INFRA_DIR/docker-compose.yml" up -d --build ec2-api api-rest api-web gateway

log "Aguardar APIs ficarem prontas (ate 4 min)"
for i in $(seq 1 48); do
  if curl -sf http://localhost:8081/actuator/health >/dev/null && curl -sf http://localhost:8082/actuator/health >/dev/null; then
    echo "APIs prontas."
    break
  fi
  sleep 5
done

log "Carregar imagem web no kind"
kind load docker-image vocealuga/api-web:latest --name voce-aluga

log "Ansible deploy-docker"
ansible-playbook -i "$INFRA_DIR/ansible/inventory/hosts.ini" "$INFRA_DIR/ansible/playbooks/deploy-docker.yml" \
  -e docker_image=vocealuga/api-rest -e docker_tag=latest

log "Ansible deploy-k8s"
ansible-playbook -i "$INFRA_DIR/ansible/inventory/hosts.ini" "$INFRA_DIR/ansible/playbooks/deploy-k8s.yml" \
  -e docker_image_web=vocealuga/api-web -e docker_tag=latest

log "Validacao final"
bash "$INFRA_DIR/scripts/validate.sh"
