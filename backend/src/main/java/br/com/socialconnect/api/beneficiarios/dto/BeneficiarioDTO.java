package br.com.socialconnect.api.beneficiarios.dto;

import java.time.LocalDate;

/**
 * Dados que entram e saem da API, separados da Entity.
 * Record não tem getters: o acesso é dto.nome(), e não dto.getNome().
 * Na Etapa 3 este DTO se divide em Request, Response e Patch.
 */
public record BeneficiarioDTO(
        Long idBeneficiario,
        String nome,
        String cpf,
        String telefone,
        String endereco,
        String situacaoVulnerabilidade,
        LocalDate dataCadastro
) {
}
