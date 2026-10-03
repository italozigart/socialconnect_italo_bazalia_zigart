package br.com.socialconnect.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Traduz as exceções da API para Problem Details (RFC 7807), em JSON.
 * Vale para todos os controllers, inclusive o de beneficiários.
 * <p>
 * Regras deste handler:
 * <ul>
 *   <li>Os textos vêm do messages.properties (i18n), nunca de ex.getMessage(),
 *       que pode expor detalhes internos (classes, SQL, posição no JSON).</li>
 *   <li>Erros do cliente têm handler próprio (400, 404, 405, 415). Sem eles, o
 *       handler genérico de Exception transformaria esses erros em 500.</li>
 *   <li>O handler genérico é o último recurso: responde 500 sem detalhes e
 *       registra o stack trace no log, porque ao tratar a exceção o Spring
 *       deixa de imprimi-la sozinho.</li>
 * </ul>
 * Sem @ResponseStatus nos métodos: o status sai do ResponseEntity, e a
 * documentação dos erros fica por endpoint, nas @ApiResponse dos controllers.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String TIPO_BASE = "https://socialconnect.api/errors/";

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // ===================== Erros de negócio =====================

    // 404: id inexistente no GET, PUT ou DELETE.
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ProblemDetail> tratarRecursoNaoEncontrado(RecursoNaoEncontradoException ex,
                                                                     HttpServletRequest request) {
        // O id vai como String: o MessageFormat formataria um Long com separador
        // de milhar ("99.999").
        return responder(HttpStatus.NOT_FOUND, "recurso-nao-encontrado",
                mensagem("erro.recurso-nao-encontrado.titulo"),
                mensagem("erro.recurso-nao-encontrado.detalhe", ex.getRecurso(), String.valueOf(ex.getId())),
                request);
    }

    // ===================== Erros do cliente =====================

    // 400: Bean Validation (@Valid) recusou um ou mais campos.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> tratarValidacao(MethodArgumentNotValidException ex,
                                                         HttpServletRequest request) {
        // Lista (e não Map): um campo pode ter dois erros, e um Map com chave
        // repetida lançaria exceção. Ordenada por campo para a resposta ser estável.
        List<ProblemDetail.ErroCampo> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> new ProblemDetail.ErroCampo(erro.getField(), erro.getDefaultMessage()))
                .sorted(Comparator.comparing(ProblemDetail.ErroCampo::field))
                .toList();

        return responder(HttpStatus.BAD_REQUEST, "validacao",
                mensagem("erro.validacao.titulo"),
                mensagem("erro.validacao.detalhe"),
                request, erros);
    }

    // 400: JSON malformado, ou valor que não converte (ex.: "categoria": "XYZ").
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> tratarCorpoInvalido(HttpMessageNotReadableException ex,
                                                             HttpServletRequest request) {
        return responder(HttpStatus.BAD_REQUEST, "corpo-invalido",
                mensagem("erro.corpo-invalido.titulo"),
                mensagem("erro.corpo-invalido.detalhe"),
                request);
    }

    // 400: parâmetro da URL com tipo errado (ex.: /produtos/abc ou ?categoria=XYZ).
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> tratarParametroInvalido(MethodArgumentTypeMismatchException ex,
                                                                 HttpServletRequest request) {
        String detalhe = mensagem("erro.parametro-invalido.detalhe", ex.getName(), String.valueOf(ex.getValue()));

        // Para enum, mostra os valores aceitos (ex.: ALIMENTO, ROUPA, HIGIENE, OUTROS).
        Class<?> tipo = ex.getRequiredType();
        if (tipo != null && tipo.isEnum()) {
            String valores = Arrays.stream(tipo.getEnumConstants())
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            detalhe = detalhe + " " + mensagem("erro.parametro-invalido.valores", valores);
        }

        return responder(HttpStatus.BAD_REQUEST, "parametro-invalido",
                mensagem("erro.parametro-invalido.titulo"), detalhe, request);
    }

    // 400: sort por um atributo que não existe na Entity (ex.: ?sort=preco).
    // No Spring Data 4.1 esta classe fica em org.springframework.data.core.
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ProblemDetail> tratarOrdenacaoInvalida(PropertyReferenceException ex,
                                                                 HttpServletRequest request) {
        return responder(HttpStatus.BAD_REQUEST, "ordenacao-invalida",
                mensagem("erro.ordenacao-invalida.titulo"),
                mensagem("erro.ordenacao-invalida.detalhe", ex.getPropertyName()),
                request);
    }

    // 404: endereço que não existe na API (ex.: /api/v1/produtoss, /favicon.ico).
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> tratarEndpointNaoEncontrado(NoResourceFoundException ex,
                                                                     HttpServletRequest request) {
        return responder(HttpStatus.NOT_FOUND, "endpoint-nao-encontrado",
                mensagem("erro.endpoint-nao-encontrado.titulo"),
                mensagem("erro.endpoint-nao-encontrado.detalhe", request.getRequestURI()),
                request);
    }

    // 405: método HTTP não aceito no endereço (ex.: DELETE /api/v1/produtos, sem id).
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ProblemDetail> tratarMetodoNaoSuportado(HttpRequestMethodNotSupportedException ex,
                                                                  HttpServletRequest request) {
        return responder(HttpStatus.METHOD_NOT_ALLOWED, "metodo-nao-suportado",
                mensagem("erro.metodo-nao-suportado.titulo"),
                mensagem("erro.metodo-nao-suportado.detalhe", ex.getMethod()),
                request);
    }

    // 415: corpo enviado sem Content-Type: application/json.
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ProblemDetail> tratarTipoDeMidia(HttpMediaTypeNotSupportedException ex,
                                                           HttpServletRequest request) {
        return responder(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "tipo-de-midia",
                mensagem("erro.tipo-midia.titulo"),
                mensagem("erro.tipo-midia.detalhe"),
                request);
    }

    // ===================== Último recurso =====================

    // 500: qualquer outra exceção. Resposta genérica, sem stack trace nem
    // mensagem interna; o detalhe completo vai só para o log.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> tratarErroInesperado(Exception ex, HttpServletRequest request) {
        log.error("Erro inesperado em {} {}", request.getMethod(), request.getRequestURI(), ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "erro-interno",
                mensagem("erro.interno.titulo"),
                mensagem("erro.interno.detalhe"),
                request);
    }

    // ===================== Apoio =====================

    private ResponseEntity<ProblemDetail> responder(HttpStatus status, String tipo, String titulo,
                                                    String detalhe, HttpServletRequest request) {
        return responder(status, tipo, titulo, detalhe, request, List.of());
    }

    private ResponseEntity<ProblemDetail> responder(HttpStatus status, String tipo, String titulo,
                                                    String detalhe, HttpServletRequest request,
                                                    List<ProblemDetail.ErroCampo> erros) {
        ProblemDetail corpo = new ProblemDetail(
                TIPO_BASE + tipo,
                titulo,
                status.value(),
                detalhe,
                request.getRequestURI(), // só o caminho, ex.: /api/v1/produtos/99
                Instant.now(),
                erros
        );
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON) // o contrato declara application/json
                .body(corpo);
    }

    // Busca o texto no messages.properties, no idioma da requisição (fixado em
    // pt-BR no application.properties). Se a chave faltar, devolve a própria
    // chave em vez de quebrar a resposta de erro.
    private String mensagem(String codigo, Object... argumentos) {
        return messageSource.getMessage(codigo, argumentos, codigo, LocaleContextHolder.getLocale());
    }
}
