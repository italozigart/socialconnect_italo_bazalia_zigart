package br.com.socialconnect.api.beneficiarios.controller;

import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioPatchDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioRequestDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioResponseDTO;
import br.com.socialconnect.api.beneficiarios.service.BeneficiarioService;
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

/**
 * Endpoints de beneficiário, documentados no Swagger (Etapa 5).
 * <ul>
 *   <li>operationId e summary iguais aos do docs/openapi.yaml.</li>
 *   <li>Toda resposta de erro declara content com o ProblemDetail e um exemplo.
 *       Sem content, o springdoc preencheria o erro com o tipo de retorno do
 *       método, como se o 404 devolvesse um beneficiário.</li>
 *   <li>As respostas de sucesso ficam sem content: o springdoc usa o tipo de
 *       retorno (BeneficiarioResponse ou PageBeneficiarioResponse).</li>
 *   <li>Os status ficam aqui, por endpoint. Com @ResponseStatus nos handlers,
 *       o springdoc repetiria cada status em todos os endpoints.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/beneficiarios")
@Tag(name = "Beneficiários", description = "Gestão de beneficiários")
public class BeneficiarioController {

    private final BeneficiarioService service;

    public BeneficiarioController(BeneficiarioService service) {
        this.service = service;
    }

    // GET /api/v1/beneficiarios?nome=&cpf=&page=0&size=10&sort=nome,asc
    // - Nome explícito nos @RequestParam e nos @PathVariable: não depende de o
    //   compilador guardar o nome dos parâmetros (-parameters), o que falha
    //   quando a IDE compila sozinha.
    // - @PageableDefault: size 10, como no docs/openapi.yaml (sem ele, o Boot
    //   usaria 20). O sort usa o nome do atributo da Entity (nome, dataCadastro),
    //   não o da coluna.
    // - @ParameterObject: sem ele, o springdoc documenta o Pageable como um JSON
    //   único com "sort": ["string"], e executar esse exemplo no Swagger dá 500.
    // - Page é serializado direto (modo DIRECT, o padrão do Spring Data). O log
    //   avisa uma vez que esse JSON não tem estrutura garantida, mas o modo VIA_DTO
    //   sugerido aninha os totais em "page" e remove first/last, o que quebraria
    //   o PageBeneficiarioResponse do contrato.
    // - Filtros sem example: o Swagger UI preencheria o campo no "Try it out" e
    //   a listagem viria filtrada sem o usuário pedir.
    @GetMapping
    @Operation(operationId = "listarBeneficiarios",
            summary = "Lista beneficiários com paginação e filtros",
            description = "Filtros opcionais: cpf (busca exata) tem prioridade sobre nome (parte do nome, "
                    + "sem diferenciar maiúsculas). Paginação: page começa em 0, size padrão 10 e sort "
                    + "no formato atributo,direção (ex.: nome,asc ou dataCadastro,desc).")
    @ApiResponse(responseCode = "200", description = "Página de beneficiários")
    @ApiResponse(responseCode = "400", description = "Ordenação por atributo inexistente (ex.: sort=idade)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = BeneficiarioExemplos.ORDENACAO_INVALIDA)))
    @ApiResponse(responseCode = "500", description = "Erro inesperado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = BeneficiarioExemplos.ERRO_INTERNO)))
    public ResponseEntity<Page<BeneficiarioResponseDTO>> listar(
            @Parameter(description = "Parte do nome, sem diferenciar maiúsculas (ex.: maria)")
            @RequestParam(name = "nome", required = false) String nome,
            @Parameter(description = "CPF exato: 11 dígitos, sem máscara (ex.: 52998224725)")
            @RequestParam(name = "cpf", required = false) String cpf,
            @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listar(nome, cpf, pageable));
    }

    @GetMapping("/{idBeneficiario}")
    @Operation(operationId = "buscarBeneficiarioPorId", summary = "Busca um beneficiário por ID")
    @ApiResponse(responseCode = "200", description = "Dados do beneficiário")
    @ApiResponse(responseCode = "400", description = "Id inválido: não numérico ou acima do limite",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = BeneficiarioExemplos.ID_INVALIDO)))
    @ApiResponse(responseCode = "404", description = "Beneficiário não encontrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = BeneficiarioExemplos.NAO_ENCONTRADO)))
    @ApiResponse(responseCode = "500", description = "Erro inesperado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = BeneficiarioExemplos.ERRO_INTERNO)))
    public ResponseEntity<BeneficiarioResponseDTO> buscarPorId(
            @Parameter(description = "Id do beneficiário", example = "1")
            @PathVariable("idBeneficiario") Long idBeneficiario) {
        return ResponseEntity.ok(service.buscarPorId(idBeneficiario));
    }

    // 201 Created + Location: o recurso novo ganha endereço próprio.
    // Sem o @RequestBody, os campos do JSON chegariam nulos.
    // @Valid: aplica as anotações do DTO antes de chamar o Service. Se algum
    // campo falhar, o Spring lança MethodArgumentNotValidException e o
    // GlobalExceptionHandler responde 400 com a lista de campos. Sem o @Valid,
    // as anotações do DTO são ignoradas.
    @PostMapping
    @Operation(operationId = "criarBeneficiario", summary = "Cria um novo beneficiário",
            description = "idBeneficiario e dataCadastro são gerados pelo servidor; se vierem no JSON, "
                    + "são ignorados. O CPF precisa ser válido, ter 11 dígitos sem máscara e não repetir.")
    @ApiResponse(responseCode = "201", description = "Beneficiário criado",
            headers = @Header(name = "Location", description = "Endereço do beneficiário criado",
                    schema = @Schema(type = "string", example = "/api/v1/beneficiarios/1")))
    @ApiResponse(responseCode = "400", description = "Dados inválidos (lista os campos em errors) ou JSON malformado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = BeneficiarioExemplos.DADOS_INVALIDOS)))
    @ApiResponse(responseCode = "409", description = "CPF já cadastrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = BeneficiarioExemplos.CPF_DUPLICADO)))
    @ApiResponse(responseCode = "500", description = "Erro inesperado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = BeneficiarioExemplos.ERRO_INTERNO)))
    public ResponseEntity<BeneficiarioResponseDTO> criar(@Valid @RequestBody BeneficiarioRequestDTO dto) {
        BeneficiarioResponseDTO criado = service.criar(dto);
        URI location = URI.create("/api/v1/beneficiarios/" + criado.idBeneficiario());
        return ResponseEntity.created(location).body(criado);
    }

    // PUT: substituição total (campo ausente no JSON vira null).
    // 200 com o recurso atualizado, como no contrato.
    @PutMapping("/{idBeneficiario}")
    @Operation(operationId = "atualizarBeneficiario", summary = "Atualiza um beneficiário (substituição total)",
            description = "Substitui os cinco campos enviáveis: campo ausente no JSON vira null. "
                    + "idBeneficiario e dataCadastro não mudam.")
    @ApiResponse(responseCode = "200", description = "Beneficiário atualizado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos, JSON malformado ou id inválido",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = BeneficiarioExemplos.DADOS_INVALIDOS)))
    @ApiResponse(responseCode = "404", description = "Beneficiário não encontrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = BeneficiarioExemplos.NAO_ENCONTRADO)))
    @ApiResponse(responseCode = "409", description = "Outro beneficiário já usa este CPF",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = BeneficiarioExemplos.CPF_DUPLICADO)))
    @ApiResponse(responseCode = "500", description = "Erro inesperado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = BeneficiarioExemplos.ERRO_INTERNO)))
    public ResponseEntity<BeneficiarioResponseDTO> atualizar(
            @Parameter(description = "Id do beneficiário", example = "1")
            @PathVariable("idBeneficiario") Long idBeneficiario,
            @Valid @RequestBody BeneficiarioRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(idBeneficiario, dto));
    }

    // PATCH: atualização parcial (campo ausente ou null não muda).
    // Sem cpf no DTO, o PATCH nunca responde 409.
    @PatchMapping("/{idBeneficiario}")
    @Operation(operationId = "atualizarParcialmenteBeneficiario", summary = "Atualiza parcialmente um beneficiário",
            description = "Só os campos enviados e não nulos mudam. O CPF não muda por aqui; para isso, use o PUT.")
    @ApiResponse(responseCode = "200", description = "Beneficiário atualizado")
    @ApiResponse(responseCode = "400", description = "Nome em branco, campo acima do tamanho, JSON malformado ou id inválido",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = BeneficiarioExemplos.DADOS_INVALIDOS_PATCH)))
    @ApiResponse(responseCode = "404", description = "Beneficiário não encontrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = BeneficiarioExemplos.NAO_ENCONTRADO)))
    @ApiResponse(responseCode = "500", description = "Erro inesperado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = BeneficiarioExemplos.ERRO_INTERNO)))
    public ResponseEntity<BeneficiarioResponseDTO> atualizarParcial(
            @Parameter(description = "Id do beneficiário", example = "1")
            @PathVariable("idBeneficiario") Long idBeneficiario,
            @Valid @RequestBody BeneficiarioPatchDTO dto) {
        return ResponseEntity.ok(service.atualizarParcial(idBeneficiario, dto));
    }

    // 204 No Content: sucesso sem corpo (content = @Content deixa a resposta sem schema).
    @DeleteMapping("/{idBeneficiario}")
    @Operation(operationId = "deletarBeneficiario", summary = "Remove um beneficiário")
    @ApiResponse(responseCode = "204", description = "Beneficiário removido, sem corpo", content = @Content)
    @ApiResponse(responseCode = "400", description = "Id inválido: não numérico ou acima do limite",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = BeneficiarioExemplos.ID_INVALIDO)))
    @ApiResponse(responseCode = "404", description = "Beneficiário não encontrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = BeneficiarioExemplos.NAO_ENCONTRADO)))
    @ApiResponse(responseCode = "500", description = "Erro inesperado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = BeneficiarioExemplos.ERRO_INTERNO)))
    public ResponseEntity<Void> deletar(
            @Parameter(description = "Id do beneficiário", example = "1")
            @PathVariable("idBeneficiario") Long idBeneficiario) {
        service.deletar(idBeneficiario);
        return ResponseEntity.noContent().build();
    }
}
