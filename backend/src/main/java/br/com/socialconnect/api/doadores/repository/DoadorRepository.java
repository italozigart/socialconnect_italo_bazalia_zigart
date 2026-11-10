package br.com.socialconnect.api.doadores.repository;

import br.com.socialconnect.api.doadores.model.Doador;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Sem métodos próprios: o JpaRepository já traz o que a Etapa 7 usa.
 * <ul>
 *   <li>save: grava o doador novo (POST);</li>
 *   <li>findById: busca por id (GET /{idDoador});</li>
 *   <li>findAll(Pageable): listagem paginada e ordenada (GET).</li>
 * </ul>
 * Na Etapa 8, o DoacaoService usa o findById daqui para saber se o doador de
 * uma doação existe.
 */
public interface DoadorRepository extends JpaRepository<Doador, Long> {
}
