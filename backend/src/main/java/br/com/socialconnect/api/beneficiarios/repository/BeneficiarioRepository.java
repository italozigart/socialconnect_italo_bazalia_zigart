package br.com.socialconnect.api.beneficiarios.repository;

import br.com.socialconnect.api.beneficiarios.model.Beneficiario;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JpaRepository&lt;entidade, tipo do @Id&gt;: save, findById, findAll e
 * deleteById já vêm prontos, e o Spring Data cria a implementação na subida.
 */
public interface BeneficiarioRepository extends JpaRepository<Beneficiario, Long> {
}
