package br.com.socialconnect.api.beneficiarios.dto;

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
 * As anotações de Bean Validation entram na Etapa 4, junto com o @Valid no
 * Controller e o tratamento dos erros.
 */
public record BeneficiarioRequestDTO(
        String nome,
        String cpf,
        String telefone,
        String endereco,
        String situacaoVulnerabilidade
) {
}
