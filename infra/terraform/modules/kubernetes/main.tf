variable "namespace" { type = string }

resource "kubernetes_namespace" "main" {
  depends_on = [null_resource.ingress_nginx]

  metadata {
    name = var.namespace
    labels = {
      "app.kubernetes.io/part-of" = "voce-aluga"
      "managed-by"                = "terraform"
    }
  }
}

resource "kubernetes_resource_quota" "main" {
  metadata {
    name      = "voce-aluga-quota"
    namespace = kubernetes_namespace.main.metadata[0].name
  }

  spec {
    hard = {
      pods                   = "20"
      "requests.cpu"         = "4"
      "requests.memory"      = "8Gi"
      "limits.cpu"           = "8"
      "limits.memory"        = "16Gi"
      persistentvolumeclaims = "5"
    }
  }
}

output "namespace" {
  value = kubernetes_namespace.main.metadata[0].name
}
