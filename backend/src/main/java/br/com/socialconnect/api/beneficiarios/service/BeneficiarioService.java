package br.com.socialconnect.api.beneficiarios.service;

import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioPatchDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioRequestDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioResponseDTO;
import br.com.socialconnect.api.beneficiarios.model.Beneficiario;
import br.com.socialconnect.api.beneficiarios.repository.BeneficiarioRepository;
import br.com.socialconnect.api.exception.CpfDuplicadoException;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class BeneficiarioService {

    private final BeneficiarioRepository repository;

    // Injeção pelo construtor, sem @Autowired: com um construtor só, o Spring
    // injeta sozinho. Como o Service recebe o Repository pronto, um teste pode
    // montá-lo com um Repository falso (mock), sem banco e sem Spring.
    public BeneficiarioService(BeneficiarioRepository repository) {
        this.repository = repository;
    }

    // Filtros opcionais, como na nota da Aula 05: o CPF (busca exata) tem
    // prioridade e, se os dois vierem juntos, o nome é ignorado. Como o CPF é
    // único, o resultado quase não muda. Filtro em branco conta como ausente.
    public Page<BeneficiarioResponseDTO> listar(String nome, String cpf, Pageable pageable) {
        Page<Beneficiario> pagina;

        if (cpf != null && !cpf.isBlank()) {
            pagina = repository.findByCpf(cpf, pageable);
        } else if (nome != null && !nome.isBlank()) {
            pagina = repository.findByNomeContainingIgnoreCase(nome, pageable);
        } else {
            pagina = repository.findAll(pageable);
        }

        return pagina.map(this::toResponseDTO);
    }

    public BeneficiarioResponseDTO buscarPorId(Long idBeneficiario) {
        return toResponseDTO(buscarEntidadePorId(idBeneficiario));
    }

    // @Transactional nos três métodos de escrita: a consulta e a gravação correm
    // na mesma transação. O existsByCpf resolve o caso comum (409 sem tentar o
    // INSERT), mas não impede dois POSTs simultâneos com o mesmo CPF: os dois
    // podem passar pela consulta antes de um deles gravar. Nesse caso, o UNIQUE
    // da V1 barra o segundo, e o método salvar transforma o erro do banco em 409.
    @Transactional
    public BeneficiarioResponseDTO criar(BeneficiarioRequestDTO dto) {
        // O GlobalExceptionHandler transforma a CpfDuplicadoException em 409.
        if (repository.existsByCpf(dto.cpf())) {
            throw new CpfDuplicadoException(dto.cpf());
        }

        // Sem idBeneficiario: com id nulo, a gravação faz persist (INSERT) e o
        // banco gera o id. A data de cadastro é do servidor, não do cliente.
        Beneficiario novo = Beneficiario.builder()
                .nome(dto.nome())
                .cpf(dto.cpf())
                .telefone(dto.telefone())
                .endereco(dto.endereco())
                .situacaoVulnerabilidade(dto.situacaoVulnerabilidade())
                .dataCadastro(LocalDate.now())
                .build();

        return toResponseDTO(salvar(novo));
    }

    // PUT: substituição total. Os cinco campos do cliente são copiados sempre,
    // inclusive os nulos (telefone ausente no JSON apaga o telefone).
    // idBeneficiario e dataCadastro são do servidor e não mudam.
    @Transactional
    public BeneficiarioResponseDTO atualizar(Long idBeneficiario, BeneficiarioRequestDTO dto) {
        Beneficiario beneficiario = buscarEntidadePorId(idBeneficiario);

        // Verifica ANTES de alterar a entidade. Dentro da transação, o Hibernate
        // envia as mudanças pendentes ao banco antes de qualquer consulta; se o
        // setCpf viesse antes, o UPDATE com o CPF repetido sairia primeiro e o
        // erro viria do UNIQUE, não desta verificação.
        if (repository.existsByCpfAndIdBeneficiarioNot(dto.cpf(), idBeneficiario)) {
            throw new CpfDuplicadoException(dto.cpf());
        }

        beneficiario.setNome(dto.nome());
        beneficiario.setCpf(dto.cpf());
        beneficiario.setTelefone(dto.telefone());
        beneficiario.setEndereco(dto.endereco());
        beneficiario.setSituacaoVulnerabilidade(dto.situacaoVulnerabilidade());

        // Sem o flush, o Hibernate só enviaria o UPDATE no commit, depois do
        // return, fora do try do salvar. O saveAndFlush envia o UPDATE aqui, e
        // um CPF repetido por corrida entre dois PUTs também vira 409.
        return toResponseDTO(salvar(beneficiario));
    }

    // PATCH: atualização parcial. Só os campos não nulos são aplicados; o CPF
    // não está no BeneficiarioPatchDTO e só muda pelo PUT. Sem CPF, não há
    // UNIQUE a violar, então o save comum basta: na transação, a entidade
    // continua gerenciada e o Hibernate envia o UPDATE no commit (dirty checking).
    @Transactional
    public BeneficiarioResponseDTO atualizarParcial(Long idBeneficiario, BeneficiarioPatchDTO dto) {
        Beneficiario beneficiario = buscarEntidadePorId(idBeneficiario);

        if (dto.nome() != null) {
            beneficiario.setNome(dto.nome());
        }
        if (dto.telefone() != null) {
            beneficiario.setTelefone(dto.telefone());
        }
        if (dto.endereco() != null) {
            beneficiario.setEndereco(dto.endereco());
        }
        if (dto.situacaoVulnerabilidade() != null) {
            beneficiario.setSituacaoVulnerabilidade(dto.situacaoVulnerabilidade());
        }

        return toResponseDTO(repository.save(beneficiario));
    }

    // Busca antes de apagar: o deleteById ignora id inexistente, e o DELETE
    // responderia 204 para um registro que nunca existiu. Assim dá 404.
    // @Transactional: a busca e o DELETE correm na mesma transação.
    @Transactional
    public void deletar(Long idBeneficiario) {
        Beneficiario beneficiario = buscarEntidadePorId(idBeneficiario);
        repository.delete(beneficiario);
    }

    // Usado por buscarPorId, atualizar, atualizarParcial e deletar.
    // O GlobalExceptionHandler transforma a RecursoNaoEncontradoException em 404.
    private Beneficiario buscarEntidadePorId(Long idBeneficiario) {
        return repository.findById(idBeneficiario)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Beneficiário", idBeneficiario));
    }

    // Usado por criar e atualizar, os dois métodos que gravam CPF.
    // O saveAndFlush envia o SQL ao banco aqui dentro do try. Se outra requisição
    // gravou o mesmo CPF depois da consulta do existsByCpf, o UNIQUE da V1 recusa
    // e o Spring lança DataIntegrityViolationException. O @Valid já conferiu os
    // campos obrigatórios e os tamanhos, então a única restrição que sobra para
    // violar é o UNIQUE do CPF: por isso o erro vira 409, e não o 500 genérico.
    // A transação é desfeita do mesmo jeito (rollback), porque a exceção sobe.
    private Beneficiario salvar(Beneficiario beneficiario) {
        try {
            return repository.saveAndFlush(beneficiario);
        } catch (DataIntegrityViolationException e) {
            throw new CpfDuplicadoException(beneficiario.getCpf());
        }
    }

    private BeneficiarioResponseDTO toResponseDTO(Beneficiario entity) {
        return new BeneficiarioResponseDTO(
                entity.getIdBeneficiario(),
                entity.getNome(),
                entity.getCpf(),
                entity.getTelefone(),
                entity.getEndereco(),
                entity.getSituacaoVulnerabilidade(),
                entity.getDataCadastro()
        );
    }
}
