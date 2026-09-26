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
- Como regra, o código e os artefatos do repositório são escritos por mim. Quando uma saída da IA for copiada para o repositório, isso é declarado na linha da sessão, com o arquivo afetado e o que a IA alterou (primeiro caso: `application.properties`, sessão #10).
- Todo alerta técnico vindo da IA é verificado na prática (log, teste ou documentação oficial) antes de virar decisão.
- Este arquivo é organizado com apoio da IA a partir das sessões de orientação; o conteúdo das colunas "Uso da saída" e "Como validei" é confirmado por mim antes de cada commit.
- Sessões curtas de suporte (dúvidas pontuais e erros de terminal) são agrupadas numa única linha, com os tópicos listados.

## Resumo por etapa

| Etapa | Sessões de IA | Situação |
|-------|---------------|----------|
| Planejamento | #1 | Roteiro em etapas (0 a 7) adotado |
| Etapa 0 — Repositório | #2 a #9 | Concluída: PR #2 mergeado, issue #1 fechada, `main` protegida, backlog (issues #3 a #9) criado na milestone A1 |
| Etapa 1 — Esqueleto Spring Boot | #10 | Em PR (issue #3): aplicação sobe com H2, Flyway, H2 console e Swagger |

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

### Perguntas de defesa — Etapa 0 (respostas minhas)

1. Por que `docs/extensao/` precisou de um arquivo dentro? O Git não versiona pasta vazia.
2. O que o `Closes #1` fez? Fechou a issue automaticamente no merge do PR.
3. Merge commit × squash: o squash junta os commits pequenos em um só; o merge commit os preserva na linha do tempo do desenvolvimento.

### Perguntas de defesa — Etapa 1 (respostas minhas)

1. Por que `ddl-auto=validate`, e não `update`, com Flyway? Porque o Flyway é responsável por criar e alterar a estrutura do banco, enquanto o Hibernate apenas valida se as entidades estão de acordo com as tabelas existentes.
2. Projeto em Java 21 rodando com JDK 25: o JDK 25 é compatível com versões anteriores do Java, permitindo executar aplicações compiladas para versões anteriores.
3. Prova de que o Flyway roda: ao acessar o H2, é possível verificar a tabela `flyway_schema_history`, criada e utilizada pelo Flyway para registrar as migrations futuras.
4. Por que `git add --chmod=+x` no `mvnw`? Porque o `mvnw` é um script que precisa ter permissão de execução. O comando faz o Git registrar o arquivo com essa permissão, permitindo executá-lo diretamente, principalmente em ambientes Linux/Unix e no CI/CD.

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

---

_Declaração: Ao submeter este repositório, confirmo que todo o código foi
revisado, testado e compreendido por mim._
