package br.com.socialconnect.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Confere o formato (11 dígitos) e os dois dígitos verificadores do CPF.
 * <p>
 * Cálculo de cada dígito verificador:
 * <ol>
 *   <li>multiplica os dígitos anteriores por pesos decrescentes, terminando em 2
 *       (10 a 2 para o 1º verificador, sobre 9 dígitos; 11 a 2 para o 2º, sobre 10);</li>
 *   <li>soma os produtos e calcula o resto da divisão por 11;</li>
 *   <li>resto 0 ou 1 dá dígito 0; senão, o dígito é 11 menos o resto.</li>
 * </ol>
 * Exemplo com 529.982.247-25: a soma do 1º é 295, resto 9, dígito 11 - 9 = 2.
 */
public class CpfValidator implements ConstraintValidator<CPF, String> {

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        // Nulo ou em branco é válido AQUI de propósito: quem obriga o campo é o
        // @NotBlank. Assim o campo vazio mostra uma mensagem só, e não duas.
        if (cpf == null || cpf.isBlank()) {
            return true;
        }
        // Só 11 dígitos: com máscara, letras ou outro tamanho, é inválido.
        if (!cpf.matches("\\d{11}")) {
            return false;
        }
        // 111.111.111-11, 222... e os demais com todos os dígitos iguais passam
        // no cálculo, mas não são CPFs emitidos.
        if (cpf.chars().distinct().count() == 1) {
            return false;
        }
        return digitoVerificador(cpf, 9) == digito(cpf, 9)
                && digitoVerificador(cpf, 10) == digito(cpf, 10);
    }

    // Calcula o verificador a partir dos 'quantidade' primeiros dígitos:
    // 9 para o 1º verificador (pesos 10 a 2) e 10 para o 2º (pesos 11 a 2).
    private static int digitoVerificador(String cpf, int quantidade) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            int peso = quantidade + 1 - i;
            soma += digito(cpf, i) * peso;
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    // '7' - '0' = 7: converte o caractere do dígito no número.
    private static int digito(String cpf, int posicao) {
        return cpf.charAt(posicao) - '0';
    }
}
