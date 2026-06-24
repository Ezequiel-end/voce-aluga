#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
BIN_DIR="${HOME}/.local/bin"
mkdir -p "$BIN_DIR"
export PATH="$BIN_DIR:$PATH"

install_kind() {
  if command -v kind >/dev/null 2>&1; then
    return 0
  fi
  echo "Instalando kind..."
  curl -fsSL "https://kind.sigs.k8s.io/dl/v0.26.0/kind-linux-amd64" -o "$BIN_DIR/kind"
  chmod +x "$BIN_DIR/kind"
}

install_kubectl() {
  if command -v kubectl >/dev/null 2>&1; then
    return 0
  fi
  echo "Instalando kubectl..."
  curl -fsSL "https://dl.k8s.io/release/v1.31.0/bin/linux/amd64/kubectl" -o "$BIN_DIR/kubectl"
  chmod +x "$BIN_DIR/kubectl"
}

install_kind
install_kubectl

if kind get clusters 2>/dev/null | grep -qx voce-aluga; then
  echo "Cluster kind voce-aluga ja existe."
else
  kind create cluster --name voce-aluga --config "$ROOT_DIR/kubernetes/kind-config.yaml"
fi

kubectl cluster-info --context kind-voce-aluga
kubectl get nodes --context kind-voce-aluga
echo "Cluster K8s com 2 workers pronto."
