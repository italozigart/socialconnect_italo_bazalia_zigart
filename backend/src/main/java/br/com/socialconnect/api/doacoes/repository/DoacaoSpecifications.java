package br.com.socialconnect.api.doacoes.repository;

import br.com.socialconnect.api.doacoes.model.Doacao;
import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Filtros da listagem de doações (GET /api/v1/doacoes), como Specification
 * do Spring Data JPA: uma condição do WHERE para cada filtro que veio.
 * <p>
 * Os três são opcionais e combinam de qualquer jeito: só tipo, só o período,
 * só uma das datas, os três juntos ou nenhum. No repositório do professor, o
 * filtro só valia com os três preenchidos.
 * <p>
 * Os nomes ("dataDoacao", "tipo") são os atributos da Entity Doacao, e não as
 * colunas do banco, como no sort do Pageable.
 */
public final class DoacaoSpecifications {

    private DoacaoSpecifications() {
    }

    /**
     * Filtro em null fica de fora. O período inclui as duas pontas: com
     * dataInicio = dataFim, vêm as doações daquele dia.
     */
    public static Specification<Doacao> comFiltros(LocalDate dataInicio, LocalDate dataFim, TipoDoacao tipo) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> condicoes = new ArrayList<>();

            if (dataInicio != null) {
                condicoes.add(criteriaBuilder.greaterThanOrEqualTo(root.<LocalDate>get("dataDoacao"), dataInicio));
            }
            if (dataFim != null) {
                condicoes.add(criteriaBuilder.lessThanOrEqualTo(root.<LocalDate>get("dataDoacao"), dataFim));
            }
            if (tipo != null) {
                condicoes.add(criteriaBuilder.equal(root.get("tipo"), tipo));
            }

            // Sem filtro nenhum, devolve null: o Spring Data monta a consulta
            // sem WHERE. Com filtros, junta todos com AND.
            return condicoes.isEmpty() ? null : criteriaBuilder.and(condicoes.toArray(new Predicate[0]));
        };
    }
}
