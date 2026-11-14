# socialconnect_italo_bazalia_zigart

# Social Connect — INDIVIDUAL

> Solução web full stack desenvolvida na disciplina **Tópicos Especiais em
> Sistemas para Internet III**, Tecnologia em Sistemas para Internet,
> 2º semestre de 2026 — vinculada ao **projeto de extensão** da disciplina.

## 🧭 Sobre a solução
- **Problema que resolve:** [Conecta ação social ao beneficiário.]
- **Público-alvo:** [quem se beneficia da solução]
- **Status:** 🚧 em construção — produto do semestre letivo

## 👥 Equipe
| Nome | GitHub | Papéis rotativos (rodízio por sprint) |
|---|---|---|
| [Italo Bazalia Zigart] | [@italozigart](https://github.com/italozigart) | Todo o projeto

## 🛠️ Stack
| Camada | Tecnologia | Versão registrada |
|---|---|---|
| Back-end | Java 21 + Spring Boot 4.1.1 + Maven [versão do ./mvnw -v] (Apache Maven 3.9.16) | JDK 25.0.1 usado no laboratório; o projeto compila para Java 21 |
| Banco de dados | H2 2.4.240 (desenvolvimento) · PostgreSQL | PostgreSQL: [registrar na aula 04] |
| Migrations | Flyway (starter do Spring Boot) | 12.4.0 |
| Documentação da API | springdoc-openapi | 3.1.0 |
| Testes | JUnit Jupiter + Mockito + AssertJ (starters de teste do Spring Boot); integração com H2 em memória, sem Docker | JUnit Jupiter 6.0.3 · Mockito 5.23.0 · AssertJ 3.27.7 (versões gerenciadas pelo Spring Boot 4.1.1) |
| Front-end | React + TypeScript + Vite (criado com `create-vite` 9.3.4, template `react-ts`) + Axios; Material UI (MUI), React Hook Form e Zod a partir da aula 10 | React 19.3.1 · TypeScript 7.0.2 · Vite 8.3.8 · Axios 1.21.1 · MUI 9.5.0 (`@mui/material` e `@mui/icons-material`, com `@emotion/react` 11.14.0 e `@emotion/styled` 11.14.1) · React Hook Form 7.92.0 (com `@hookform/resolvers` 7.0.6) · Zod 4.6.7 |
| Ferramentas do front-end | Node.js + npm; lint com Oxlint (padrão do template) | Node.js 26.13.1 (LTS, já instalado no laboratório) · npm 11.20.0 · Oxlint 1.93.0. Versões exatas no `frontend/package-lock.json` |

> As versões são registradas no início do semestre e só mudam com decisão da equipe documentada.

## 🚀 Como executar
**Pré-requisitos:**
- JDK 21 ou superior. Não é preciso instalar o Maven: o `./mvnw` baixa a versão certa.
- Node.js 20.19+ ou 22.12+ (o Vite 8 recusa versões mais antigas), com o npm que vem junto.
- Docker entra nas próximas etapas.

### Back-end (a partir da aula 03)
```bash
cd backend
./mvnw spring-boot:run
```

Com o back-end rodando:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI (JSON): http://localhost:8080/api-docs
- Console do H2: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:socialconnectdb`, usuário `sa`, senha vazia)

O H2 é em memória: os dados somem quando a API para.

Testes do back-end (a partir da aula 07), unitários e de integração, sem Docker:
```bash
cd backend
./mvnw test
```

### Front-end (a partir da aula 09)
Em outro terminal, com o back-end rodando:
```bash
cd frontend
npm install
npm run dev
```
Abra http://localhost:5173. A tela de beneficiários:
- **lista** numa tabela do MUI, 10 por página (`GET /api/v1/beneficiarios?page=0&size=10&sort=nome,asc&sort=idBeneficiario,asc`), com ordenação por nome, CPF ou data de cadastro (clique no título da coluna), paginação embaixo da tabela, linhas cinza (Skeleton) durante o carregamento e um aviso com ícone quando não há ninguém cadastrado;
- **cadastra** pelo botão **Novo beneficiário** (`POST /api/v1/beneficiarios`), com a validação do Zod antes do envio e os erros da API (400 e 409) embaixo de cada campo;
- **edita** pelo ícone de lápis da linha (`PUT /api/v1/beneficiarios/{id}`), no mesmo formulário do cadastro, já preenchido;
- **exclui** pelo ícone de lixeira (`DELETE /api/v1/beneficiarios/{id}`), depois de uma confirmação.

No celular (tela com menos de 600px), a tabela mostra só o nome, o CPF e os botões; o telefone aparece a partir de 600px, e a data de cadastro a partir de 900px.

Outros comandos, dentro de `frontend/`:
```bash
npm run build     # confere os tipos (tsc) e gera o build em frontend/dist
npm run lint      # Oxlint
npm run preview   # serve o build, também na porta 5173
```

O `npm run build` avisa que o pacote JavaScript passa de 500 kB (*Some chunks are larger than 500 kB*): é só um aviso, porque o React, o MUI, o Zod, o Axios e o React Hook Form vão juntos num arquivo só (cerca de 210 kB com gzip). Dos ícones do `@mui/icons-material`, só os três usados entram no build.

O front precisa rodar em **http://localhost:5173**: é a única origem que o `CorsConfig` da API libera. Por isso o `vite.config.ts` fixa a porta (`strictPort`): se a 5173 estiver ocupada, o Vite para com erro em vez de subir em outra porta. Abrir pelo endereço `http://127.0.0.1:5173` também não funciona, porque é outra origem.

## 🌿 Fluxo de trabalho Git
1. Toda tarefa nasce como **issue** vinculada ao backlog.
2. Branch a partir da `main`: `feat/nome-curto`, `fix/nome-curto`, `docs/nome-curto`.
3. Commits pequenos referenciando a issue (ex.: `feat: adiciona endpoint de cadastro #12`).
4. **Pull request** com descrição do que mudou, como testar e evidências.
5. CI deve passar + 1 aprovação → merge na `main` (branch protegida, sem push direto).

## ✅ Definition of Done
_Consultar a seção Working agreement acima. A definição evolui nas retrospectivas._

## 📁 Estrutura do repositório
```
├── docs/extensao/    # artefatos do projeto de extensão
├── backend/          # API Spring Boot (aula 03+)
├── frontend/         # aplicação React (aula 09+)
└── docker-compose.yml# ambiente integrado (aula 13+)
```

## 🤖 Uso responsável de IA generativa
Este projeto pode utilizar IA generativa **como apoio**, seguindo as regras da disciplina:
- Toda contribuição relevante de IA é **declarada** na descrição do PR;
- Prompts significativos são registrados em `AI_USAGE.md` (data, ferramenta, prompt, como a saída foi validada);
- Saídas de IA **não são confiadas cegamente**: passam por revisão, testes e verificação de segurança antes do merge.

## 📚 Documentação e referências
- Plano de ensino da disciplina (AVA/Teams)
- [Spring Boot Docs](https://docs.spring.io/spring-boot/) • [React](https://react.dev/) • [Material UI](https://mui.com/)
- [OpenAPI Specification](https://spec.openapis.org/oas/latest.html) • [OWASP Top 10](https://owasp.org/www-project-top-ten/)

## 📄 Licença
[Definir com o professor/parceiro externo na aula 15, considerando o contexto da extensão]