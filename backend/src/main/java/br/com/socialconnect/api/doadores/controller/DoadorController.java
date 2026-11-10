package br.com.socialconnect.api.doadores.controller;

import br.com.socialconnect.api.doadores.dto.DoadorRequestDTO;
import br.com.socialconnect.api.doadores.dto.DoadorResponseDTO;
import br.com.socialconnect.api.doadores.service.DoadorService;
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
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * Endpoints de doador, no mesmo molde do BeneficiarioController:
 * <ul>
 *   <li>nomes explícitos no @PathVariable (não dependem do -parameters do compilador);</li>
 *   <li>toda resposta de erro declara content com o ProblemDetail e um exemplo
 *       (DoadorExemplos); as de sucesso usam o tipo de retorno do método;</li>
 *   <li>operationId e summary iguais aos do docs/openapi.yaml.</li>
 * </ul>
 * Sem PUT, PATCH e DELETE: não aparecem nas aulas nem no contrato.
 */
@RestController
@RequestMapping("/api/v1/doadores")
@Tag(name = "Doadores", description = "Gestão de doadores")
public class DoadorController {

    private final DoadorService service;

    public DoadorController(DoadorService service) {
        this.service = service;
    }

    // GET /api/v1/doadores?page=0&size=10&sort=nome,asc
    // - @PageableDefault: size 10, como no contrato, e ordem por nome quando o
    //   cliente não manda sort (o índice idx_doadores_nome da V3 atende a essa ordem).
    // - @ParameterObject: sem ele, o springdoc documentaria o Pageable como um
    //   JSON único, e o "Try it out" daria 500 (o mesmo cuidado do beneficiário).
    @GetMapping
    @Operation(operationId = "listarDoadores",
            summary = "Lista doadores com paginação",
            description = "Paginação: page começa em 0, size padrão 10 e sort no formato atributo,direção "
                    + "(ex.: nome,asc ou tipo,desc). Sem sort, a lista vem em ordem de nome.")
    @ApiResponse(responseCode = "200", description = "Página de doadores")
    @ApiResponse(responseCode = "400", description = "Ordenação por atributo inexistente (ex.: sort=idade)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = DoadorExemplos.ORDENACAO_INVALIDA)))
    @ApiResponse(responseCode = "500", description = "Erro inesperado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = DoadorExemplos.ERRO_INTERNO)))
    public ResponseEntity<Page<DoadorResponseDTO>> listar(
            @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listar(pageable));
    }

    @GetMapping("/{idDoador}")
    @Operation(operationId = "buscarDoadorPorId", summary = "Busca um doador por ID")
    @ApiResponse(responseCode = "200", description = "Dados do doador")
    @ApiResponse(responseCode = "400", description = "Id inválido: não numérico ou acima do limite",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = DoadorExemplos.ID_INVALIDO)))
    @ApiResponse(responseCode = "404", description = "Doador não encontrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = DoadorExemplos.NAO_ENCONTRADO)))
    @ApiResponse(responseCode = "500", description = "Erro inesperado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = DoadorExemplos.ERRO_INTERNO)))
    public ResponseEntity<DoadorResponseDTO> buscarPorId(
            @Parameter(description = "Id do doador", example = "1")
            @PathVariable("idDoador") Long idDoador) {
        return ResponseEntity.ok(service.buscarPorId(idDoador));
    }

    // 201 Created + Location, que aponta para o GET por id acima.
    // @Valid: sem ele, as anotações do DoadorRequestDTO seriam ignoradas.
    // O 400 tem dois exemplos (lista no Swagger): campos recusados pela
    // validação e tipo fora da lista, que o Jackson não converte para o enum.
    @PostMapping
    @Operation(operationId = "criarDoador", summary = "Cria um novo doador",
            description = "idDoador é gerado pelo servidor; se vier no JSON, é ignorado. tipo aceita "
                    + "PESSOA_FISICA ou PESSOA_JURIDICA. E-mail e telefone são opcionais (em branco, "
                    + "são gravados como nulos); o telefone vai só com dígitos, DDD + número.")
    @ApiResponse(responseCode = "201", description = "Doador criado",
            headers = @Header(name = "Location", description = "Endereço do doador criado",
                    schema = @Schema(type = "string", example = "/api/v1/doadores/1")))
    @ApiResponse(responseCode = "400", description = "Dados inválidos (lista os campos em errors), "
            + "tipo fora da lista ou JSON malformado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Campos inválidos", value = DoadorExemplos.DADOS_INVALIDOS),
                            @ExampleObject(name = "Tipo fora da lista", value = DoadorExemplos.CORPO_INVALIDO)
                    }))
    @ApiResponse(responseCode = "500", description = "Erro inesperado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = DoadorExemplos.ERRO_INTERNO)))
    public ResponseEntity<DoadorResponseDTO> criar(@Valid @RequestBody DoadorRequestDTO dto) {
        DoadorResponseDTO criado = service.criar(dto);
        URI location = URI.create("/api/v1/doadores/" + criado.idDoador());
        return ResponseEntity.created(location).body(criado);
    }
}
