package br.com.socialconnect.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CPF válido: 11 dígitos, sem máscara, com os dois dígitos verificadores
 * corretos (regra em {@link CpfValidator}).
 * <p>
 * DECISÃO: só 11 dígitos, sem pontos nem traço. Guardar "529.982.247-25" e
 * "52998224725" como valores diferentes deixaria o UNIQUE da V1 e o filtro
 * por CPF sem efeito. Com um formato só, o banco nunca tem o mesmo CPF
 * escrito de dois jeitos.
 * <p>
 * Contrato (Etapa 5): o docs/openapi.yaml trazia ^\d{11}$|^\d{14}$, mas \d{14}
 * são 14 dígitos (tamanho de CNPJ), e não o CPF com máscara (11 dígitos e 3
 * separadores). O contrato foi alinhado a ^\d{11}$, e o springdoc publica esse
 * pattern pelo @Schema do BeneficiarioRequestDTO, porque não conhece esta anotação.
 * <p>
 * A mensagem é uma chave do messages.properties (i18n).
 */
@Documented
@Constraint(validatedBy = CpfValidator.class) // classe que faz a checagem
@Target({ElementType.FIELD})                  // usada em campos (componentes de record)
@Retention(RetentionPolicy.RUNTIME)           // precisa existir em tempo de execução
public @interface CPF {

    String message() default "{validacao.cpf.invalido}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
