package br.com.socialconnect.api.doacoes.dto;

import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import br.com.socialconnect.api.validation.DataNaoFutura;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Dados do PATCH de doação: todos opcionais. Campo nulo ou ausente no JSON
 * significa "não mexer", como no BeneficiarioPatchDTO.
 * <p>
 * Mesmas regras de formato do DoacaoRequestDTO, mas sem o @NotNull: aqui null
 * quer dizer "não mexer", e o @NotNull recusaria um PATCH com um campo só. As
 * outras anotações aceitam null, então só conferem o campo que veio:
 * <ul>
 *   <li>idDoador: se vier, o Service confere se o doador existe (422, ADR 0001)
 *       e troca o doador da doação.</li>
 *   <li>dataDoacao: a @DataNaoFutura aceita null (quem obriga a data no POST e
 *       no PUT é o @NotNull), então serve aqui sem obrigar a data.</li>
 *   <li>valor: @Positive e @Digits, como no POST. Por isso o PATCH não apaga o
 *       valor: null quer dizer "não mexer", e 0 dá 400. Para apagar, use o PUT.</li>
 *   <li>tipo: um valor fora da lista dá 400 de corpo inválido, como no POST.</li>
 *   <li>descricao: texto em branco ("" ou "   ") vira null no Service, como no
 *       POST e no PUT. É o jeito de apagar a descrição pelo PATCH.</li>
 * </ul>
 * Sem idDoacao: o id é o da URL. Se vier no JSON, o Jackson descarta.
 * <p>
 * Swagger: o springdoc lê sozinho o @Positive e o @Size, mas não o @Digits
 * nem a @DataNaoFutura; por isso essas regras estão na descrição dos campos.
 */
@Schema(name = "DoacaoPatch",
        description = "Atualização parcial: só os campos enviados e não nulos mudam. Descrição em branco "
                + "apaga a descrição; para apagar o valor, use o PUT.")
public record DoacaoPatchDTO(

        @Schema(description = "Id de um doador já cadastrado; se vier, troca o doador da doação", example = "1")
        Long idDoador,

        @Schema(description = "Data em que a doação foi recebida (aaaa-mm-dd): hoje ou antes",
                example = "2026-11-10")
        @DataNaoFutura
        LocalDate dataDoacao,

        @Schema(description = "Valor em reais, maior que zero e com até 2 casas decimais", example = "80.50")
        @Positive(message = "{doacao.valor.positivo}")
        @Digits(integer = 8, fraction = 2, message = "{doacao.valor.digitos}")
        BigDecimal valor,

        @Schema(description = "ALIMENTO, ROUPA ou FINANCEIRA", example = "ROUPA")
        TipoDoacao tipo,

        @Schema(description = "Descrição livre; em branco, apaga a descrição", example = "Agasalhos de inverno")
        @Size(max = 500, message = "{doacao.descricao.tamanho}")
        String descricao
) {
}
