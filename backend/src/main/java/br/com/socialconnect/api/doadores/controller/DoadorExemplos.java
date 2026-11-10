package br.com.socialconnect.api.doadores.controller;

/**
 * Exemplos de erro mostrados no Swagger para os endpoints de doador, um por
 * situação, com os textos do messages.properties. Mesmo molde do
 * BeneficiarioExemplos: se uma mensagem mudar lá, mude aqui também, porque o
 * Swagger não lê o messages.properties.
 * <p>
 * Os erros de validação vêm em ordem alfabética do campo, como o
 * GlobalExceptionHandler devolve.
 */
final class DoadorExemplos {

    private DoadorExemplos() {
    }

    static final String DADOS_INVALIDOS = """
            {
              "type": "https://socialconnect.api/errors/validacao",
              "title": "Dados inválidos",
              "status": 400,
              "detail": "Um ou mais campos são inválidos. Veja a lista em errors.",
              "instance": "/api/v1/doadores",
              "timestamp": "2026-11-09T22:30:00Z",
              "errors": [
                { "field": "email", "message": "E-mail inválido. Use o formato nome@dominio.com." },
                { "field": "nome", "message": "O nome é obrigatório." },
                { "field": "telefone", "message": "Telefone inválido. Informe DDD e número, só dígitos: 10 para fixo ou 11 para celular (ex.: 11999999999)." },
                { "field": "tipo", "message": "O tipo é obrigatório: PESSOA_FISICA ou PESSOA_JURIDICA." }
              ]
            }
            """;

    static final String CORPO_INVALIDO = """
            {
              "type": "https://socialconnect.api/errors/corpo-invalido",
              "title": "Corpo da requisição inválido",
              "status": 400,
              "detail": "O JSON está malformado ou algum campo tem tipo ou valor não aceito.",
              "instance": "/api/v1/doadores",
              "timestamp": "2026-11-09T22:30:00Z",
              "errors": []
            }
            """;

    static final String ORDENACAO_INVALIDA = """
            {
              "type": "https://socialconnect.api/errors/ordenacao-invalida",
              "title": "Ordenação inválida",
              "status": 400,
              "detail": "Não é possível ordenar por \\"idade\\". Use um atributo do recurso, como nome.",
              "instance": "/api/v1/doadores",
              "timestamp": "2026-11-09T22:30:00Z",
              "errors": []
            }
            """;

    static final String ID_INVALIDO = """
            {
              "type": "https://socialconnect.api/errors/parametro-invalido",
              "title": "Parâmetro inválido",
              "status": 400,
              "detail": "O parâmetro \\"idDoador\\" não aceita o valor \\"abc\\".",
              "instance": "/api/v1/doadores/abc",
              "timestamp": "2026-11-09T22:30:00Z",
              "errors": []
            }
            """;

    static final String NAO_ENCONTRADO = """
            {
              "type": "https://socialconnect.api/errors/recurso-nao-encontrado",
              "title": "Recurso não encontrado",
              "status": 404,
              "detail": "Doador com id 99 não encontrado.",
              "instance": "/api/v1/doadores/99",
              "timestamp": "2026-11-09T22:30:00Z",
              "errors": []
            }
            """;

    static final String ERRO_INTERNO = """
            {
              "type": "https://socialconnect.api/errors/erro-interno",
              "title": "Erro interno do servidor",
              "status": 500,
              "detail": "Ocorreu um erro inesperado. Tente novamente mais tarde.",
              "instance": "/api/v1/doadores",
              "timestamp": "2026-11-09T22:30:00Z",
              "errors": []
            }
            """;
}
