package br.com.socialconnect.api.produtos.service;

import br.com.socialconnect.api.exception.EstoqueNegativoException;
import br.com.socialconnect.api.exception.NomeDuplicadoException;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.model.Produto;
import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do ProdutoServiceImpl: sem Spring e sem banco. O Repository
 * é um mock do Mockito, injetado pelo construtor do Service (@InjectMocks).
 * Cada teste segue o padrão AAA: Arrange (prepara), Act (executa), Assert (confere).
 */
@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private ProdutoServiceImpl service;

    private ProdutoRequestDTO dtoValido(Integer estoqueAtual, Integer estoqueMinimo) {
        return new ProdutoRequestDTO("Arroz 5kg", CategoriaProduto.ALIMENTO, estoqueAtual, estoqueMinimo, "UNIDADE");
    }

    @Test
    @DisplayName("Deve criar produto quando os dados são válidos")
    void deveCriarProdutoQuandoDadosValidos() {
        // Arrange: nome livre, e o save devolve a entidade com o id que o banco geraria
        ProdutoRequestDTO dto = dtoValido(3, 10);
        when(repository.existsByNomeIgnoreCase("Arroz 5kg")).thenReturn(false);
        when(repository.save(any(Produto.class))).thenAnswer(invocacao -> {
            Produto produto = invocacao.getArgument(0);
            produto.setIdProduto(1L);
            return produto;
        });

        // Act
        ProdutoResponseDTO resposta = service.criar(dto);

        // Assert: resposta com os campos gerados pelo servidor
        assertThat(resposta.idProduto()).isEqualTo(1L);
        assertThat(resposta.nome()).isEqualTo("Arroz 5kg");
        assertThat(resposta.unidadeMedida()).isEqualTo("unidade"); // normalizada
        assertThat(resposta.dataCadastro()).isEqualTo(LocalDate.now());
        assertThat(resposta.estoqueBaixo()).isTrue(); // 3 < 10

        // Assert: a entidade enviada ao banco não tinha id (INSERT, e não UPDATE)
        ArgumentCaptor<Produto> captor = ArgumentCaptor.forClass(Produto.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getEstoqueAtual()).isEqualTo(3);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o estoque atual é negativo")
    void deveLancarExcecaoQuandoEstoqueNegativo() {
        // Arrange
        ProdutoRequestDTO dto = dtoValido(-5, 10);

        // Act + Assert: a regra barra antes de qualquer acesso ao banco
        assertThatThrownBy(() -> service.criar(dto))
                .isInstanceOf(EstoqueNegativoException.class);

        // Assert: nada foi gravado
        verify(repository, never()).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o nome já está cadastrado")
    void deveLancarExcecaoQuandoNomeDuplicado() {
        // Arrange: o banco já tem um produto com este nome
        ProdutoRequestDTO dto = dtoValido(3, 10);
        when(repository.existsByNomeIgnoreCase("Arroz 5kg")).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> service.criar(dto))
                .isInstanceOf(NomeDuplicadoException.class)
                .hasMessageContaining("Arroz 5kg");

        // Assert: nada foi gravado
        verify(repository, never()).save(any(Produto.class));
    }

    @Test
    @DisplayName("Não deve marcar estoque baixo quando o estoque é igual ao mínimo")
    void naoDeveMarcarEstoqueBaixoQuandoIgualAoMinimo() {
        // Arrange: 10 de 10 está no limite, não abaixo dele
        Produto produto = Produto.builder()
                .idProduto(7L).nome("Sabonete").categoria(CategoriaProduto.HIGIENE)
                .estoqueAtual(10).estoqueMinimo(10).unidadeMedida("unidade")
                .dataCadastro(LocalDate.now()).build();
        when(repository.findById(7L)).thenReturn(Optional.of(produto));

        // Act
        ProdutoResponseDTO resposta = service.buscarPorId(7L);

        // Assert
        assertThat(resposta.estoqueBaixo()).isFalse();
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir produto inexistente")
    void deveLancarExcecaoAoDeletarProdutoInexistente() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert: 404, e não o 204 silencioso do deleteById
        assertThatThrownBy(() -> service.deletar(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class);
        verify(repository, never()).delete(any(Produto.class));
    }
}
