package br.com.socialconnect.api.beneficiarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * Dados que a API devolve em todas as respostas de beneficiário.
 * Inclui os campos gerados pelo servidor: idBeneficiario (pelo banco) e
 * dataCadastro (pelo BeneficiarioService, no cadastro).
 * Record não tem getters: o acesso é dto.nome(), e não dto.getNome().
 * <p>
 * O name do @Schema também nomeia a página no Swagger: PageBeneficiarioResponse,
 * como no docs/openapi.yaml.
 */
@Schema(name = "BeneficiarioResponse", description = "Beneficiário como devolvido pela API")
public record BeneficiarioResponseDTO(

        @Schema(description = "Identificador gerado pelo banco", example = "1")
        Long idBeneficiario,

        @Schema(description = "Nome completo", example = "Maria da Silva")
        String nome,

        @Schema(description = "CPF com 11 dígitos, sem máscara", example = "52998224725")
        String cpf,

        @Schema(description = "Telefone com DDD", example = "11999999999")
        String telefone,

        @Schema(description = "Endereço completo", example = "Rua das Flores, 123 - Centro")
        String endereco,

        @Schema(description = "Situação de vulnerabilidade da família", example = "Renda familiar baixa")
        String situacaoVulnerabilidade,

        @Schema(description = "Data do cadastro, gerada pelo servidor", example = "2026-10-03")
        LocalDate dataCadastro
) {
}
