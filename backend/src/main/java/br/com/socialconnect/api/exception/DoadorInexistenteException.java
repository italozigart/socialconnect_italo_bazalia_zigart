package br.com.socialconnect.api.exception;

/**
 * O idDoador enviado no corpo de uma doação não é de nenhum doador
 * cadastrado: o GlobalExceptionHandler responde 422. O porquê do 422, e não
 * do 404, está em docs/adr/0001-doador-inexistente.md.
 * <p>
 * É diferente da RecursoNaoEncontradoException (404): lá, o id que não existe
 * está na URL; aqui, a URL existe, e quem não existe é um recurso citado no
 * corpo da requisição.
 * <p>
 * Guarda o id (e não o texto pronto) para a mensagem vir do
 * messages.properties.
 */
public class DoadorInexistenteException extends RuntimeException {

    private final Long idDoador;

    public DoadorInexistenteException(Long idDoador) {
        super("Doação com doador inexistente: id " + idDoador); // texto para o log
        this.idDoador = idDoador;
    }

    public Long getIdDoador() {
        return idDoador;
    }
}
