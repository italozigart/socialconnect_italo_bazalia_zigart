package br.com.socialconnect.api.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes unitários da regra da @Telefone: sem Spring, chamando o validador
 * direto. O isValid não usa o ConstraintValidatorContext, por isso ele vai null.
 * <p>
 * Cada método é um @ParameterizedTest: o JUnit roda o mesmo teste uma vez para
 * cada valor do @ValueSource e conta cada execução como um teste no relatório.
 * O ARRANGE é o próprio valor recebido como parâmetro.
 */
class TelefoneValidatorTest {

    private final TelefoneValidator validador = new TelefoneValidator();

    @ParameterizedTest(name = "aceita {0}")
    @ValueSource(strings = {
            "1133334444",   // fixo, número começando com 3
            "2125556677",   // fixo, começando com 2
            "6140041234",   // fixo, começando com 4
            "5150001234",   // fixo, começando com 5
            "11999999999",  // celular
            "21987654321"   // celular
    })
    @DisplayName("Deve aceitar quando o telefone é válido")
    void deveAceitarQuandoTelefoneValido(String telefone) {
        // ACT
        boolean valido = validador.isValid(telefone, null);

        // ASSERT
        assertThat(valido).isTrue();
    }

    @ParameterizedTest(name = "recusa {0}")
    @ValueSource(strings = {
            "(11) 99999-9999", // com máscara
            "1199999999a",     // com letra
            "119999999",       // 9 dígitos
            "119999999999",    // 12 dígitos
            "1023334444",      // DDD 10 não existe
            "1112345678",      // número começando com 1
            "1163334444",      // fixo começando com 6
            "1199999999",      // celular antigo, sem o nono dígito
            "11899999999"      // 11 dígitos sem o 9 na frente
    })
    @DisplayName("Deve recusar quando o telefone é inválido")
    void deveRecusarQuandoTelefoneInvalido(String telefone) {
        // ACT
        boolean valido = validador.isValid(telefone, null);

        // ASSERT
        assertThat(valido).isFalse();
    }

    // Nulo e em branco passam: o telefone é opcional, e quem obrigaria o campo
    // seria o @NotBlank. O @NullAndEmptySource manda null e "".
    @ParameterizedTest(name = "aceita [{0}]")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Deve aceitar quando o telefone é nulo ou está em branco")
    void deveAceitarQuandoTelefoneNuloOuEmBranco(String telefone) {
        // ACT
        boolean valido = validador.isValid(telefone, null);

        // ASSERT
        assertThat(valido).isTrue();
    }
}
