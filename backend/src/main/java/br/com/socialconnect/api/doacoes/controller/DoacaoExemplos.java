package br.com.socialconnect.api.doacoes.controller;

/**
 * Exemplos de erro mostrados no Swagger para os endpoints de doação, um por
 * situação, com os textos do messages.properties. Mesmo molde do
 * DoadorExemplos: se uma mensagem mudar lá, mude aqui também, porque o
 * Swagger não lê o messages.properties.
 * <p>
 * Os erros de validação vêm em ordem alfabética do campo, como o
 * GlobalExceptionHandler devolve.
 * <p>
 * O PUT e o PATCH (Etapa 9) reaproveitam os exemplos do POST, como o PUT do
 * beneficiário: o corpo do erro é o mesmo, e só o instance mudaria
 * (/api/v1/doacoes/1). O PATCH tem um exemplo próprio de validação, porque
 * lá nenhum campo é obrigatório.
 */
final class DoacaoExemplos {

    private DoacaoExemplos() {
    }

    static final String DADOS_INVALIDOS = """
            {
              "type": "https://socialconnect.api/errors/validacao",
              "title": "Dados inválidos",
              "status": 400,
              "detail": "Um ou mais campos são inválidos. Veja a lista em errors.",
              "instance": "/api/v1/doacoes",
              "timestamp": "2026-11-12T22:30:00Z",
              "errors": [
                { "field": "dataDoacao", "message": "A data não pode estar no futuro. Informe a data de hoje ou uma data passada." },
                { "field": "idDoador", "message": "O doador é obrigatório: informe o idDoador." },
                { "field": "tipo", "message": "O tipo é obrigatório: ALIMENTO, ROUPA ou FINANCEIRA." },
                { "field": "valor", "message": "O valor deve ser maior que zero." }
              ]
            }
            """;

    static final String DADOS_INVALIDOS_PATCH = """
            {
              "type": "https://socialconnect.api/errors/validacao",
              "title": "Dados inválidos",
              "status": 400,
              "detail": "Um ou mais campos são inválidos. Veja a lista em errors.",
              "instance": "/api/v1/doacoes/1",
              "timestamp": "2026-11-12T22:30:00Z",
              "errors": [
                { "field": "dataDoacao", "message": "A data não pode estar no futuro. Informe a data de hoje ou uma data passada." },
                { "field": "valor", "message": "O valor deve ser maior que zero." }
              ]
            }
            """;

    static final String CORPO_INVALIDO = """
            {
              "type": "https://socialconnect.api/errors/corpo-invalido",
              "title": "Corpo da requisição inválido",
              "status": 400,
              "detail": "O JSON está malformado ou algum campo tem tipo ou valor não aceito.",
              "instance": "/api/v1/doacoes",
              "timestamp": "2026-11-12T22:30:00Z",
              "errors": []
            }
            """;

    static final String DOADOR_INEXISTENTE = """
            {
              "type": "https://socialconnect.api/errors/doador-inexistente",
              "title": "Doador inexistente",
              "status": 422,
              "detail": "Não existe doador com id 99. Informe o id de um doador já cadastrado.",
              "instance": "/api/v1/doacoes",
              "timestamp": "2026-11-12T22:30:00Z",
              "errors": []
            }
            """;

    static final String PERIODO_INVALIDO = """
            {
              "type": "https://socialconnect.api/errors/periodo-invalido",
              "title": "Período inválido",
              "status": 400,
              "detail": "A dataInicio (2026-11-10) não pode ser depois da dataFim (2026-11-01).",
              "instance": "/api/v1/doacoes",
              "timestamp": "2026-11-12T22:30:00Z",
              "errors": []
            }
            """;

    static final String TIPO_INVALIDO = """
            {
              "type": "https://socialconnect.api/errors/parametro-invalido",
              "title": "Parâmetro inválido",
              "status": 400,
              "detail": "O parâmetro \\"tipo\\" não aceita o valor \\"DINHEIRO\\". Valores aceitos: ALIMENTO, ROUPA, FINANCEIRA.",
              "instance": "/api/v1/doacoes",
              "timestamp": "2026-11-12T22:30:00Z",
              "errors": []
            }
            """;

    static final String ORDENACAO_INVALIDA = """
            {
              "type": "https://socialconnect.api/errors/ordenacao-invalida",
              "title": "Ordenação inválida",
              "status": 400,
              "detail": "Não é possível ordenar por \\"idade\\": o recurso não tem esse atributo.",
              "instance": "/api/v1/doacoes",
              "timestamp": "2026-11-12T22:30:00Z",
              "errors": []
            }
            """;

    static final String ID_INVALIDO = """
            {
              "type": "https://socialconnect.api/errors/parametro-invalido",
              "title": "Parâmetro inválido",
              "status": 400,
              "detail": "O parâmetro \\"idDoacao\\" não aceita o valor \\"abc\\".",
              "instance": "/api/v1/doacoes/abc",
              "timestamp": "2026-11-12T22:30:00Z",
              "errors": []
            }
            """;

    static final String NAO_ENCONTRADO = """
            {
              "type": "https://socialconnect.api/errors/recurso-nao-encontrado",
              "title": "Recurso não encontrado",
              "status": 404,
              "detail": "Doação com id 99 não existe.",
              "instance": "/api/v1/doacoes/99",
              "timestamp": "2026-11-12T22:30:00Z",
              "errors": []
            }
            """;

    static final String ERRO_INTERNO = """
            {
              "type": "https://socialconnect.api/errors/erro-interno",
              "title": "Erro interno do servidor",
              "status": 500,
              "detail": "Ocorreu um erro inesperado. Tente novamente mais tarde.",
              "instance": "/api/v1/doacoes",
              "timestamp": "2026-11-12T22:30:00Z",
              "errors": []
            }
            """;
}
