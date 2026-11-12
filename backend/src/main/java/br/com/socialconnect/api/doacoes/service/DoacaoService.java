package br.com.socialconnect.api.doacoes.service;

import br.com.socialconnect.api.doacoes.dto.DoacaoRequestDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoResponseDTO;
import br.com.socialconnect.api.doacoes.model.Doacao;
import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import br.com.socialconnect.api.doacoes.repository.DoacaoRepository;
import br.com.socialconnect.api.doacoes.repository.DoacaoSpecifications;
import br.com.socialconnect.api.doadores.model.Doador;
import br.com.socialconnect.api.doadores.repository.DoadorRepository;
import br.com.socialconnect.api.exception.DoadorInexistenteException;
import br.com.socialconnect.api.exception.PeriodoInvalidoException;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Regras da doação. Mesmo molde do DoadorService: injeção pelo construtor,
 * Entity só aqui dentro e DTO na saída.
 * <p>
 * Recebe dois repositories: o de doações e o de doadores, que confere se o
 * doador informado existe antes de gravar (422, decisão no ADR 0001).
 * <p>
 * Leituras com @Transactional(readOnly = true): a doação traz o doador como
 * proxy LAZY. Ler só o id dele não vai ao banco (o Hibernate já leu a coluna
 * id_doador), mas qualquer outro dado do doador precisaria da sessão do
 * Hibernate aberta, e com o open-in-view desligado ela fecha junto com a
 * transação. Assim o toResponseDTO sempre roda com a sessão aberta, e o
 * readOnly avisa o Hibernate de que nada será gravado.
 */
@Service
public class DoacaoService {

    private final DoacaoRepository repository;
    private final DoadorRepository doadorRepository;

    public DoacaoService(DoacaoRepository repository, DoadorRepository doadorRepository) {
        this.repository = repository;
        this.doadorRepository = doadorRepository;
    }

    // Filtros opcionais e combináveis (DoacaoSpecifications). O único caso
    // recusado é o período invertido: dataInicio depois de dataFim nunca traz
    // nada, e uma página vazia esconderia o erro de quem montou a busca.
    @Transactional(readOnly = true)
    public Page<DoacaoResponseDTO> listar(LocalDate dataInicio, LocalDate dataFim, TipoDoacao tipo,
                                          Pageable pageable) {
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new PeriodoInvalidoException(dataInicio, dataFim);
        }
        return repository.findAll(DoacaoSpecifications.comFiltros(dataInicio, dataFim, tipo), pageable)
                .map(this::toResponseDTO);
    }

    // O GlobalExceptionHandler transforma a RecursoNaoEncontradoException em 404.
    @Transactional(readOnly = true)
    public DoacaoResponseDTO buscarPorId(Long idDoacao) {
        Doacao doacao = repository.findById(idDoacao)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Doação", idDoacao));
        return toResponseDTO(doacao);
    }

    // Doador inexistente: 422, e não 404 (ADR 0001). O 404 fica para o id da
    // URL que não existe; aqui a URL existe, e o problema está no corpo.
    // A consulta antes do INSERT dá ao cliente uma resposta clara; a FK da V4
    // continua como segunda barreira.
    // A mesma transação busca o doador e grava a doação. Sem idDoacao no
    // builder: com id nulo, a gravação faz INSERT e o banco gera o id.
    @Transactional
    public DoacaoResponseDTO criar(DoacaoRequestDTO dto) {
        Doador doador = doadorRepository.findById(dto.idDoador())
                .orElseThrow(() -> new DoadorInexistenteException(dto.idDoador()));

        Doacao nova = Doacao.builder()
                .doador(doador)
                .dataDoacao(dto.dataDoacao())
                .valor(dto.valor())
                .tipo(dto.tipo())
                .descricao(textoOuNulo(dto.descricao()))
                .build();

        return toResponseDTO(repository.save(nova));
    }

    // Descrição em branco ("" ou "   ") vira null, como o e-mail e o telefone
    // do doador: o banco não guarda dois jeitos de dizer "não informado".
    private static String textoOuNulo(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor;
    }

    private DoacaoResponseDTO toResponseDTO(Doacao entity) {
        return new DoacaoResponseDTO(
                entity.getIdDoacao(),
                entity.getDoador().getIdDoador(),
                entity.getDataDoacao(),
                entity.getValor(),
                entity.getTipo(),
                entity.getDescricao()
        );
    }
}
