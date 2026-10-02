package br.com.socialconnect.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Locale;
import java.util.Set;

/**
 * Aceita a unidade sem diferenciar maiúsculas e ignorando espaços nas pontas
 * ("KG" e " kg " valem). O Service grava sempre a forma normalizada (minúsculas).
 * <p>
 * Se mudar esta lista, mude também a mensagem validacao.unidadeMedida.invalida
 * no messages.properties, que mostra os valores aceitos.
 */
public class UnidadeMedidaValidator implements ConstraintValidator<UnidadeMedida, String> {

    public static final Set<String> UNIDADES_ACEITAS =
            Set.of("kg", "g", "l", "ml", "unidade", "pacote", "caixa");

    @Override
    public boolean isValid(String unidade, ConstraintValidatorContext context) {
        // Nulo ou em branco é válido AQUI de propósito: quem obriga o campo é o
        // @NotBlank. Assim o campo vazio mostra uma mensagem só, e não duas.
        if (unidade == null || unidade.isBlank()) {
            return true;
        }
        return UNIDADES_ACEITAS.contains(normalizar(unidade));
    }

    /** Forma gravada no banco: sem espaços nas pontas e em minúsculas. */
    public static String normalizar(String unidade) {
        return unidade.trim().toLowerCase(Locale.ROOT);
    }
}
