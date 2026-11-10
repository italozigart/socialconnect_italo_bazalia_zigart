package br.com.socialconnect.api.doadores.dto;

import br.com.socialconnect.api.doadores.model.TipoDoador;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Dados que a API devolve em todas as respostas de doador, incluindo o
 * idDoador gerado pelo banco. E-mail e telefone saem null quando não foram
 * informados.
 * <p>
 * O name do @Schema também nomeia a página no Swagger: PageDoadorResponse,
 * como no docs/openapi.yaml.
 */
@Schema(name = "DoadorResponse", description = "Doador como devolvido pela API")
public record DoadorResponseDTO(

        @Schema(description = "Identificador gerado pelo banco", example = "1")
        Long idDoador,

        @Schema(description = "Nome da pessoa ou razão social da empresa", example = "Mercado Solidário Ltda")
        String nome,

        @Schema(description = "PESSOA_FISICA ou PESSOA_JURIDICA", example = "PESSOA_JURIDICA")
        TipoDoador tipo,

        @Schema(description = "E-mail para contato", example = "contato@example.com")
        String email,

        @Schema(description = "Telefone com DDD, só dígitos", example = "11999999999")
        String telefone
) {
}
