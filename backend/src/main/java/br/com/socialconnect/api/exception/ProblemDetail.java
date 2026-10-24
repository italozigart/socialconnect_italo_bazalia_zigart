package br.com.socialconnect.api.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

/**
 * Corpo de todas as respostas de erro, no formato Problem Details (RFC 7807,
 * atualizada pela RFC 9457), com os campos do schema ProblemDetail do
 * docs/openapi.yaml: os cinco da RFC mais timestamp e errors.
 * <p>
 * Atenção ao import: o Spring tem outra classe com o mesmo nome
 * (org.springframework.http.ProblemDetail). Neste projeto usamos só esta.
 * <p>
 * Os exemplos abaixo descrevem um 400 de validação. Cada endpoint mostra no
 * Swagger o exemplo do próprio erro (BeneficiarioExemplos).
 */
@Schema(name = "ProblemDetail", description = "Erro no formato Problem Details (RFC 7807)")
public record ProblemDetail(

        @Schema(description = "URI que identifica o tipo de erro",
                example = "https://socialconnect.api/errors/validacao")
        String type,

        @Schema(description = "Resumo do erro", example = "Dados inválidos")
        String title,

        @Schema(description = "Código HTTP", example = "400")
        int status,

        @Schema(description = "Explicação desta ocorrência",
                example = "Um ou mais campos são inválidos. Veja a lista em errors.")
        String detail,

        @Schema(description = "Caminho da requisição que falhou", example = "/api/v1/beneficiarios")
        String instance,

        // Instant sai com o "Z" (UTC), como pede o format date-time do contrato.
        @Schema(description = "Momento do erro (UTC)", example = "2026-10-03T22:30:00Z")
        Instant timestamp,

        @Schema(description = "Campos inválidos (só nos erros 400 de validação; nos demais, lista vazia)")
        List<ErroCampo> errors
) {

    /** Um campo inválido: nome do campo e mensagem já traduzida. */
    @Schema(name = "ErroCampo", description = "Campo recusado pela validação")
    public record ErroCampo(
            @Schema(description = "Nome do campo no JSON", example = "cpf") String field,
            @Schema(description = "Mensagem já traduzida",
                    example = "CPF inválido. Informe os 11 dígitos, sem pontos nem traço.") String message
    ) {
    }
}
