#!/usr/bin/env bash
set -euo pipefail

INFRA_DIR="$(cd "$(dirname "$0")/.." && pwd)"
NS=voce-aluga
DB_PASS="${DB_PASSWORD:-senha123}"

log() { echo "==> REPLICACAO: $*"; }

kubectl delete job mysql-setup-replication -n "$NS" --ignore-not-found
kubectl apply -f "$INFRA_DIR/kubernetes/database/job-replication.yaml"

log "Aguardando job mysql-setup-replication"
kubectl wait --for=condition=complete job/mysql-setup-replication -n "$NS" --timeout=300s

log "Verificando Replica_IO_Running nas replicas"
for pod in mysql-replica-0 mysql-replica-1; do
  kubectl exec -n "$NS" "$pod" -c mysql -- \
    mysql -uroot -p"${DB_PASS}" -e "SHOW REPLICA STATUS\G" 2>/dev/null \
    | grep -q "Replica_IO_Running: Yes"
  log "$pod: replicacao ativa"
done

log "MySQL replicacao OK"
