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
│   │   file-system.probe
│   │
│   ├───8.14.2
│   │   │   gc.properties
│   │   │
│   │   ├───checksums
│   │   │       checksums.lock
│   │   │       md5-checksums.bin
│   │   │       sha1-checksums.bin
│   │   │
│   │   ├───executionHistory
│   │   │       executionHistory.bin
│   │   │       executionHistory.lock
│   │   │
│   │   ├───expanded
│   │   ├───fileChanges
│   │   │       last-build.bin
│   │   │
│   │   ├───fileHashes
│   │   │       fileHashes.bin
│   │   │       fileHashes.lock
│   │   │       resourceHashesCache.bin
│   │   │
│   │   └───vcsMetadata
│   ├───buildOutputCleanup
│   │       buildOutputCleanup.lock
│   │       cache.properties
│   │       outputFiles.bin
│   │       
│   └───vcs-1
│           gc.properties
│
├───.vscode
│       settings.json
│
├───build
│   │   resolvedMainClassName
│   │
│   ├───classes
│   │   └───java
│   │       ├───main
│   │       │   └───com
│   │       │       └───vocealuga
│   │       │           │   VoceAlugaApplication.class
│   │       │           │
│   │       │           ├───controller
│   │       │           │   │   ClienteController.class
│   │       │           │   │   EstoqueController.class
│   │       │           │   │   FilialController.class
│   │       │           │   │   FormaPagamentoController.class
│   │       │           │   │   FuncionarioController.class
│   │       │           │   │   GrupoVeiculoController.class
│   │       │           │   │   ManutencaoController.class
│   │       │           │   │   PagamentoController.class
│   │       │           │   │   ReservaController.class
│   │       │           │   │   VeiculoController.class
│   │       │           │   │
│   │       │           │   └───web
│   │       │           │           ClienteViewController.class
│   │       │           │
│   │       │           ├───dao
│   │       │           │       ClienteRepository.class
│   │       │           │       EstoqueRepository.class
│   │       │           │       FilialRepository.class
│   │       │           │       FormaPagamentoRepository.class
│   │       │           │       FuncionarioRepository.class
│   │       │           │       GrupoVeiculoRepository.class
│   │       │           │       ManutencaoRepository.class
│   │       │           │       PagamentoRepository.class
│   │       │           │       ReservaRepository.class
│   │       │           │       VeiculoRepository.class
│   │       │           │
│   │       │           ├───model
│   │       │           │       Cliente.class
│   │       │           │       Estoque.class
│   │       │           │       Filial.class
│   │       │           │       FormaPagamento.class
│   │       │           │       Funcionario.class
│   │       │           │       GrupoVeiculo.class
│   │       │           │       Manutencao.class
│   │       │           │       Pagamento.class
│   │       │           │       Reserva.class
│   │       │           │       Veiculo.class
│   │       │           │
│   │       │           └───service
│   │       │                   ClienteService.class
│   │       │                   EstoqueService.class
│   │       │                   FilialService.class
│   │       │                   FormaPagamentoService.class
│   │       │                   FuncionarioService.class
│   │       │                   GrupoVeiculoService.class
│   │       │                   ManutencaoService.class
│   │       │                   PagamentoService.class
│   │       │                   ReservaService.class
│   │       │                   VeiculoService.class
│   │       │
│   │       └───test
│   ├───generated
│   │   └───sources
│   │       ├───annotationProcessor
│   │       │   └───java
│   │       │       └───main
│   │       └───headers
│   │           └───java
│   │               └───main
│   ├───reports
│   │   └───problems
│   │           problems-report.html
│   │
│   ├───resources
│   │   ├───main
│   │   │   │   application.properties
│   │   │   │
│   │   │   ├───static
│   │   │   └───templates
│   │   │           clientes.html
│   │   │           form-cliente.html
│   │   │
│   │   └───test
│   └───tmp
│       └───compileJava
│               previous-compilation-data.bin
│
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
    │   │           │   ├───p_api
    │   │           │   │       ClienteController.java
    │   │           │   │       EstoqueController.java
    │   │           │   │       FilialController.java
    │   │           │   │       FormaPagamentoController.java
    │   │           │   │       FuncionarioController.java
    │   │           │   │       GrupoVeiculoController.java
    │   │           │   │       ManutencaoController.java
    │   │           │   │       PagamentoController.java
    │   │           │   │       ReservaController.java
    │   │           │   │       VeiculoController.java
    │   │           │   │
    │   │           │   └───web
    │   │           │           ClienteViewController.java
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
    │               clientes.html
    │               form-cliente.html
    │
    └───test
        └───java
            │   Funciona.class
            │   Funciona.java
            │
            └───com
                └───vocealuga
                        VoceAlugaApplicationTests.java
