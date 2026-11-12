# ADR 0001 — Doador inexistente no registro de doação: 422, e não 404

- **Status:** aceita
- **Data:** 12/11/2026
- **Issue:** #17 (Etapa 8 — Doação: registro, listagem com filtros e consulta)
- **Uso de IA:** análise feita com apoio de IA, registrada no `AI_USAGE.md` (sessão #24)

## Contexto

O `POST /api/v1/doacoes` recebe o doador pelo id, no corpo:

```json
{ "idDoador": 99, "dataDoacao": "2026-11-10", "tipo": "ALIMENTO" }
```

O JSON pode estar bem formado e passar em todo o Bean Validation (`idDoador`
presente, data válida, tipo da lista) e, mesmo assim, o doador 99 não existir.
A V4 tem a FK `fk_doacoes_doador`, então o banco recusaria o INSERT. Sem
tratamento, essa recusa vira uma `DataIntegrityViolationException` e cai no
handler genérico (500), como se o erro fosse do servidor.

O roteiro do dojo da Aula 07 pede um teste para esse caso
(`deveLancarExcecaoQuandoDoadorNaoExistir`), mas não diz qual status a API
devolve. A issue #17 pede que a escolha entre 404 e 422 fique registrada aqui.

## Opções consideradas

| Status | Quando a RFC 9110 usa | Serve aqui? |
|---|---|---|
| **404 Not Found** | O servidor não achou o recurso **da URL** (o alvo da requisição). | Não. O alvo é `/api/v1/doacoes`, e ele existe. O 404 faria o cliente achar que errou o endereço. Além disso, a API já usa 404 no `GET /api/v1/doacoes/{idDoacao}` com um id que não existe: o mesmo código teria dois sentidos no mesmo recurso. |
| **400 Bad Request** | Erro do cliente na forma da requisição (sintaxe malformada, por exemplo). | Não. Neste projeto, o 400 é do JSON malformado e do Bean Validation, que o cliente corrige mudando o formato dos dados. Aqui o formato está certo. |
| **409 Conflict** | A requisição conflita com o **estado atual do recurso alvo** (no projeto: CPF repetido). | Não. Não há conflito com uma doação existente; falta outro recurso, o doador. |
| **422 Unprocessable Content** | O servidor entende o tipo do conteúdo e a sintaxe está correta, mas não consegue processar as instruções que ele traz. | Sim. O JSON é válido, mas aponta para um doador que não existe. A tabela de status da Aula 05 também traz o 422 para "semântica inválida". |

## Decisão

**422**, em Problem Details:

- `type`: `https://socialconnect.api/errors/doador-inexistente`
- `title`: `Doador inexistente`
- `detail`: `Não existe doador com id 99. Cadastre o doador antes de registrar a doação.`

Como funciona:

1. O `@Valid` confere o formato. Se algum campo falhar, a resposta é 400, e o
   banco nem é consultado.
2. O `DoacaoService.criar` busca o doador com `DoadorRepository.findById`. Se
   não achar, lança `DoadorInexistenteException`, e o `GlobalExceptionHandler`
   responde 422.
3. Se achar, a doação é gravada apontando para esse doador. A FK da V4 continua
   como segunda barreira.

O `GET /api/v1/doacoes/{idDoacao}` continua com 404 para uma doação que não
existe: aí, o id que falta está na URL.

## Consequências

- O front-end (Etapas 10 a 12) consegue separar "endereço ou doação
  inexistente" (404), "corrija o formulário" (400) e "o doador escolhido não
  existe" (422).
- O teste do dojo (`deveLancarExcecaoQuandoDoadorNaoExistir`) cobre a regra no
  Service, sem banco, simulando o `findById` do `DoadorRepository`; o
  `DoacaoControllerIntegrationTest` cobre o 422 de ponta a ponta.
- Cada POST de doação faz um `SELECT` a mais (a busca do doador antes do
  INSERT). Pela chave primária de `doadores`, é uma leitura direta.
- Corrida: se o doador fosse apagado entre a busca e o INSERT, a FK recusaria
  a gravação e a resposta seria 500. Hoje a API não tem DELETE de doador, então
  isso não acontece. Se um dia tiver, o caminho é o mesmo do CPF repetido na
  Etapa 6: transformar a `DataIntegrityViolationException` em
  `DoadorInexistenteException`.
- O 422 está documentado no Swagger (`DoacaoController`) e no
  `docs/openapi.yaml`.

## Referências

- RFC 9110 (HTTP Semantics), seções 15.5.1 (400), 15.5.5 (404), 15.5.10 (409)
  e 15.5.21 (422).
- RFC 9457 (Problem Details for HTTP APIs), que substitui a RFC 7807 citada na
  Aula 06.
- Aula 05: tabela de códigos de status (422, "semântica inválida").
- Aula 07 e roteiro do dojo: teste `deveLancarExcecaoQuandoDoadorNaoExistir`.
