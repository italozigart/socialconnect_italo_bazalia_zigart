package br.com.socialconnect.api.exception;

/**
 * Recurso pedido por id não existe: o GlobalExceptionHandler responde 404.
 * Guarda os dados (e não o texto pronto) para a mensagem vir do
 * messages.properties, no idioma configurado.
 * <p>
 * Os textos não têm gênero ("... não existe"): a mesma exceção vale para
 * Beneficiário e Doador (masculinos) e para Doação (feminino).
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    private final String recurso;
    private final Long id;

    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(recurso + " com id " + id + " não existe"); // texto para o log
        this.recurso = recurso;
        this.id = id;
    }

    public String getRecurso() {
        return recurso;
    }

    public Long getId() {
        return id;
    }
}
