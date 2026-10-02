package br.com.socialconnect.api.produtos.service;

import br.com.socialconnect.api.exception.EstoqueNegativoException;
import br.com.socialconnect.api.exception.NomeDuplicadoException;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.model.Produto;
import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import br.com.socialconnect.api.validation.UnidadeMedidaValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Regras de negócio de produtos:
 * <ol>
 *   <li>Estoque não negativo: estoqueAtual &lt; 0 lança EstoqueNegativoException (422).</li>
 *   <li>Nome único (sem diferenciar maiúsculas): lança NomeDuplicadoException (409).</li>
 *   <li>Estoque baixo: estoqueBaixo = estoqueAtual &lt; estoqueMinimo, calculado na resposta.</li>
 * </ol>
 */
@Service
public class ProdutoServiceImpl implements ProdutoService {

    private static final String RECURSO = "Produto";

    private final ProdutoRepository repository;

    // Injeção pelo construtor, com campo final e sem @Autowired: com um
    // construtor só, o Spring injeta sozinho. No teste unitário, o Mockito
    // passa um Repository falso por este mesmo construtor.
    public ProdutoServiceImpl(ProdutoRepository repository) {
        this.repository = repository;
    }

    // Filtros opcionais e combináveis: nome (parcial) e categoria (exata).
    // Filtro de nome em branco conta como ausente.
    @Override
    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> listar(String nome, CategoriaProduto categoria, Pageable pageable) {
        boolean temNome = nome != null && !nome.isBlank();
        Page<Produto> pagina;

        if (temNome && categoria != null) {
            pagina = repository.findByNomeContainingIgnoreCaseAndCategoria(nome.trim(), categoria, pageable);
        } else if (temNome) {
            pagina = repository.findByNomeContainingIgnoreCase(nome.trim(), pageable);
        } else if (categoria != null) {
            pagina = repository.findByCategoria(categoria, pageable);
        } else {
            pagina = repository.findAll(pageable);
        }

        return pagina.map(this::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarPorId(Long id) {
        return toResponseDTO(buscarEntidadePorId(id));
    }

    @Override
    @Transactional
    public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {
        // Regras antes de qualquer gravação. A do estoque vem primeiro porque
        // não precisa do banco.
        validarEstoqueNaoNegativo(dto.estoqueAtual());

        String nome = dto.nome().trim();
        if (repository.existsByNomeIgnoreCase(nome)) {
            throw new NomeDuplicadoException(nome);
        }

        // Sem idProduto: com id nulo, o save faz INSERT e o banco gera o id.
        // A data de cadastro é do servidor, não do cliente.
        Produto novo = Produto.builder()
                .nome(nome)
                .categoria(dto.categoria())
                .estoqueAtual(dto.estoqueAtual())
                .estoqueMinimo(dto.estoqueMinimo())
                .unidadeMedida(UnidadeMedidaValidator.normalizar(dto.unidadeMedida()))
                .dataCadastro(LocalDate.now())
                .build();

        return toResponseDTO(repository.save(novo));
    }

    // PUT: substituição total dos campos editáveis. idProduto e dataCadastro
    // são do servidor e não mudam.
    @Override
    @Transactional
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        Produto produto = buscarEntidadePorId(id); // 404 primeiro

        validarEstoqueNaoNegativo(dto.estoqueAtual());

        // Verifica ANTES de alterar a entidade: dentro da transação, o Hibernate
        // envia as mudanças pendentes ao banco antes de uma consulta, e o erro
        // viria do UNIQUE (500) em vez desta verificação (409).
        String nome = dto.nome().trim();
        if (repository.existsByNomeIgnoreCaseAndIdProdutoNot(nome, id)) {
            throw new NomeDuplicadoException(nome);
        }

        produto.setNome(nome);
        produto.setCategoria(dto.categoria());
        produto.setEstoqueAtual(dto.estoqueAtual());
        produto.setEstoqueMinimo(dto.estoqueMinimo());
        produto.setUnidadeMedida(UnidadeMedidaValidator.normalizar(dto.unidadeMedida()));

        return toResponseDTO(repository.save(produto));
    }

    // O deleteById do Spring Data ignora id inexistente em silêncio (daria 204).
    // Buscando primeiro, o id inexistente vira 404, como pede a A1.
    @Override
    @Transactional
    public void deletar(Long id) {
        repository.delete(buscarEntidadePorId(id));
    }

    // Usado por buscarPorId, atualizar e deletar.
    private Produto buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(RECURSO, id));
    }

    private void validarEstoqueNaoNegativo(Integer estoqueAtual) {
        // Nulo é barrado antes, pelo @NotNull do DTO; aqui só o valor negativo.
        if (estoqueAtual != null && estoqueAtual < 0) {
            throw new EstoqueNegativoException(estoqueAtual);
        }
    }

    private ProdutoResponseDTO toResponseDTO(Produto produto) {
        // Estritamente menor: 3 de 10 é baixo; 10 de 10 não é.
        boolean estoqueBaixo = produto.getEstoqueAtual() < produto.getEstoqueMinimo();

        return new ProdutoResponseDTO(
                produto.getIdProduto(),
                produto.getNome(),
                produto.getCategoria(),
                produto.getEstoqueAtual(),
                produto.getEstoqueMinimo(),
                produto.getUnidadeMedida(),
                produto.getDataCadastro(),
                estoqueBaixo
        );
    }
}
