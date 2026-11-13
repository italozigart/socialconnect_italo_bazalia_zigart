package br.com.socialconnect.api.doacoes.controller;

import br.com.socialconnect.api.doacoes.dto.DoacaoPatchDTO;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
 * PUT, PATCH e DELETE (Etapa 9) seguem o molde do beneficiário: PUT com
 * substituição total, PATCH só com os campos enviados e DELETE com busca antes
 * (204 ou 404). No PUT e no PATCH, a ordem dos erros é: id da URL ou corpo
 * inválidos (400), doação inexistente (404) e doador inexistente (422).
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

    // PUT: substituição total, com as regras do POST (o mesmo DoacaoRequestDTO
    // e o mesmo @Valid). 200 com a doação atualizada, como no beneficiário.
    // Ordem das checagens: o id da URL (400 se não for número), o JSON e o
    // @Valid (400), a doação da URL (404) e o doador do corpo (422). Um corpo
    // inválido não chega a consultar o banco, e o doador só é procurado
    // depois de a doação ser encontrada.
    @PutMapping("/{idDoacao}")
    @Operation(operationId = "atualizarDoacao", summary = "Atualiza uma doação (substituição total)",
            description = "Substitui os cinco campos enviáveis, com as regras do POST: valor e descricao "
                    + "ausentes no JSON viram null. O idDoacao é o da URL; se vier no JSON, é ignorado. "
                    + "Ordem dos erros: 400 (id, JSON ou campos), 404 (doação) e 422 (doador).")
    @ApiResponse(responseCode = "200", description = "Doação atualizada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos (lista os campos em errors), "
            + "tipo fora da lista, JSON malformado ou id inválido",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Campos inválidos", value = DoacaoExemplos.DADOS_INVALIDOS),
                            @ExampleObject(name = "Tipo fora da lista", value = DoacaoExemplos.CORPO_INVALIDO),
                            @ExampleObject(name = "Id inválido", value = DoacaoExemplos.ID_INVALIDO)
                    }))
    @ApiResponse(responseCode = "404", description = "Doação não encontrada",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = DoacaoExemplos.NAO_ENCONTRADO)))
    @ApiResponse(responseCode = "422",
            description = "Doador inexistente: o idDoador não é de nenhum doador cadastrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = DoacaoExemplos.DOADOR_INEXISTENTE)))
    @ApiResponse(responseCode = "500", description = "Erro inesperado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = DoacaoExemplos.ERRO_INTERNO)))
    public ResponseEntity<DoacaoResponseDTO> atualizar(
            @Parameter(description = "Id da doação", example = "1")
            @PathVariable("idDoacao") Long idDoacao,
            @Valid @RequestBody DoacaoRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(idDoacao, dto));
    }

    // PATCH: só os campos enviados e não nulos mudam (DoacaoPatchDTO). O @Valid
    // confere só os campos que vieram: a data continua sem poder estar no
    // futuro, e o valor, maior que zero. O idDoador, se vier, passa pela mesma
    // conferência do POST (422). Mesma ordem de erros do PUT.
    @PatchMapping("/{idDoacao}")
    @Operation(operationId = "atualizarParcialmenteDoacao", summary = "Atualiza parcialmente uma doação",
            description = "Só os campos enviados e não nulos mudam, com as regras do POST: a data continua "
                    + "sem poder estar no futuro. Se o idDoador vier, o doador precisa estar cadastrado "
                    + "(senão, 422). Descrição em branco apaga a descrição; para apagar o valor, use o PUT. "
                    + "Ordem dos erros: 400, 404 e 422, como no PUT.")
    @ApiResponse(responseCode = "200", description = "Doação atualizada")
    @ApiResponse(responseCode = "400", description = "Campo inválido (data no futuro, valor ou tamanho "
            + "da descrição), tipo fora da lista, JSON malformado ou id inválido",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Campos inválidos", value = DoacaoExemplos.DADOS_INVALIDOS_PATCH),
                            @ExampleObject(name = "Tipo fora da lista", value = DoacaoExemplos.CORPO_INVALIDO),
                            @ExampleObject(name = "Id inválido", value = DoacaoExemplos.ID_INVALIDO)
                    }))
    @ApiResponse(responseCode = "404", description = "Doação não encontrada",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = DoacaoExemplos.NAO_ENCONTRADO)))
    @ApiResponse(responseCode = "422",
            description = "Doador inexistente: o idDoador enviado não é de nenhum doador cadastrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = DoacaoExemplos.DOADOR_INEXISTENTE)))
    @ApiResponse(responseCode = "500", description = "Erro inesperado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = DoacaoExemplos.ERRO_INTERNO)))
    public ResponseEntity<DoacaoResponseDTO> atualizarParcial(
            @Parameter(description = "Id da doação", example = "1")
            @PathVariable("idDoacao") Long idDoacao,
            @Valid @RequestBody DoacaoPatchDTO dto) {
        return ResponseEntity.ok(service.atualizarParcial(idDoacao, dto));
    }

    // 204 No Content: sucesso sem corpo (content = @Content deixa a resposta
    // sem schema). O segundo DELETE do mesmo id dá 404: a doação já não existe.
    @DeleteMapping("/{idDoacao}")
    @Operation(operationId = "deletarDoacao", summary = "Remove uma doação",
            description = "Remove só a doação; o doador continua cadastrado. Um segundo DELETE com o mesmo "
                    + "id responde 404.")
    @ApiResponse(responseCode = "204", description = "Doação removida, sem corpo", content = @Content)
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
    public ResponseEntity<Void> deletar(
            @Parameter(description = "Id da doação", example = "1")
            @PathVariable("idDoacao") Long idDoacao) {
        service.deletar(idDoacao);
        return ResponseEntity.noContent().build();
    }
}
