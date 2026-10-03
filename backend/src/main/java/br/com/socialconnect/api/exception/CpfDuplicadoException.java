package br.com.socialconnect.api.exception;

/**
 * Já existe outro beneficiário com o CPF informado: o GlobalExceptionHandler
 * responde 409. Guarda o CPF (e não o texto pronto) para a mensagem vir do
 * messages.properties.
 */
public class CpfDuplicadoException extends RuntimeException {

    private final String cpf;

    public CpfDuplicadoException(String cpf) {
        super("CPF já cadastrado: " + cpf); // texto para o log
        this.cpf = cpf;
    }

    public String getCpf() {
        return cpf;
    }
}
