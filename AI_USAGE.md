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
- O código e os artefatos do repositório são escritos por mim; a IA não entrega código pronto das etapas.
- Todo alerta técnico vindo da IA é verificado na prática (log, teste ou documentação oficial) antes de virar decisão.
- Este arquivo é organizado com apoio da IA a partir das sessões de orientação; o conteúdo das colunas "Uso da saída" e "Como validei" é confirmado por mim antes de cada commit.

## Resumo por etapa

| Etapa | Sessões de IA | Situação |
|-------|---------------|----------|
| Planejamento | #1 | Roteiro em etapas (0 a 7) adotado |
| Etapa 0 — Repositório | #2, #3, #4, #5, #6, #7 | Em andamento |

## Registro

| # | Data | Aula / Etapa | Ferramenta | Prompt (resumo) | Uso da saída | Como validei |
|---|------|--------------|------------|-----------------|--------------|--------------|
| 1 | 25/09/2026 | Planejamento (material das Aulas 03–07) | Claude Opus 5.5 (claude.ai) | Analisar as anotações de aula e os templates (zip anexado) e explicar estrutura, repositórios e tecnologias; montar um passo a passo para iniciar o projeto, com orientação em vez de código pronto. | Usado como referência: roteiro em etapas (0 a 7) adotado como plano de trabalho. Para responder, a IA também leu o repositório público do professor e consultou documentação do Spring Boot 4 e do Testcontainers na web. | Pendente: os alertas técnicos (Flyway, H2 console e Testcontainers no Spring Boot 4) serão conferidos na execução das Etapas 1, 2 e 6. |
| 2 | 25/09/2026 | Etapa 0 (repositório) | Claude Opus 5.5 (claude.ai) | Pedir o passo a passo da Etapa 0, o que mais ler (ex.: repositório do professor) e que a IA mantenha este registro. | Guia da Etapa 0 e lista de leitura usados como referência; rascunho deste arquivo gerado pela IA e revisado por mim. | _Pendente — confirmar ao concluir a Etapa 0._ |
| 3 | 25/09/2026 | Etapa 0 (detalhamento) | Claude Opus 5.5 (claude.ai) | Dúvida sobre rodar `git config --global` sem repositório criado; pedido de passo a passo detalhado de cada ação da Etapa 0. | Guia usado como referência para executar a Etapa 0; comandos digitados e arquivos preenchidos por mim. | _Pendente — ex.: `git config --global --list` conferido; issue #1 fechada pelo PR._ |
| 4 | 25/09/2026 | Etapa 0 (proteção da `main`) | Claude Opus 5.5 (claude.ai) | Envio de print da tela Settings do GitHub: não havia "Branches" nem regra clássica de proteção. | Orientação para proteger a `main` via Rulesets, seguida por mim; a IA também apontou erro de digitação no nome do repositório. | _Pendente — ex.: ruleset `proteger-main` ativo; push direto na `main` recusado com GH013._ |
| 5 | 25/09/2026 | Etapa 0 (registro de IA) | Claude Opus 5.5 (claude.ai) | Perguntar quem mantém este registro e pedir que a IA vá organizando as informações. | IA passou a entregar este arquivo completo e atualizado a cada sessão; eu reviso e commito. | Revisei o arquivo antes do commit. |
| 6 | 25/09/2026 | Etapa 0 (issue #1) | Claude Opus 5.5 (claude.ai) | Envio de print da criação da issue #1 para conferir se o passo B4 estava correto. | IA confirmou o formato e apontou item faltante no checklist (template de PR); issue ajustada e criada por mim. | _Pendente — ex.: issue #1 criada com o checklist completo._ |
| 7 | 25/09/2026 | Etapa 0 (issue #1) | Claude Opus 5.5 (claude.ai) | Pedido de confirmação direta de que a issue do print estava no formato certo. | IA confirmou que sim, com um item a acrescentar ao checklist; issue criada por mim. | _Pendente — ex.: issue #1 criada._ |

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

---

_Declaração: Ao submeter este repositório, confirmo que todo o código foi
revisado, testado e compreendido por mim._
