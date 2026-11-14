package br.com.socialconnect.api.beneficiarios.dto;

import br.com.socialconnect.api.validation.Telefone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Dados do PATCH: todos opcionais. Campo nulo ou ausente no JSON significa
 * "não mexer", então o PATCH não consegue apagar um campo; para isso existe o PUT.
 * A exceção é o telefone em branco (Etapa 11): o BeneficiarioService grava o
 * telefone em branco como nulo, então um PATCH com "telefone": "" apaga o
 * telefone, como a descrição em branco no PATCH de doação.
 * <p>
 * Sem cpf, como na nota da Aula 05 e no BeneficiarioPatch do docs/openapi.yaml:
 * o CPF só muda pelo PUT, que confere a duplicidade.
 * <p>
 * Sem @NotBlank: ele recusa null, e aqui null quer dizer "não mexer". @Size,
 * @Pattern e @Telefone aceitam null. No nome, o @Pattern exige ao menos um
 * caractere que não seja espaço: recusa "" e "   ", que apagariam o nome
 * obrigatório.
 * <p>
 * Swagger (Etapa 5): sem cpf, o PATCH nunca responde 409. O springdoc publica
 * o pattern do nome sozinho. O do telefone está no @Schema, porque o springdoc
 * não conhece a @Telefone; aqui ele aceita também "", que apaga o telefone.
 */
@Schema(name = "BeneficiarioPatch",
        description = "Atualização parcial: só os campos enviados e não nulos mudam. "
                + "O CPF não muda por aqui; para isso, use o PUT.")
public record BeneficiarioPatchDTO(

        @Schema(description = "Nome completo; se vier, não pode ficar em branco", example = "Maria da Silva Souza")
        @Size(max = 150, message = "{beneficiario.nome.tamanho}")
        @Pattern(regexp = ".*\\S.*", message = "{beneficiario.nome.vazio}")
        String nome,

        // Mesma regra do BeneficiarioRequestDTO (Etapa 11).
        @Schema(description = "Telefone com DDD, só dígitos: 10 (fixo) ou 11 (celular). "
                + "Em branco (\"\"), apaga o telefone.", example = "11988887777", pattern = "^(\\d{10,11})?$")
        @Telefone
        String telefone,

        @Schema(description = "Endereço completo", example = "Rua Nova, 456 - Centro")
        @Size(max = 255, message = "{beneficiario.endereco.tamanho}")
        String endereco,

        @Schema(description = "Situação de vulnerabilidade da família", example = "Renda familiar baixa")
        @Size(max = 500, message = "{beneficiario.situacaoVulnerabilidade.tamanho}")
        String situacaoVulnerabilidade
) {
}
