# Você Aluga

Sistema de gestão para locação de veículos, desenvolvido em Java com Spring Boot.

## Descrição

O **Você Aluga** é uma aplicação web para gerenciamento de aluguel de veículos, permitindo o cadastro de clientes, funcionários, veículos, reservas, pagamentos, manutenções e filiais. O sistema possui áreas distintas para clientes e funcionários, com autenticação e controle de acesso.

## Funcionalidades

- Cadastro e login de clientes e funcionários
- Gerenciamento de veículos, reservas e pagamentos
- Controle de manutenções e estoque de veículos
- Gestão de filiais e grupos de veículos
- Interface web para clientes e funcionários

## Estrutura do Projeto

```
src/
  main/
    java/com/vocealuga/
      controller/      # Controllers REST e Web (MVC)
      dao/             # Repositórios (JPA)
      model/           # Entidades do domínio
      service/         # Lógica de negócio
      utils/           # Utilitários
      SecurityConfig.java # Configuração de segurança (Spring Security)
      VoceAlugaApplication.java # Classe principal
    resources/
      static/          # Arquivos estáticos (CSS)
      templates/       # Templates Thymeleaf (HTML)
      application.properties # Configurações da aplicação
  test/
    java/com/vocealuga/ # Testes unitários e de integração
```

## Tecnologias Utilizadas

- Java 11+
- Spring Boot
- Spring MVC
- Spring Data JPA
- Spring Security
- Thymeleaf
- Gradle

## Como Executar

1. **Pré-requisitos:**  
   - Java 11 ou superior  
   - Gradle (ou use o wrapper `./gradlew`)

2. **Clone o repositório:**
   ```bash
   git clone <url-do-repositorio>
   cd voce-aluga-main
   ```

3. **Execute a aplicação:**
   ```bash
   ./gradlew bootRun
   ```

4. **Acesse no navegador:**  
   ```
   http://localhost:8080
   ```

## Estrutura de Telas

- **Cliente:** Cadastro, login, dashboard, perfil, reservas, pagamento
- **Funcionário:** Login, dashboard, gerenciamento de veículos, reservas, manutenções, filiais

## Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## Trabalho Final — DevOps (Gerência de Configuração)

Repositório original validado: **https://github.com/Ezequiel-end/voce-aluga**

### APIs do projeto

| API | Linguagem | Responsabilidade | Rotas principais |
|-----|-----------|------------------|------------------|
| **API REST** | Java / Spring Boot | Endpoints JSON para o domínio de locação | `/api/clientes`, `/api/veiculos`, `/api/reservas`, `/api/pagamentos`, etc. |
| **API Web** | Java / Spring Boot + Thymeleaf | Interface web para clientes e funcionários | `/cliente/*`, `/funcionario/*` |

Deploy:
- **API REST** → Docker (multi-stage) + EC2 via Ansible
- **API Web** → Kubernetes (2+ réplicas, HPA, Ingress, probes)

Documentação completa da infraestrutura: [infra/README.md](infra/README.md)

