package br.com.socialconnect.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Confere o telefone em três passos:
 * <ol>
 *   <li>formato: 10 ou 11 dígitos, sem máscara;</li>
 *   <li>DDD (os 2 primeiros dígitos): de 11 a 99, porque não existe DDD
 *       começando com 0 e o menor é 11. Não confere a lista oficial de DDDs;</li>
 *   <li>início do número, logo depois do DDD: fixo (10 dígitos) começa com
 *       2, 3, 4 ou 5; celular (11 dígitos) começa com 9.</li>
 * </ol>
 * Exemplos: 1133334444 (fixo) e 11999999999 (celular) passam; (11) 99999-9999
 * (com máscara), 1112345678 (número começando com 1) e 11899999999 (celular
 * sem o 9 na frente) não passam.
 */
public class TelefoneValidator implements ConstraintValidator<Telefone, String> {

    @Override
    public boolean isValid(String telefone, ConstraintValidatorContext context) {
        // Nulo ou em branco é válido AQUI de propósito: o telefone é opcional.
        // Se um campo precisar dele, quem obriga é o @NotBlank. O Service grava
        // o telefone em branco como nulo.
        if (telefone == null || telefone.isBlank()) {
            return true;
        }
        // Só dígitos, 10 ou 11: com máscara, letras ou outro tamanho, é inválido.
        if (!telefone.matches("\\d{10,11}")) {
            return false;
        }
        int ddd = Integer.parseInt(telefone.substring(0, 2));
        if (ddd < 11) {
            return false;
        }
        char inicioDoNumero = telefone.charAt(2);
        if (telefone.length() == 10) {
            return inicioDoNumero >= '2' && inicioDoNumero <= '5'; // fixo
        }
        return inicioDoNumero == '9'; // celular
    }
}
