package br.com.socialconnect.api.doacoes.service;

import br.com.socialconnect.api.doacoes.dto.DoacaoRequestDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoResponseDTO;
import br.com.socialconnect.api.doacoes.model.Doacao;
import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import br.com.socialconnect.api.doacoes.repository.DoacaoRepository;
import br.com.socialconnect.api.doadores.model.Doador;
import br.com.socialconnect.api.doadores.model.TipoDoador;
import br.com.socialconnect.api.doadores.repository.DoadorRepository;
import br.com.socialconnect.api.exception.DoadorInexistenteException;
import br.com.socialconnect.api.exception.PeriodoInvalidoException;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do DoacaoService, no molde do DoadorServiceTest: sem
 * Spring e sem banco, com os dois repositories mockados pelo Mockito e
 * injetados pelo construtor do Service (@InjectMocks). Padrão AAA em todos
 * os testes.
 * <p>
 * O MockitoExtension está no modo estrito: cada teste prepara só as chamadas
 * que aquele caminho do Service faz.
 */
@ExtendWith(MockitoExtension.class)
class DoacaoServiceTest {

    private static final LocalDate DATA = LocalDate.of(2026, 11, 10);

    @Mock
    private DoacaoRepository repository;

    @Mock
    private DoadorRepository doadorRepository;

    @InjectMocks
    private DoacaoService service;

    private Doador doador(Long idDoador) {
        return Doador.builder()
                .idDoador(idDoador)
                .nome("Mercado Solidário Ltda")
                .tipo(TipoDoador.PESSOA_JURIDICA)
                .build();
    }

    // Doação como viria do banco: com id. Um objeto novo por chamada.
    private Doacao doacaoSalva(Long idDoacao, String descricao) {
        return Doacao.builder()
                .idDoacao(idDoacao)
                .doador(doador(1L))
                .dataDoacao(DATA)
                .valor(new BigDecimal("150.00"))
                .tipo(TipoDoacao.ALIMENTO)
                .descricao(descricao)
                .build();
    }

    // ===================== POST (criar) =====================

