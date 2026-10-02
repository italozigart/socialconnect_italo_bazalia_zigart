package br.com.socialconnect.api.beneficiarios.dto;

/**
 * Dados do PATCH: todos opcionais. Campo nulo ou ausente no JSON significa
 * "não mexer", então o PATCH não consegue apagar um campo; para isso existe o PUT.
 * <p>
 * Sem cpf, como na nota da Aula 05 e no BeneficiarioPatch do docs/openapi.yaml:
 * o CPF só muda pelo PUT, que confere a duplicidade.
 */
public record BeneficiarioPatchDTO(
        String nome,
        String telefone,
        String endereco,
        String situacaoVulnerabilidade
) {
}
