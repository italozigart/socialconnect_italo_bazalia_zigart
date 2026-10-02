package br.com.socialconnect.api.produtos.dto;

import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.validation.UnidadeMedida;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Dados que o cliente envia no POST e no PUT. Sem idProduto, dataCadastro e
 * estoqueBaixo: os três são do servidor (o Jackson descarta se vierem no JSON).
 * <p>
 * As mensagens são chaves do messages.properties. O Spring Boot troca a chave
 * pelo texto, e o Hibernate Validator preenche os {max}/{value} com os valores
 * da anotação.
 * <p>
 * DECISÃO: estoqueAtual tem @NotNull, mas NÃO tem @Min(0). Com @Min, um estoque
 * negativo seria barrado aqui com 400 e nunca chegaria ao Service, que é quem
 * responde 422 pela regra "estoque não negativo" (requisito 3 da A1).
 */
@Schema(description = "Dados para cadastrar ou substituir um produto")
public record ProdutoRequestDTO(

        @Schema(description = "Nome do produto (único, sem diferenciar maiúsculas)",
                example = "Arroz 5kg", maxLength = 150)
        @NotBlank(message = "{produto.nome.obrigatorio}")
        @Size(max = 150, message = "{produto.nome.tamanho}")
        String nome,

        @Schema(description = "Categoria do produto", example = "ALIMENTO")
        @NotNull(message = "{produto.categoria.obrigatoria}")
        CategoriaProduto categoria,

        @Schema(description = "Quantidade em estoque; negativo responde 422", example = "3")
        @NotNull(message = "{produto.estoqueAtual.obrigatorio}")
        Integer estoqueAtual,

        @Schema(description = "Quantidade mínima desejada; abaixo dela, estoqueBaixo = true",
                example = "10", minimum = "0")
        @NotNull(message = "{produto.estoqueMinimo.obrigatorio}")
        @Min(value = 0, message = "{produto.estoqueMinimo.negativo}")
        Integer estoqueMinimo,

        @Schema(description = "Unidade de medida: kg, g, l, ml, unidade, pacote ou caixa",
                example = "unidade", maxLength = 20)
        @NotBlank(message = "{produto.unidadeMedida.obrigatoria}")
        @Size(max = 20, message = "{produto.unidadeMedida.tamanho}")
        @UnidadeMedida
        String unidadeMedida
) {
}
