package br.com.socialconnect.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Telefone brasileiro: DDD + número, só dígitos (regra em {@link TelefoneValidator}).
 * <p>
 * Base: a @Telefone do repositório do professor (Aula 06). Duas diferenças:
 * <ul>
 *   <li>Só dígitos, sem parênteses, espaço nem traço: a mesma decisão da @CPF,
 *       e a mesma regra do schema Zod da Aula 10 (^\d{10,11}$). Assim o banco
 *       não guarda o mesmo telefone escrito de dois jeitos.</li>
 *   <li>Confere o início do número: fixo (10 dígitos) começa com 2 a 5, e
 *       celular (11 dígitos) começa com 9. A do professor só recusava 0 e 1.</li>
 * </ul>
 * Na Etapa 7 vale só para o Doador. O Beneficiário continua com @Size(max = 20)
 * até a issue do formulário do front-end, que alinha os dois ao Zod.
 * <p>
 * A mensagem é uma chave do messages.properties (i18n).
 */
@Documented
@Constraint(validatedBy = TelefoneValidator.class) // classe que faz a checagem
@Target({ElementType.FIELD})                        // usada em campos (componentes de record)
@Retention(RetentionPolicy.RUNTIME)                 // precisa existir em tempo de execução
public @interface Telefone {

    String message() default "{validacao.telefone.invalido}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
