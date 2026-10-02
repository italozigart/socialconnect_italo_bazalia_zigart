package br.com.socialconnect.api.beneficiarios.service;

import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioPatchDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioRequestDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioResponseDTO;
import br.com.socialconnect.api.beneficiarios.model.Beneficiario;
import br.com.socialconnect.api.beneficiarios.repository.BeneficiarioRepository;
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
    // na mesma transação. Isso não impede dois POSTs simultâneos com o mesmo CPF;
    // quem garante a unicidade é o UNIQUE da V1. O existsByCpf existe para a API
    // devolver um erro próprio (409 na Etapa 4), e não o erro do banco.
    @Transactional
    public BeneficiarioResponseDTO criar(BeneficiarioRequestDTO dto) {
        // Por enquanto vira 500; na Etapa 4, CpfDuplicadoException e 409.
        if (repository.existsByCpf(dto.cpf())) {
            throw new RuntimeException("CPF já cadastrado: " + dto.cpf());
        }

        // Sem idBeneficiario: com id nulo, o save faz persist (INSERT) e o banco
        // gera o id. A data de cadastro é do servidor, não do cliente.
        Beneficiario novo = Beneficiario.builder()
                .nome(dto.nome())
                .cpf(dto.cpf())
                .telefone(dto.telefone())
                .endereco(dto.endereco())
                .situacaoVulnerabilidade(dto.situacaoVulnerabilidade())
                .dataCadastro(LocalDate.now())
                .build();

        return toResponseDTO(repository.save(novo));
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
            throw new RuntimeException("CPF já cadastrado: " + dto.cpf());
        }

        beneficiario.setNome(dto.nome());
        beneficiario.setCpf(dto.cpf());
        beneficiario.setTelefone(dto.telefone());
        beneficiario.setEndereco(dto.endereco());
        beneficiario.setSituacaoVulnerabilidade(dto.situacaoVulnerabilidade());

        // Na transação, a entidade continua gerenciada e o Hibernate detecta a
        // mudança sozinho no commit (dirty checking), então o save é opcional.
        // Fica pela clareza e para o teste com mock da Etapa 6 poder verificá-lo.
        return toResponseDTO(repository.save(beneficiario));
    }

    // PATCH: atualização parcial. Só os campos não nulos são aplicados; o CPF
    // não está no BeneficiarioPatchDTO e só muda pelo PUT.
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

    public void deletar(Long idBeneficiario) {
        // deleteById ignora id inexistente: hoje isso responde 204 (tratar na Etapa 4).
        repository.deleteById(idBeneficiario);
    }

    // Usado por buscarPorId, atualizar e atualizarParcial.
    // Por enquanto esta exceção vira 500; na Etapa 4 ela passa a responder 404.
    private Beneficiario buscarEntidadePorId(Long idBeneficiario) {
        return repository.findById(idBeneficiario)
                .orElseThrow(() -> new RuntimeException(
                        "Beneficiário não encontrado com o ID: " + idBeneficiario));
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
