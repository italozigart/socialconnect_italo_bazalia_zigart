package br.com.socialconnect.api.doacoes.controller;

import br.com.socialconnect.api.doacoes.dto.DoacaoRequestDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoResponseDTO;
import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import br.com.socialconnect.api.doacoes.service.DoacaoService;
import br.com.socialconnect.api.exception.ProblemDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDate;

/**
 * Endpoints de doação, no mesmo molde do DoadorController:
 * <ul>
 *   <li>nomes explícitos no @PathVariable e nos @RequestParam (não dependem do
 *       -parameters do compilador);</li>
 *   <li>toda resposta de erro declara content com o ProblemDetail e um exemplo
 *       (DoacaoExemplos); as de sucesso usam o tipo de retorno do método;</li>
 *   <li>operationId e summary iguais aos do docs/openapi.yaml.</li>
 * </ul>
 * Sem PUT, PATCH e DELETE: alteração e remoção são a Etapa 9.
 */
@RestController
@RequestMapping("/api/v1/doacoes")
@Tag(name = "Doações", description = "Gestão de doações")
public class DoacaoController {

    private final DoacaoService service;

    public DoacaoController(DoacaoService service) {
        this.service = service;
    }

    // GET /api/v1/doacoes?dataInicio=2026-10-01&dataFim=2026-10-31&tipo=ALIMENTO&page=0&size=10
    // - Filtros opcionais, em qualquer combinação (DoacaoSpecifications).
    // - @DateTimeFormat(iso = DATE): datas só no formato aaaa-mm-dd, como no
    //   contrato. Data que não converte (31/10/2026) e tipo fora da lista
    //   (DINHEIRO) dão 400 no GlobalExceptionHandler; para o tipo, a resposta
    //   lista os valores aceitos.
    // - @PageableDefault: size 10 e as mais recentes primeiro. O idDoacao
    //   desempata as doações do mesmo dia: sem ele, a ordem delas pode mudar
    //   de uma página para outra, e uma doação apareceria duas vezes (ou
    //   nenhuma). O roteiro do mob escreve "sort.Direction.DESC" como argumento
    //   solto, o que não compila; o certo é o atributo direction.
    // - @ParameterObject: o mesmo cuidado do beneficiário (sem ele, o "Try it
    //   out" do Swagger daria 500).
    // - Filtros sem example, como no beneficiário: o Swagger UI preencheria o
    //   campo no "Try it out" e a lista viria filtrada sem o usuário pedir.
    @GetMapping
    @Operation(operationId = "listarDoacoes",
            summary = "Lista doações com paginação e filtros",
            description = "Filtros opcionais e combináveis: dataInicio e dataFim (aaaa-mm-dd, as duas datas "
                    + "incluídas) e tipo. dataInicio depois de dataFim dá 400. Paginação: page começa em 0, "
                    + "size padrão 10 e sort no formato atributo,direção (ex.: valor,desc). Sem sort, as "
                    + "doações mais recentes vêm primeiro.")
    @ApiResponse(responseCode = "200", description = "Página de doações")
    @ApiResponse(responseCode = "400", description = "Período invertido, data ou tipo inválidos, "
            + "ou ordenação por atributo inexistente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Período invertido", value = DoacaoExemplos.PERIODO_INVALIDO),
                            @ExampleObject(name = "Tipo fora da lista", value = DoacaoExemplos.TIPO_INVALIDO),
                            @ExampleObject(name = "Ordenação inválida", value = DoacaoExemplos.ORDENACAO_INVALIDA)
                    }))
    @ApiResponse(responseCode = "500", description = "Erro inesperado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = DoacaoExemplos.ERRO_INTERNO)))
    public ResponseEntity<Page<DoacaoResponseDTO>> listar(
            @Parameter(description = "Início do período, incluído (aaaa-mm-dd)")
            @RequestParam(name = "dataInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @Parameter(description = "Fim do período, incluído (aaaa-mm-dd)")
            @RequestParam(name = "dataFim", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @Parameter(description = "ALIMENTO, ROUPA ou FINANCEIRA")
            @RequestParam(name = "tipo", required = false) TipoDoacao tipo,
            @ParameterObject
            @PageableDefault(size = 10, sort = {"dataDoacao", "idDoacao"}, direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(service.listar(dataInicio, dataFim, tipo, pageable));
    }

    @GetMapping("/{idDoacao}")
    @Operation(operationId = "buscarDoacaoPorId", summary = "Busca uma doação por ID")
    @ApiResponse(responseCode = "200", description = "Dados da doação")
    @ApiResponse(responseCode = "400", description = "Id inválido: não numérico ou acima do limite",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = DoacaoExemplos.ID_INVALIDO)))
    @ApiResponse(responseCode = "404", description = "Doação não encontrada",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = DoacaoExemplos.NAO_ENCONTRADO)))
    @ApiResponse(responseCode = "500", description = "Erro inesperado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = DoacaoExemplos.ERRO_INTERNO)))
    public ResponseEntity<DoacaoResponseDTO> buscarPorId(
            @Parameter(description = "Id da doação", example = "1")
            @PathVariable("idDoacao") Long idDoacao) {
        return ResponseEntity.ok(service.buscarPorId(idDoacao));
    }

    // 201 Created + Location, que aponta para o GET por id acima.
    // @Valid: sem ele, as anotações do DoacaoRequestDTO seriam ignoradas.
    // Ordem das checagens: o JSON (400 de corpo inválido), o @Valid (400 com a
    // lista de campos) e só então o Service, que procura o doador (422). Um
    // corpo com campo inválido nem chega a consultar o banco.
    @PostMapping
    @Operation(operationId = "criarDoacao", summary = "Registra uma nova doação",
            description = "idDoacao é gerado pelo servidor; se vier no JSON, é ignorado. idDoador precisa "
                    + "ser de um doador cadastrado: se não for, a resposta é 422. As regras de cada campo "
                    + "estão no schema DoacaoRequest.")
    @ApiResponse(responseCode = "201", description = "Doação registrada",
            headers = @Header(name = "Location", description = "Endereço da doação registrada",
                    schema = @Schema(type = "string", example = "/api/v1/doacoes/1")))
    @ApiResponse(responseCode = "400", description = "Dados inválidos (lista os campos em errors), "
            + "tipo fora da lista ou JSON malformado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Campos inválidos", value = DoacaoExemplos.DADOS_INVALIDOS),
                            @ExampleObject(name = "Tipo fora da lista", value = DoacaoExemplos.CORPO_INVALIDO)
                    }))
    @ApiResponse(responseCode = "422",
            description = "Doador inexistente: o idDoador não é de nenhum doador cadastrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = DoacaoExemplos.DOADOR_INEXISTENTE)))
    @ApiResponse(responseCode = "500", description = "Erro inesperado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = DoacaoExemplos.ERRO_INTERNO)))
    public ResponseEntity<DoacaoResponseDTO> criar(@Valid @RequestBody DoacaoRequestDTO dto) {
        DoacaoResponseDTO criada = service.criar(dto);
        URI location = URI.create("/api/v1/doacoes/" + criada.idDoacao());
        return ResponseEntity.created(location).body(criada);
    }
}
