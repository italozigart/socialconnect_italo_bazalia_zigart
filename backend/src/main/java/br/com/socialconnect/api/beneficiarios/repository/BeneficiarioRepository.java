package br.com.socialconnect.api.beneficiarios.repository;

import br.com.socialconnect.api.beneficiarios.model.Beneficiario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JpaRepository&lt;entidade, tipo do @Id&gt;: save, findById, findAll e
 * deleteById já vêm prontos, e o Spring Data cria a implementação na subida.
 * <p>
 * Os demais métodos são consultas derivadas: o Spring Data monta o SQL a partir
 * do nome do método, usando os nomes dos atributos da Entity (não os das colunas).
 */
public interface BeneficiarioRepository extends JpaRepository<Beneficiario, Long> {

    // POST: já existe algum beneficiário com este CPF?
    boolean existsByCpf(String cpf);

    // PUT: existe OUTRO beneficiário com este CPF? O "Not" vira "<>" no SQL e
    // exclui o próprio registro. Com o existsByCpf puro, todo PUT que mantém o
    // CPF encontraria o próprio beneficiário e seria recusado.
    boolean existsByCpfAndIdBeneficiarioNot(String cpf, Long idBeneficiario);

    // Listagem: receber Pageable e devolver Page faz o Spring Data paginar o SQL
    // e, quando precisa, disparar a consulta de contagem que preenche o totalElements.
    Page<Beneficiario> findByCpf(String cpf, Pageable pageable);

    // Containing = LIKE %nome%; IgnoreCase = sem diferenciar maiúsculas.
    Page<Beneficiario> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
