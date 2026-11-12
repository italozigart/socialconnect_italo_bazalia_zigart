package br.com.socialconnect.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

/**
 * Confere se a data é hoje ou antes.
 * <p>
 * "Hoje" é a data do relógio do servidor, no fuso da JVM (LocalDate.now()).
 * No laboratório, é o horário de Brasília. Uma doação recebida hoje pode ser
 * registrada com a data de hoje.
 */
public class DataNaoFuturaValidator implements ConstraintValidator<DataNaoFutura, LocalDate> {

    @Override
    public boolean isValid(LocalDate data, ConstraintValidatorContext context) {
        // Nula é válida AQUI de propósito: quem obriga o campo é o @NotNull.
        // Assim a data ausente mostra uma mensagem só, e não duas.
        if (data == null) {
            return true;
        }
        // isAfter é estrito: hoje não está depois de hoje, então passa.
        return !data.isAfter(LocalDate.now());
    }
}
