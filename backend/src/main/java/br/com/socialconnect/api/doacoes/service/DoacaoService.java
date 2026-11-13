package br.com.socialconnect.api.doacoes.service;

import br.com.socialconnect.api.doacoes.dto.DoacaoPatchDTO;
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
 * Regras da doação. Mesmo molde do DoadorService e do BeneficiarioService:
 * injeção pelo construtor, Entity só aqui dentro e DTO na saída.
 * <p>
 * Recebe dois repositories: o de doações e o de doadores, que confere se o
 * doador informado existe antes de gravar (422, decisão no ADR 0001). A
 * conferência vale no POST, no PUT e no PATCH que traz o idDoador.
 * <p>
 * Leituras com @Transactional(readOnly = true): a doação traz o doador como
 * proxy LAZY. Ler só o id dele não vai ao banco (o Hibernate já leu a coluna
 * id_doador), mas qualquer outro dado do doador precisaria da sessão do
 * Hibernate aberta, e com o open-in-view desligado ela fecha junto com a
 * transação. Assim o toResponseDTO sempre roda com a sessão aberta, e o
 * readOnly avisa o Hibernate de que nada será gravado.
 * <p>
 * Escritas (criar, atualizar, atualizarParcial e deletar) com @Transactional:
 * as buscas e a gravação correm na mesma transação. Se uma exceção sobe no
 * meio do método, a transação é desfeita (rollback) e nada chega ao banco.
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

    @Transactional(readOnly = true)
    public DoacaoResponseDTO buscarPorId(Long idDoacao) {
        return toResponseDTO(buscarEntidadePorId(idDoacao));
    }

    // A mesma transação busca o doador e grava a doação. Sem idDoacao no
    // builder: com id nulo, a gravação faz INSERT e o banco gera o id.
    @Transactional
    public DoacaoResponseDTO criar(DoacaoRequestDTO dto) {
        Doador doador = buscarDoador(dto.idDoador());

        Doacao nova = Doacao.builder()
                .doador(doador)
                .dataDoacao(dto.dataDoacao())
                .valor(dto.valor())
                .tipo(dto.tipo())
                .descricao(textoOuNulo(dto.descricao()))
                .build();

        return toResponseDTO(repository.save(nova));
    }

    // PUT: substituição total, com as regras do POST (mesmo DoacaoRequestDTO).
    // Os cinco campos do cliente são copiados sempre, inclusive os nulos: valor
    // e descricao ausentes no JSON apagam o que havia. O idDoacao é o da URL.
    // Ordem: primeiro a doação da URL (404), depois o doador do corpo (422), e
    // só então os setters. Se a doação não existe, o doador nem é consultado;
    // se o doador não existe, a entidade não chega a ser alterada.
    // Sem saveAndFlush, diferente do beneficiário: a doação não tem UNIQUE, e o
    // @Valid e a busca do doador já cobrem o CHECK, o NOT NULL e a FK da V4. Na
    // transação, a entidade continua gerenciada, e o Hibernate envia o UPDATE
    // no commit, e só se algum campo mudou (dirty checking).
    @Transactional
    public DoacaoResponseDTO atualizar(Long idDoacao, DoacaoRequestDTO dto) {
        Doacao doacao = buscarEntidadePorId(idDoacao);
        Doador doador = buscarDoador(dto.idDoador());

        doacao.setDoador(doador);
        doacao.setDataDoacao(dto.dataDoacao());
        doacao.setValor(dto.valor());
        doacao.setTipo(dto.tipo());
        doacao.setDescricao(textoOuNulo(dto.descricao()));

        return toResponseDTO(repository.save(doacao));
    }

    // PATCH: atualização parcial. Só os campos não nulos são aplicados, como no
    // beneficiário. Diferenças:
    // - o idDoador pode vir: o doador é conferido (422) e trocado;
    // - a descrição em branco vira null, como no POST e no PUT. É o jeito de
    //   apagar a descrição pelo PATCH; o valor só se apaga pelo PUT.
    // O doador é o primeiro campo aplicado, pelo mesmo motivo do PUT: se ele
    // não existir (422), nenhum setter rodou e a doação fica como estava.
    @Transactional
    public DoacaoResponseDTO atualizarParcial(Long idDoacao, DoacaoPatchDTO dto) {
        Doacao doacao = buscarEntidadePorId(idDoacao);

        if (dto.idDoador() != null) {
            doacao.setDoador(buscarDoador(dto.idDoador()));
        }
        if (dto.dataDoacao() != null) {
            doacao.setDataDoacao(dto.dataDoacao());
        }
        if (dto.valor() != null) {
            doacao.setValor(dto.valor());
        }
        if (dto.tipo() != null) {
            doacao.setTipo(dto.tipo());
        }
        if (dto.descricao() != null) {
            doacao.setDescricao(textoOuNulo(dto.descricao()));
        }

        return toResponseDTO(repository.save(doacao));
    }

    // Busca antes de apagar, como no beneficiário: o deleteById ignora id
    // inexistente, e o DELETE responderia 204 para uma doação que nunca
    // existiu. Assim dá 404, e o segundo DELETE do mesmo id também.
    // Apagar a doação não mexe no doador: a FK fica em doacoes, e o
    // @ManyToOne não tem cascade.
    @Transactional
    public void deletar(Long idDoacao) {
        Doacao doacao = buscarEntidadePorId(idDoacao);
        repository.delete(doacao);
    }

    // Usado por buscarPorId, atualizar, atualizarParcial e deletar.
    // O GlobalExceptionHandler transforma a RecursoNaoEncontradoException em 404.
    private Doacao buscarEntidadePorId(Long idDoacao) {
        return repository.findById(idDoacao)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Doação", idDoacao));
    }

    // Usado por criar, atualizar e atualizarParcial.
    // Doador inexistente: 422, e não 404 (ADR 0001). O 404 fica para o id da
    // URL; aqui, a URL existe, e o problema está no corpo. A consulta antes da
    // gravação dá ao cliente uma resposta clara; a FK da V4 continua como
    // segunda barreira.
    private Doador buscarDoador(Long idDoador) {
        return doadorRepository.findById(idDoador)
                .orElseThrow(() -> new DoadorInexistenteException(idDoador));
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
