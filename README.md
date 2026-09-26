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
| Back-end | Java LTS + Spring Boot + Maven | [registrar na aula 03] |
| Front-end | React + TypeScript + Vite + Material UI | [registrar na aula 09] |
| Banco de dados | [PostgreSQL ou similar] | [registrar na aula 04] |
| DevOps | Git + Docker + Docker Compose + CI ([GitHub Actions/GitLab CI]) | [registrar na aula 13] |

> As versões são registradas no início do semestre e só mudam com decisão da equipe documentada.

## 🚀 Como executar
> _Seção preenchida a partir da aula 03. Enquanto isso, nada roda aqui ainda._

**Pré-requisitos:** [JDK LTS, Maven Wrapper, Node.js, Docker…]

```bash
# Back-end (a partir da aula 03)
cd backend
./mvnw spring-boot:run

# Front-end (a partir da aula 09)
cd frontend
npm install
npm run dev
```

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
- Prompts significativos são registrados em `docs/extensao/AI_USAGE.md` (data, ferramenta, prompt, como a saída foi validada);
- Saídas de IA **não são confiadas cegamente**: passam por revisão, testes e verificação de segurança antes do merge.

## 📚 Documentação e referências
- Plano de ensino da disciplina (AVA/Teams)
- [Spring Boot Docs](https://docs.spring.io/spring-boot/) • [React](https://react.dev/) • [Material UI](https://mui.com/)
- [OpenAPI Specification](https://spec.openapis.org/oas/latest.html) • [OWASP Top 10](https://owasp.org/www-project-top-ten/)

## 📄 Licença
[Definir com o professor/parceiro externo na aula 15, considerando o contexto da extensão]