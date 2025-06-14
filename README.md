# voce-aluga
Projeto: Eng. Software



# Estrutura de Diretórios do Projeto

Este é o layout do projeto.

```plaintext
C:.
│   .gitattributes
│   .gitignore
│   build.gradle
│   gradlew
│   gradlew.bat
│   HELP.md
│   settings.gradle
│
├───.gradle
│   ├───8.14.2
│   │   │   gc.properties
│   │   │
│   │   ├───checksums
│   │   │       checksums.lock
│   │   │       md5-checksums.bin
│   │   │       sha1-checksums.bin
│   │   │
│   │   ├───expanded
│   │   ├───fileChanges
│   │   │       last-build.bin
│   │   │
│   │   ├───fileHashes
│   │   │       fileHashes.bin
│   │   │       fileHashes.lock
│   │   │
│   │   └───vcsMetadata
│   ├───buildOutputCleanup
│   │       buildOutputCleanup.lock
│   │       cache.properties
│   │
│   └───vcs-1
│           gc.properties
│
├───.vscode
│       settings.json
│
├───build
│   ├───classes
│   │   └───java
│   │       ├───main
│   │       └───test
│   ├───reports
│   │   └───problems
│   │           problems-report.html
│   │
│   └───resources
│       ├───main
│       └───test
├───gradle
│   └───wrapper
│           gradle-wrapper.jar
│           gradle-wrapper.properties
│
└───src
    ├───main
    │   ├───java
    │   │   └───com
    │   │       └───vocealuga
    │   │           │   VoceAlugaApplication.java
    │   │           │
    │   │           ├───controller
    │   │           │       ClienteController.java
    │   │           │       EstoqueController.java
    │   │           │       FilialController.java
    │   │           │       FormaPagamentoController.java
    │   │           │       FuncionarioController.java
    │   │           │       GrupoVeiculoController.java
    │   │           │       ManutencaoController.java
    │   │           │       PagamentoController.java
    │   │           │       ReservaController.java
    │   │           │       VeiculoController.java
    │   │           │
    │   │           ├───dao
    │   │           │       ClienteRepository.java
    │   │           │       EstoqueRepository.java
    │   │           │       FilialRepository.java
    │   │           │       FormaPagamentoRepository.java
    │   │           │       FuncionarioRepository.java
    │   │           │       GrupoVeiculoRepository.java
    │   │           │       ManutencaoRepository.java
    │   │           │       PagamentoRepository.java
    │   │           │       ReservaRepository.java
    │   │           │       VeiculoRepository.java
    │   │           │
    │   │           ├───model
    │   │           │       Cliente.java
    │   │           │       Estoque.java
    │   │           │       Filial.java
    │   │           │       FormaPagamento.java
    │   │           │       Funcionario.java
    │   │           │       GrupoVeiculo.java
    │   │           │       Manutencao.java
    │   │           │       Pagamento.java
    │   │           │       Reserva.java
    │   │           │       Veiculo.java
    │   │           │
    │   │           └───service
    │   │                   ClienteService.java
    │   │                   EstoqueService.java
    │   │                   FilialService.java
    │   │                   FormaPagamentoService.java
    │   │                   FuncionarioService.java
    │   │                   GrupoVeiculoService.java
    │   │                   ManutencaoService.java
    │   │                   PagamentoService.java
    │   │                   ReservaService.java
    │   │                   VeiculoService.java
    │   │
    │   └───resources
    │       │   application.properties
    │       │
    │       ├───static
    │       └───templates
    └───test
        └───java
            │   Funciona.class
            │   Funciona.java
            │
            └───com
                └───vocealuga
                        VoceAlugaApplicationTests.java
