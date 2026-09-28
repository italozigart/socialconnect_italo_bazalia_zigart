package br.com.socialconnect.api.beneficiarios.service;

import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioDTO;
import br.com.socialconnect.api.beneficiarios.model.Beneficiario;
import br.com.socialconnect.api.beneficiarios.repository.BeneficiarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BeneficiarioService {

    private final BeneficiarioRepository repository;

    // Injeção pelo construtor, sem @Autowired: com um construtor só, o Spring
    // injeta sozinho. Como o Service recebe o Repository pronto, um teste pode
    // montá-lo com um Repository falso (mock), sem banco e sem Spring.
    public BeneficiarioService(BeneficiarioRepository repository) {
        this.repository = repository;
    }

    public List<BeneficiarioDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public BeneficiarioDTO buscarPorId(Long idBeneficiario) {
        // Por enquanto esta exceção vira 500; na Etapa 4 ela passa a responder 404.
        Beneficiario beneficiario = repository.findById(idBeneficiario)
                .orElseThrow(() -> new RuntimeException(
                        "Beneficiário não encontrado com o ID: " + idBeneficiario));
        return toDTO(beneficiario);
    }

    public BeneficiarioDTO salvar(BeneficiarioDTO dto) {
        Beneficiario salvo = repository.save(toEntity(dto));
        return toDTO(salvo);
    }

    public void deletar(Long idBeneficiario) {
        // deleteById ignora id inexistente: hoje isso responde 204 (tratar na Etapa 4).
        repository.deleteById(idBeneficiario);
    }

    private BeneficiarioDTO toDTO(Beneficiario entity) {
        return new BeneficiarioDTO(
                entity.getIdBeneficiario(),
                entity.getNome(),
                entity.getCpf(),
                entity.getTelefone(),
                entity.getEndereco(),
                entity.getSituacaoVulnerabilidade(),
                entity.getDataCadastro()
        );
    }

    private Beneficiario toEntity(BeneficiarioDTO dto) {
        // Copia também o idBeneficiario, como nas notas. Com id preenchido, o save
        // faz merge em vez de INSERT; a Etapa 3 corrige com um Request DTO sem id.
        return Beneficiario.builder()
                .idBeneficiario(dto.idBeneficiario())
                .nome(dto.nome())
                .cpf(dto.cpf())
                .telefone(dto.telefone())
                .endereco(dto.endereco())
                .situacaoVulnerabilidade(dto.situacaoVulnerabilidade())
                .dataCadastro(dto.dataCadastro())
                .build();
    }
}
