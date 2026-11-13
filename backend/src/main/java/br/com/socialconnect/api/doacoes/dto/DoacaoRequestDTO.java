package br.com.socialconnect.api.doacoes.dto;

import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import br.com.socialconnect.api.validation.DataNaoFutura;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Dados que o cliente envia no POST e no PUT de doação.
 * <p>
 * No PUT (substituição total), estes campos substituem todos os da doação:
 * valor e descricao ausentes no JSON viram null. Por isso o PUT tem as mesmas
 * regras do POST: é o mesmo DTO, com o mesmo @Valid.
 * <p>
 * Sem idDoacao: o id é do servidor (no PUT, o da URL). Se vier no JSON, o
 * Jackson descarta, como nos outros Request DTOs.
 * <p>
 * Bean Validation: só vale com o @Valid no Controller. Limites da V4; as
 * mensagens são chaves do messages.properties.
 * <ul>
 *   <li>idDoador: o @NotNull cobre o campo ausente. Se o doador não existir,
 *       quem responde é o Service, com 422 (ADR 0001): a validação do DTO só
 *       confere o formato dos dados e não consulta o banco.</li>
 *   <li>dataDoacao: o @NotNull cobre a data ausente, e a @DataNaoFutura
 *       (validação customizada, feita em TDD) recusa uma data depois de hoje.</li>
 *   <li>valor: opcional. Quando vier, o @Positive espelha o CHECK (valor > 0)
 *       e o @Digits espelha o DECIMAL(10,2). Sem o @Digits, o banco
 *       arredondaria 10.555 para 10.56 sem avisar, e um valor com 9 dígitos
 *       inteiros daria erro no INSERT (500).</li>
 *   <li>tipo: um valor fora da lista ("tipo": "DINHEIRO") nem chega aqui: o
 *       Jackson não converte o texto para o enum e o GlobalExceptionHandler
 *       responde 400 de corpo inválido.</li>
 *   <li>descricao: opcional; em branco, o Service grava nulo.</li>
 * </ul>
 * Swagger: o name do @Schema é o do docs/openapi.yaml. O springdoc lê sozinho
 * o @NotNull (required), o @Positive (mínimo exclusivo 0) e o @Size
 * (maxLength), mas não o @Digits nem a @DataNaoFutura; por isso essas regras
 * estão na descrição dos campos valor e dataDoacao.
 */
@Schema(name = "DoacaoRequest", description = "Dados para registrar (POST) ou substituir (PUT) uma doação")
public record DoacaoRequestDTO(

        @Schema(description = "Id de um doador já cadastrado", example = "1")
        @NotNull(message = "{doacao.idDoador.obrigatorio}")
        Long idDoador,

        @Schema(description = "Data em que a doação foi recebida (aaaa-mm-dd): hoje ou antes",
                example = "2026-11-10")
        @NotNull(message = "{doacao.dataDoacao.obrigatoria}")
        @DataNaoFutura
        LocalDate dataDoacao,

        @Schema(description = "Valor em reais, maior que zero e com até 2 casas decimais. Opcional.",
                example = "150.00")
        @Positive(message = "{doacao.valor.positivo}")
        @Digits(integer = 8, fraction = 2, message = "{doacao.valor.digitos}")
        BigDecimal valor,

        @Schema(description = "ALIMENTO, ROUPA ou FINANCEIRA", example = "ALIMENTO")
        @NotNull(message = "{doacao.tipo.obrigatorio}")
        TipoDoacao tipo,

        @Schema(description = "Descrição livre. Opcional.", example = "Cestas básicas")
        @Size(max = 500, message = "{doacao.descricao.tamanho}")
        String descricao
) {
}
