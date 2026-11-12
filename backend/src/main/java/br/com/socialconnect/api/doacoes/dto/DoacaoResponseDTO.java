package br.com.socialconnect.api.doacoes.dto;

import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Dados que a API devolve em todas as respostas de doação, incluindo o
 * idDoacao gerado pelo banco. O doador vem só pelo id, como no contrato.
 * valor e descricao saem null quando não foram informados.
 * <p>
 * Diferença em relação ao repositório do professor: lá a resposta não tinha
 * a descricao, e o toEntity nem a copiava para a entidade; a descrição
 * enviada no POST se perdia.
 * <p>
 * O name do @Schema também nomeia a página no Swagger: PageDoacaoResponse,
 * como no docs/openapi.yaml.
 */
@Schema(name = "DoacaoResponse", description = "Doação como devolvida pela API")
public record DoacaoResponseDTO(

        @Schema(description = "Identificador gerado pelo banco", example = "1")
        Long idDoacao,

        @Schema(description = "Id do doador", example = "1")
        Long idDoador,

        @Schema(description = "Data em que a doação foi recebida", example = "2026-11-10")
        LocalDate dataDoacao,

        @Schema(description = "Valor em reais (null quando não informado)", example = "150.00")
        BigDecimal valor,

        @Schema(description = "ALIMENTO, ROUPA ou FINANCEIRA", example = "ALIMENTO")
        TipoDoacao tipo,

        @Schema(description = "Descrição livre (null quando não informada)", example = "Cestas básicas")
        String descricao
) {
}
