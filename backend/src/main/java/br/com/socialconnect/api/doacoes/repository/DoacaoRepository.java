package br.com.socialconnect.api.doacoes.repository;

import br.com.socialconnect.api.doacoes.model.Doacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Sem métodos próprios. O que o DoacaoService usa:
 * <ul>
 *   <li>save: grava a doação nova (POST) e a alterada (PUT e PATCH);</li>
 *   <li>findById: busca por id (GET, PUT, PATCH e DELETE em /{idDoacao});</li>
 *   <li>delete: apaga a doação já encontrada (DELETE). O deleteById não é
 *       usado: ele ignora um id inexistente, e o DELETE daria 204 em vez de 404;</li>
 *   <li>findAll(Specification, Pageable): listagem paginada com os filtros
 *       opcionais, montados em {@link DoacaoSpecifications}.</li>
 * </ul>
 * Por que JpaSpecificationExecutor, e não o método do roteiro do mob
 * (findByDataDoacaoBetweenAndTipo): o nome do método fixa a consulta, e ela
 * só funciona com os três filtros preenchidos. Com três filtros opcionais
 * são 8 combinações, e seriam 8 métodos. A Specification monta a consulta só
 * com os filtros que vieram.
 */
public interface DoacaoRepository extends JpaRepository<Doacao, Long>, JpaSpecificationExecutor<Doacao> {
}
