package br.com.socialconnect.api.exception;

/**
 * Já existe um produto com o nome informado: o GlobalExceptionHandler responde 409.
 */
public class NomeDuplicadoException extends RuntimeException {

    private final String nome;

    public NomeDuplicadoException(String nome) {
        super("Nome de produto já cadastrado: " + nome);
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
