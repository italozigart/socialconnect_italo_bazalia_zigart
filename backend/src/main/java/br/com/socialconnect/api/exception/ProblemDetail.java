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
 */
@Schema(name = "ProblemDetail", description = "Erro no formato Problem Details (RFC 7807)")
public record ProblemDetail(

        @Schema(description = "URI que identifica o tipo de erro",
                example = "https://socialconnect.api/errors/estoque-negativo")
        String type,

        @Schema(description = "Resumo do erro", example = "Estoque negativo")
        String title,

        @Schema(description = "Código HTTP", example = "422")
        int status,

        @Schema(description = "Explicação desta ocorrência",
                example = "O estoque atual não pode ficar negativo (valor recebido: -5).")
        String detail,

        @Schema(description = "Caminho da requisição que falhou", example = "/api/v1/produtos")
        String instance,

        // Instant sai com o "Z" (UTC), como pede o format date-time do contrato.
        @Schema(description = "Momento do erro (UTC)", example = "2026-10-02T22:30:00Z")
        Instant timestamp,

        @Schema(description = "Campos inválidos (só nos erros 400 de validação)")
        List<ErroCampo> errors
) {

    /** Um campo inválido: nome do campo e mensagem já traduzida. */
    @Schema(name = "ErroCampo")
    public record ErroCampo(
            @Schema(example = "estoqueMinimo") String field,
            @Schema(example = "O estoque mínimo não pode ser negativo.") String message
    ) {
    }
}
