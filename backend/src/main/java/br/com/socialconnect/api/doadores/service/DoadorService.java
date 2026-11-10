package br.com.socialconnect.api.doadores.service;

import br.com.socialconnect.api.doadores.dto.DoadorRequestDTO;
import br.com.socialconnect.api.doadores.dto.DoadorResponseDTO;
import br.com.socialconnect.api.doadores.model.Doador;
import br.com.socialconnect.api.doadores.repository.DoadorRepository;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regras do doador. Mesmo molde do BeneficiarioService: injeção pelo
 * construtor, Entity só aqui dentro e DTO na saída.
 * <p>
 * Diferente do beneficiário, o doador não tem campo único (não há CPF nem CNPJ
 * no contrato da disciplina), então o POST não tem 409 nem saveAndFlush: as
 * restrições da V3 (NOT NULL, tamanhos e o CHECK do tipo) já foram garantidas
 * pelo @Valid e pelo enum antes de chegar aqui.
 */
@Service
public class DoadorService {

    private final DoadorRepository repository;

    public DoadorService(DoadorRepository repository) {
        this.repository = repository;
    }

    // Sem filtros, como no contrato: só paginação e ordenação.
    public Page<DoadorResponseDTO> listar(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponseDTO);
    }

    // O GlobalExceptionHandler transforma a RecursoNaoEncontradoException em 404.
    public DoadorResponseDTO buscarPorId(Long idDoador) {
        Doador doador = repository.findById(idDoador)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Doador", idDoador));
        return toResponseDTO(doador);
    }

    // Sem idDoador: com id nulo, a gravação faz INSERT e o banco gera o id.
    // Com GenerationType.IDENTITY, o Hibernate envia o INSERT já no save,
    // porque precisa do id gerado; por isso a resposta sai com o id preenchido.
    @Transactional
    public DoadorResponseDTO criar(DoadorRequestDTO dto) {
        Doador novo = Doador.builder()
                .nome(dto.nome())
                .tipo(dto.tipo())
                .email(textoOuNulo(dto.email()))
                .telefone(textoOuNulo(dto.telefone()))
                .build();

        return toResponseDTO(repository.save(novo));
    }

    // E-mail e telefone são opcionais. Texto em branco ("" ou "   ") vira null
    // para o banco não guardar dois jeitos de dizer "não informado".
    private static String textoOuNulo(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor;
    }

    private DoadorResponseDTO toResponseDTO(Doador entity) {
        return new DoadorResponseDTO(
                entity.getIdDoador(),
                entity.getNome(),
                entity.getTipo(),
                entity.getEmail(),
                entity.getTelefone()
        );
    }
}
