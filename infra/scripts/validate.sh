#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
export PATH="${HOME}/.local/bin:${PATH}"

PASS=0
FAIL=0

check() {
  local name="$1"
  local cmd="$2"
  if eval "$cmd" >/dev/null 2>&1; then
    echo "[OK] $name"
    PASS=$((PASS + 1))
  else
    echo "[FALHA] $name"
    FAIL=$((FAIL + 1))
  fi
}

echo "=== Validacao voce-aluga (completa) ==="

check "Gateway responde" "curl -sf http://localhost:8080/cliente/login"
check "API REST via gateway /api/reservas" "curl -sf http://localhost:8080/api/reservas"
check "API REST health" "curl -sf http://localhost:8081/actuator/health"
check "API Web via gateway" "curl -sf http://localhost:8080/cliente/login"
check "API Web K8s health" "kubectl exec -n voce-aluga deploy/api-web -c api-web -- wget -qO- http://localhost:8082/actuator/health"
check "MySQL Docker (RDS)" "docker exec voce-aluga-rds-mysql mysqladmin ping -uroot -psenha123"
check "LocalStack health" "curl -sf http://localhost:4566/_localstack/health"
check "Registry privado" "curl -sf http://localhost:5000/v2/"
check "Registry tem imagem api-rest" "curl -sf http://localhost:5000/v2/vocealuga/api-rest/tags/list | grep -q latest"
check "Registry tem imagem api-web" "curl -sf http://localhost:5000/v2/vocealuga/api-web/tags/list | grep -q latest"
check "SonarQube UP" "curl -sf http://localhost:9000/api/system/status | grep -q UP"
check "EC2 SSH acessivel" "ssh -o BatchMode=yes -o IdentitiesOnly=yes -o ConnectTimeout=5 -p 2224 -i $ROOT_DIR/ansible/files/voce-aluga ubuntu@127.0.0.1 echo ok"
check "API REST na EC2 (Ansible)" "curl -sf http://localhost:8081/actuator/health | grep -q UP"

if command -v kubectl >/dev/null 2>&1 && kubectl config get-contexts -o name 2>/dev/null | grep -q kind-voce-aluga; then
  check "K8s nodes >= 3" "test \$(kubectl get nodes --no-headers 2>/dev/null | wc -l) -ge 3"
  check "Namespace voce-aluga" "kubectl get ns voce-aluga"
  check "Deployment api-web 2 replicas" "test \$(kubectl get deploy api-web -n voce-aluga -o jsonpath='{.status.readyReplicas}' 2>/dev/null || echo 0) -ge 2"
  check "Deployment gateway K8s" "kubectl get deploy gateway -n voce-aluga"
  check "HPA api-web CPU 60%" "kubectl get hpa api-web -n voce-aluga -o yaml | grep -q 'averageUtilization: 60'"
  check "Ingress aponta gateway" "kubectl get ingress api-web -n voce-aluga -o yaml | grep -q 'name: gateway'"
  check "ResourceQuota" "kubectl get resourcequota -n voce-aluga"
  check "LimitRange" "kubectl get limitrange -n voce-aluga"
  check "MySQL primary pod" "kubectl get pod -n voce-aluga -l app=mysql-primary --field-selector=status.phase=Running"
  check "MySQL 2 replicas" "test \$(kubectl get pod -n voce-aluga -l app=mysql-replica --field-selector=status.phase=Running --no-headers 2>/dev/null | wc -l) -ge 2"
  check "Service gateway K8s" "kubectl get svc gateway -n voce-aluga"
  check "MySQL replicacao ativa" "kubectl exec -n voce-aluga mysql-replica-0 -c mysql -- mysql -uroot -psenha123 -e 'SHOW REPLICA STATUS\\G' 2>/dev/null | grep -q 'Replica_IO_Running: Yes'"
fi

if [ -f "$ROOT_DIR/terraform/terraform.tfstate" ]; then
  check "Terraform state" "test -s $ROOT_DIR/terraform/terraform.tfstate"
  check "Terraform EC2" "grep -q aws_instance $ROOT_DIR/terraform/terraform.tfstate"
  check "Terraform API Gateway" "grep -q aws_api_gateway_rest_api $ROOT_DIR/terraform/terraform.tfstate"
  check "Terraform S3 storage" "grep -q aws_s3_bucket $ROOT_DIR/terraform/terraform.tfstate"
fi

check "Imagens locais api-rest" "docker image inspect vocealuga/api-rest:latest"
check "Imagens locais api-web" "docker image inspect vocealuga/api-web:latest"

echo ""
echo "Resultado: $PASS ok, $FAIL falhas"
[ "$FAIL" -eq 0 ]
