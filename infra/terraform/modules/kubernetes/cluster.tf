variable "cluster_name" {
  type    = string
  default = "voce-aluga"
}

variable "kind_config_path" {
  type = string
}

resource "null_resource" "kind_cluster" {
  triggers = {
    config_hash = filemd5(var.kind_config_path)
  }

  provisioner "local-exec" {
    command     = "bash '${abspath("${path.root}/../scripts/setup-kind.sh")}'"
    working_dir = abspath("${path.root}/..")
  }
}

resource "null_resource" "ingress_nginx" {
  depends_on = [null_resource.kind_cluster]

  provisioner "local-exec" {
    command = <<-EOT
      export PATH="${path.root}/../scripts:${path.root}/../../../.local/bin:${path.root}/../../.local/bin:$HOME/.local/bin:$PATH"
      kubectl config use-context kind-${var.cluster_name} 2>/dev/null || true
      kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.11.3/deploy/static/provider/kind/deploy.yaml
      kubectl wait --namespace ingress-nginx --for=condition=ready pod --selector=app.kubernetes.io/component=controller --timeout=180s
    EOT
  }
}

output "cluster_name" {
  value = var.cluster_name
}

output "ingress_ready" {
  value = null_resource.ingress_nginx.id
}
