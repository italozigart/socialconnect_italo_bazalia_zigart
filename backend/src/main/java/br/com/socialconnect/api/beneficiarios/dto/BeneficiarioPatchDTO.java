package br.com.socialconnect.api.beneficiarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Dados do PATCH: todos opcionais. Campo nulo ou ausente no JSON significa
 * "não mexer", então o PATCH não consegue apagar um campo; para isso existe o PUT.
 * <p>
 * Sem cpf, como na nota da Aula 05 e no BeneficiarioPatch do docs/openapi.yaml:
 * o CPF só muda pelo PUT, que confere a duplicidade.
 * <p>
 * Sem @NotBlank: ele recusa null, e aqui null quer dizer "não mexer". @Size e
 * @Pattern aceitam null. No nome, o @Pattern exige ao menos um caractere que
 * não seja espaço: recusa "" e "   ", que apagariam o nome obrigatório.
 * <p>
 * Swagger (Etapa 5): sem cpf, o PATCH nunca responde 409. O springdoc publica
 * o pattern do nome sozinho.
 */
@Schema(name = "BeneficiarioPatch",
        description = "Atualização parcial: só os campos enviados e não nulos mudam. "
                + "O CPF não muda por aqui; para isso, use o PUT.")
public record BeneficiarioPatchDTO(

        @Schema(description = "Nome completo; se vier, não pode ficar em branco", example = "Maria da Silva Souza")
        @Size(max = 150, message = "{beneficiario.nome.tamanho}")
        @Pattern(regexp = ".*\\S.*", message = "{beneficiario.nome.vazio}")
        String nome,

        @Schema(description = "Telefone com DDD", example = "11988887777")
        @Size(max = 20, message = "{beneficiario.telefone.tamanho}")
        String telefone,

        @Schema(description = "Endereço completo", example = "Rua Nova, 456 - Centro")
        @Size(max = 255, message = "{beneficiario.endereco.tamanho}")
        String endereco,

        @Schema(description = "Situação de vulnerabilidade da família", example = "Renda familiar baixa")
        @Size(max = 500, message = "{beneficiario.situacaoVulnerabilidade.tamanho}")
        String situacaoVulnerabilidade
) {
}
