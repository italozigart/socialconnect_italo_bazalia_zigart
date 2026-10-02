package br.com.socialconnect.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validação customizada da A1: a unidade de medida precisa ser uma das
 * aceitas pela ONG (lista em {@link UnidadeMedidaValidator#UNIDADES_ACEITAS}).
 * <p>
 * Por que não uma @EstoqueNaoNegativo: estoque negativo é regra de negócio e
 * precisa responder 422 (requisito 3). Uma constraint de Bean Validation
 * responderia 400 e impediria o 422. Ver a seção "Decisões" do README.
 * <p>
 * A mensagem é uma chave do messages.properties (i18n).
 */
@Documented
@Constraint(validatedBy = UnidadeMedidaValidator.class) // classe que faz a checagem
@Target({ElementType.FIELD})                            // usada em campos (componentes de record)
@Retention(RetentionPolicy.RUNTIME)                     // precisa existir em tempo de execução
public @interface UnidadeMedida {

    String message() default "{validacao.unidadeMedida.invalida}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
