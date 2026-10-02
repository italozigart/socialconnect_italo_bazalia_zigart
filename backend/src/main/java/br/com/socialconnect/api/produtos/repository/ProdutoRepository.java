package br.com.socialconnect.api.produtos.repository;

import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JpaRepository&lt;entidade, tipo do @Id&gt;: save, findById, findAll e delete
 * já vêm prontos. Os demais são consultas derivadas: o Spring Data monta o SQL
 * a partir do nome do método, usando os nomes dos atributos da Entity.
 */
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Nome único sem diferenciar maiúsculas: "Arroz 5kg" e "arroz 5kg" contam
    // como o mesmo produto. O UNIQUE da V2 diferencia; esta checagem é mais rígida.
    boolean existsByNomeIgnoreCase(String nome);

    // PUT: existe OUTRO produto com este nome? O "Not" vira "<>" no SQL e exclui
    // o próprio registro; sem ele, todo PUT que mantém o nome seria recusado.
    boolean existsByNomeIgnoreCaseAndIdProdutoNot(String nome, Long idProduto);

    // Filtros da listagem. Containing = LIKE %nome%; IgnoreCase = sem diferenciar maiúsculas.
    Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    Page<Produto> findByCategoria(CategoriaProduto categoria, Pageable pageable);

    Page<Produto> findByNomeContainingIgnoreCaseAndCategoria(String nome, CategoriaProduto categoria,
                                                             Pageable pageable);
}
