# Infraestrutura DevOps — Você Aluga

Repositório original: https://github.com/Ezequiel-end/voce-aluga

## Arquitetura

```
                    ┌─────────────────┐
                    │  API Gateway    │  :8080 (nginx + LocalStack APIGW)
                    └────────┬────────┘
              ┌──────────────┴──────────────┐
              ▼                             ▼
    ┌──────────────────┐          ┌──────────────────┐
    │  API 1 — REST    │          │  API 2 — Web     │
    │  perfil: api     │          │  perfil: web     │
    │  Docker + EC2    │          │  Kubernetes      │
    │  :8081           │          │  :8082           │
    └────────┬─────────┘          └────────┬─────────┘
             │                             │
             └──────────────┬──────────────┘
                            ▼
                 ┌─────────────────────┐
                 │  MySQL (RDS)        │
                 │  + réplicas K8s     │
                 └─────────────────────┘
```

## APIs

| API | Papel | Rotas | Deploy |
|-----|-------|-------|--------|
| **API REST** (`p_api`) | CRUD JSON do domínio (clientes, veículos, reservas, pagamentos…) | `/api/*` | Docker multi-stage → EC2 (Ansible) |
| **API Web** (`web`) | Interface Thymeleaf para cliente e funcionário | `/cliente/*`, `/funcionario/*`, `/login` | Kubernetes (Deployment, Ingress, HPA) |

As APIs compartilham o banco MySQL e se comunicam via gateway e dados persistidos.

## Estrutura

```
infra/
├── apis/api-1/Docker/
├── apis/api-2/Docker/
├── gateway/nginx.conf
├── kubernetes/
├── terraform/
├── ansible/
├── docker/
├── scripts/
└── docker-compose.yml
```

## Pré-requisitos

- Java 21, Docker, Docker Compose
- Terraform >= 1.5
- Ansible
- kubectl + kind (cluster com 2 workers)

## Uso local

```bash
./infra/scripts/start-local.sh

./infra/scripts/setup-kind.sh

cd infra/terraform && terraform init && terraform apply

ansible-playbook -i infra/ansible/inventory/hosts.ini infra/ansible/playbooks/deploy-docker.yml
ansible-playbook -i infra/ansible/inventory/hosts.ini infra/ansible/playbooks/deploy-k8s.yml
```

## Pipeline CI/CD

Arquivo: `.github/workflows/pipeline.yml`

1. Testes unitários API REST e Web
2. SonarQube (Quality Gate obrigatório)
3. SAST: Semgrep + Trivy
4. Build/push imagem Docker (registry privado)
5. Deploy Ansible (Docker + K8s)
6. DAST: OWASP ZAP baseline no gateway
7. Notificação de sucesso/falha

## Secrets necessários (GitHub Actions)

- `DOCKER_USERNAME` / `DOCKER_PASSWORD`
- `SONAR_TOKEN` (opcional)
