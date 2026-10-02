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

> As versões são registradas no início do semestre e só mudam com decisão da equipe documentada.

## 🚀 Como executar
**Pré-requisitos:** JDK 21 ou superior. Não é preciso instalar o Maven: o `./mvnw` baixa a versão certa. Node.js e Docker entram nas próximas etapas.

```bash
# Back-end (a partir da aula 03)
cd backend
./mvnw spring-boot:run

# Front-end (a partir da aula 09)
cd frontend
npm install
npm run dev

Com o back-end rodando:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI (JSON): http://localhost:8080/api-docs
- Console do H2: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:socialconnectdb`, usuário `sa`, senha vazia)
```

## 📦 Módulo de Produtos (Avaliação A1)

Controle do estoque de doações físicas: cadastro, consulta, atualização e remoção de produtos.

### Como rodar e testar
```bash
cd backend
./mvnw clean compile      # compila
./mvnw test               # testes unitários e de integração (H2 em memória, sem Docker)
./mvnw spring-boot:run    # sobe a API em http://localhost:8080
```
Swagger UI: http://localhost:8080/swagger-ui.html (tag **Produtos**).

### Endpoints
| Método | Caminho | Sucesso | Erros |
|---|---|---|---|
| GET | `/api/v1/produtos?nome=&categoria=&page=0&size=10&sort=nome,asc` | 200 (página) | 400 |
| GET | `/api/v1/produtos/{id_produto}` | 200 | 400, 404 |
| POST | `/api/v1/produtos` | 201 + `Location` | 400, 409, 422 |
| PUT | `/api/v1/produtos/{id_produto}` | 200 | 400, 404, 409, 422 |
| DELETE | `/api/v1/produtos/{id_produto}` | 204 | 400, 404 |

Todos os erros saem em Problem Details (RFC 7807): `type`, `title`, `status`, `detail`, `instance`, `timestamp` e `errors` (campos inválidos).

### Regras de negócio
- **Estoque não negativo:** `estoqueAtual < 0` → **422**.
- **Nome único** (sem diferenciar maiúsculas) → **409**, no POST e no PUT.
- **Estoque baixo:** a resposta traz `estoqueBaixo = estoqueAtual < estoqueMinimo` (calculado, não gravado).
- `idProduto` e `dataCadastro` são gerados pelo servidor.

### Decisões
- **422 e não 400 para estoque negativo:** o `estoqueAtual` tem `@NotNull`, mas não `@Min(0)`. Com `@Min`, o Bean Validation responderia 400 e a regra de negócio (422) nunca seria alcançada. O banco tem `CHECK (estoque_atual >= 0)` como última barreira.
- **Validação customizada `@UnidadeMedida`:** aceita `kg`, `g`, `l`, `ml`, `unidade`, `pacote` ou `caixa` (sem diferenciar maiúsculas; gravada em minúsculas).
- **Migration V2:** é o próximo número livre deste repositório (só existia a V1).
- **i18n:** mensagens em `messages.properties`, com locale fixo em pt-BR (`spring.web.locale`).
- **Testes de integração com H2:** a orientação final da avaliação dispensou o Docker, então o `ProdutoControllerIntegrationTest` usa `@SpringBootTest(webEnvironment = RANDOM_PORT)` com o H2 em memória, em vez de Testcontainers.

## Declaração de Uso de IA (A1)

### Ferramentas utilizadas:
- [x] Claude (claude.ai, Claude Opus 5.5)
- [ ] Copilot / Codeium
- [ ] Nenhuma

### Como utilizei:
- Pedi à IA a leitura do enunciado da A1 e um levantamento do que criar, com as armadilhas do enunciado (conflito entre `@Min` e o 422, `@NotNull` faltando no exemplo, `@Enumerated(EnumType.STRING)`).
- Com o códigos prontos, revisei toda a aplicação com IA corrigindo bugs e falhas. Antes da entrega, a IA testou a V2 num H2 2.4.240, compilou as classes e rodou os testes unitários num ambiente próprio, sem o Spring completo. A validação final é a minha: `./mvnw clean compile`, `./mvnw test` e os testes pelo Swagger.
- Após isso compilei, testei e fiz os commits para o repositórios mergeando a PR.

### O que eu entendo 100%:
- A lógica da POO e MVC, testes de API Rest.

### O que precisei estudar mais:
- métodos, funcções, bibliootecas e dependências disponíveis do framework pra facilitar o trabalho e principalmente CI/CD com base em versionamento Git.

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