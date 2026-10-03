# Registro de Uso de IA Generativa

> **Política da disciplina:** O uso de IA generativa é permitido como
> assistente. O discente é **integralmente responsável** por testar, auditar e
> defender todo o código entregue, independentemente de como foi gerado.

## Instruções

Para cada aula ou entrega, registre abaixo:
- **Data**
- **Ferramenta** (ChatGPT, Copilot, Claude, etc.)
- **Prompt(s) utilizado(s)** (resumo na tabela; íntegra na seção "Prompts na íntegra")
- **O que foi feito com a saída** (copiado integralmente, adaptado, usado como referência, descartado)
- **Como a saída foi validada** (exigência do README do projeto)

## Acordo de uso neste projeto

- A IA atua como tutora: explica conceitos, estrutura o roteiro de trabalho e revisa o que eu produzo.
- Como regra, o código e os artefatos do repositório são escritos por mim. Quando uma saída da IA for copiada para o repositório, isso é declarado na linha da sessão, com o arquivo afetado e o que a IA alterou (casos: `application.properties`, sessão #10; código da Etapa 2, sessão #12; código da Etapa 3, sessão #14; código da A1, sessão #16, parte dele reaproveitada na Etapa 4, sessão #17; código da Etapa 4, sessão #18).
- Todo alerta técnico vindo da IA é verificado na prática (log, teste ou documentação oficial) antes de virar decisão.
- Este arquivo é organizado com apoio da IA a partir das sessões de orientação; o conteúdo das colunas "Uso da saída" e "Como validei" é confirmado por mim antes de cada commit.
- Sessões curtas de suporte (dúvidas pontuais e erros de terminal) são agrupadas numa única linha, com os tópicos listados.

## Resumo por etapa

| Etapa | Sessões de IA | Situação |
|-------|---------------|----------|
| Planejamento | #1 | Roteiro em etapas (0 a 7) adotado |
| Etapa 0 — Repositório | #2 a #9 | Concluída: PR #2 mergeado, issue #1 fechada, `main` protegida, backlog (issues #3 a #9) criado na milestone A1 |
| Etapa 1 — Esqueleto Spring Boot | #10 | Concluída: PR #10 mergeado, issue #3 fechada |
| Etapa 2 — Beneficiário: primeira fatia vertical | #11 e #12 | Concluída: PR #11 mergeado em 28/09/2026, issue #4 fechada |
| Etapa 3 — Beneficiário: CRUD completo e semântica HTTP | #13 e #14 | Concluída: PR #12 mergeado em 02/10/2026. Este registro ficou de fora do merge e entrou depois, no primeiro commit da branch da Etapa 4 |
| A1 — Desafio prático individual (módulo de Produtos) | #16 | Entregue em 02/10/2026 na branch `avaliacao1` (PR #13 aberto, sem merge até a correção); declaração de IA no README da branch |
| Etapa 4 — Beneficiário: validação, erros e i18n | #15, #17 e #18 | Concluída no PR com `Closes #6`: Bean Validation nos DTOs com `@Valid`, constraint `@CPF`, erros em Problem Details (400, 404, 409 e 500 genérico) e mensagens em pt-BR; infraestrutura de erros e i18n reaproveitada da A1 |

## Registro

| # | Data | Aula / Etapa | Ferramenta | Prompt (resumo) | Uso da saída | Como validei |
|---|------|--------------|------------|-----------------|--------------|--------------|
| 1 | 25/09/2026 | Planejamento (material das Aulas 03–07) | Claude Opus 5.5 (claude.ai) | Analisar as anotações de aula e os templates (zip anexado) e explicar estrutura, repositórios e tecnologias; montar um passo a passo para iniciar o projeto, com orientação em vez de código pronto. | Usado como referência: roteiro em etapas (0 a 7) adotado como plano de trabalho. Para responder, a IA também leu o repositório público do professor e consultou documentação do Spring Boot 4 e do Testcontainers na web. | Alertas do Flyway e do H2 console confirmados na Etapa 1 (sessão #10): com `spring-boot-starter-flyway` o log mostra o Flyway executando, e com `spring-boot-h2console` o console abre. Alerta do Testcontainers: pendente, será conferido na Etapa 6. |
| 2 | 25/09/2026 | Etapa 0 (repositório) | Claude Opus 5.5 (claude.ai) | Pedir o passo a passo da Etapa 0, o que mais ler (ex.: repositório do professor) e que a IA mantenha este registro. | Guia da Etapa 0 e lista de leitura usados como referência; rascunho deste arquivo gerado pela IA e revisado por mim. | Etapa 0 executada: PR #2 mergeado com merge commit; issue #1 fechada pelo PR. |
| 3 | 25/09/2026 | Etapa 0 (detalhamento) | Claude Opus 5.5 (claude.ai) | Dúvida sobre rodar `git config --global` sem repositório criado; pedido de passo a passo detalhado de cada ação da Etapa 0. | Guia usado como referência para executar a Etapa 0; comandos digitados e arquivos preenchidos por mim. | `git config --global` configurado; `git log --oneline --graph` mostra os 6 commits da branch e o merge commit do PR #2. |
| 4 | 25/09/2026 | Etapa 0 (proteção da `main`) | Claude Opus 5.5 (claude.ai) | Envio de print da tela Settings do GitHub: não havia "Branches" nem regra clássica de proteção. | Orientação para proteger a `main` via Rulesets, seguida por mim; a IA também apontou erro de digitação no nome do repositório, corrigido. | Ruleset `proteger-main` ativo; push direto na `main` recusado com `GH013: Repository rule violations found` ("Changes must be made through a pull request"). |
| 5 | 25/09/2026 | Etapa 0 (registro de IA) | Claude Opus 5.5 (claude.ai) | Perguntar quem mantém este registro e pedir que a IA vá organizando as informações. | IA passou a entregar este arquivo completo e atualizado; eu reviso e commito. | Revisei o arquivo antes do commit. |
| 6 | 25/09/2026 | Etapa 0 (issue #1) | Claude Opus 5.5 (claude.ai) | Envio de print da criação da issue #1 para conferir se o passo B4 estava correto. | IA confirmou o formato e apontou item faltante no checklist (template de PR). | Issue #1 criada e vinculada ao PR #2 (seção Development). |
| 7 | 25/09/2026 | Etapa 0 (issue #1) | Claude Opus 5.5 (claude.ai) | Pedido de confirmação direta de que a issue do print estava correta. | IA confirmou; issue criada por mim. | Issue #1 fechada automaticamente no merge do PR #2. |
| 8 | 25/09/2026 | Etapa 0 (suporte à execução) | Claude Opus 5.5 (claude.ai) | Sessões de suporte (ver lista abaixo): terminal errado, erros de clone, repositório vazio, avisos e erros do Git, sintaxe Markdown, revisão do PR e conferência dos resultados. | Explicações e comandos usados como referência; todos os comandos digitados por mim e erros corrigidos por mim. A IA revisou o PR #2 e apontou itens a limpar na descrição. | Clone, commits, push, PR e merge concluídos; perguntas de defesa respondidas por mim (ver abaixo). |
| 9 | 25/09/2026 | Etapa 0 (backlog, Parte F) | Claude Opus 5.5 (claude.ai) | Pedir o passo a passo para criar a milestone e as sete issues do roteiro; avisar que a issue da Etapa 1 ficou sem responsável. | Títulos e descrições das issues redigidos pela IA a partir do roteiro, revisados e criados por mim no GitHub; orientação para corrigir o assignee da issue #3. | Milestone A1 com as issues #3 a #9 abertas e atribuídas a mim; issue #1 fechada. |
| 10 | 26/09/2026 | Etapa 1 (esqueleto Spring Boot, issue #3) | Claude Opus 5.5 (claude.ai) | Pedir o guia da Etapa 1; dúvidas de execução (JDK 25 no laboratório, conferência do start.spring.io, `unzip`); pedir o `application.properties` pronto, a partir da referência do professor; revisão do log e do README (ver lista abaixo). | Guia usado como referência; projeto gerado por mim no start.spring.io. **`application.properties` copiado integralmente da resposta da IA:** é o arquivo do repositório do professor com três correções feitas pela IA (`ddl-auto=validate` no lugar de `update`, remoção de `baseline-on-migrate`, prefixo `springdoc.open-api.info.*` no lugar de `springdoc.info.*`). Trechos do README fornecidos pela IA e aplicados por mim. Para montar o guia, a IA consultou o meu repositório, o do professor, o código do start.spring.io e as versões do Spring Boot 4.1.1 e do springdoc. | Log da subida: Spring Boot 4.1.1 rodando no JDK 25.0.1; Flyway executando (`Successfully validated 0 migrations`, criação da `flyway_schema_history`, `No migration necessary`); `H2 console available at '/h2-console'`; `Started ApiApplication`. No navegador: `/swagger-ui.html` e `/api-docs` com o título "SocialConnect API" (prova da correção do prefixo do springdoc); login no `/h2-console` com `jdbc:h2:mem:socialconnectdb` e tabela `flyway_schema_history` visível. |
| 11 | 28/09/2026 | Etapa 2 (guia, issue #4) | Claude Opus 5.5 (claude.ai) | Pedir o guia da Etapa 2; erro ao apagar a branch local já mergeada (ver lista abaixo). | Guia usado como roteiro de testes e decisões da etapa (checkpoints, experimentos, perguntas de defesa, índice extra em `cpf`, `open-in-view`); o código acabou gerado pela IA na sessão #12. Para montar o guia, a IA consultou o meu repositório, a issue #4, as notas das Aulas 03 e 04, o repositório do professor e o código-fonte do Spring Boot, do Hibernate e do Flyway, e testou o SQL num H2 2.4.240. | Alertas do guia conferidos na execução da etapa (ver "Como validei" da linha #12). |
| 12 | 28/09/2026 | Etapa 2 (código, issue #4) | Claude Opus 5.5 (claude.ai) | Pedir que a IA codificasse a Etapa 2 inteira, com o link do repositório, o zip do projeto (meu repositório e o do professor) e o guia da sessão #11 colado. | **Código da Etapa 2 copiado integralmente da resposta da IA:** `V1__create_beneficiarios_table.sql`, `Beneficiario.java`, `BeneficiarioRepository.java`, `BeneficiarioDTO.java`, `BeneficiarioService.java`, `BeneficiarioController.java` e o bloco `spring.jpa.open-in-view=false` do `application.properties`. Base: o Beneficiário em camadas do commit `db0b1ee` do repositório do professor (conteúdo das Aulas 03 e 04) e a V1 do mesmo repositório. O que a IA alterou: V1 sem o `CREATE INDEX` em `cpf` (o `UNIQUE` já cria um índice); `@PathVariable("idBeneficiario")` com nome explícito; imports explícitos no lugar de `*`; Repository sem os métodos derivados do professor (`findByCpf`, `existsByCpf`, `findByNomeContainingIgnoreCase`), que esta etapa não usa; comentários explicando as decisões. Commits feitos por mim, um por camada. | Pela IA, antes da entrega: a V1 rodou num H2 2.4.240 (um único índice em `cpf`, contra dois na V1 original; id gerado quando o INSERT não traz; nome de 151 caracteres, CPF repetido e nome nulo recusados); as classes compilaram com javac e Lombok contra stubs das APIs, e o Service passou num teste com Repository falso. O ambiente da IA não acessa o Maven Central, então isso não substitui o meu `./mvnw`. Por mim: log da subida de 28/09/2026 às 20:12 (Spring Boot 4.1.1, Hibernate 7.4.5.Final, H2 2.4.240, JDK 25.0.1) com `Successfully validated 1 migration`, `Current version of schema "PUBLIC": << Empty Schema >>`, `Migrating schema "PUBLIC" to version "1 - create beneficiarios table"`, `Successfully applied 1 migration to schema "PUBLIC", now at version v1` e `Started ApiApplication`; `SELECT * FROM "flyway_schema_history"` com a versão 1 (`create beneficiarios table`, script `V1__create_beneficiarios_table.sql`, `success = TRUE`); no Swagger, POST sem `idBeneficiario` com 201, `location: /api/v1/beneficiarios/1` e `idBeneficiario: 1` no corpo, GET da lista 200, GET `/1` 200 e DELETE `/1` 204 sem corpo, com o log do Hibernate mostrando o INSERT com `default` em `id_beneficiario` (id gerado pelo banco), os SELECTs e o DELETE; experimento 1 (`@Column(name = "id_benef")`) derrubou a subida com `Schema validation: missing column [id_benef] in table [beneficiarios]` e `BUILD FAILURE`, desfeito com `git restore`. |
| 13 | 30/09/2026 | Etapa 3 (guia, issue #5) | Claude Opus 5.5 (claude.ai) | Pedir o guia da Etapa 3, com a Etapa 2 mergeada (ver lista abaixo). | Guia usado como roteiro de testes e decisões da etapa (armadilhas do PUT, do Swagger e do WARN de `PageImpl`, experimentos, perguntas de defesa e script de dados de teste para o H2 console); o código acabou gerado pela IA na sessão #14. Para montar o guia, a IA consultou o meu repositório (PR #11 e issue #5), a nota da Aula 05, o `docs/openapi.yaml`, o repositório do professor e o código-fonte do Spring Boot 4.1.1, do Spring Data 4.1.1 e do springdoc 3.1.0, e testou o script de dados num H2 2.4.240. | Alertas do guia conferidos na execução da etapa (ver "Como validei" da linha #14). |
| 14 | 30/09/2026 | Etapa 3 (código, issue #5) | Claude Opus 5.5 (claude.ai) | Pedir que a IA codificasse todos os arquivos da Etapa 3, para eu revisar e testar; pedir que retomasse depois de a resposta ser interrompida pelo limite de uso; enviar as respostas e os prints dos testes para conferência. | **Código da Etapa 3 copiado integralmente da resposta da IA:** `BeneficiarioRequestDTO.java`, `BeneficiarioResponseDTO.java` (substitui o `BeneficiarioDTO.java`), `BeneficiarioPatchDTO.java`, `BeneficiarioRepository.java`, `BeneficiarioService.java` e `BeneficiarioController.java`. Base: seções "Implementação dos DTOs", "Controller Completo", "Service Completo" e "Implementação: Repository com Paginação" da nota da Aula 05. O que a IA alterou em relação à nota: o PUT confere CPF duplicado excluindo o próprio registro (`existsByCpfAndIdBeneficiarioNot`), o que a nota não faz; `@Transactional` em `criar`, `atualizar` e `atualizarParcial`; `@ParameterObject` no `Pageable`; nomes explícitos no `@PathVariable` e no `@RequestParam`; Request DTO sem as anotações de Bean Validation (ficam para a Etapa 4); busca por id num método privado usado por GET, PUT e PATCH; comentários explicando as decisões. Commits feitos por mim, um por camada. | Pela IA, antes da entrega: o código compilou com o javac do JDK 21 (com `-Xlint:all`, sem avisos) contra stubs das APIs do Spring e do springdoc, com `@PageableDefault` e `@ParameterObject` copiadas dos fontes reais, e passou em 44 verificações num roteiro com Repository falso em memória. O ambiente da IA não acessa o Maven Central nem tem o Lombok, então isso não substitui o meu `./mvnw`. Por mim, em 30/09/2026, pelo Swagger e pelo navegador, com o script de dados da sessão #13: listagem, paginação e filtros por nome e CPF (roteiro L0 a L6); POST com `idBeneficiario: 2` e `dataCadastro` no corpo devolveu 201 com id gerado pelo banco e a data do dia, e a Ana (id 2) continuou intacta; POST com CPF repetido deu 500; PUT em `/2` sem telefone deu 200 com `telefone: null`, id e `dataCadastro` (2026-09-01) preservados; PUT repetido deu a mesma resposta; PUT em `/4` mantendo o próprio CPF deu 200, e com o CPF da Ana deu 500; PUT em `/99` deu 500; PATCH em `/3` só com telefone mudou só o telefone, com `null` e com `cpf` não mudou nada, e com nome vazio gravou vazio (defeito anotado para a Etapa 4); DELETE em `/9` deu 204 e o GET seguinte, 500; DELETE em `/8`, que nunca existiu, também deu 204 (defeito anotado para a Etapa 4). Log do Hibernate conferido com a IA: INSERT com `default` no id; CPF repetido barrado sem INSERT nem UPDATE; PUT com um SELECT por id, um SELECT de CPF e o UPDATE; PUT repetido e PATCH sem mudança sem UPDATE; DELETE de id inexistente sem DELETE. O cadastro recebeu o id 9 em vez do 8; a hipótese é o script de dados ter rodado duas vezes no H2 console (a IA reproduziu isso num H2 2.4.240), mas não confirmei. Os experimentos de "desligar a proteção" do guia (C4, F1 e G4) não foram executados, por decisão minha. |
| 15 | 02/10/2026 | Etapa 4 (guia, issue #6) | Claude Opus 5.5 (claude.ai) | Pedir o guia da Etapa 4, com a Etapa 3 mergeada. | Guia em Partes A a G (registro da Etapa 3, Bean Validation nos DTOs, constraint `@CPF` com o cálculo feito por mim, exceções, `GlobalExceptionHandler` com Problem Details, locale pt-BR e testes finais), com armadilhas, checkpoints e perguntas de defesa. Para montar o guia, a IA consultou o meu repositório, a nota da Aula 06, o `docs/openapi.yaml`, o repositório do professor e o código-fonte do Spring Framework 7.0.9, do Spring Boot 4.1.1, do Spring Data 4.1.1, do Hibernate Validator 9.1.3 e do springdoc 3.1.0. A etapa foi pausada no mesmo dia por causa da A1 (sessão #16). | Em andamento: os alertas do guia são conferidos na execução das Partes B a G. |
| 16 | 02/10/2026 | A1 (desafio prático individual, módulo de Produtos) | Claude Opus 5.5 (claude.ai) | Enviar o enunciado da A1 e pedir o levantamento do que criar; pedir que a IA codificasse tudo, cabendo a mim revisar, implementar e explicar o código na avaliação; avisar que o professor proibiu o Docker no meio da atividade; suporte para levar o projeto a outro PC do laboratório e conferir os testes (ver lista abaixo). | **Código da A1 copiado integralmente da resposta da IA** (22 arquivos, branch `avaliacao1`): migration `V2__create_produtos_table.sql`, `Produto`, `CategoriaProduto`, `ProdutoRepository`, DTOs com Bean Validation, a constraint `@UnidadeMedida`, as exceções, o `GlobalExceptionHandler` com Problem Details, `ProdutoService`/`ProdutoServiceImpl`, `ProdutoController` com Swagger, `messages.properties`, o locale pt-BR no `application.properties`, testes unitários e de integração e a seção da A1 no README. Depois da mudança do enunciado, a IA trocou o Testcontainers pelo H2 em memória nos testes de integração e tirou o `@Import(TestcontainersConfiguration.class)` do `ApiApplicationTests`. A declaração de uso de IA da A1 está no README da branch `avaliacao1`. Commits feitos por mim, um por camada. | Pela IA, antes da entrega: a V2 rodou num H2 2.4.240; as classes compilaram com os jars reais do Lombok, do Jakarta Validation e do Swagger, contra stubs das APIs do Spring; os testes unitários passaram com o Mockito real. Por mim, em 02/10/2026, no PC do laboratório: `./mvnw clean compile` com `BUILD SUCCESS`; `./mvnw test` com 11 testes passando (5 unitários, 5 de integração e o `contextLoads`); no Swagger, 201 com `Location` e `estoqueBaixo`, 409 para nome repetido sem diferenciar maiúsculas, 400 com a lista de campos inválidos, 400 para categoria inválida, 200 na listagem filtrada, 400 para `sort` inválido, 404 em id inexistente e 204 no DELETE. |
| 17 | 03/10/2026 | Etapa 4 (retomada, issue #6) | Claude Opus 5.5 (claude.ai) | Perguntar o melhor caminho para retomar a Etapa 4 com a `avaliacao1` ainda sem correção; pedir o passo a passo das limpezas e dos comandos Git; enviar as respostas de defesa da Etapa 3 (ver lista abaixo). | Decisão minha, com base na análise da IA: criar a branch da Etapa 4 a partir da `main` e deixar a `avaliacao1` intocada até a correção. Por orientação da IA, trouxe da `avaliacao1` com `git restore --source=origin/avaliacao1` o `GlobalExceptionHandler`, o `ProblemDetail`, a `RecursoNaoEncontradoException`, o `messages.properties` e o bloco de locale do `application.properties` (código gerado pela IA na sessão #16), e removi os dois handlers e as chaves de Produtos (commit `44d1f74`). A IA apontou erros nas minhas primeiras respostas às perguntas 3, 5 e 6 da Etapa 3, e eu as reescrevi. Este arquivo foi reorganizado pela IA a partir das sessões #13 a #17 e revisado por mim. | `grep` sem nenhuma referência às classes e chaves de Produtos; `./mvnw clean compile` com `BUILD SUCCESS` (11 arquivos-fonte); branch enviada ao GitHub com `git push -u`. |
| 18 | 03/10/2026 | Etapa 4 (código, issue #6) | Claude Opus 5.5 (claude.ai) | Perguntar o que faltava para concluir a Etapa 4 depois de trazer a infraestrutura da A1; pedir que a IA gerasse os arquivos, incluindo o cálculo do CPF; enviar os resultados dos testes para conferência (ver lista abaixo). | **Código da Etapa 4 copiado integralmente da resposta da IA:** `validation/CPF.java`, `validation/CpfValidator.java` (cálculo dos dígitos verificadores feito pela IA, e não por mim, como o roteiro previa), `exception/CpfDuplicadoException.java`, as anotações de Bean Validation no `BeneficiarioRequestDTO` e no `BeneficiarioPatchDTO`, o `@Valid` no `BeneficiarioController`, o handler de 409 no `GlobalExceptionHandler`, as exceções e o DELETE com busca prévia no `BeneficiarioService` e as chaves do Beneficiário no `messages.properties`. Molde: a `@UnidadeMedida`, a `NomeDuplicadoException` e o handler da A1, e a nota da Aula 06. Decisão registrada na `@CPF`: só 11 dígitos, sem máscara, para o banco não guardar o mesmo CPF de dois jeitos. Commits feitos por mim: `@CPF`, validação de entrada e erros 404/409. | Pela IA, antes da entrega: os DTOs, o validador e as exceções compilaram contra stubs do Jakarta Validation; o `CpfValidator` acertou 19 casos (os 7 CPFs do script de dados e o `12345678909` válidos; nulo e vazio aceitos; máscara, letras, tamanho errado, dígitos iguais e dígito trocado recusados). Service, Controller e handler não foram compilados pela IA. Por mim, em 03/10/2026: `./mvnw clean compile` com `BUILD SUCCESS` (14 arquivos-fonte); pelo Swagger e pelo navegador, com o script de dados: POST com nome vazio e CPF inválido → 400 com os dois campos em `errors`, em português; CPF com máscara → 400 só no `cpf`; CPF repetido no POST e no PUT `/4` → 409; GET, PUT, PATCH e DELETE em `/99` → 404; PUT `/2` sem nome → 400; PATCH `/3` com nome vazio → 400 e com `null` → 200 sem mudança; `/abc` → 400, `/beneficiarioss` → 404 e `sort=data_cadastro` → 400, todos em Problem Details; DELETE `/1` → 204 e o segundo → 404. No log: 409 sem INSERT nem UPDATE; PATCH com `null` só com SELECT, sem UPDATE; DELETE com SELECT e DELETE. `./mvnw test` não foi rodado: o `contextLoads` usa Testcontainers e o PC do laboratório não tem Docker. |

### Tópicos da sessão #8 (suporte)

- Uso do CMD em vez do Git Bash; comandos colados na mesma linha.
- Máquina do laboratório: decisão minha de manter o `git config --global`, removendo as credenciais do Gerenciador de Credenciais ao fim de cada sessão.
- Caractere invisível colado antes do `git`; URL do clone sem o usuário; nome do repositório digitado errado.
- Repositório clonado vazio (criado sem README): commit inicial criado e enviado por mim.
- Aviso `LF will be replaced by CRLF` (finais de linha) e erro `src refspec main does not match any` (push sem commit).
- Paginador `(END)` do `git diff`; arquivo criado como `.gitignore.md` e renomeado com `git mv`.
- Por que a pasta `docs/extensao/` precisa de um arquivo; sintaxe Markdown do README da pasta.
- Erro de digitação `.gihub/` no `cp`.
- Onde ver o vínculo entre PR e issue (seção Development); limpeza da descrição do PR.
- Conferência do `git log --graph` após o merge; mensagens de commit com erros de digitação (`shore:`, `#'`) mantidas, pois a `main` protegida não deve ter a história reescrita.

### Tópicos da sessão #10 (Etapa 1)

- JDK 25 no PC do laboratório, sem permissão para trocar: decisão minha de manter o projeto em Java 21 e compilar com o JDK 25 (o parent do Spring Boot passa `maven.compiler.release=21`).
- Conferência da tela do start.spring.io antes do GENERATE (campos Name e Description ausentes nessa versão da tela; uso do EXPLORE para ver o pom).
- Conferência da saída do `unzip`: pastas vazias (`db/migration`, `templates`, `static`) não são versionadas; `mvnw` marcado como executável com `git add --chmod=+x`.
- Configuração do `application.properties` não encontrada na nota da Aula 03; pedido de cópia do arquivo do professor (ver "Uso da saída" da linha #10).
- Leitura do log da subida e explicação dos avisos (`No migrations found`, versão do H2 acima da verificada pelo Flyway, `open-in-view`, avisos do SpringDoc).
- README: versões na tabela de stack, seção "Como executar" e correção do caminho deste arquivo (`AI_USAGE.md` na raiz, não em `docs/extensao/`).

### Tópicos da sessão #11 (Etapa 2)

- Seis commits, um por camada, em Conventional Commits; o último commit da Etapa 1 saiu sem o prefixo `docs:` e fica assim, porque a `main` não é reescrita.
- Índice extra em `cpf`: decidido antes do merge, porque depois a V1 não pode mais ser editada (o Flyway guarda o checksum).
- H2 console: `SELECT * FROM "flyway_schema_history";` precisa das aspas (o Flyway cria a tabela em minúsculas, entre aspas), e a estrutura sai com `SCRIPT NODATA TABLE beneficiarios;`, não com `SCRIPT beneficiarios;`.
- `ddl-auto=validate` no Hibernate 7.4.5: confere tabela, colunas e tipo, mas não compara tamanho nem NOT NULL; a linha `HHH000228` das notas não aparece no log.
- `idBeneficiario` no corpo do POST: o `save` faz merge em vez de INSERT (500 com id inexistente; com id existente, sobrescreve o registro e ainda devolve 201). Correção planejada para a Etapa 3.
- JSON em camelCase (record) × snake_case no `docs/openapi.yaml`: dúvida para o professor, a resolver na Etapa 5.
- `open-in-view` desligado: o Service já converte tudo para DTO; o tema volta em Doação → Doador, com `@Transactional(readOnly = true)`.
- Limpeza da branch local: `git branch -d feat/esqueleto-beckend` falhou por erro de digitação e porque eu estava na própria branch; resolvido indo para a `main` atualizada antes do `-d`.

### Tópicos da sessão #13 (Etapa 3)

- Armadilha do PUT: o `existsByCpf` puro encontra o próprio beneficiário e recusaria todo PUT que mantém o CPF; adotado o `existsByCpfAndIdBeneficiarioNot`, conferido antes de alterar a entidade.
- Swagger × `Pageable`: sem `@ParameterObject`, o springdoc 3.1.0 documenta a paginação como um JSON único com `"sort": ["string"]`, e executar o exemplo dá 500.
- WARN `Serializing PageImpl instances as-is is not supported`: mantido o formato atual, porque o `VIA_DTO` aninha os totais em `page`, remove `first` e `last` e quebraria o contrato do `docs/openapi.yaml`.
- `@Transactional` nos métodos de escrita: sem transação, o PUT relê a entidade no `merge`; com transação, ela continua gerenciada e o Hibernate detecta a mudança sozinho (dirty checking).
- Script de dados de teste para o H2 console (fora do repositório, CPFs com dígitos válidos), para repovoar o banco em memória a cada subida.
- Anotado para a Etapa 4: CPF duplicado (500 → 409), id inexistente (500 → 404; DELETE 204 → 404), PUT sem campos obrigatórios (500 → 400), PATCH com nome vazio, `sort` por propriedade inexistente (500 → 400) e CPF com e sem máscara. Para a Etapa 5: o contrato não lista 409 no PUT.
- Suporte durante a sessão #14: `git add` rodado na pasta errada, caminho de pacote digitado errado, `Closes #5` (e não `#%`) na descrição do PR, e o PR que parecia ter sumido (só a faixa "Compare & pull request" tinha saído da tela; o PR ainda não existia).

### Tópicos da sessão #16 (A1)

- Pontos do enunciado discutidos antes de codificar: `@Min(0)` no estoque impediria o 422 da regra de negócio; `@NotNull` faltando no exemplo; `@Enumerated(EnumType.STRING)` para a categoria.
- Mudança do enunciado no meio da atividade: sem Docker, então testes de integração com o H2 em memória (`@SpringBootTest` com porta aleatória) e sem o `TestcontainersConfiguration` no `ApiApplicationTests`.
- Migration numerada V2 (próximo número livre deste repositório), e não V4, como no repositório do professor.
- Branch criada como `avaliacao1`; o enunciado escreve `avalicao1`. O commit `feat(ptodutos)` saiu com erro de digitação e ficou assim, para não reescrever o histórico enviado.
- Troca de PC no laboratório: clone, `git config --global`, reaplicação dos arquivos e remoção de uma pasta vazia chamada literalmente `{service,controller}`.
- PR #13 aberto sem merge, para a `main` só receber a A1 depois da correção.

### Tópicos da sessão #17 (Etapa 4, retomada)

- Por que não criar a branch da Etapa 4 a partir da `avaliacao1`: o PR levaria os 8 commits de Produtos para a `main`, e o GitHub marcaria o PR #13 como mergeado antes da correção.
- Cuidados até a correção: não fazer commit, merge nem rebase na `avaliacao1`; não usar o botão "Resolve conflicts" do PR #13, que criaria um commit nela; evitar duas migrations V2 (o Git não acusa, mas o Flyway não sobe).
- Perguntei se podia manter na Etapa 4 os handlers e as chaves de Produtos; decidi removê-los, porque o handler não compilaria sem as exceções de Produtos, ficaria código morto fora do escopo da issue #6, e esses trechos voltam com o merge da A1.
- Branch criada como `fear/...` por erro de digitação e renomeada com `git branch -m` antes do primeiro push.
- Caractere invisível colado antes do `git` (`$'\302\203git': command not found`), resolvido digitando o comando.
- Diferença entre `git commit` (repositório local, só neste PC) e `git push` (GitHub), e por que fazer push antes de sair do laboratório.
- Experimentos pendentes da Etapa 3 (C4, F1 e G4) e linha de base B1 a B6: decidi não executar.

### Tópicos da sessão #18 (Etapa 4, código)

- O que faltava depois da infraestrutura da A1: validação nos DTOs, `@Valid`, `@CPF`, 409 do CPF, 404 no lugar das `RuntimeException` e DELETE com busca prévia.
- PATCH enviado como `{"nome": "null"}`, com aspas: a API gravou o texto "null". O teste certo é o `null` do JSON, sem aspas, que não altera nada.
- PUT `/99` com corpo inválido deu 400, e não 404: o `@Valid` roda antes de o Service procurar o id. Com corpo válido, deu 404.
- `sort=data_cadastro`: o `detail` cita `"data"`, porque o Spring Data lê o `_` como navegação entre atributos e para no primeiro pedaço inexistente.
- WARN do springdoc ao abrir o Swagger (`Json Processing Exception ... HashSet ... 'integer'`): não afeta as respostas; fica para a Etapa 5.
- Erros de terminal: `git add` rodado dentro de `backend/` e nome de pasta digitado errado (`beneficarios`); uso do Tab para completar caminhos.
- Anotado para a Etapa 5: o contrato aceita CPF com 14 caracteres (`\d{14}`) e não lista 400 nem 409 no PUT e no PATCH. Dois POSTs simultâneos com o mesmo CPF ainda passam pelo `existsByCpf`, e o segundo cai no UNIQUE com 500 genérico.

### Perguntas de defesa — Etapa 0 (respostas minhas)

1. Por que `docs/extensao/` precisou de um arquivo dentro? O Git não versiona pasta vazia.
2. O que o `Closes #1` fez? Fechou a issue automaticamente no merge do PR.
3. Merge commit × squash: o squash junta os commits pequenos em um só; o merge commit os preserva na linha do tempo do desenvolvimento.

### Perguntas de defesa — Etapa 1 (respostas minhas)

1. Por que `ddl-auto=validate`, e não `update`, com Flyway? Porque o Flyway é responsável por criar e alterar a estrutura do banco, enquanto o Hibernate apenas valida se as entidades estão de acordo com as tabelas existentes.
2. Projeto em Java 21 rodando com JDK 25: o JDK 25 é compatível com versões anteriores do Java, permitindo executar aplicações compiladas para versões anteriores.
3. Prova de que o Flyway roda: ao acessar o H2, é possível verificar a tabela `flyway_schema_history`, criada e utilizada pelo Flyway para registrar as migrations futuras.
4. Por que `git add --chmod=+x` no `mvnw`? Porque o `mvnw` é um script que precisa ter permissão de execução. O comando faz o Git registrar o arquivo com essa permissão, permitindo executá-lo diretamente, principalmente em ambientes Linux/Unix e no CI/CD.

### Perguntas de defesa — Etapa 2 (respostas minhas)

1. O que acontece na subida se SQL e Entity divergirem? E o que o `validate` não confere? Se o banco estiver diferente do que a Entity espera, por exemplo com uma coluna faltando ou com tipo incompatível, o ddl-auto=validate identifica a divergência durante a inicialização e impede a aplicação de subir. Porém, ele não confere algumas características como o tamanho da coluna (length × VARCHAR) e NOT NULL. Nesses casos, se houver divergência, a aplicação pode subir normalmente e o erro aparecer somente quando tentar fazer o INSERT.
2. Se o Boot já converte `idBeneficiario` em `id_beneficiario` sozinho, por que escrever o `@Column(name = …)`? Para deixar explícito qual é o nome da coluna no banco e não depender da estratégia de nomenclatura automática do Spring Boot. Além disso, sem essa estratégia, como em um Hibernate fora do Spring Boot, o nome padrão poderia ser idBeneficiario, fazendo a validação falhar.
3. Por que o Service recebe o Repository pelo construtor, e o que isso permite na Etapa 6? Porque o Service recebe o Repository por injeção de dependência, em vez de criar essa dependência diretamente. Na Etapa 6, que trata de testes, isso permite montar o Service em um teste unitário passando um Repository falso, usando mock do Mockito, sem precisar de banco de dados ou subir o Spring.
4. Por que o POST devolve 201 com `Location`, e não 200? E o que acontece se o corpo trouxer um `idBeneficiario`? O 201 Created indica que um novo recurso foi criado, e o Location informa a URI desse recurso. Porém, com o código atual, se o corpo trouxer um idBeneficiario, o toEntity copia esse ID e o save pode fazer um merge em vez de um INSERT. Se o ID não existir, o POST pode resultar em 500; se já existir, o registro existente pode ser sobrescrito, mesmo retornando 201. A correção vem na Etapa 3, usando um Request DTO sem o ID, para que o cliente não envie o identificador.

### Perguntas de defesa — Etapa 3 (respostas minhas)

> As respostas 3, 5 e 6 foram reescritas por mim em 03/10/2026, depois de a IA apontar erros nas primeiras versões (sessão #17).

1. Por que o `Request` não tem `id` nem data de cadastro? Porque o `Request` representa os dados que entram na API, e não a entidade completa do banco. `id` e `dataCadastro` são campos gerados/controlados pelo servidor, então não devem ser enviados pelo cliente. Eles pertencem à `Entity`/`Response`, não ao `Request`.
2. Por que o PUT não usa o `existsByCpf` puro, e por que o `UNIQUE` continua? No PUT, o CPF do próprio registro já existe. Então um `existsByCpf(cpf)` simples retornaria `true` mesmo quando o CPF não está sendo alterado, gerando falso conflito. Por isso é necessário verificar se existe outro registro com aquele CPF, excluindo o próprio `id`. O `UNIQUE` continua porque a validação da aplicação não substitui a restrição do banco. O `UNIQUE` é a garantia definitiva de integridade, inclusive contra concorrência entre duas requisições simultâneas.
3. Como se apaga o telefone: com PUT ou com PATCH? Nesta API, o telefone é apagado com PUT, e não com PATCH. No método `atualizarParcial`, cada campo só é alterado quando o valor recebido não é `null`, como em `if (dto.telefone() != null)`. Portanto, enviar `"telefone": null` no PATCH significa não alterar o telefone. Enviar `""` também não apaga, apenas grava uma string vazia. Já no método `atualizar`, usado pelo PUT, os cinco campos são copiados para a entidade sempre, inclusive quando o valor é `null`, como em `beneficiario.setTelefone(dto.telefone())`. Assim, um PUT sem telefone no JSON pode deixar o telefone como `null`, apagando o valor existente. Isso também está indicado no comentário da linha 74.
4. O PATCH é idempotente? E o DELETE, quando a segunda chamada der 404? Depende da implementação. Um PATCH que simplesmente define `telefone = null` é idempotente: executar uma ou várias vezes produz o mesmo estado final. O DELETE também é considerado idempotente quanto ao estado do recurso, mesmo que a segunda chamada responda `404`. A primeira remove o recurso; a segunda não muda o estado porque ele já não existe. O código HTTP pode mudar, mas isso não elimina a idempotência da operação.
5. Por que `Page`, e por que não `VIA_DTO` apesar do WARN? `Page` é usado porque a consulta é paginada e ele carrega não apenas os registros, mas também informações como página atual, quantidade de elementos e total de páginas. O motivo de não usar `VIA_DTO` não é simplesmente porque o aviso é um WARN. O aviso informa que a serialização direta de `PageImpl` não garante o mesmo formato entre versões e sugere o modo `VIA_DTO`. O problema é que `VIA_DTO` altera o formato do JSON. Com ele, os metadados como `size`, `number`, `totalElements` e `totalPages` ficam dentro de um objeto `page`, enquanto campos como `first`, `last` e `pageable` deixam de aparecer. Como o `docs/openapi.yaml` já descreve o formato atual da resposta, mudar para `VIA_DTO` quebraria o contrato da API. Por isso, a decisão foi manter o formato atual definido no contrato e aceitar conscientemente o WARN. Isso não tem relação com projeção ou DTO de consulta.
6. Por que há dois `SELECT`s sem `@Transactional`? Não são duas leituras independentes: é o mesmo beneficiário sendo lido duas vezes. O primeiro `SELECT` acontece no `buscarEntidadePorId`, através do `findById`. Como o método não possui `@Transactional`, essa operação ocorre em uma transação própria. Quando o `findById` termina, o contexto de persistência é encerrado e a entidade fica desanexada (detached). Depois, no `save`, como a entidade já possui um `id`, o Spring Data utiliza o `merge`. Para realizar o merge, o Hibernate precisa buscar novamente o registro no banco para obter uma cópia gerenciada, gerando o segundo `SELECT`. Com `@Transactional`, o método inteiro utilizaria o mesmo contexto de persistência. A entidade continuaria gerenciada durante a operação e o `merge` não precisaria fazer esse segundo `SELECT`. O `SELECT` feito por `existsByCpfAndIdBeneficiarioNot` é um terceiro SELECT, separado dessa sequência.

## Prompts na íntegra

**#1 — 25/09/2026** (com o zip das anotações de aula anexado)

> Analise todas as anotações de aula do professor e me explique a estrutura, repositórios, tecnologias, tudo envolvido nessa demanda.
>
> Preciso que você estruture o passo a passo de como avançar, implementação a implementação ciente de que já passamos do meio do projeto e eu ainda não iniciei o meu. Não quero que você faça por mim quero que você me guie, ensine, e estruture o projeto.

**#2 — 25/09/2026**

> Perfeito, agora vamos para execução, vamos começar na Etapa 0 e ir avançando nos próximos dias. Uma dúvida. Precisa ler algo mais como o repositório do professor por exemplo? E eu sei que preciso documentar todas atuações da IA, então vá registrando nosso caminho pois faz parte da avaliação.

**#3 — 25/09/2026**

> Desculpe, como rodar git config --global se ainda não possuo repositório criado? Vamos pra cada passo exaustivamente explicado em detalhes pra que eu tenha não só um roadmap mas que tenha o passo a passo de cada ação para construir o projeto.

**#4 — 25/09/2026** (com print da tela Settings do GitHub anexado)

> depois de entrar em settings não achei Branches, nem Add classic branch protection rule, passo B3

**#5 — 25/09/2026**

> Esse md de AI_USAGE você está registrando tudo né? Ou eu quem preciso registrar? Se possível vai registrando meu trabalho e organizando as informações pra me ganhar tempo.

**#6 — 25/09/2026** (com print da tela de criação da issue anexado)

> No passo B4 é isso que preciso fazer?

**#7 — 25/09/2026**

> Desculpe, não entendi, no passo B4 é da maneira que está na imagem que preciso criar a ISSUE?

**#8 — 25/09/2026** — prompts de suporte resumidos na lista "Tópicos da sessão #8".

**#9 — 25/09/2026**

> Eu não sei os próximos passos pra criar 07 issues do que faremos

> Acho que a issue etapa 1 ficou sem minha imagem como owner

**#10 — 26/09/2026** — prompts principais abaixo; os demais (conferência de saídas, prints e dúvidas pontuais) estão resumidos em "Tópicos da sessão #10".

> Vamos começar a Etapa 1 agora?

> Parte A, A1 a versão do Java e 25 e não tem como trocar nesse pc da faculdade, não tenho acesso, seguiremos assim, com ess aversão, não tem problema.

> Parte G, G1 eu não encontrei os dados de application.properties no arquivo aula03.html pra copiar e colar na minha application.properties

> Pesquise no zip que te enviei do projeto do professor e coloca aqui pra copiar e colar, meu projeto não muda, o que tem no dele vai ter no meu.

**#11 — 28/09/2026**

> Vamos para a Etapa 2 (issue #4), Beneficiário: primeira fatia vertical. A Etapa 1 foi mergeada.

> Não consigo limpar a branch: (saída do terminal com `error: branch 'feat/esqueleto-beckend' not found`)

**#12 — 28/09/2026** (com o zip do projeto anexado — meu repositório e o do professor — e o guia da sessão #11 colado)

> Codifique as telas necessárias nesse projeto. Para isso vou te mandar o estágio atual do projeto via github e o que preciso fazer nessa etapa 2, codifique tudo.

**#13 — 30/09/2026**

> Vamos para a Etapa 3 (issue #5), Etapa 3 — Beneficiário: CRUD completo e semântica HTTP A Etapa 2 foi mergeada.

**#14 — 30/09/2026** — prompts principais abaixo; o envio dos prints de teste e as dúvidas de Git estão nos tópicos da sessão #13.

> Codifique todos os arquivos, irei revisá-los e testa-los

> Os limites acabaram mas você já fez boa parte, retome de onde acabou

**#15 — 02/10/2026**

> Vamos para a Etapa 4 (issue #6), Etapa 4 — Beneficiário: validação, erros e i18n A Etapa 2 foi mergeada.

**#16 — 02/10/2026** (com o enunciado da A1 anexado) — prompts principais abaixo; o suporte à troca de PC e a conferência dos testes estão nos tópicos da sessão #16.

> Antes de seguirmos com a a Etapa 4, tenho essa avaliação em anexo pra fazer, leia e me diga o que preciso criar pra que possamos cumprir a atividade e depois retomar as etapas do projeto, se tiver etapas obrigatórias podemos fazê-las ainda hoje para podermos estarmos prontos pra fazer a avaliação, se pudermos já seguir pra avaliação melhor ainda.

> Ok, li a estrutura e os planejamentos, codifique tudo, gere todos os códigos pois o meu papel é de revisor dos códigos e implementador, os códigos posso explicá-los como critério da avaliação. Sim possui Docker aqui, gere todo o passo a passo em detalhes.

> Não é pra usar docker, professor mudou o trabalho no meio, retome o que já fez pra não gastar token e vamos focar só na avaliação, exclusivamente

**#17 — 03/10/2026** — prompts principais abaixo; os demais estão nos tópicos da sessão #17.

> Durante a Etapa 4 tivemosmos que fazer a branch de avaliacao1. Precisamos retomar o que falta da Etapa 4 que ainda não foi implementado. Como a branch de avaliacao1 ainda não foi corrigida qual o melhor caminho? Devemos criar a branch da etapa 4 e ir mergeando as etapas e deixando a branch de avaliacao1 de lado por hora?

> Agora me dê mais detalhes a respeito das limpezas

> Não posso deixar esses trechos? Afinal vou usá-los depois, o que acha?

> Pular os experimentos (seguido das respostas às perguntas de defesa da Etapa 3)

**#18 — 03/10/2026** — prompts principais abaixo; o envio dos resultados de teste está nos tópicos da sessão #18.

> Mano to fazendo a Etapa 4, quero concluir logo, já peguei os códigos da AV1 o que falta?

> Pode gerar

> O do CPF fiz sim ta colado ai, e os dos /99 fiz todos, revise

---

_Declaração: Ao submeter este repositório, confirmo que todo o código foi
revisado, testado e compreendido por mim._
