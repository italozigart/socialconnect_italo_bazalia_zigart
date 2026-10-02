package br.com.socialconnect.api.produtos.controller;

import br.com.socialconnect.api.exception.ProblemDetail;
import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * Endpoints do módulo de produtos (A1).
 * <p>
 * - O id no caminho se chama id_produto, como no docs/openapi.yaml; por isso o
 *   nome explícito em cada @PathVariable("id_produto").
 * - @Valid dispara o Bean Validation do ProdutoRequestDTO; os erros viram 400
 *   no GlobalExceptionHandler.
 * - Cada @ApiResponse de erro aponta para o schema ProblemDetail.
 */
@RestController
@RequestMapping("/api/v1/produtos")
@Tag(name = "Produtos", description = "Gestão do estoque de produtos doados (alimentos, roupas, higiene)")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    // GET /api/v1/produtos?page=0&size=10&sort=nome,asc&categoria=ALIMENTO&nome=arroz
    // - @ParameterObject: o Swagger mostra page, size e sort como campos separados.
    //   Sem ele, o exemplo vem como JSON com "sort": ["string"], que dá erro.
    // - @PageableDefault: 10 por página, ordenado por nome (sem ele, o Boot usaria 20).
    //   O sort usa o nome do atributo Java (estoqueAtual), não o da coluna.
    @GetMapping
    @Operation(summary = "Lista produtos com paginação e filtros",
            description = "Filtros opcionais e combináveis: nome (parcial, sem diferenciar maiúsculas) "
                    + "e categoria. Ordenação por atributo do produto, ex.: sort=nome,asc ou sort=estoqueAtual,desc.")
    @ApiResponse(responseCode = "200", description = "Página de produtos (cada um com o campo estoqueBaixo)")
    @ApiResponse(responseCode = "400", description = "Parâmetro inválido (categoria fora da lista ou sort inexistente)",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "500", description = "Erro inesperado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Page<ProdutoResponseDTO>> listar(
            @Parameter(description = "Parte do nome do produto", example = "arroz")
            @RequestParam(name = "nome", required = false) String nome,
            @Parameter(description = "Categoria do produto", example = "ALIMENTO")
            @RequestParam(name = "categoria", required = false) CategoriaProduto categoria,
            @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listar(nome, categoria, pageable));
    }

    @GetMapping("/{id_produto}")
    @Operation(summary = "Busca um produto por id")
    @ApiResponse(responseCode = "200", description = "Produto encontrado")
    @ApiResponse(responseCode = "400", description = "Id não numérico",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Produto não encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "500", description = "Erro inesperado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(
            @Parameter(description = "Id do produto", example = "1")
            @PathVariable("id_produto") Long idProduto) {
        return ResponseEntity.ok(service.buscarPorId(idProduto));
    }

    // 201 Created + Location: o produto novo ganha endereço próprio.
    @PostMapping
    @Operation(summary = "Cadastra um produto",
            description = "O servidor gera idProduto e dataCadastro e calcula estoqueBaixo.")
    @ApiResponse(responseCode = "201", description = "Produto criado; o cabeçalho Location traz o endereço dele")
    @ApiResponse(responseCode = "400", description = "Dados inválidos (lista os campos em errors)",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Já existe um produto com este nome",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "422", description = "Estoque atual negativo",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "500", description = "Erro inesperado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<ProdutoResponseDTO> criar(@Valid @RequestBody ProdutoRequestDTO dto) {
        ProdutoResponseDTO criado = service.criar(dto);
        URI location = URI.create("/api/v1/produtos/" + criado.idProduto());
        return ResponseEntity.created(location).body(criado);
    }

    // PUT: substituição total; devolve 200 com o produto atualizado.
    @PutMapping("/{id_produto}")
    @Operation(summary = "Atualiza um produto (substituição total)")
    @ApiResponse(responseCode = "200", description = "Produto atualizado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou id não numérico",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Produto não encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Outro produto já usa este nome",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "422", description = "Estoque atual negativo",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "500", description = "Erro inesperado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @Parameter(description = "Id do produto", example = "1")
            @PathVariable("id_produto") Long idProduto,
            @Valid @RequestBody ProdutoRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(idProduto, dto));
    }

    // 204 No Content: sucesso sem corpo.
    @DeleteMapping("/{id_produto}")
    @Operation(summary = "Remove um produto")
    @ApiResponse(responseCode = "204", description = "Produto removido", content = @Content)
    @ApiResponse(responseCode = "400", description = "Id não numérico",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Produto não encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "500", description = "Erro inesperado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Void> deletar(
            @Parameter(description = "Id do produto", example = "1")
            @PathVariable("id_produto") Long idProduto) {
        service.deletar(idProduto);
        return ResponseEntity.noContent().build();
    }
}
