package br.com.socialconnect.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Data que não está no futuro: hoje ou antes (regra em
 * {@link DataNaoFuturaValidator}). Usada na data da doação: a doação é
 * registrada quando chega, então a data dela não pode ser depois de hoje.
 * <p>
 * Feita em TDD (Aula 07), em três commits:
 * <ol>
 *   <li>RED: o teste deveRetornar400QuandoDataFutura, que falhava (a API
 *       aceitava a data no futuro e respondia 201);</li>
 *   <li>GREEN: esta anotação e o validador, com a mensagem em texto fixo;</li>
 *   <li>REFACTOR: a mensagem foi para o messages.properties (i18n), como as
 *       da @CPF e da @Telefone, e o teste continuou passando.</li>
 * </ol>
 * O Bean Validation já tem a @PastOrPresent, que faz a mesma conferência. A
 * @DataNaoFutura é o exercício de validação customizada das Aulas 06 e 07,
 * com o nome da regra em português e a mensagem no messages.properties.
 */
@Documented
@Constraint(validatedBy = DataNaoFuturaValidator.class) // classe que faz a checagem
@Target({ElementType.FIELD})                            // usada em campos (componentes de record)
@Retention(RetentionPolicy.RUNTIME)                     // precisa existir em tempo de execução
public @interface DataNaoFutura {

    String message() default "{validacao.data.futura}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
