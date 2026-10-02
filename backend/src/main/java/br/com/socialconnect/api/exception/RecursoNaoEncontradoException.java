package br.com.socialconnect.api.exception;

/**
 * Recurso pedido por id não existe: o GlobalExceptionHandler responde 404.
 * Guarda os dados (e não o texto pronto) para a mensagem vir do
 * messages.properties, no idioma configurado.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    private final String recurso;
    private final Long id;

    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(recurso + " não encontrado com o id " + id); // texto para o log
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
