package br.com.socialconnect.api.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes unitários da regra da @DataNaoFutura, no molde do
 * TelefoneValidatorTest: sem Spring, chamando o validador direto. O isValid
 * não usa o ConstraintValidatorContext, por isso ele vai null.
 * <p>
 * As datas são contadas a partir de hoje (LocalDate.now()), como no
 * validador: o @ValueSource traz quantos dias somar ou subtrair, e cada valor
 * conta como um teste no relatório. Rodar exatamente na virada da meia-noite
 * poderia mudar o "hoje" entre o teste e o validador; o risco é desprezível.
 */
class DataNaoFuturaValidatorTest {

    private final DataNaoFuturaValidator validador = new DataNaoFuturaValidator();

    @ParameterizedTest(name = "aceita hoje menos {0} dia(s)")
    @ValueSource(longs = {0, 1, 30, 3650})
    @DisplayName("Deve aceitar quando a data é hoje ou passada")
    void deveAceitarQuandoDataHojeOuPassada(long diasAtras) {
        // ARRANGE
        LocalDate data = LocalDate.now().minusDays(diasAtras);

        // ACT
        boolean valida = validador.isValid(data, null);

        // ASSERT
        assertThat(valida).isTrue();
    }

    @ParameterizedTest(name = "recusa hoje mais {0} dia(s)")
    @ValueSource(longs = {1, 10, 365})
    @DisplayName("Deve recusar quando a data está no futuro")
    void deveRecusarQuandoDataFutura(long diasAFrente) {
        // ARRANGE
        LocalDate data = LocalDate.now().plusDays(diasAFrente);

        // ACT
        boolean valida = validador.isValid(data, null);

        // ASSERT
        assertThat(valida).isFalse();
    }

    // Nula passa: a data é obrigatória, mas quem cobra isso é o @NotNull do DTO.
    @Test
    @DisplayName("Deve aceitar quando a data é nula")
    void deveAceitarQuandoDataNula() {
        // ACT
        boolean valida = validador.isValid(null, null);

        // ASSERT
        assertThat(valida).isTrue();
    }
}
