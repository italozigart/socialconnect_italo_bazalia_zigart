package br.com.socialconnect.api.beneficiarios.dto;

import br.com.socialconnect.api.validation.CPF;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dados que o cliente pode enviar no POST e no PUT.
 * <p>
 * Sem idBeneficiario e sem dataCadastro: esses campos são do servidor. Se o
 * JSON trouxer os dois, o Jackson os descarta, porque o Spring Boot desliga o
 * FAIL_ON_UNKNOWN_PROPERTIES. Assim o cliente não consegue forçar o id (o
 * defeito da Etapa 2) nem a data de cadastro.
 * <p>
 * A ordem dos componentes segue a nota da Aula 05 e o teste de integração do
 * professor, que monta o record por posição. Como os cinco são String, outra
 * ordem compilaria e trocaria os dados em silêncio.
 * <p>
 * Bean Validation (Etapa 4): só valem com o @Valid no Controller. Os tamanhos
 * são os da V1. As mensagens são chaves do messages.properties; o Hibernate
 * Validator preenche o {max} com o valor da anotação.
 * <p>
 * Swagger (Etapa 5): o name do @Schema é o do docs/openapi.yaml. O springdoc lê
 * sozinho o @NotBlank (required) e o @Size (maxLength), mas não conhece a nossa
 * anotação de CPF; por isso o formato do CPF está no pattern do @Schema.
 */
@Schema(name = "BeneficiarioRequest",
        description = "Dados para cadastrar (POST) ou substituir (PUT) um beneficiário")
public record BeneficiarioRequestDTO(

        @Schema(description = "Nome completo", example = "Maria da Silva")
        @NotBlank(message = "{beneficiario.nome.obrigatorio}")
        @Size(max = 150, message = "{beneficiario.nome.tamanho}")
        String nome,

        // Sem @Size: a @CPF já exige exatamente 11 dígitos.
        @Schema(description = "CPF com 11 dígitos, sem pontos nem traço, e dígitos verificadores válidos. "
                + "Não pode repetir.", example = "52998224725", pattern = "^\\d{11}$")
        @NotBlank(message = "{beneficiario.cpf.obrigatorio}")
        @CPF
        String cpf,

        @Schema(description = "Telefone com DDD", example = "11999999999")
        @Size(max = 20, message = "{beneficiario.telefone.tamanho}")
        String telefone,

        @Schema(description = "Endereço completo", example = "Rua das Flores, 123 - Centro")
        @Size(max = 255, message = "{beneficiario.endereco.tamanho}")
        String endereco,

        @Schema(description = "Situação de vulnerabilidade da família", example = "Renda familiar baixa")
        @Size(max = 500, message = "{beneficiario.situacaoVulnerabilidade.tamanho}")
        String situacaoVulnerabilidade
) {
}
