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
 * Feita em TDD (Aula 07). Este é o GREEN: o mínimo para o teste
 * deveRetornar400QuandoDataFutura passar, com a mensagem em texto fixo.
 */
@Documented
@Constraint(validatedBy = DataNaoFuturaValidator.class) // classe que faz a checagem
@Target({ElementType.FIELD})                            // usada em campos (componentes de record)
@Retention(RetentionPolicy.RUNTIME)                     // precisa existir em tempo de execução
public @interface DataNaoFutura {

    String message() default "A data não pode estar no futuro.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
