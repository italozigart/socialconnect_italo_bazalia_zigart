package br.com.socialconnect.api.produtos.dto;

import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * Dados que a API devolve em todas as respostas de produto.
 * estoqueBaixo não existe no banco: é calculado no Service a cada resposta
 * (estoqueAtual < estoqueMinimo), então nunca fica desatualizado.
 */
@Schema(description = "Produto como devolvido pela API")
public record ProdutoResponseDTO(

        @Schema(description = "Identificador gerado pelo banco", example = "1")
        Long idProduto,

        @Schema(description = "Nome do produto", example = "Arroz 5kg")
        String nome,

        @Schema(description = "Categoria do produto", example = "ALIMENTO")
        CategoriaProduto categoria,

        @Schema(description = "Quantidade em estoque", example = "3")
        Integer estoqueAtual,

        @Schema(description = "Quantidade mínima desejada", example = "10")
        Integer estoqueMinimo,

        @Schema(description = "Unidade de medida", example = "unidade")
        String unidadeMedida,

        @Schema(description = "Data do cadastro, gerada pelo servidor", example = "2026-10-02")
        LocalDate dataCadastro,

        @Schema(description = "true quando estoqueAtual < estoqueMinimo", example = "true")
        boolean estoqueBaixo
) {
}
