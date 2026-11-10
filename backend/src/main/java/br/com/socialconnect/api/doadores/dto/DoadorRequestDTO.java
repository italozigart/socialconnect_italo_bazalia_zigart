package br.com.socialconnect.api.doadores.dto;

import br.com.socialconnect.api.doadores.model.TipoDoador;
import br.com.socialconnect.api.validation.Telefone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Dados que o cliente pode enviar no POST de doador.
 * <p>
 * Sem idDoador: o id é do servidor. Se vier no JSON, o Jackson descarta,
 * como no BeneficiarioRequestDTO.
 * <p>
 * Bean Validation: só vale com o @Valid no Controller. Tamanhos da V3; as
 * mensagens são chaves do messages.properties.
 * <ul>
 *   <li>tipo: o @NotNull cobre o campo ausente. Um valor fora da lista
 *       ("tipo": "ONG") nem chega aqui: o Jackson não converte o texto para
 *       o enum e o GlobalExceptionHandler responde 400 de corpo inválido.</li>
 *   <li>email: o @Email aceita "" e também endereço sem ponto no domínio
 *       ("maria@localhost"), que o padrão permite. O Service grava "" como nulo.</li>
 *   <li>telefone: a @Telefone aceita nulo e em branco, porque o campo é opcional;
 *       o Service grava em branco como nulo.</li>
 * </ul>
 * Swagger: o name do @Schema é o do docs/openapi.yaml. O springdoc lê sozinho
 * o @NotBlank e o @NotNull (required), o @Size (maxLength) e o @Email (format),
 * mas não conhece a @Telefone; por isso a regra está no pattern do @Schema.
 */
@Schema(name = "DoadorRequest", description = "Dados para cadastrar um doador")
public record DoadorRequestDTO(

        @Schema(description = "Nome da pessoa ou razão social da empresa", example = "Mercado Solidário Ltda")
        @NotBlank(message = "{doador.nome.obrigatorio}")
        @Size(max = 200, message = "{doador.nome.tamanho}")
        String nome,

        @Schema(description = "PESSOA_FISICA ou PESSOA_JURIDICA", example = "PESSOA_JURIDICA")
        @NotNull(message = "{doador.tipo.obrigatorio}")
        TipoDoador tipo,

        @Schema(description = "E-mail para contato (opcional)", example = "contato@example.com")
        @Email(message = "{doador.email.invalido}")
        @Size(max = 254, message = "{doador.email.tamanho}")
        String email,

        // Sem @Size: a @Telefone já limita o campo a 10 ou 11 dígitos.
        @Schema(description = "Telefone com DDD, só dígitos: 10 (fixo) ou 11 (celular). Opcional.",
                example = "11999999999", pattern = "^\\d{10,11}$")
        @Telefone
        String telefone
) {
}