    @Test
    @DisplayName("Deve criar doação quando os dados são válidos")
    void deveCriarDoacaoQuandoDadosValidos() {
        // ARRANGE: o save simulado devolve OUTRO objeto, com o id gerado. Assim
        // o captor vê a entidade como o Service a enviou (o molde da Etapa 6).
        Doador doador = doador(1L);
        DoacaoRequestDTO dto = new DoacaoRequestDTO(
                1L, DATA, new BigDecimal("150.00"), TipoDoacao.ALIMENTO, "Cestas básicas");
        when(doadorRepository.findById(1L)).thenReturn(Optional.of(doador));
        when(repository.save(any(Doacao.class))).thenReturn(doacaoSalva(1L, "Cestas básicas"));

        // ACT
        DoacaoResponseDTO resposta = service.criar(dto);

        // ASSERT: a resposta é montada com o que o banco devolveu
        assertThat(resposta.idDoacao()).isEqualTo(1L);
        assertThat(resposta.idDoador()).isEqualTo(1L);
        assertThat(resposta.descricao()).isEqualTo("Cestas básicas");

        // ASSERT: a entidade enviada ao banco não tinha id (INSERT, e não
        // UPDATE), apontava para o doador encontrado e levava os campos do cliente
        ArgumentCaptor<Doacao> captor = ArgumentCaptor.forClass(Doacao.class);
        verify(repository).save(captor.capture());
        Doacao enviada = captor.getValue();
        assertThat(enviada.getIdDoacao()).isNull();
        assertThat(enviada.getDoador()).isSameAs(doador);
        assertThat(enviada.getDataDoacao()).isEqualTo(DATA);
        assertThat(enviada.getValor()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(enviada.getTipo()).isEqualTo(TipoDoacao.ALIMENTO);
        assertThat(enviada.getDescricao()).isEqualTo("Cestas básicas");
    }

    @Test
    @DisplayName("Deve lançar exceção quando o doador não existe")
    void deveLancarExcecaoQuandoDoadorNaoExistir() {
        // ARRANGE: o cenário do dojo da Aula 07, com o doador 999 inexistente
        DoacaoRequestDTO dto = new DoacaoRequestDTO(999L, DATA, null, TipoDoacao.ROUPA, null);
        when(doadorRepository.findById(999L)).thenReturn(Optional.empty());

        // ACT + ASSERT: o assertThrows do roteiro do dojo, com a exceção exata
        // (o roteiro aceita qualquer RuntimeException). No Controller, vira 422.
        DoadorInexistenteException erro =
                assertThrows(DoadorInexistenteException.class, () -> service.criar(dto));
        assertThat(erro.getIdDoador()).isEqualTo(999L);

        // ASSERT: nada foi gravado
        verify(repository, never()).save(any(Doacao.class));
    }

    @Test
    @DisplayName("Deve gravar a descrição como nula quando vem em branco")
    void deveGravarDescricaoNulaQuandoEmBranco() {
        // ARRANGE: doação de roupa sem valor; "   " passa na validação
        DoacaoRequestDTO dto = new DoacaoRequestDTO(1L, DATA, null, TipoDoacao.ROUPA, "   ");
        when(doadorRepository.findById(1L)).thenReturn(Optional.of(doador(1L)));
        Doacao gravada = Doacao.builder()
                .idDoacao(2L)
                .doador(doador(1L))
                .dataDoacao(DATA)
                .tipo(TipoDoacao.ROUPA)
                .build();
        when(repository.save(any(Doacao.class))).thenReturn(gravada);

        // ACT
        DoacaoResponseDTO resposta = service.criar(dto);

        // ASSERT: o banco recebeu null, e não texto em branco
        ArgumentCaptor<Doacao> captor = ArgumentCaptor.forClass(Doacao.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getDescricao()).isNull();
        assertThat(captor.getValue().getValor()).isNull();

        // ASSERT: e a resposta também sai com null
        assertThat(resposta.descricao()).isNull();
        assertThat(resposta.valor()).isNull();
    }

    // ===================== GET por id =====================

    @Test
    @DisplayName("Deve retornar a doação quando o id existe")
    void deveRetornarDoacaoQuandoIdExistir() {
        // ARRANGE
        when(repository.findById(1L)).thenReturn(Optional.of(doacaoSalva(1L, "Cestas básicas")));

        // ACT
        DoacaoResponseDTO resposta = service.buscarPorId(1L);

        // ASSERT: os seis campos foram para o DTO; do doador, só o id
        assertThat(resposta.idDoacao()).isEqualTo(1L);
        assertThat(resposta.idDoador()).isEqualTo(1L);
        assertThat(resposta.dataDoacao()).isEqualTo(DATA);
        assertThat(resposta.valor()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(resposta.tipo()).isEqualTo(TipoDoacao.ALIMENTO);
        assertThat(resposta.descricao()).isEqualTo("Cestas básicas");
    }

    @Test
    @DisplayName("Deve lançar exceção quando o id não existe")
    void deveLancarExcecaoQuandoIdNaoExistir() {
        // ARRANGE
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // ACT + ASSERT: 404 no Controller (RecursoNaoEncontradoException)
        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Doação")
                .hasMessageContaining("99");
    }

    // ===================== GET da lista =====================

    @Test
    @DisplayName("Deve listar as doações da página convertidas para DTO")
    void deveConverterPaginaParaDtoQuandoListar() {
        // ARRANGE: a página que o banco devolveria, com 2 de 3 doações. Os
        // filtros viram uma Specification, que o Mockito aceita qualquer.
        Pageable pageable = PageRequest.of(0, 2);
        Page<Doacao> pagina = new PageImpl<>(
                List.of(doacaoSalva(2L, "Leite em pó"), doacaoSalva(1L, "Cestas básicas")), pageable, 3);
        when(repository.findAll(ArgumentMatchers.<Specification<Doacao>>any(), eq(pageable))).thenReturn(pagina);

        // ACT: outubro inteiro, só alimentos
        Page<DoacaoResponseDTO> resposta = service.listar(
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31), TipoDoacao.ALIMENTO, pageable);

        // ASSERT: mesmos totais e o conteúdo convertido, na mesma ordem
        assertThat(resposta.getTotalElements()).isEqualTo(3);
        assertThat(resposta.getTotalPages()).isEqualTo(2);
        assertThat(resposta.getContent())
                .extracting(DoacaoResponseDTO::descricao)
                .containsExactly("Leite em pó", "Cestas básicas");
    }

    @Test
    @DisplayName("Deve aceitar o período de um dia quando dataInicio é igual a dataFim")
    void deveAceitarPeriodoDeUmDiaQuandoDatasIguais() {
        // ARRANGE: as duas pontas do período entram, então datas iguais valem
        Pageable pageable = PageRequest.of(0, 10);
        Page<Doacao> pagina = new PageImpl<>(List.of(doacaoSalva(1L, "Cestas básicas")), pageable, 1);
        when(repository.findAll(ArgumentMatchers.<Specification<Doacao>>any(), eq(pageable))).thenReturn(pagina);

        // ACT
        Page<DoacaoResponseDTO> resposta = service.listar(DATA, DATA, null, pageable);

        // ASSERT: consultou o banco, sem lançar exceção
        assertThat(resposta.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve lançar exceção quando dataInicio é depois de dataFim")
    void deveLancarExcecaoQuandoDataInicioDepoisDaDataFim() {
        // ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        LocalDate inicio = LocalDate.of(2026, 11, 10);
        LocalDate fim = LocalDate.of(2026, 11, 1);

        // ACT + ASSERT: 400 no Controller (PeriodoInvalidoException)
        assertThatThrownBy(() -> service.listar(inicio, fim, null, pageable))
                .isInstanceOf(PeriodoInvalidoException.class)
                .hasMessageContaining("2026-11-10")
                .hasMessageContaining("2026-11-01");

        // ASSERT: o banco nem foi consultado
        verify(repository, never()).findAll(ArgumentMatchers.<Specification<Doacao>>any(), any(Pageable.class));
    }
}
