package br.com.socialconnect.api.beneficiarios.dto;

import java.time.LocalDate;

/**
 * Dados que a API devolve em todas as respostas de beneficiário.
 * Inclui os campos gerados pelo servidor: idBeneficiario (pelo banco) e
 * dataCadastro (pelo BeneficiarioService, no cadastro).
 * Record não tem getters: o acesso é dto.nome(), e não dto.getNome().
 */
public record BeneficiarioResponseDTO(
        Long idBeneficiario,
        String nome,
        String cpf,
        String telefone,
        String endereco,
        String situacaoVulnerabilidade,
        LocalDate dataCadastro
) {
}
