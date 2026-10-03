package br.com.socialconnect.api.beneficiarios.dto;

import br.com.socialconnect.api.validation.CPF;
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
 */
public record BeneficiarioRequestDTO(

        @NotBlank(message = "{beneficiario.nome.obrigatorio}")
        @Size(max = 150, message = "{beneficiario.nome.tamanho}")
        String nome,

        // Sem @Size: a @CPF já exige exatamente 11 dígitos.
        @NotBlank(message = "{beneficiario.cpf.obrigatorio}")
        @CPF
        String cpf,

        @Size(max = 20, message = "{beneficiario.telefone.tamanho}")
        String telefone,

        @Size(max = 255, message = "{beneficiario.endereco.tamanho}")
        String endereco,

        @Size(max = 500, message = "{beneficiario.situacaoVulnerabilidade.tamanho}")
        String situacaoVulnerabilidade
) {
}
