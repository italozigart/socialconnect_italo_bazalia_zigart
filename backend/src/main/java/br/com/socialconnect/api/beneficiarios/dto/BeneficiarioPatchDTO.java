package br.com.socialconnect.api.beneficiarios.dto;

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
 */
public record BeneficiarioPatchDTO(

        @Size(max = 150, message = "{beneficiario.nome.tamanho}")
        @Pattern(regexp = ".*\\S.*", message = "{beneficiario.nome.vazio}")
        String nome,

        @Size(max = 20, message = "{beneficiario.telefone.tamanho}")
        String telefone,

        @Size(max = 255, message = "{beneficiario.endereco.tamanho}")
        String endereco,

        @Size(max = 500, message = "{beneficiario.situacaoVulnerabilidade.tamanho}")
        String situacaoVulnerabilidade
) {
}
