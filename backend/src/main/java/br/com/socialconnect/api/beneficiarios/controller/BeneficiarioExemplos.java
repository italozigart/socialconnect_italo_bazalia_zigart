package br.com.socialconnect.api.beneficiarios.controller;

/**
 * Exemplos de erro mostrados no Swagger, um por situação, com os textos do
 * messages.properties. Se uma mensagem mudar lá, mude aqui também: o Swagger
 * não lê o messages.properties. Valor de anotação precisa ser constante, e
 * text block em static final é constante.
 */
final class BeneficiarioExemplos {

    private BeneficiarioExemplos() {
    }

    static final String DADOS_INVALIDOS = """
            {
              "type": "https://socialconnect.api/errors/validacao",
              "title": "Dados inválidos",
              "status": 400,
              "detail": "Um ou mais campos são inválidos. Veja a lista em errors.",
              "instance": "/api/v1/beneficiarios",
              "timestamp": "2026-10-03T22:30:00Z",
              "errors": [
                { "field": "cpf", "message": "CPF inválido. Informe os 11 dígitos, sem pontos nem traço." },
                { "field": "nome", "message": "O nome é obrigatório." }
              ]
            }
            """;

    static final String DADOS_INVALIDOS_PATCH = """
            {
              "type": "https://socialconnect.api/errors/validacao",
              "title": "Dados inválidos",
              "status": 400,
              "detail": "Um ou mais campos são inválidos. Veja a lista em errors.",
              "instance": "/api/v1/beneficiarios/1",
              "timestamp": "2026-10-03T22:30:00Z",
              "errors": [
                { "field": "nome", "message": "O nome não pode ficar em branco." }
              ]
            }
            """;

    static final String ORDENACAO_INVALIDA = """
            {
              "type": "https://socialconnect.api/errors/ordenacao-invalida",
              "title": "Ordenação inválida",
              "status": 400,
              "detail": "Não é possível ordenar por \\"idade\\". Use um atributo do recurso, como nome.",
              "instance": "/api/v1/beneficiarios",
              "timestamp": "2026-10-03T22:30:00Z",
              "errors": []
            }
            """;

    static final String ID_INVALIDO = """
            {
              "type": "https://socialconnect.api/errors/parametro-invalido",
              "title": "Parâmetro inválido",
              "status": 400,
              "detail": "O parâmetro \\"idBeneficiario\\" não aceita o valor \\"abc\\".",
              "instance": "/api/v1/beneficiarios/abc",
              "timestamp": "2026-10-03T22:30:00Z",
              "errors": []
            }
            """;

    static final String NAO_ENCONTRADO = """
            {
              "type": "https://socialconnect.api/errors/recurso-nao-encontrado",
              "title": "Recurso não encontrado",
              "status": 404,
              "detail": "Beneficiário com id 99 não encontrado.",
              "instance": "/api/v1/beneficiarios/99",
              "timestamp": "2026-10-03T22:30:00Z",
              "errors": []
            }
            """;

    static final String CPF_DUPLICADO = """
            {
              "type": "https://socialconnect.api/errors/cpf-duplicado",
              "title": "CPF já cadastrado",
              "status": 409,
              "detail": "Já existe um beneficiário com o CPF 52998224725.",
              "instance": "/api/v1/beneficiarios",
              "timestamp": "2026-10-03T22:30:00Z",
              "errors": []
            }
            """;

    static final String ERRO_INTERNO = """
            {
              "type": "https://socialconnect.api/errors/erro-interno",
              "title": "Erro interno do servidor",
              "status": 500,
              "detail": "Ocorreu um erro inesperado. Tente novamente mais tarde.",
              "instance": "/api/v1/beneficiarios",
              "timestamp": "2026-10-03T22:30:00Z",
              "errors": []
            }
            """;
}
