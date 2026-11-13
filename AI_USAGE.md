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
- Como regra, o código e os artefatos do repositório são escritos por mim. Quando uma saída da IA for copiada para o repositório, isso é declarado na linha da sessão, com o arquivo afetado e o que a IA alterou (casos: `application.properties`, sessão #10; código da Etapa 2, sessão #12; código da Etapa 3, sessão #14; código da A1, sessão #16, parte dele reaproveitada na Etapa 4, sessão #17; código da Etapa 4, sessão #18; código da Etapa 5, sessões #19 e #20; código da Etapa 6, sessão #21; código da Etapa 7, sessão #22; código da Etapa 8, sessão #24; código da Etapa 9, sessão #25).
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
| Etapa 4 — Beneficiário: validação, erros e i18n | #15, #17 e #18 | Concluída: PR #14 mergeado em 03/10/2026 (`Closes #6`): Bean Validation nos DTOs com `@Valid`, constraint `@CPF`, erros em Problem Details (400, 404, 409 e 500 genérico) e mensagens em pt-BR; infraestrutura de erros e i18n reaproveitada da A1 |
| Etapa 5 — Documentação Swagger | #19 e #20 | Concluída no PR com `Closes #7`: endpoints, DTOs e erros de beneficiário documentados no Swagger, com exemplo por erro, e contrato `docs/openapi.yaml` alinhado à API; springdoc mantido no 3.1.0, com o WARN `Json Processing Exception` registrado como problema conhecido |
| Etapa 6 — Testes unitários e de integração | #21 | Concluída no PR com `Closes #8`: 12 testes unitários do `BeneficiarioService` (JUnit, Mockito e AssertJ), 8 testes de integração da API (porta aleatória, `RestClient` e H2) e o `contextLoads` sem Docker, todos no perfil `test`; CPF repetido por corrida entre requisições passa a responder 409, e não 500. Critérios da issue adaptados ao laboratório sem Docker: H2 no lugar do PostgreSQL 17 via Testcontainers e `RestClient` no lugar do MockMvc |
| Mapeamento das Aulas 09 e 10 e do edital da A2 | #22 | Issue #9 (Doador e Doação) dividida em seis: a #9 reescrita como Etapa 7 (Doador) e cinco issues novas para as Etapas 8 a 12 (Doação em duas partes e três de front-end) |
| Etapa 7 — Doador: cadastro e consulta | #22 e #23 | Concluída no PR com `Closes #9`: migration V3 `doadores` (a V2 fica para `produtos` da A1) com CHECK do tipo e índice `idx_doadores_nome`, enum `TipoDoador` gravado como texto, constraint `@Telefone`, POST (201 + `Location`), GET paginado em ordem de nome e GET por id (200 ou 404), Swagger e `docs/openapi.yaml` da parte de Doadores; 28 testes novos (5 unitários do `DoadorService`, 18 execuções da `@Telefone` e 5 de integração), 49 no total, sem Docker. Fora do escopo: PUT, PATCH e DELETE de doador |
| Etapa 8 — Doação: registro, listagem com filtros e consulta | #24 | Concluída no PR #23 (`Closes #17`): migration V4 `doacoes` (FK `fk_doacoes_doador` criada depois dos índices, CHECK do valor e do tipo, índices em `data_doacao`, `tipo` e `id_doador`), `Doacao` com `@ManyToOne` LAZY para `Doador`, `TipoDoacao` gravado como texto, `GET` paginado (size 10, mais recentes primeiro, desempate por `idDoacao`) com filtros `dataInicio`, `dataFim` e `tipo` combináveis por `Specification` (período invertido → 400; tipo inválido → 400 com os valores aceitos), `POST` (201 + `Location`, resposta com `descricao`), `GET` por id (200 ou 404), `@DataNaoFutura` em TDD (RED `c40d8ac`, GREEN `4717962`, REFACTOR `714c63f`), doador inexistente → 422 (`docs/adr/0001-doador-inexistente.md`), Swagger e `docs/openapi.yaml` de Doações em camelCase; mensagens de 404 e de ordenação inválida generalizadas; 28 testes novos (8 do `DoacaoServiceTest`, 8 da `@DataNaoFutura` e 12 de integração, incluindo os 3 do dojo), 77 no total, sem Docker. Fora do escopo: PUT, PATCH e DELETE de doação (Etapa 9) |
| Etapa 9 — Doação: alterar e remover (PUT, PATCH e DELETE) | #25 | Concluída no PR com `Closes #18`: PUT com substituição total e as regras do POST (mesmo `DoacaoRequestDTO`, 200), PATCH com o novo `DoacaoPatchDTO` (só os campos não nulos mudam; `idDoador` conferido se vier; descrição em branco apaga a descrição; o valor só se apaga pelo PUT) e DELETE com busca antes (204; o segundo, 404); ordem dos erros 400 → 404 → 422, com o doador conferido antes de qualquer setter; 422 estendido ao PUT e ao PATCH no ADR 0001, com a mensagem generalizada; Swagger e `docs/openapi.yaml` com as três operações e o schema `DoacaoPatch`; 18 testes novos (9 do `DoacaoServiceTest` e 9 de integração), 95 no total, sem Docker. Fora do escopo: a idempotência do POST com `Idempotency-Key` (item opcional da #18) |

## Registro

| # | Data | Aula / Etapa | Ferramenta | Prompt (resumo) | Uso da saída | Como validei |
|---|------|--------------|------------|-----------------|--------------|--------------|
| 1 | 25/09/2026 | Planejamento (material das Aulas 03–07) | Claude Opus 5.5 (claude.ai) | Analisar as anotações de aula e os templates (zip anexado) e explicar estrutura, repositórios e tecnologias; montar um passo a passo para iniciar o projeto, com orientação em vez de código pronto. | Usado como referência: roteiro em etapas (0 a 7) adotado como plano de trabalho. Para responder, a IA também leu o repositório público do professor e consultou documentação do Spring Boot 4 e do Testcontainers na web. | Alertas do Flyway e do H2 console confirmados na Etapa 1 (sessão #10): com `spring-boot-starter-flyway` o log mostra o Flyway executando, e com `spring-boot-h2console` o console abre. Alerta do Testcontainers conferido em parte na Etapa 6 (sessão #21): o pom usa os artefatos `testcontainers-*` sem versão (Testcontainers 2.0.5, gerenciado pelo Boot 4.1.1), e o `application-test.properties` ficou em `src/test/resources`, carregado pelo perfil `test` (o log do `./mvnw test` mostra `The following 1 profile is active: "test"`); o do professor está em `src/test/java`, fora do classpath. A parte do Docker não foi conferida: o laboratório não tem Docker, e os testes de integração rodam no H2. |
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
| 19 | 03/10/2026 | Etapa 5 (código, issue #7) | Claude Opus 5.5 (claude.ai) | Pedir a conferência do repositório (`main` e `avaliacao1`), o que faltava na Etapa 5 e o código para eu revisar, testar e commitar, com as pendências anotadas (CPF no contrato, 400 e 409 faltando, camelCase × snake_case, WARN do springdoc). | **Código da Etapa 5 copiado integralmente da resposta da IA** (reentregue como arquivos inteiros na sessão #20): anotações do Swagger no `BeneficiarioController` (`@Tag`, `@Operation` com os `operationId` do contrato, `@ApiResponse` por status com `ProblemDetail` e exemplo, `@Parameter`), o novo `BeneficiarioExemplos`, os `@Schema` dos três DTOs (com os nomes de schema do contrato), os exemplos do `ProblemDetail`, o Javadoc da `@CPF`, a linha `springdoc.default-produces-media-type` do `application.properties` e o `docs/openapi.yaml` alinhado. Molde: `ProdutoController` e DTOs da A1, lidos da `avaliacao1` sem trazer código de Produtos, e a nota da Aula 06. Decisões tomadas com base na análise da IA: JSON em camelCase com o contrato alinhado, CPF só com 11 dígitos no contrato, 500 documentado só no Swagger, PATCH sem 409. A IA também recomendou o springdoc 3.1.1 para corrigir o WARN; não adotei (sessão #20). Para responder, a IA consultou o meu repositório, a issue #7, o repositório do professor, as notas das Aulas 05 e 06 e o código-fonte do springdoc 3.1.0 e 3.1.1, do swagger-core 2.2.55 e do swagger-ui 5.32.14. | Pela IA, antes da entrega: leitura do código-fonte para confirmar a causa do WARN, o nome `PageBeneficiarioResponse`, o preenchimento das respostas sem `content` com o tipo de retorno e a validação de inteiros do Swagger UI; o código não foi compilado pela IA. Por mim, em 24/10/2026 (sessão #20), no PC do laboratório: `./mvnw clean compile` com `BUILD SUCCESS` (14 arquivos-fonte depois dos DTOs, 15 depois do controller); pelo Swagger, com o banco vazio: POST válido → 201 (id 1) e o mesmo POST → 409; CPF com máscara e nome vazio → 400 com `cpf` e `nome` em `errors`; GET `/99` → 404; GET `/99999999999999999999` → 400 `parametro-invalido`; `sort=idade` → 400 `ordenacao-invalida`; PATCH `/1` com nome `"   "` → 400 só com `nome`; DELETE `/2` antes de existir → 404; POST com o CPF `12345678909` → 201 com `location: /api/v1/beneficiarios/2`; PUT `/2` com o CPF do id 1 → 409; DELETE `/2` → 204. No log: o 409 sem INSERT, e o 400 do id e o do PATCH sem nenhum SELECT. `git diff` do `docs/openapi.yaml` conferido linha a linha (49 inserções e 12 remoções, só na parte de Beneficiários). |
| 20 | 24/10/2026 | Etapa 5 (retomada e execução, issue #7) | Claude Opus 5.5 (claude.ai) | Perguntar se algo da sessão #19 já tinha sido feito e como garantir que o projeto no PC estava atualizado; perguntar se o WARN do springdoc precisava sumir; pedir os passos para resolver a issue; suporte de terminal e conferência dos testes (ver lista abaixo). | Usado como referência: a IA conferiu o GitHub (`main` em `312ae6b`, nenhuma branch nova nem PR depois do #14, nada da Etapa 5 no código, repositório do professor no mesmo commit `286d78b`) e passou o roteiro de conferência do PC. Decisão minha, depois de a IA explicar que o WARN não é critério da issue nem muda as respostas da API: manter o springdoc 3.1.0. A IA reentregou o código da sessão #19 como arquivos inteiros (blocos `cat` para o Git Bash), sem a troca de versão, e organizou a etapa em quatro commits; este arquivo foi atualizado pela IA e revisado por mim. | Conferência do PC antes de começar: remoto certo; nenhuma mudança, stash ou commit fora do GitHub; `main` em `312ae6b` e `up to date with 'origin/main'`; identidade do Git conferida; Java 25.0.1 e `./mvnw clean compile` com `BUILD SUCCESS` (14 arquivos-fonte). Testes da etapa: ver "Como validei" da linha #19. |
| 21 | 26/10/2026 | Etapa 6 (código, issue #8) | Claude Opus 5.5 (claude.ai) | Pedir a conferência do repositório (`main`, `avaliacao1` e o do professor), o que faltava na issue #8 e o código dos testes para eu revisar, testar e commitar, com os testes da A1 como molde e as pendências anotadas (`contextLoads` dependente do Testcontainers, corrida no CPF, WARN do springdoc). | **Código da Etapa 6 copiado integralmente da resposta da IA:** `BeneficiarioServiceTest.java` (12 testes), `BeneficiarioControllerIntegrationTest.java` (8 testes), `src/test/resources/application-test.properties` (perfil `test`), o `ApiApplicationTests` sem o `@Import(TestcontainersConfiguration.class)` e com `@ActiveProfiles("test")`, o método `salvar` do `BeneficiarioService` (`saveAndFlush` dentro de um `try`, com a `DataIntegrityViolationException` virando `CpfDuplicadoException`) e as linhas de testes do README. Molde: `ProdutoServiceTest` e `ProdutoControllerIntegrationTest` da A1, lidos da `avaliacao1` com `git show`, sem trazer código de Produtos; os testes de beneficiário do professor; a nota da Aula 07 e o roteiro do dojo. O que a IA alterou em relação ao molde: tabela limpa antes de cada teste de integração (`deleteAll`), no lugar de um nome diferente por teste; perfil `test`; no teste do POST, o `saveAndFlush` simulado devolve outro objeto, para o `ArgumentCaptor` ver a entidade como o Service a enviou (no molde, o `thenAnswer` alterava o próprio argumento, e o captor via o id já preenchido). Decisões tomadas com base na análise da IA: H2 e `RestClient` no lugar do PostgreSQL 17 via Testcontainers e do MockMvc pedidos na issue; `TestcontainersConfiguration` e `TestApiApplication` mantidos como o Initializr gerou. Para responder, a IA consultou o meu repositório, o do professor, as notas da Aula 07 e o código-fonte do Spring Framework 7.0.9, do Spring Data 4.1.1 e do Spring Boot 4.1.1. | Pela IA, antes da entrega: os 12 testes unitários passaram com JUnit 5.10, Mockito 2.23 e AssertJ 3.14 reais (pacotes do Ubuntu), com o Lombok real e stubs das APIs do Spring; com defeitos colocados de propósito no Service (gravação fora do `try`, verificação de CPF depois dos setters no PUT, PATCH aplicando nulo e prioridade dos filtros invertida), o teste correspondente falhou; os CPFs usados nos testes passaram no `CpfValidator`. O teste de integração só foi compilado, contra assinaturas copiadas do código-fonte do Spring, e não rodou, porque o ambiente da IA não acessa o Maven Central. Por mim, em 09/11/2026, no PC do laboratório, sem Docker: `./mvnw clean test` com `Tests run: 21, Failures: 0, Errors: 0, Skipped: 0` e `BUILD SUCCESS` (1 teste no `ApiApplicationTests`, 8 no `BeneficiarioControllerIntegrationTest` e 12 no `BeneficiarioServiceTest`); no log, o perfil `test` ativo, a V1 aplicada no H2 e o WARN de `PageImpl` só uma vez, no teste da listagem. |
| 22 | 09/11/2026 | Mapeamento das Aulas 03–10 e Etapa 7 (código, issue #9) | Claude Opus 5.5 (claude.ai) | Enviar o zip com os materiais do Teams e pedir: o mapeamento, aula por aula, do que o professor pediu e ainda não está no repositório (incluindo as avaliações A2 a A4); a comparação com a issue #9 e a proposta de mantê-la, mudá-la ou dividi-la, com títulos e critérios prontos para colar; só depois, o passo a passo e o código da primeira issue. | Mapeamento usado como referência: são novos em relação ao projeto a Aula 09 (CORS, front-end React + TypeScript com Vite e Axios), a Aula 10 (MUI, React Hook Form + Zod, acessibilidade) e o edital da A2 (seminário em grupo); não há material das Aulas 11 a 20 nem edital da A3 ou da A4. A IA também apontou problemas nas Doações do repositório do professor, que decidi não copiar (ver tópicos). Decisão minha, com base na proposta da IA: dividir a #9 em seis issues. O texto da nova #9 (Etapa 7, Doador) e das cinco issues novas (Etapas 8 a 12) foi redigido pela IA, revisado por mim e colado no GitHub. **Código da Etapa 7 copiado integralmente da resposta da IA** (zip `etapa7-doador.zip`, 17 arquivos): `V3__create_doadores_table.sql`, `Doador`, `TipoDoador`, `DoadorRepository`, `DoadorRequestDTO`, `DoadorResponseDTO`, `validation/Telefone.java`, `validation/TelefoneValidator.java`, `DoadorService`, `DoadorController`, `DoadorExemplos`, `DoadorServiceTest`, `TelefoneValidatorTest`, `DoadorControllerIntegrationTest` e as alterações no `messages.properties`, no comentário do `ApiApplicationTests` e no `docs/openapi.yaml`. Base: a dinâmica da Aula 04 (migration e `Doador.java`), o contrato `docs/openapi.yaml`, a `@Telefone` do repositório do professor (Aula 06) e o molde do Beneficiário e da Etapa 6. O que a IA alterou em relação às bases: tabela na V3, e não na V2; CHECK com os valores do enum e índice em `nome`; `@Telefone` só com dígitos e conferindo o início do número (2 a 5 no fixo, 9 no celular), como o Zod da Aula 10, enquanto a do professor aceitava máscara e só recusava 0 e 1; e-mail e telefone em branco gravados como nulos; GET por id, que o contrato não tinha, para o `Location` do POST apontar para um endereço que existe; `enumAsRef` no `TipoDoador`, para o Swagger publicar o schema separado, como no contrato. A IA também salvou no Projeto do Claude as Aulas 09 e 10 e o edital da A2 em texto. Para responder, consultou os materiais, o meu repositório (a `avaliacao1` só lida), o do professor e o código-fonte do springdoc 3.1.0 e do swagger-core 2.2.52. | Pela IA, antes da entrega: o código novo e o de beneficiário compilaram com o Lombok e as anotações reais do Swagger e do Jakarta Validation, com o Spring só como esqueleto das APIs; 35 testes unitários passaram com JUnit, Mockito e AssertJ reais (5 do `DoadorServiceTest`, 18 do `TelefoneValidatorTest` e 12 do `BeneficiarioServiceTest`); 7 defeitos colocados de propósito no Service e no validador fizeram falhar ao menos um teste cada; a V3 rodou num H2 2.2.220, e o CHECK, o NOT NULL e os tamanhos recusaram o que deviam; o zip foi aplicado num clone limpo da `main` e a sequência de commits deixou o `git status` vazio. O `DoadorControllerIntegrationTest` só foi compilado, porque o ambiente da IA não acessa o Maven Central. Por mim: ver "Como validei" da linha #23. |
| 23 | 09/11/2026 | Etapa 7 (execução, issue #9) | Claude Opus 5.5 (claude.ai) | Pedir a conferência do repositório e do zip da sessão #22 antes de aplicar (a `main` ainda em `621fe37`? o zip bate com a #9 como está no GitHub?), o roteiro de execução (zip, testes, Swagger, commits, push e PR com `Closes #9`) e, depois do resultado dos testes, este arquivo com as sessões #22 e #23 e as perguntas de defesa da Etapa 7. | Usado como referência: a IA conferiu o GitHub (`main` em `621fe37`, `avaliacao1` intacta em `5e68c1b`, nenhuma branch nova, último PR o #16, repositório do professor ainda em `286d78b`) e a página pública da #9: o corpo bate com o zip, mas o título e a milestone A1 ainda eram os antigos. A IA passou o hash SHA-256 do zip conferido, o `git status` esperado depois do `unzip`, a contagem esperada de testes por classe, o roteiro do Swagger, a sequência de 7 commits da sessão #22 e o texto do PR. Nenhum código novo nesta sessão; este arquivo foi atualizado pela IA e revisado por mim. | Pela IA, antes do roteiro: o zip tem 17 arquivos em UTF-8 e LF, sem tabs, e os 3 alterados só acrescentam linhas; o `docs/openapi.yaml` abre como YAML válido, sem referência quebrada; os 23 testes unitários do doador e da `@Telefone` passaram de novo com JUnit, Mockito, AssertJ e Lombok reais, e 3 defeitos colocados de propósito foram pegos; a V1 e a V3 rodaram num H2 2.2.220 (o CHECK recusou `ONG` e `pessoa_fisica`, o banco recusou nome e tipo nulos, nome com 201 caracteres e e-mail com 255, e o plano da listagem usou o `idx_doadores_nome`); os 7 commits foram ensaiados numa cópia limpa da `main`. Sem Maven Central, o `contextLoads` e os testes de integração não rodaram no ambiente da IA. Por mim, em 09/11/2026, no LAB03-PC03, sem Docker: hash do zip igual ao conferido pela IA (`b6494d7d…950e`); depois do `unzip -o`, 3 arquivos alterados e 6 novos no `git status --short`; `./mvnw clean test` com `Tests run: 49, Failures: 0, Errors: 0, Skipped: 0` e `BUILD SUCCESS` em 23,7 s (1 no `ApiApplicationTests`, 8 no `BeneficiarioControllerIntegrationTest`, 12 no `BeneficiarioServiceTest`, 5 no `DoadorControllerIntegrationTest`, 5 no `DoadorServiceTest` e 18 no `TelefoneValidatorTest`); no log do Flyway, `Migrating schema "PUBLIC" to version "3 - create doadores table"` e `Successfully applied 2 migrations to schema "PUBLIC", now at version v3`. Pelo Swagger, com o banco vazio: POST completo → 201 com `location: /api/v1/doadores/1`; POST com e-mail `""` e telefone `"   "` → 201 (id 2) com `email` e `telefone` null; nome vazio, sem tipo, e-mail sem @ e telefone com máscara → 400 com `email`, `nome`, `telefone` e `tipo` em `errors`, em ordem alfabética; telefone `11899999999` → 400 só com `telefone`; GET da lista sem parâmetros → 200 com `totalElements: 2`, `pageSize: 10` e a Ana antes do Mercado; `sort=idade` → 400 `ordenacao-invalida`; GET `/1` → 200 e `/99` → 404 `Doador com id 99 não encontrado.`. No navegador, `/api/v1/doadores/abc` → 400 `parametro-invalido`. No Git Bash, `curl -X DELETE /api/v1/doadores/1` → 405 (DELETE fora do escopo). O `"tipo": "ONG"` → 400 `corpo-invalido` ficou coberto pelo `DoadorControllerIntegrationTest`. |
| 24 | 12/11/2026 | Etapa 8 (código e execução, issue #17) | Claude Opus 5.5 (claude.ai) | Pedir, antes do código, a conferência do GitHub (merge do PR da Etapa 7 com `Closes #9` e a #9 fechada, `avaliacao1` intacta, número e texto da issue da Etapa 8, repositório do professor) e a comparação da issue com o que já existe; depois, o passo a passo, o código e os testes; enviar as saídas do terminal e do Swagger para conferência e as respostas de defesa das Etapas 7 e 8 para revisão (ver tópicos). | Conferência usada como referência: a IA confirmou a `main` em `e223352` (merge do PR #22, com `Closes #9`), a `avaliacao1` intacta em `5e68c1b`, a issue da Etapa 8 como #17 (critérios iguais à proposta da sessão #22, sem milestone e sem responsável) e o repositório do professor ainda em `286d78b`; não conseguiu confirmar o fechamento da #9 (ver tópicos). **Código da Etapa 8 copiado integralmente da resposta da IA** (zip `etapa8-doacao.zip`, um zip por commit, 10 commits): `V4__create_doacoes_table.sql`, `Doacao`, `TipoDoacao`, `DoacaoRepository`, `DoacaoSpecifications`, `DoacaoRequestDTO`, `DoacaoResponseDTO`, `DoacaoService`, `DoacaoController`, `DoacaoExemplos`, `DoadorInexistenteException`, `PeriodoInvalidoException`, `validation/DataNaoFutura.java`, `validation/DataNaoFuturaValidator.java`, `DoacaoServiceTest`, `DataNaoFuturaValidatorTest`, `DoacaoControllerIntegrationTest`, `docs/adr/0001-doador-inexistente.md` e as alterações no `messages.properties`, no `GlobalExceptionHandler`, na `RecursoNaoEncontradoException`, no `BeneficiarioExemplos`, no `DoadorExemplos`, no `BeneficiarioControllerIntegrationTest`, no `DoadorControllerIntegrationTest`, no `ApiApplicationTests` e no `docs/openapi.yaml`. Base: o script de referência da Aula 05, o roteiro do mob (GET com filtros), a Aula 07 e o roteiro do dojo (TDD e os três testes) e o molde do Doador (Etapa 7). O que a IA alterou em relação às bases: FK `fk_doacoes_doador` criada por último, para o H2 não duplicar o índice de `id_doador`; CHECKs do valor e do tipo; `Doacao` ligada ao `Doador` por `@ManyToOne` LAZY, e não por um `Long idDoador`; filtros por `Specification`, que funcionam em qualquer combinação (no professor, só os três juntos); `@PageableDefault` com `direction` (o do roteiro do mob não compila) e desempate por `idDoacao`; `descricao` gravada e devolvida (o `toEntity` do professor a perdia); POST, que o controller do professor não tinha; `@Positive` e `@Digits` no valor; testes do dojo no H2, sem Testcontainers. Decisões tomadas com base na análise da IA: 422 para doador inexistente (ADR 0001); mensagem de 404 sem gênero (`... não existe`) e mensagem de ordenação inválida sem o "como nome", num commit `fix` separado. Para responder, a IA consultou os materiais das Aulas 04, 05 e 07 (mob e dojo), o meu repositório (a `avaliacao1` só lida), o do professor e o código-fonte do Spring Boot 4.1.1, do Spring Framework 7.0.9, do Spring Data JPA e Commons 4.1.1, do Hibernate 7.4.5, do H2 2.4.240, do springdoc 3.1.0 e do swagger-core 2.2.52. A IA também revisou as minhas respostas de defesa das Etapas 7 e 8 e apontou erros, que corrigi. | Pela IA, antes da entrega: a V1, a V3 e a V4 rodaram num H2 2.2.220 (CHECKs, NOT NULL, FK e tamanhos recusaram o que deviam; a FK reaproveitou o `idx_doacoes_id_doador`; com a FK dentro do `CREATE TABLE`, ficariam dois índices em `id_doador`; o banco arredondou 10.555 para 10.56); os 10 commits compilaram com Lombok, JPA 3.2, Bean Validation e anotações do Swagger reais e esqueletos do Spring; 51 testes unitários passaram com JUnit, Mockito e AssertJ reais, e 9 defeitos colocados de propósito foram pegos; um erro de genéricos no teste de integração foi achado e corrigido antes da entrega; o zip foi reaplicado num clone limpo da `main`. Sem Maven Central, os testes com Spring não rodaram no ambiente da IA. Por mim, em 12/11/2026, no LAB03-PC03, sem Docker: hash do zip igual ao conferido pela IA (`b8bc7300…6589`); depois dos commits 1 a 4, `./mvnw clean test` com `Tests run: 49, Failures: 0` e `BUILD SUCCESS` (Flyway: `Migrating schema "PUBLIC" to version "4 - create doacoes table"` e `Successfully applied 3 migrations ... now at version v4`); RED (`c40d8ac`): `Tests run: 2, Failures: 1` no `deveRetornar400QuandoDataFutura`, com `expected: 400` e `but was: 201`, e `BUILD FAILURE`; GREEN (`4717962`) e REFACTOR (`714c63f`): `Tests run: 2, Failures: 0` e `BUILD SUCCESS`; rodada final: `Tests run: 77, Failures: 0, Errors: 0, Skipped: 0` e `BUILD SUCCESS` em 24,3 s (1 no `ApiApplicationTests`, 8 no `BeneficiarioControllerIntegrationTest`, 12 no `BeneficiarioServiceTest`, 12 no `DoacaoControllerIntegrationTest`, 8 no `DoacaoServiceTest`, 5 no `DoadorControllerIntegrationTest`, 5 no `DoadorServiceTest`, 8 no `DataNaoFuturaValidatorTest` e 18 no `TelefoneValidatorTest`); `git status` vazio e 10 commits, de `ff58a83` a `2d27a09`. Pelo Swagger, com o banco vazio: POST de doador → 201 com `location: /api/v1/doadores/1`; POST de doação → 201 com `location: /api/v1/doacoes/1` e `descricao` na resposta; data no futuro → 400 só no `dataDoacao`, com a mensagem do `messages.properties`; `idDoador` 99 → 422 `doador-inexistente`; `dataInicio` 2026-11-12 e `dataFim` 2026-11-11 → 400 `periodo-invalido`; GET `/99` → 404 `Doação com id 99 não existe.`. No log do Hibernate: a data futura e o período invertido sem nenhum SQL, e o 422 só com o SELECT do doador, sem INSERT. Pelo `curl`: `?tipo=DINHEIRO` → 400 `parametro-invalido` com `Valores aceitos: ALIMENTO, ROUPA, FINANCEIRA.`; listagem sem filtros → 200 com `size: 10`, `totalElements: 1` e `valor: 150.00` (o POST devolveu `150`, como enviado). |
| 25 | 12/11/2026 | Etapa 9 (código e execução, issue #18) | Claude Opus 5.5 (claude.ai) | Pedir, antes do código, a conferência do GitHub (merge do PR #23 com `Closes #17`, a #17 e a #9 fechadas, `avaliacao1` intacta, número e texto da issue da Etapa 9, repositório do professor) e a comparação da issue com o que já existe; depois, o passo a passo, o código e os testes; enviar as saídas do terminal, do Swagger e do `curl` para conferência e as respostas de defesa da Etapa 9 para revisão (ver tópicos). | Conferência usada como referência: a IA confirmou a `main` em `a95c347` (merge do PR #23, com `Closes #17`), a `avaliacao1` intacta em `5e68c1b`, a issue da Etapa 9 como #18 (aberta, critérios iguais à proposta da sessão #22) e o repositório do professor ainda em `286d78b`, sem PUT, PATCH e DELETE de doação nem idempotência; não conseguiu ler o estado da #9 e da #17, que conferi com `curl` (ver tópicos). Decisões tomadas com base na análise da IA: o PATCH pode trocar o `idDoador` (conferido, 422); o valor só se apaga pelo PUT, e a descrição pelo PUT ou pelo PATCH com `""` (texto em branco vira null, como no POST); ordem dos erros 400 → 404 → 422, com o doador conferido antes de qualquer setter; idempotência do POST fora desta etapa; mensagem do 422 generalizada num commit `fix` separado. **Código da Etapa 9 copiado integralmente da resposta da IA** (zip `etapa9-doacao.zip`, um zip por commit, 6 commits): `DoacaoPatchDTO` (novo) e as alterações no `messages.properties`, no `DoacaoExemplos`, no `DoacaoRequestDTO`, no `DoacaoService` (`atualizar`, `atualizarParcial`, `deletar`, `buscarEntidadePorId` e `buscarDoador`), no `DoacaoController` (PUT, PATCH e DELETE documentados no Swagger), no Javadoc do `DoacaoRepository`, no `DoacaoServiceTest` (9 testes), no `DoacaoControllerIntegrationTest` (9 testes), no `docs/openapi.yaml` e no `docs/adr/0001-doador-inexistente.md` (seção "Atualização — Etapa 9"). Base: o molde do Beneficiário (PUT, PATCH e DELETE da Etapa 3, testes da Etapa 6) e a nota da Aula 05 (PUT × PATCH e idempotência); o repositório do professor não tem essas operações para doação. A IA também revisou as minhas respostas de defesa da Etapa 9 e apontou erros, que corrigi. | Pela IA, antes da entrega: o código de produção e os testes de doação compilaram (javac 17) com Lombok 1.18.24, Bean Validation 3.0.2 e anotações do Swagger reais e esqueletos do Spring e do JPA com as assinaturas reais (inclusive os genéricos do `RestClient`); o `DoacaoServiceTest` passou 17 de 17 com JUnit 5.10, Mockito 2.23 (modo estrito) e AssertJ 3.14 reais, e 12 defeitos colocados de propósito no `DoacaoService` foram todos pegos; cada um dos 6 commits compilou sozinho; o `docs/openapi.yaml` passou no validador de OpenAPI 3.1, com `operationId`, `summary` e status iguais aos do controller; os JSONs de exemplo do Swagger são válidos e usam os textos do `messages.properties`; o bloco de commits foi ensaiado num clone limpo da `main`. Sem Maven Central, os testes com Spring não rodaram no ambiente da IA. Por mim, em 12/11/2026, no LAB03-PC03, sem Docker: `curl` na API do GitHub com a #9 e a #17 `closed` e a #18 `open`; `main` em `a95c347` e `avaliacao1` em `5e68c1b`; branch `feat/doacao` apagada (local e remota); hash do zip igual ao conferido pela IA (`039f02ec…bfa34`); 6 commits, de `140f922` a `f15834c`, com o `git status` vazio; `./mvnw clean test` com `Tests run: 95, Failures: 0, Errors: 0, Skipped: 0` e `BUILD SUCCESS` em 24,9 s (1 no `ApiApplicationTests`, 8 no `BeneficiarioControllerIntegrationTest`, 12 no `BeneficiarioServiceTest`, 21 no `DoacaoControllerIntegrationTest`, 17 no `DoacaoServiceTest`, 5 no `DoadorControllerIntegrationTest`, 5 no `DoadorServiceTest`, 8 no `DataNaoFuturaValidatorTest` e 18 no `TelefoneValidatorTest`). Pelo Swagger, com o banco vazio: dois doadores e uma doação → 201; PUT `/1` com outro doador, data e tipo → 200 com `idDoador` 2 e `descricao` null (o `valor` 150 veio no corpo que enviei); o mesmo PUT de novo → 200 igual; PUT `/99` → 404 `Doação com id 99 não existe.`; PUT `/1` com `idDoador` 99 → 422 com o texto novo; PUT `/1` com campos obrigatórios faltando → 400 com a lista em `errors`; PATCH `/1` com valor e descrição → 200, só com esses dois mudados; PATCH com `"descricao": ""` → 200 com `descricao` null; PATCH com data 2099-01-01 e valor 0 → 400 com `dataDoacao` e `valor`; PATCH com `idDoador` 99 → 422; PATCH `{}` → 200 sem mudança; DELETE `/1` → 204 e o segundo → 404; GET do doador 2 → 200. No log do Hibernate: o primeiro PUT com o SELECT da doação, o SELECT do doador e o UPDATE; o PUT repetido e o PATCH `{}` sem UPDATE; o PUT em `/99` só com o SELECT da doação; os 422 sem UPDATE; os 400 sem nenhum SQL; o DELETE com SELECT e DELETE. Pelo `curl`: PATCH com `"tipo": "DINHEIRO"` → 400 `corpo-invalido`; DELETE `/api/v1/doacoes/abc` → 400 `parametro-invalido`. |

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

### Tópicos da sessão #19 (Etapa 5, código)

- WARN `Json Processing Exception ... HashSet ... 'integer'`: bug do springdoc 3.1.0, disparado pelo `@ParameterObject Pageable` (o modelo de paginação do springdoc tem `@Min(0)` no `page` e `@Min(1)` no `size`). Corrigido no 3.1.1, que a IA recomendou; ver a decisão na sessão #20.
- CPF no contrato: `^\d{14}$` são 14 dígitos (tamanho de CNPJ), e não o CPF com máscara. O validador do professor remove a máscara, por isso a coluna é `VARCHAR(14)`.
- camelCase × snake_case: o prefixo `id_` da Aula 05 é regra das chaves no banco; os exemplos das Aulas 05 e 06 e o teste do professor (`jsonPath("$.idBeneficiario")`) usam camelCase. Contrato alinhado à API.
- PATCH sem 409: o `BeneficiarioPatchDTO` não tem `cpf`.
- Antes da etapa, o Swagger mostrava o POST e o DELETE com 200, que o springdoc assume sem `@ApiResponse` nem `@ResponseStatus`.
- 400 do id pelo Swagger: o Swagger UI só confere `^-?\d+$`, então `99999999999999999999` passa no navegador e estoura o `Long` no servidor.
- Filtros sem `example`: o Swagger UI preencheria o campo no "Try it out".
- Commit `5e0250c` saiu com `#5` no lugar de `#6`; fica assim, porque a `main` não é reescrita.

### Tópicos da sessão #20 (Etapa 5, retomada e execução)

- Conferência do PC antes de começar: `git fetch --prune`, `git status --short`, `git stash list` e `git log --oneline --branches --not --remotes` (lista commits que existem só na máquina), depois `git pull --ff-only` e `./mvnw clean compile` como linha de base.
- WARN do springdoc: não é critério da issue #7 e não muda as respostas da API; o efeito fica na documentação (um limite de validação pode aparecer no parâmetro errado) e no log. Decidi manter o 3.1.0 e registrar o WARN no PR como problema conhecido.
- Terminal: comando rodado fora do repositório (`not a git repository`), resolvido localizando a pasta com `find`; caminho digitado como `/c/User` em vez de `/c/Users`; caractere invisível colado antes do comando (`$'\302\203java'`); push com o nome da branch digitado errado (`src refspec ... does not match any`), resolvido com `git push -u origin HEAD`.
- Colar no Git Bash: Shift+Insert ou botão direito, e não Ctrl+V. Uma colagem cortada deixou o `cat` esperando o `EOF` (linhas com `>`); o Ctrl+C cancelou sem alterar o arquivo. Os arquivos longos foram colados pelo Bloco de Notas.
- `docs/openapi.yaml` colado sem quebra de linha no fim: o `git diff` mostrava a última linha como alterada; corrigido com `echo >> docs/openapi.yaml`.
- Aviso `LF will be replaced by CRLF`: o `cat` grava com final de linha Unix e o Git do Windows converte; não altera o conteúdo versionado.
- WARN do Lombok com o JDK 25 (`sun.misc.Unsafe::objectFieldOffset`): aviso de método marcado para remoção, sem efeito na compilação.
- DELETE `/2` deu 404 na primeira tentativa porque o beneficiário 2 ainda não existia; depois do POST, deu 204.
- Comparação do `/api-docs` com o `docs/openapi.yaml` registrada na descrição do PR.

### Tópicos da sessão #21 (Etapa 6)

- Issue #8 × laboratório: a issue pede PostgreSQL 17 via Testcontainers, MockMvc e `./mvnw test` com o Docker ligado. Sem Docker no laboratório (e com o professor tendo tirado o Docker da A1), os testes de integração usam o H2 em memória e o `RestClient` numa porta aleatória, como na A1. A adaptação fica registrada no PR.
- `contextLoads`: o `@Import(TestcontainersConfiguration.class)` subia um PostgreSQL no Docker; sem ele, o contexto sobe no H2. `TestcontainersConfiguration` e `TestApiApplication` continuam no projeto: não rodam no `./mvnw test` (o `TestApiApplication` não tem `@Test`, e a configuração só entra com `@Import`) e servem para subir a API com PostgreSQL num PC com Docker.
- Perfil `test`: `application-test.properties` em `src/test/resources`, ativado com `@ActiveProfiles("test")`. Ele só sobrescreve o que muda nos testes (sem SQL no log, Flyway em INFO, console do H2 desligado). Um `application.properties` em `src/test/resources` esconderia o de `src/main/resources`.
- Corrida no CPF: o `existsByCpf` não segura dois POSTs simultâneos; o UNIQUE da V1 barra o segundo, que saía como 500. Agora o `salvar` grava com `saveAndFlush` dentro de um `try` e transforma a `DataIntegrityViolationException` em `CpfDuplicadoException` (409). No PUT, o flush é necessário porque, sem ele, o UPDATE só iria ao banco no commit, depois do `return` e fora do `try`. O teste unitário simula a corrida com o Mockito: `existsByCpf` devolve `false` e o `saveAndFlush` lança a exceção.
- Mockito no modo estrito: um `when` que o Service não chega a usar derruba o teste (`UnnecessaryStubbingException`), então cada teste prepara só as chamadas do próprio caminho.
- `ArgumentCaptor`: se o `thenAnswer` altera o próprio argumento, o captor vê o objeto já alterado. Com o `saveAndFlush` simulado devolvendo outro objeto, ele vê a entidade como o Service a enviou: sem id e com a data de hoje.
- Testes de integração independentes: a tabela é limpa antes de cada teste, porque o H2 e o contexto do Spring são reaproveitados entre os testes da classe.
- Avisos esperados no `./mvnw test`: o Mockito avisa que se anexa sozinho como agente da JVM (sem efeito no resultado) e o Spring Data mostra uma vez o WARN de `PageImpl`, no teste da listagem. O WARN do springdoc não aparece, porque nenhum teste abre o `/api-docs`.

### Tópicos da sessão #22 (mapeamento e Etapa 7)

- Material novo: Aula 09 (`CorsConfig` para `http://localhost:5173`, front-end React + TypeScript com Vite e Axios), Aula 10 (MUI, React Hook Form + Zod com CPF `^\d{11}$` e telefone `^\d{10,11}$`, WCAG, teste de usabilidade) e o edital da A2 (seminário em grupo de 3 a 4, inscrição até 13/11 e apresentação em 27/11).
- Doações do repositório do professor, que não devem ser copiadas: a V2 e a V3 dele criam `doacoes` duas vezes e a V3 aponta para `doadores`, que nenhuma migration cria; o Flyway nem roda lá (só `flyway-core`, sem o starter do Boot 4); o `DoacaoController` não tem POST, mas o dojo testa um; o `toEntity` perde a `descricao`; o `listar` só filtra com os três filtros juntos; o `@PageableDefault` do roteiro do mob não compila.
- Por que dividir a #9: juntava duas tabelas, um CRUD que o mob pede completo, o TDD da `@DataNaoFutura`, o ADR e o dojo; não tinha endpoint de doador, mas a doação precisa de um doador gravado por causa da FK; e "ensaio da A1" deixou de valer.
- `doadores` na V3: a V2 é a de `produtos` da A1, ainda sem merge. Com números diferentes, as duas convivem quando o PR #13 entrar.
- Doador sem 409: o contrato não tem CPF nem CNPJ, então não há campo único para repetir.
- Ordem sugerida das issues: 7 → 8 → 9 → 10 → 11 → 12; a 10 só depende do Beneficiário e pode vir antes, se o professor cobrar o front-end.

### Tópicos da sessão #23 (Etapa 7, execução)

- Conferência feita pela IA sem a API do GitHub (não liberada na sessão): `git ls-remote` nos dois repositórios e a página pública da #9.
- A #9 tinha o corpo novo, mas o título e a milestone A1 antigos; a sessão #22 pedia para trocar os dois.
- Hash SHA-256 do zip para garantir que o arquivo aplicado no laboratório era o mesmo conferido pela IA.
- Maior risco antes dos testes: o `ddl-auto=validate` com o enum gravado como texto. O mesmo mapeamento já tinha rodado no laboratório na A1, com a `categoria` do Produto, e passou de novo aqui.
- `"email": "   "` (só espaços) dá 400, porque o `@Email` recusa; e-mail vazio e telefone vazio ou só com espaços viram `null`.
- Log dos testes gravado com `tee` em `~/etapa7-testes.log`, fora do repositório, para não entrar no commit.
- O bloco de clone (para PC sem a pasta) foi rodado num PC que já tinha o repositório: deu `destination path ... already exists` e não alterou nada.

### Tópicos da sessão #24 (Etapa 8)

- Conferência sem a API do GitHub (a conta não estava vinculada nesta sessão): clone dos dois repositórios e páginas públicas. O PR #22 tem `Closes #9` e mostra a #9 vinculada com o título novo, mas a página da #9 que a IA conseguiu ler era uma cópia de 09/11, ainda aberta e com o título antigo; o fechamento da #9 ficou para eu conferir no GitHub.
- O ambiente da IA não acessa o Maven Central: a validação foi feita com bibliotecas reais (Lombok, JPA, Bean Validation, Swagger, JUnit, Mockito, AssertJ e H2 2.2.220) e esqueletos das APIs do Spring, conferidos no código-fonte. Os testes com Spring só rodaram no laboratório.
- Texto do 404 com gênero: `{0} com id {1} não encontrado.` daria "Doação com id 99 não encontrado.". Virou `... não existe.`, num commit `fix` separado, junto com a mensagem de ordenação inválida, que sugeria "como nome" (a doação não tem nome).
- Commits divididos em blocos, e não num bloco único, por causa do TDD: o RED precisa ser visto falhando antes do GREEN. Um zip por commit, aplicado com `unzip -o`, e `git add $(unzip -Z1 …)` para adicionar exatamente os arquivos do zip.
- FK por último na V4: o H2 cria um índice para a coluna da FK quando não acha um pronto. A IA testou as duas ordens no H2 2.2.220 e conferiu no código do H2 2.4.240.
- O H2 arredondou 10.555 para 10.56 sem avisar, e um valor com 9 dígitos inteiros deu erro no INSERT: motivo do `@Digits` no DTO.
- Doador LAZY na `Doacao`: ler só o id do doador não faz SELECT (o Hibernate já tem o valor da coluna `id_doador`); as leituras do `DoacaoService` têm `@Transactional(readOnly = true)` para qualquer outro dado do doador.
- Avisos no `./mvnw`, nenhum vindo da Etapa 8: `LF will be replaced by CRLF` (Git do Windows); `Using H2 2.4.240 which is newer than the version Flyway has been verified with` (versões do Spring Boot 4.1.1); `stringOrNumberMigrationVersionConverter … not eligible` (o bean citado é do springdoc 3.1.0; "specification" ali é a especificação OpenAPI, não a `Specification` do Spring Data); `Json Processing Exception` do springdoc, já conhecido.
- No Swagger, o `tipo` da listagem é uma lista fechada (enum), então o teste do tipo inválido foi feito com `curl`.
- O primeiro `curl` rodou com a API já parada e saiu vazio; o H2 em memória perdeu os dados ao parar. Na segunda tentativa, os POSTs com acento deram 400 `corpo-invalido`: o `curl` do Git Bash enviou o texto acentuado fora de UTF-8. Com o texto sem acento, deram 201.
- Um caractere invisível colado antes de um `cd` gerou `command not found`, sem efeito, porque o terminal já estava na pasta do repositório.
- Respostas de defesa: a IA apontou erros nas minhas primeiras versões das perguntas 1, 4, 5 e 6 da Etapa 7 e 1, 3 e 6 da Etapa 8 (e pediu complementos na 2 e na 5), e eu as reescrevi.

### Tópicos da sessão #25 (Etapa 9)

- Conferência sem a API do GitHub (conta não vinculada nesta sessão): clone dos dois repositórios e páginas públicas. O PR #23 tem `Closes #17`, mas as páginas da #9 e da #17 que a IA leu eram cópias antigas (a da #9 ainda com o título anterior a 09/11). O estado das duas foi conferido por mim com `curl` na API pública do GitHub: `closed`.
- A branch `feat/doacao` tinha ficado no GitHub depois do merge do PR #23; foi apagada no início da etapa (local e remota).
- Decisões: o PATCH pode trocar o doador, porque a #18 já previa "o doador é conferido se vier"; o valor só se apaga pelo PUT (`null` no PATCH é "não mexer" e `0` dá 400); a descrição em branco vira null pela mesma regra do POST (`textoOuNulo`); o 404 vem antes do 422 porque a doação da URL é o alvo da requisição; o doador é conferido antes de qualquer setter.
- PUT e PATCH com `save` comum, e não `saveAndFlush` como o PUT do beneficiário: a doação não tem UNIQUE, e o Hibernate envia o UPDATE no commit, só se algum campo mudou (dirty checking).
- Mensagem do 422: "Cadastre o doador antes de registrar a doação" só servia para o POST. Virou "Informe o id de um doador já cadastrado.", num commit `fix` separado, e o ADR 0001 ganhou a seção "Atualização — Etapa 9" e o título "no corpo de uma doação".
- Idempotência do POST (opcional da #18) fora desta etapa: fazê-la direito pede uma tabela nova (V5) para as chaves e as respostas, a recusa da mesma chave com outro corpo (422), o tratamento de requisições simultâneas (409) e um prazo de validade; pode virar uma issue própria.
- Swagger: o PUT e o PATCH reaproveitam os exemplos de erro do POST (só o `instance` mudaria); o PATCH tem um exemplo próprio de validação, porque lá nenhum campo é obrigatório.
- Validação feita pela IA com bibliotecas do apt do Ubuntu e esqueletos do Spring; o pacote do Mockito não traz a extensão do JUnit 5, e a IA escreveu uma com o mesmo comportamento padrão (modo estrito).
- Roteiro manual: no primeiro PUT, o corpo saiu com o `valor` do exemplo preenchido pelo Swagger, e a resposta trouxe `valor` 150 em vez de null; o caso "PUT sem valor apaga o valor" está coberto pelo teste `deveSubstituirDadosQuandoPutValido`. Os 400 do PUT foram feitos em `/1`, e não em `/99`; a ordem 400 antes do 404 está coberta pelo `deveRetornar400AntesDo404QuandoPutInvalidoEmDoacaoInexistente`.
- `./mvnw spring-boot:run` na raiz do repositório deu `No such file or directory`: o `mvnw` fica em `backend/`.
- Avisos no `./mvnw`, nenhum novo: `LF will be replaced by CRLF`, `Using H2 2.4.240 which is newer...`, `stringOrNumberMigrationVersionConverter ... not eligible` e `Json Processing Exception` do springdoc.
- Respostas de defesa: a IA apontou erro na minha primeira versão da pergunta 4 (o valor não é obrigatório) e pediu complementos na 2, na 3, na 5 e na 6, e eu as reescrevi.

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

### Perguntas de defesa — Etapa 5 (respostas minhas)

> As respostas foram reescritas por mim em 24/10/2026, depois de a IA apontar erros nas primeiras versões das perguntas 2 e 3 e um motivo faltando na 1 (sessão #20).

1. Por que as respostas de erro declaram `content` com o `ProblemDetail`, e as de sucesso não? O `content` é declarado nas respostas de erro para especificar o schema correto do corpo retornado, que é o `ProblemDetail`. Sem essa declaração, o springdoc preenche automaticamente o `content` com base no tipo de retorno do método, podendo documentar um erro 404 como se retornasse um `BeneficiarioResponse`. Nas respostas de sucesso, esse preenchimento automático é desejado, pois corresponde ao tipo de retorno real do método.
2. O que disparava o WARN `Json Processing Exception` neste projeto, e por que ele não muda as respostas da API? O WARN é causado por um bug do springdoc 3.1.0 durante a geração da documentação OpenAPI, quando ele tenta copiar um schema por meio de JSON e ocorre uma exceção. Neste projeto, isso é disparado pelo `Pageable` anotado com `@ParameterObject`: seus campos `page` e `size` possuem, respectivamente, as validações `@Min(0)` e `@Min(1)`, o que explica as duas linhas de WARN. Isso acontece quando o Swagger é aberto, durante a geração de `/api-docs`, e não durante uma requisição da API. O springdoc trata a exceção internamente e continua utilizando o schema original. Portanto, o WARN não altera as respostas HTTP, pois nenhum controller ou handler de erros participa desse processo.
3. Por que o contrato ficou em camelCase, se a regra da disciplina é o prefixo `id_`? O contrato está correto porque a regra do prefixo `id_` se aplica às chaves primárias no banco de dados, não aos campos do JSON. No banco, a coluna segue a convenção com o nome `id_beneficiario`, definida tanto na migration V1 quanto no mapeamento `@Column(name = "id_beneficiario")` da Entity. Já o JSON utiliza `camelCase`, seguindo os nomes dos componentes dos records Java, os exemplos das Aulas 05 e 06 e o padrão esperado pelo teste do professor, que verifica `jsonPath("$.idBeneficiario")`. Portanto, as duas convenções são respeitadas em suas respectivas camadas.

### Perguntas de defesa — Etapa 7 (respostas minhas)

> Perguntas da IA (sessões #22 e #23). As respostas 1, 4, 5 e 6 foram reescritas por mim em 12/11/2026, depois de a IA apontar erros nas primeiras versões (sessão #24).

1. Por que `doadores` é a V3? O que o Flyway faria com duas V2? A V3 foi criada para permitir que as migrations das duas etapas convivam sem conflito. A V2 está reservada para a tabela produtos da A1, na branch avaliacao1 (PR #13, ainda sem merge). Quando a A1 entrar na main, a V3 evita que existam duas migrations com a mesma versão. Se houver duas V2, o Flyway identifica a duplicidade e impede a execução das migrations.
2. Se o enum já limita os valores, para que serve o CHECK no banco? E por que `EnumType.STRING`, e não o padrão? O enum limita os valores aceitos pela aplicação, enquanto o `CHECK` garante que o banco também rejeite valores inválidos, inclusive quando os dados são inseridos diretamente no banco, fora da API. O `EnumType.STRING` é utilizado para armazenar o nome do enum como texto, em vez de utilizar o padrão `ORDINAL`, que armazena sua posição numérica. Isso evita problemas caso a ordem dos elementos do enum seja alterada no código.
3. Por que `"tipo": "ONG"` dá 400 de corpo inválido, sem o campo na lista `errors`? Quando enviamos `"tipo": "ONG"`, o Jackson tenta converter esse texto para um valor do enum antes de chegar à validação do DTO. Como `ONG` não existe no enum, ocorre um erro de desserialização do JSON, resultando em 400. Por isso, o erro não aparece na lista `errors` de validação dos campos: o objeto nem chega a ser construído corretamente.
4. Por que o POST de doador não tem 409, e o de beneficiário tem? O POST de beneficiário retorna 409 porque existe uma regra de negócio que impede cadastros duplicados por um campo único. Já o doador não possui CPF ou CNPJ no contrato, nem outro campo que permita identificar uma duplicidade. Por isso, não existe conflito de unicidade que justifique um 409 nesse cadastro. Esse status deve ser utilizado quando há conflito com uma regra de unicidade ou outra regra de negócio.
5. Por que `"telefone": "   "` é gravado como `null`, mas `"email": "   "` dá 400? O campo telefone é opcional e a anotação @Telefone considera valores nulos ou em branco como válidos. No DoadorService, o método textoOuNulo transforma o texto em branco em null antes de gravar. Já o @Email aceita uma string vazia, que também vira null, mas rejeita uma string contendo apenas espaços, fazendo a API retornar 400 antes de chegar ao Service.
6. O H2 em memória aplica V1, V2 e V3 em ordem a cada subida. O que aconteceria num PostgreSQL que já aplicou a V3 quando a V2 da A1 entrar na `main`? O H2 em memória executa as migrations V1, V2 e V3 em ordem a cada inicialização. No PostgreSQL, se a V3 já tiver sido aplicada e a V2 da A1 entrar posteriormente na main, o Flyway, por padrão, falha na validação porque encontra uma migration nos arquivos que ainda não foi aplicada. Com outOfOrder=true, esse comportamento pode mudar, permitindo executar migrations fora de ordem. Para evitar esse conflito, a migration da A1 deve ser renumerada antes do merge. Como a V4 já foi utilizada para doacoes, o próximo número livre é a V5.

### Perguntas de defesa — Etapa 8 (respostas minhas)

> Perguntas da IA (sessão #24). As respostas 1, 3 e 6 foram reescritas e a 2 e a 5 completadas por mim em 12/11/2026, depois de a IA apontar o que faltava nas primeiras versões (sessão #24).

1. Por que a FK da V4 é criada num `ALTER TABLE` no fim, depois dos índices? E por que o índice em `id_doador` continua necessário no PostgreSQL? A FK foi criada por último, em um ALTER TABLE, para evitar a duplicação de índices no H2. Quando o H2 cria uma FK e não encontra um índice existente na coluna, ele cria um índice automaticamente. Se a FK estivesse dentro do CREATE TABLE, seriam criados dois índices em id_doador: o automático e o idx_doacoes_id_doador. Criando primeiro o índice e depois a FK, o H2 reaproveita o índice existente. No PostgreSQL, a FK não cria automaticamente um índice na coluna referenciadora, por isso o índice continua necessário.
2. Por que 422, e não 404, quando o `idDoador` não existe? Em que caso as doações continuam com 404? O status 422 é utilizado porque o 404 se refere ao recurso da URL. No POST /api/v1/doacoes, a URL existe, mas o idDoador informado no corpo da requisição não corresponde a nenhum doador. Como o JSON está bem formado, mas não pode ser processado devido a essa referência inválida, retornamos 422, conforme definido no ADR 0001. O 404 continua sendo utilizado quando a própria doação solicitada não existe, como em consultas, atualizações ou exclusões.
3. Mostre no `git log` os commits RED, GREEN e REFACTOR. O que o RED provou, e o que mudou e o que não mudou no REFACTOR? No TDD, o RED (c40d8ac) provou que o teste falhava pela razão esperada: a API aceitava uma data futura, retornando 201 em vez dos 400 esperados. O GREEN (4717962) corrigiu esse comportamento. No REFACTOR (714c63f), a mensagem de validação saiu do texto fixo da anotação e foi transferida para o messages.properties, com um texto melhor. O teste continuou passando sem alterações, pois verifica apenas se a mensagem contém a palavra "futuro". Para visualizar os commits, utilizo git log --oneline main..HEAD.
4. Por que `Specification`, e não o `findByDataDoacaoBetweenAndTipo` do mob? Quantos métodos derivados cobririam todas as combinações? Foi utilizada Specification porque permite combinar filtros opcionais dinamicamente. Com três filtros (data inicial, data final e tipo), existem oito combinações possíveis, incluindo a consulta sem filtros, que seria atendida pelo findAll. O método derivado do mob cobria apenas a combinação dos três filtros juntos. Com Specification, evitamos criar vários métodos no repositório.
5. Por que a ordenação padrão tem `idDoacao` além de `dataDoacao`? O idDoacao é utilizado como critério secundário para desempatar doações que possuem a mesma dataDoacao, garantindo uma ordenação determinística. Sem esse desempate, a ordem dos registros poderia mudar entre páginas, fazendo uma doação aparecer duas vezes ou não aparecer em nenhuma página durante a navegação.
6. Se a coluna já é `DECIMAL(10,2)` com `CHECK (valor > 0)`, para que servem o `@Positive` e o `@Digits` no DTO? As validações no DTO impedem que valores inválidos cheguem ao banco e provoquem erros internos. Sem o @Digits, o H2 pode arredondar um valor como 10.555 para 10.56 sem avisar, enquanto um valor com nove dígitos inteiros pode provocar erro no INSERT e resultar em uma resposta 500. Com @Positive e @Digits, a API valida o valor antecipadamente e retorna 400 com a mensagem correspondente ao campo. O banco continua garantindo a integridade como segunda barreira, por meio do DECIMAL(10,2) e do CHECK (valor > 0).

### Perguntas de defesa — Etapa 9 (respostas minhas)

> Perguntas da IA (sessão #25). A resposta 4 foi reescrita e as respostas 2, 3, 5 e 6 completadas por mim em 12/11/2026, depois de a IA apontar o que faltava nas primeiras versões (sessão #25).

1. O PUT usa o mesmo `DoacaoRequestDTO` do POST, e o PATCH ganhou o `DoacaoPatchDTO`. O que quebraria se o PATCH usasse o `DoacaoRequestDTO`? E por que a `@DataNaoFutura` funciona no `DoacaoPatchDTO` sem obrigar a data? O `DoacaoRequestDTO` é utilizado no POST e no PUT porque essas operações exigem os dados obrigatórios da doação. Já o PATCH utiliza o `DoacaoPatchDTO`, pois permite enviar apenas os campos que serão alterados. Se utilizasse o DTO do POST, os campos obrigatórios provocariam erros 400 quando não fossem enviados, impedindo atualizações parciais. A `@DataNaoFutura` funciona no DTO do PATCH porque sua validação permite valores nulos, verificando a data somente quando ela é informada. Assim, a data continua opcional sem deixar de validar datas futuras.
2. No PUT e no PATCH, a ordem dos erros é 400 → 404 → 422. Por que o 400 vem antes de tudo, e o 404 antes do 422? Que teste prova que o 400 vem antes do 404? O erro 400 vem primeiro porque a validação acontece antes de qualquer consulta ao banco. Isso inclui o JSON inválido, as validações do `@Valid` e até um ID da URL que não seja numérico, como `/doacoes/abc`. O 404 vem antes do 422 porque a doação da URL é o alvo da requisição: se ela não existe, não há o que processar, e o cliente precisa saber primeiro que informou um endereço inexistente. Isso também evita consultar o doador desnecessariamente. O teste `deveRetornar400AntesDo404QuandoPutInvalidoEmDoacaoInexistente` comprova que o 400 tem prioridade sobre o 404.
3. No `atualizarParcial`, o doador é o primeiro campo aplicado. Se ele fosse conferido depois dos outros setters e não existisse, a doação mudaria no banco? Por quê? Então por que o teste unitário confere que a entidade ficou intacta? Não, a doação não mudaria no banco se o doador fosse verificado depois dos outros setters, porque a exceção provocaria o rollback da transação, impedindo a persistência das alterações. O teste unitário verifica se a entidade permanece intacta porque não utiliza Spring nem gerenciamento transacional. Assim, conferir o próprio objeto é a maneira de provar que nenhum setter foi executado antes da validação do doador.
4. Como se apaga o valor de uma doação? E a descrição? Por que `"valor": null` no PATCH não apaga nada, e `"descricao": ""` apaga? O valor é opcional, pois existem doações de roupas e alimentos que não possuem valor monetário, e a coluna aceita `null`. Para apagar o valor, utilizamos o PUT sem enviar o campo, pois a substituição total grava `null` nos campos que não foram informados. Pelo PATCH isso não é possível, porque `null` significa não alterar o campo, e o valor zero é recusado pelo `@Positive`, gerando 400. A descrição pode ser apagada pelo PUT sem o campo ou pelo PATCH enviando `""`. Nesse último caso, o Service utiliza `textoOuNulo`, a mesma regra do POST, transformando o texto em branco em `null`.
5. Apagar uma doação apaga o doador? E, se um dia existir DELETE de doador, o que a FK `fk_doacoes_doador` faria com um doador que tem doações, e que status a API devolveria hoje? Apagar uma doação não apaga o doador porque a FK fica na tabela `doacoes`, apontando para `doadores`, e o relacionamento `@ManyToOne` não utiliza cascade. Assim, excluir o lado que representa as várias doações não afeta o lado que representa o doador. Se fosse criado um DELETE de doador sem tratamento específico, o banco impediria a exclusão quando existissem doações vinculadas, pois a FK não possui `ON DELETE CASCADE`. O Spring lançaria `DataIntegrityViolationException` e, sem um handler específico, a resposta seria 500 pelo handler genérico. O correto seria tratar esse conflito e retornar 409. Atualmente, a API retorna 405 porque o DELETE de doador não está implementado.
6. O PUT é idempotente e o POST não. O segundo PUT idêntico gera UPDATE no log? Por quê? E como o `Idempotency-Key` da Aula 05 protegeria o POST de doação, e por que ficou de fora desta etapa? O PUT é idempotente porque enviar a mesma atualização várias vezes produz o mesmo estado final do recurso. No segundo PUT com os mesmos dados, o Hibernate utiliza dirty checking e identifica que não houve alterações, evitando um novo UPDATE. Isso foi confirmado no log: a segunda requisição executou apenas os dois SELECTs, sem UPDATE. Já o POST não é idempotente, pois cada requisição pode criar uma nova doação. O `Idempotency-Key` permitiria reconhecer uma requisição já processada e devolver sua resposta anterior, evitando duplicidades. Porém, para implementar isso corretamente, seria necessário criar uma tabela própria em uma nova migration V5 para armazenar as chaves e respostas, garantindo que não fossem perdidas quando a API reiniciasse. Também seria necessário rejeitar o uso da mesma chave com outro corpo, retornando 422, tratar requisições simultâneas com a mesma chave, retornando 409, e estabelecer um prazo de validade para as chaves. Por isso, a idempotência ficou como funcionalidade opcional na #18, podendo ser desenvolvida em uma issue própria.

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

**#19 — 03/10/2026**

> Vamos para a Etapa 5 (issue #7) do projeto SocialConnect: documentação da API no Swagger/OpenAPI. A Etapa 4 foi mergeada.
>
> **Estado atual**
> - Repositório: italozigart/socialconnect_italo_bazalia_zigart. Referência do professor: Prof-JamesCampos/socialconnect-api.
> - `main` em `312ae6b` (merge da Etapa 4): Etapas 0 a 4 concluídas. Beneficiário com CRUD completo, paginação e filtros, Bean Validation com `@Valid`, constraint `@CPF` (só 11 dígitos, sem máscara), erros em Problem Details pelo `GlobalExceptionHandler` (400, 404, 409 e 500 genérico) e mensagens em pt-BR no `messages.properties`.
> - Branch `avaliacao1` (A1, módulo de Produtos): **congelada até a correção.** PR #13 aberto, sem merge. Não fazer commit, merge nem rebase nela, e não usar "Resolve conflicts" no PR #13. Pode ser lida para reaproveitamento, como fizemos na Etapa 4 com `git restore --source=origin/avaliacao1`, levando só o que for genérico e nunca o código de Produtos.
>
> **O que reaproveitar da `avaliacao1`**
> O `ProdutoController` tem `@Tag`, `@Operation` e `@ApiResponse` por endpoint; o `ProdutoRequestDTO` e o `ProdutoResponseDTO` têm `@Schema` com descrição e exemplo; o `ProblemDetail` já tem `@Schema`. Quero usar esses arquivos como molde para documentar o Beneficiário.
>
> **Pendências anotadas para esta etapa**
> - O `docs/openapi.yaml` aceita CPF com 14 caracteres (`\d{14}`), mas a API decidiu aceitar só 11 dígitos. É preciso alinhar ou registrar a divergência.
> - O contrato não lista 400 nem 409 no PUT e no PATCH, nem 400 no GET por id.
> - JSON em camelCase (records) × snake_case no `docs/openapi.yaml`: dúvida em aberto.
> - WARN do springdoc ao abrir o Swagger: `Json Processing Exception ... Cannot construct instance of java.util.HashSet ... ('integer')`.
> - Não pôr `@ResponseStatus` nos handlers: no springdoc 3.1.0, cada status apareceria em todos os endpoints.
>
> **Ambiente e forma de trabalho**
> - PC do laboratório: Windows, Git Bash, JDK 25 (projeto em Java 21), **sem Docker** (o `mvn test` não roda por causa do Testcontainers). Stack: Spring Boot 4.1.1, springdoc 3.1.0, H2 em memória.
> - Comandos `git` na raiz do repositório; `./mvnw` dentro de `backend/`.
> - Fluxo: branch a partir da `main` atualizada → commits em Conventional Commits com `#7` → PR com `Closes #7` → merge.
> - Quero concluir rápido: confira o repositório (a `main` e a `avaliacao1`), me diga o que falta e gere o código para eu revisar, testar e commitar, com os comandos e os testes esperados.
> - O `AI_USAGE.md` faz parte da avaliação e você o mantém atualizado. A última sessão registrada é a #18, então a próxima é a #19. Código copiado da IA é declarado na linha da sessão.

**#20 — 24/10/2026** — prompts principais abaixo; as saídas do terminal e dos testes enviadas para conferência estão nos tópicos da sessão #20.

> Eu não dei seguimento no projeto desde esse último chat. Revise o projeto, acredito que nada desse chat foi feito e ele é de onde eu preciso contuinuar, to certo? Apenas revisei e escreve se sim ou não e por quê. Segundo ponto é que eu preciso ter certez que o projeto nessa máquina está atualizado, como certificar antes de começar a fazer os passo a passo dessa conversa atual?

> Precisamos mesmo fazer o Warn sumir? É um requisito da issue e/ou vai atrapalhar o projeto no futuro?

> Ta, não rodei sed algum. Vamos resolver a issue? O que fazer agora?

> No Git Bash como colar os arquivos?

**#21 — 26/10/2026**

> Vamos para a Etapa 6 (issue #8) do projeto SocialConnect: testes automatizados. A Etapa 5 foi mergeada.
>
> **Estado atual**
> - Repositório: italozigart/socialconnect_italo_bazalia_zigart. Referência do professor: Prof-JamesCampos/socialconnect-api (tem `BeneficiarioServiceTest` e `BeneficiarioControllerIntegrationTest`, da Aula 07).
> - `main` com o merge do PR da Etapa 5 (#15): Etapas 0 a 5 concluídas. Beneficiário com CRUD completo, paginação e filtros, Bean Validation com `@Valid`, `@CPF` (só 11 dígitos, sem máscara), erros em Problem Details (400, 404, 409 e 500 genérico) em pt-BR e documentação completa no Swagger, com `docs/openapi.yaml` alinhado à API (camelCase, `{idBeneficiario}`).
> - Branch `avaliacao1` (A1, módulo de Produtos): **congelada até a correção.** PR #13 aberto, sem merge. Não fazer commit, merge nem rebase nela, e não usar "Resolve conflicts" no PR #13. Pode ser lida com `git show origin/avaliacao1:<arquivo>` ou `git restore --source=origin/avaliacao1`, levando só o que for genérico. Tem o `ProdutoServiceTest` (JUnit + Mockito) e o `ProdutoControllerIntegrationTest` (`@SpringBootTest` com porta aleatória, `RestClient` e H2), que rodaram no laboratório sem Docker. Quero usá-los como molde.
>
> **Pendências para esta etapa**
> - O `ApiApplicationTests` (`contextLoads`) usa `@Import(TestcontainersConfiguration.class)` e não roda sem Docker; na A1 ele foi trocado pelo H2. Também existem `TestApiApplication` e `TestcontainersConfiguration` gerados pelo Initializr.
> - Dois POSTs simultâneos com o mesmo CPF passam pelo `existsByCpf`, e o segundo cai no UNIQUE com 500 genérico (anotado na Etapa 4).
> - WARN conhecido do springdoc 3.1.0 ao abrir o Swagger (`Json Processing Exception`), mantido de propósito na Etapa 5.
>
> **Ambiente e forma de trabalho**
> - PC do laboratório (usuário `Alunos`): Windows, Git Bash, JDK 25.0.1 (projeto em Java 21), **sem Docker**. Stack: Spring Boot 4.1.1, springdoc 3.1.0, H2 em memória. Repositório em `/c/Users/Alunos/projeto/topicos/socialconnect_italo_bazalia_zigart`.
> - Comandos `git` na raiz do repositório; `./mvnw` dentro de `backend/`.
> - Fluxo: conferir que a máquina está igual ao GitHub → branch a partir da `main` atualizada → commits em Conventional Commits com `#8` → PR com `Closes #8` → merge.
> - Quero os arquivos inteiros, prontos para colar (blocos `cat` para o Git Bash; arquivos longos como download), nunca trechos para editar. Os testes esperados e os comandos vêm junto.
> - Quero concluir rápido: confira o repositório (a `main`, a `avaliacao1` e o repositório do professor), me diga o que falta na issue #8 e gere o código para eu revisar, testar e commitar.
> - O `AI_USAGE.md` faz parte da avaliação e você o mantém atualizado. A última sessão registrada é a #20, então a próxima é a #21. Código copiado da IA é declarado na linha da sessão.

**#22 — 09/11/2026** (com o zip dos materiais do Teams anexado)

> Vamos começar a próxima etapa do projeto SocialConnect. A Etapa 6 (issue #8, testes) foi mergeada no PR #16.
>
> **Antes de gerar código, faça o mapeamento:**
> 1. Leia todos os materiais de aula que anexei (do Teams) e o repositório do professor (Prof-JamesCampos/socialconnect-api). Não lembro até qual aula tínhamos material, então considere tudo.
> 2. Liste o que o professor pediu de novo (features, endpoints, regras, entregas e avaliações A2–A4) que ainda não está no meu repositório, aula por aula, e diga o que já está coberto.
> 3. Compare com a issue #9 (Etapa 7) do meu backlog e proponha se ela continua como está, se muda ou se vira mais de uma issue, com títulos e critérios de aceite prontos para eu colar no GitHub.
> 4. Só depois disso, o passo a passo e o código da primeira issue.
>
> **Estado atual**
> - Repositório: italozigart/socialconnect_italo_bazalia_zigart. `main` em `621fe37` (merge do PR #16). Etapas 0 a 6 concluídas.
> - Beneficiário completo: CRUD com PUT/PATCH, paginação e filtros (nome, CPF), Bean Validation com `@CPF` (11 dígitos, sem máscara), Problem Details em pt-BR (400, 404, 409 e 500 genérico), Swagger completo e `docs/openapi.yaml` alinhado (camelCase, `{idBeneficiario}`).
> - Testes (Etapa 6): `BeneficiarioServiceTest` (12, JUnit + Mockito + AssertJ, padrão AAA, nomes `deveXQuandoY`), `BeneficiarioControllerIntegrationTest` (8, `@SpringBootTest` com porta aleatória, `RestClient`, H2 e `deleteAll` antes de cada teste) e `contextLoads`, todos com `@ActiveProfiles("test")` e `src/test/resources/application-test.properties`. `./mvnw clean test`: 21 testes, BUILD SUCCESS, sem Docker. CPF repetido por corrida agora dá 409 (`saveAndFlush` + `DataIntegrityViolationException` → `CpfDuplicadoException`).
> - Migration atual: só a V1 (`beneficiarios`). A A1 usou a V2 para `produtos` na branch dela.
> - Branch `avaliacao1` (A1, módulo de Produtos): PR #13 aberto, sem merge, **congelada até a correção** (vou confirmar se o professor já corrigiu). Não fazer commit, merge nem rebase nela, nem usar "Resolve conflicts". Pode ser lida com `git show origin/avaliacao1:<arquivo>`. Atenção ao número das migrations, para não colidir com a V2 da A1.
> - `TestcontainersConfiguration` e `TestApiApplication` mantidos como o Initializr gerou, fora do `./mvnw test`.
> - WARN conhecido do springdoc 3.1.0 (`Json Processing Exception`), mantido de propósito.
>
> **Issue #9 como está no backlog (Etapa 7 — Doador e Doação, ensaio da A1)**
> - Migrations `doadores` e `doacoes`, com FK e índices
> - Enums `TipoDoador` e `TipoDoacao` com `@Enumerated(EnumType.STRING)`
> - GET de doações paginado com filtros `dataInicio`, `dataFim` e `tipo`; POST → 201
> - Validação `@DataNaoFutura` feita em TDD
> - Decisão entre 404 e 422 para doador inexistente registrada em ADR
> - Os três testes do dojo passando
> - Referências: Aulas 04, 05 e 07, roteiros do mob programming e do dojo
>
> **Ambiente e forma de trabalho**
> - PC do laboratório (usuário `Alunos`): Windows, Git Bash, JDK 25.0.1 (projeto em Java 21), **sem Docker**. Stack: Spring Boot 4.1.1, springdoc 3.1.0, H2 em memória, Flyway 12.4.0. Repositório em `/c/Users/Alunos/projeto/topicos/socialconnect_italo_bazalia_zigart`. O PC pode mudar entre aulas (LAB03-PC02, LAB03-PC03).
> - Comandos `git` na raiz do repositório; `./mvnw` dentro de `backend/`.
> - Fluxo: conferir que a máquina está igual ao GitHub → branch a partir da `main` atualizada → commits em Conventional Commits com o número da issue → PR com `Closes #N` → merge com merge commit.
> - Quero os arquivos inteiros, prontos para colar (blocos `cat` para o Git Bash, sem tabs; arquivos longos num zip com os caminhos do repositório, extraído com `unzip -o`), nunca trechos para editar. Os testes esperados e os comandos vêm junto.
> - Cada etapa nova vem com testes unitários e de integração no mesmo molde da Etapa 6.
> - O `AI_USAGE.md` faz parte da avaliação e você o mantém atualizado. A última sessão registrada é a #21, então a próxima é a #22. Código copiado da IA é declarado na linha da sessão, e o commit de docs só entra depois de eu confirmar o resultado dos testes.

**#23 — 09/11/2026** (com o zip `etapa7-doador.zip` anexado) — prompt principal abaixo; o envio da saída do terminal e das respostas do Swagger para conferência está nos tópicos da sessão #23.

> Vamos executar a Etapa 7 do projeto SocialConnect (issue #9, Doador). Anexei o etapa7-doador.zip, gerado na sessão #22.
>
> **O que já foi feito na sessão #22**
> - Mapeamento das Aulas 03–10, do edital da A2 e do repositório do professor (Prof-JamesCampos/socialconnect-api, `main` em `286d78b`).
> - A antiga #9 (Doador e Doação) virou seis issues. Já editei a #9 e criei as outras no GitHub:
>   - #9: Etapa 7, Doador.
>   - Etapas 8 e 9: Doação (registro e listagem; alteração e remoção).
>   - Etapas 10, 11 e 12: front-end.
> - Código da Etapa 7 entregue no zip anexado: 17 arquivos, sendo 14 novos e 3 alterados (`messages.properties`, `ApiApplicationTests` e `docs/openapi.yaml`). Conteúdo:
>   - V3 `doadores` com CHECK do tipo e índice `idx_doadores_nome`;
>   - `Doador`, `TipoDoador` e `DoadorRepository`;
>   - DTOs com Bean Validation;
>   - `@Telefone` (só dígitos: 10 para fixo, 11 para celular);
>   - `DoadorService` e `DoadorController`: POST → 201, GET paginado em ordem de nome, GET por id → 200 ou 404;
>   - `DoadorExemplos` para o Swagger;
>   - testes: `DoadorServiceTest` (5), `TelefoneValidatorTest` (18) e `DoadorControllerIntegrationTest` (5).
> - Esperado no `./mvnw clean test`: 49 testes e BUILD SUCCESS. Na sessão #22, o teste de integração só foi compilado, sem rodar.
>
> **O que quero agora**
> 1. Conferir o repositório e o zip antes de eu aplicar: a `main` continua em `621fe37`? O zip bate com a issue #9 como está no GitHub?
> 2. Me guiar na execução: aplicar o zip, rodar os testes e conferir pelo Swagger, fazer os commits, o push e o PR com `Closes #9`. Se algum teste falhar, corrigir com arquivos inteiros.
> 3. Depois que eu confirmar o resultado dos testes, entregar o `AI_USAGE.md` inteiro com as sessões #22 e #23.
>    - A #22 foi o mapeamento, a proposta das issues e o código da Etapa 7, copiado integralmente da IA.
>    - A #23 é esta, de execução.
>    - Inclua as perguntas de defesa da Etapa 7 para eu responder.
>
> **Estado atual**
> - Repositório: italozigart/socialconnect_italo_bazalia_zigart. `main` em `621fe37` (merge do PR #16); Etapas 0 a 6 concluídas.
> - Migrations: V1 (`beneficiarios`) na main e V3 (`doadores`) no zip. A V2 está reservada para `produtos` da A1.
> - Branch `avaliacao1` (A1, PR #13 sem merge): **congelada até a correção**. Não fazer commit, merge nem rebase nela, nem usar "Resolve conflicts". Pode ser lida com `git show origin/avaliacao1:<arquivo>`.
> - `TestcontainersConfiguration` e `TestApiApplication` continuam como o Initializr gerou, fora do `./mvnw test`.
> - O WARN do springdoc 3.1.0 (`Json Processing Exception`) é conhecido e foi mantido de propósito.
>
> **Ambiente e forma de trabalho**
> - PC do laboratório (usuário `Alunos`): Windows, Git Bash, JDK 25.0.1 (projeto em Java 21), **sem Docker**.
> - Stack: Spring Boot 4.1.1, springdoc 3.1.0, H2 em memória e Flyway 12.4.0.
> - Repositório em `/c/Users/Alunos/projeto/topicos/socialconnect_italo_bazalia_zigart`. O PC pode mudar entre aulas (LAB03-PC02, LAB03-PC03).
> - Comandos `git` na raiz do repositório; `./mvnw` dentro de `backend/`.
> - Fluxo:
>   1. conferir que a máquina está igual ao GitHub;
>   2. criar a branch `feat/doador` a partir da `main` atualizada;
>   3. commits em Conventional Commits com `#9`;
>   4. PR com `Closes #9`;
>   5. merge com merge commit.
> - Quero arquivos inteiros, prontos para colar: blocos `cat` para o Git Bash, sem tabs; arquivos longos num zip com os caminhos do repositório, extraído com `unzip -o`. Nunca trechos para editar. Os testes esperados e os comandos vêm junto.
> - O `AI_USAGE.md` faz parte da avaliação e você o mantém atualizado. A última sessão registrada é a #21. Código copiado da IA é declarado na linha da sessão, e o commit de docs só entra depois de eu confirmar o resultado dos testes.

**#24 — 12/11/2026** — prompt principal abaixo; o envio das saídas do terminal, das respostas do Swagger e das respostas de defesa está nos tópicos da sessão #24.

> Vamos para a Etapa 8 do projeto SocialConnect: Doação (registro, listagem com filtros e consulta). A Etapa 7 (issue #9, Doador) foi mergeada.
>
> **Antes de gerar código**
> 1. Confira o GitHub:
>    - a `main` com o merge do PR da Etapa 7 (`Closes #9`) e a #9 fechada;
>    - a `avaliacao1` intacta;
>    - o número e o texto da issue da Etapa 8 ("Etapa 8 — Doação: registro, listagem com filtros e consulta");
>    - o repositório do professor (Prof-JamesCampos/socialconnect-api, último commit conhecido `286d78b`).
> 2. Compare a issue com o que já existe e diga o que falta.
> 3. Depois: passo a passo, código e testes.
>
> **Estado atual**
> - Repositório: italozigart/socialconnect_italo_bazalia_zigart. Etapas 0 a 7 concluídas.
> - Beneficiário: CRUD completo, paginação e filtros, `@CPF`, Problem Details em pt-BR (400, 404, 409, 500), Swagger e `docs/openapi.yaml` alinhados (camelCase).
> - Doador (Etapa 7):
>   - V3 `doadores` com CHECK do tipo e índice `idx_doadores_nome`;
>   - `Doador`, `TipoDoador` (`@Enumerated(EnumType.STRING)`) e `DoadorRepository`;
>   - `@Telefone` (só dígitos: 10 para fixo, começando com 2 a 5; 11 para celular, começando com 9);
>   - POST → 201 com `Location`, GET paginado em ordem de nome e GET por id → 200 ou 404;
>   - sem PUT, PATCH, DELETE e 409.
> - Testes: 49 com `./mvnw clean test`, BUILD SUCCESS, sem Docker (H2, perfil `test`, `RestClient` em porta aleatória, `deleteAll` antes de cada teste).
> - Migrations: V1 `beneficiarios` e V3 `doadores` na main. A V2 está reservada para `produtos` da A1, então doações entram na **V4**.
> - Branch `avaliacao1` (A1, PR #13 sem merge): **congelada até a correção**. Não fazer commit, merge nem rebase nela, nem usar "Resolve conflicts". Pode ser lida com `git show origin/avaliacao1:<arquivo>`.
> - `TestcontainersConfiguration` e `TestApiApplication` continuam como o Initializr gerou, fora do `./mvnw test`.
> - WARN do springdoc 3.1.0 (`Json Processing Exception`) e do `PageImpl`: conhecidos, mantidos de propósito.
>
> **Critérios da issue da Etapa 8 (proposta da sessão #22)**
> - `V4__create_doacoes_table.sql` com:
>   - `id_doacao`;
>   - `id_doador` NOT NULL, com FK `fk_doacoes_doador`;
>   - `data_doacao` DATE NOT NULL;
>   - `valor` DECIMAL(10,2), maior que zero quando vier;
>   - `tipo` VARCHAR(20) NOT NULL com CHECK (`ALIMENTO`, `ROUPA`, `FINANCEIRA`);
>   - `descricao` VARCHAR(500);
>   - índices em `data_doacao`, `tipo` e `id_doador`.
> - Entity `Doacao` ligada ao `Doador` e enum `TipoDoacao` com `@Enumerated(EnumType.STRING)`.
> - `GET /api/v1/doacoes`:
>   - paginado, size 10, mais recentes primeiro;
>   - filtros `dataInicio`, `dataFim` e `tipo` opcionais, em qualquer combinação;
>   - `dataInicio` depois de `dataFim` → 400;
>   - `tipo` inválido → 400 com os valores aceitos.
> - `POST /api/v1/doacoes` → 201 com `Location`, e a resposta inclui `descricao`. `GET /api/v1/doacoes/{idDoacao}` → 200 ou 404.
> - `@DataNaoFutura` feita em TDD, com RED → GREEN → REFACTOR visível nos commits, e mensagem no `messages.properties` contendo "futuro".
> - Doador inexistente no corpo → 422, com a decisão (404 × 422) registrada em `docs/adr/0001-doador-inexistente.md`.
> - Os três testes do dojo:
>   - `deveLancarExcecaoQuandoDoadorNaoExistir` (`DoacaoServiceTest`);
>   - `deveCriarDoacaoQuandoDadosValidos` e `deveRetornar400QuandoDataFutura` (`DoacaoControllerIntegrationTest`, no H2 em vez do Testcontainers).
> - Swagger (tag Doações) e `docs/openapi.yaml` (Doações em camelCase; hoje ainda com `id_doador` em snake_case).
> - Demais testes no molde da Etapa 6; `./mvnw clean test` com BUILD SUCCESS.
> - `AI_USAGE.md` atualizado.
>
> **Cuidados já mapeados**
> - Não copiar as Doações do repositório do professor:
>   - as migrations dele criam `doacoes` duas vezes;
>   - o Flyway não roda lá;
>   - o controller não tem POST;
>   - o `toEntity` perde a `descricao`;
>   - os filtros só funcionam juntos;
>   - o `@PageableDefault` do roteiro do mob não compila.
> - Com a FK, o `deleteAll` do `DoadorControllerIntegrationTest` vai precisar apagar as doações antes.
> - Pendente: minhas respostas às 6 perguntas de defesa da Etapa 7 (estão no `AI_USAGE.md`). Vou mandá-las nesta conversa para revisão.
>
> **Ambiente e forma de trabalho**
> - PC do laboratório (usuário `Alunos`): Windows, Git Bash, JDK 25.0.1 (projeto em Java 21), **sem Docker**.
> - Stack: Spring Boot 4.1.1, Hibernate 7.4.5, springdoc 3.1.0, H2 2.4.240 em memória e Flyway 12.4.0.
> - Repositório em `/c/Users/Alunos/projeto/topicos/socialconnect_italo_bazalia_zigart`. O PC pode mudar entre aulas (LAB03-PC02, LAB03-PC03).
> - Comandos `git` na raiz do repositório; `./mvnw` dentro de `backend/`.
> - Fluxo:
>   1. conferir que a máquina está igual ao GitHub;
>   2. criar a branch `feat/doacao` a partir da `main` atualizada;
>   3. commits em Conventional Commits com o número da issue;
>   4. PR com `Closes #N`;
>   5. merge com merge commit.
> - Quero arquivos inteiros, prontos para colar: blocos `cat` para o Git Bash, sem tabs; arquivos longos num zip com os caminhos do repositório, extraído com `unzip -o`. Nunca trechos para editar. Os testes esperados, os comandos e o hash do zip vêm junto.
> - Os comandos de commit vêm num bloco único, na ordem, com o commit de docs por último.
> - O `AI_USAGE.md` faz parte da avaliação e você o mantém atualizado. A última sessão registrada é a #23, então a próxima é a #24. Código copiado da IA é declarado na linha da sessão, e o commit de docs só entra depois de eu confirmar o resultado dos testes.

**#25 — 12/11/2026** — prompt principal abaixo; o envio das saídas do terminal, das respostas do Swagger e do `curl` e das respostas de defesa está nos tópicos da sessão #25.

> Vamos para a Etapa 9 do projeto SocialConnect: Doação — alterar e remover (PUT, PATCH e DELETE). A Etapa 8 (issue #17, Doação: registro, listagem com filtros e consulta) foi mergeada no PR #23.
>
> **Antes de gerar código**
> 1. Confira o GitHub:
>    - a `main` com o merge do PR #23 (`Closes #17`) e a #17 fechada;
>    - a #9 fechada (na sessão #24 não deu para confirmar);
>    - a `avaliacao1` intacta (`5e68c1b`);
>    - o número e o texto da issue da Etapa 9 (#18, "Etapa 9 — Doação: alterar e remover (PUT, PATCH e DELETE) (Aula 05)");
>    - o repositório do professor (Prof-JamesCampos/socialconnect-api, último commit conhecido `286d78b`).
> 2. Compare a issue com o que já existe e diga o que falta.
> 3. Depois: passo a passo, código e testes.
>
> **Estado atual**
> - Repositório: italozigart/socialconnect_italo_bazalia_zigart. Etapas 0 a 8 concluídas.
> - Beneficiário (molde do PUT, PATCH e DELETE):
>   - PUT com substituição total (campo ausente vira null);
>   - PATCH parcial (null = não muda);
>   - DELETE com busca antes → 204 ou 404;
>   - paginação e filtros, `@CPF`, Problem Details em pt-BR, Swagger e `docs/openapi.yaml` em camelCase.
> - Doador: POST, GET paginado e GET por id; sem PUT, PATCH, DELETE e 409.
> - Doação (Etapa 8):
>   - V4 `doacoes`: FK `fk_doacoes_doador` criada por último (para o H2 não duplicar o índice), CHECK do valor (> 0 quando vier) e do tipo, índices em `data_doacao`, `tipo` e `id_doador`;
>   - `Doacao` com `@ManyToOne(fetch = LAZY)` para `Doador` e `TipoDoacao` com `@Enumerated(EnumType.STRING)`;
>   - `DoacaoRequestDTO`: `@NotNull`, `@DataNaoFutura`, `@Positive` + `@Digits(integer = 8, fraction = 2)` no valor, `@Size(max = 500)` na descrição;
>   - `DoacaoResponseDTO`, com `descricao`;
>   - `DoacaoService`:
>     - leituras com `@Transactional(readOnly = true)`;
>     - descrição em branco gravada como null;
>     - doador inexistente → `DoadorInexistenteException` (422, decisão em `docs/adr/0001-doador-inexistente.md`);
>     - período invertido → `PeriodoInvalidoException` (400);
>   - endpoints:
>     - GET paginado (size 10, `dataDoacao` e `idDoacao` DESC) com filtros `dataInicio`, `dataFim` e `tipo` por `Specification` (`DoacaoSpecifications`);
>     - POST → 201 com `Location`;
>     - GET `/{idDoacao}` → 200 ou 404;
>   - `@DataNaoFutura` feita em TDD (RED `c40d8ac`, GREEN `4717962`, REFACTOR `714c63f`), mensagem `validacao.data.futura`;
>   - `DoacaoExemplos` no Swagger e Doações em camelCase no contrato.
> - Mensagens genéricas desde a Etapa 8: 404 sem gênero (`{0} com id {1} não existe.`) e ordenação inválida sem sugerir atributo.
> - Testes: 77 com `./mvnw clean test`, BUILD SUCCESS, sem Docker (H2, perfil `test`, `RestClient` em porta aleatória).
>   - O `DoacaoControllerIntegrationTest` limpa doações e doadores e grava um doador antes de cada teste.
>   - O `DoadorControllerIntegrationTest` apaga as doações antes dos doadores, por causa da FK.
> - Migrations na main: V1 `beneficiarios`, V3 `doadores` e V4 `doacoes`.
>   - A V2 está reservada para `produtos` da A1, e a próxima livre é a **V5**.
>   - Se a A1 entrar na main, a migration dela precisa ser renumerada.
> - Branch `avaliacao1` (A1, PR #13 sem merge): **congelada até a correção**. Não fazer commit, merge nem rebase nela, nem usar "Resolve conflicts". Pode ser lida com `git show origin/avaliacao1:<arquivo>`.
> - `TestcontainersConfiguration` e `TestApiApplication` continuam como o Initializr gerou, fora do `./mvnw test`.
> - Avisos conhecidos e mantidos:
>   - `Json Processing Exception` (springdoc 3.1.0);
>   - `PageImpl`;
>   - `Using H2 2.4.240 which is newer...` (Flyway);
>   - `stringOrNumberMigrationVersionConverter ... not eligible` (bean do springdoc).
>
> **Critérios da issue da Etapa 9 (#18, proposta da sessão #22)**
> - `PUT /api/v1/doacoes/{idDoacao}`: substituição total com as validações do POST → 200; 404 se a doação não existir; 422 se o doador não existir.
> - `PATCH /api/v1/doacoes/{idDoacao}`: só os campos enviados mudam; a data continua sem poder estar no futuro; o doador é conferido se vier → 200.
> - `DELETE /api/v1/doacoes/{idDoacao}` → 204; o segundo DELETE → 404.
> - Swagger e `docs/openapi.yaml` com as três operações e seus erros.
> - Testes unitários e de integração no molde da Etapa 6.
> - `AI_USAGE.md` atualizado.
> - Opcional: idempotência da Aula 05. O POST de doação com cabeçalho `Idempotency-Key` devolve a mesma resposta para a mesma chave.
>
> **Cuidados já mapeados**
> - Seguir o molde do Beneficiário:
>   - o PUT copia todos os campos;
>   - o PATCH só aplica os não nulos;
>   - o DELETE busca antes (sem a busca, um id inexistente daria 204).
> - Decisões a tomar e registrar:
>   - se o PATCH pode trocar o `idDoador`;
>   - como apagar `valor` ou `descricao` (pelo PUT, como no beneficiário?);
>   - a ordem dos erros no PUT e no PATCH: corpo e `@Valid` (400) → doação da URL (404) → doador do corpo (422);
>   - se a idempotência opcional entra ou fica de fora.
> - A `@DataNaoFutura` aceita nulo, então serve no PatchDTO sem obrigar a data.
> - Atualizar o `DoacaoExemplos`, o contrato e o ADR 0001 (o 422 passa a valer também no PUT e no PATCH).
> - Ainda não há DELETE de doador, então a corrida descrita no ADR 0001 continua sem acontecer.
> - Testes manuais:
>   - o `curl` do Git Bash manda texto acentuado fora de UTF-8 e dá 400 `corpo-invalido`; usar texto sem acento ou o Swagger;
>   - no Swagger, enum é lista fechada, então valor inválido só pelo `curl`;
>   - o H2 em memória perde os dados quando a API para.
> - O ambiente da IA não acessa o Maven Central: os testes com Spring só rodam no laboratório.
>
> **Ambiente e forma de trabalho**
> - PC do laboratório (usuário `Alunos`): Windows, Git Bash, JDK 25.0.1 (projeto em Java 21), **sem Docker**.
> - Stack: Spring Boot 4.1.1, Hibernate 7.4.5, springdoc 3.1.0, H2 2.4.240 em memória e Flyway 12.4.0.
> - Repositório em `/c/Users/Alunos/projeto/topicos/socialconnect_italo_bazalia_zigart`. O PC pode mudar entre aulas (LAB03-PC02, LAB03-PC03).
> - Comandos `git` na raiz do repositório; `./mvnw` dentro de `backend/`.
> - Fluxo:
>   1. conferir que a máquina está igual ao GitHub;
>   2. criar a branch `feat/doacao-alterar-remover` a partir da `main` atualizada;
>   3. commits em Conventional Commits com o número da issue;
>   4. PR com `Closes #N`;
>   5. merge com merge commit.
> - Quero arquivos inteiros, prontos para colar: blocos `cat` para o Git Bash, sem tabs; arquivos longos num zip com os caminhos do repositório, extraído com `unzip -o`. Nunca trechos para editar.
> - Quando um arquivo muda em mais de um commit, um zip por commit, como na Etapa 8 (`unzip -o ~/etapaN/NN-nome.zip` e `git add $(unzip -Z1 ...)`).
> - Os testes esperados, os comandos e o hash do zip vêm junto.
> - Os comandos de commit vêm em ordem, com o commit de docs por último. Eles só se dividem em blocos onde um teste precisa rodar antes do commit.
> - O `AI_USAGE.md` faz parte da avaliação e você o mantém atualizado. A última sessão registrada é a #24, então a próxima é a #25. Código copiado da IA é declarado na linha da sessão, e o commit de docs só entra depois de eu confirmar o resultado dos testes.
> - As perguntas de defesa da Etapa 9 vêm com a entrega. Eu respondo nesta conversa para revisão, e elas entram no `AI_USAGE.md`.

---

_Declaração: Ao submeter este repositório, confirmo que todo o código foi
revisado, testado e compreendido por mim._
