package br.com.socialconnect.api.exception;

/**
 * A operação deixaria o estoque atual abaixo de zero: o GlobalExceptionHandler
 * responde 422. É 422, e não 400, porque o JSON está bem formado e os tipos
 * estão certos; quem recusa é a regra de negócio do estoque.
 */
public class EstoqueNegativoException extends RuntimeException {

    private final Integer estoqueAtual;

    public EstoqueNegativoException(Integer estoqueAtual) {
        super("Estoque atual não pode ser negativo: " + estoqueAtual);
        this.estoqueAtual = estoqueAtual;
    }

    public Integer getEstoqueAtual() {
        return estoqueAtual;
    }
}
