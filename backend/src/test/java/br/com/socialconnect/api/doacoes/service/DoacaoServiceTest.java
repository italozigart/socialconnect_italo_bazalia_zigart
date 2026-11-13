package br.com.socialconnect.api.doacoes.service;

import br.com.socialconnect.api.doacoes.dto.DoacaoPatchDTO;
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
import static org.mockito.ArgumentMatchers.anyLong;
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
 * <p>
 * PUT, PATCH e DELETE (Etapa 9) seguem o molde do BeneficiarioServiceTest. Nos
 * testes de erro, além da exceção, o teste confere o que NÃO aconteceu: nada
 * gravado ou apagado, o doador nem consultado quando a doação não existe, e a
 * entidade intacta quando o doador não existe.
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

    // ===================== PUT (atualizar) =====================

    @Test
    @DisplayName("Deve substituir todos os campos quando o PUT é válido")
    void deveSubstituirTodosOsCamposQuandoPutValido() {
        // ARRANGE: a doação 1 é do doador 1, com valor e descrição. O PUT troca
        // o doador, a data e o tipo, não traz valor e manda a descrição em branco.
        Doacao existente = doacaoSalva(1L, "Cestas básicas");
        Doador outroDoador = doador(2L);
        LocalDate novaData = LocalDate.of(2026, 11, 5);
        DoacaoRequestDTO dto = new DoacaoRequestDTO(2L, novaData, null, TipoDoacao.ROUPA, "   ");
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(doadorRepository.findById(2L)).thenReturn(Optional.of(outroDoador));
        when(repository.save(existente)).thenReturn(existente);

        // ACT
        DoacaoResponseDTO resposta = service.atualizar(1L, dto);

        // ASSERT: os campos do cliente foram copiados, inclusive o nulo
        assertThat(resposta.idDoador()).isEqualTo(2L);
        assertThat(resposta.dataDoacao()).isEqualTo(novaData);
        assertThat(resposta.tipo()).isEqualTo(TipoDoacao.ROUPA);
        assertThat(resposta.valor()).isNull();     // substituição total: ausente apaga
        assertThat(resposta.descricao()).isNull(); // em branco vira null, como no POST

        // ASSERT: o id é do servidor e não muda, e a doação aponta para o
        // doador encontrado no banco
        assertThat(resposta.idDoacao()).isEqualTo(1L);
        assertThat(existente.getDoador()).isSameAs(outroDoador);
    }

    @Test
    @DisplayName("Deve lançar exceção sem consultar o doador quando o PUT é em doação inexistente")
    void deveLancarExcecaoSemConsultarDoadorQuandoPutEmDoacaoInexistente() {
        // ARRANGE: corpo válido, mas a doação 99 não existe
        DoacaoRequestDTO dto = new DoacaoRequestDTO(1L, DATA, null, TipoDoacao.ROUPA, null);
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // ACT + ASSERT: 404 no Controller (RecursoNaoEncontradoException)
        assertThatThrownBy(() -> service.atualizar(99L, dto))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Doação")
                .hasMessageContaining("99");

        // ASSERT: o 404 vem antes do 422, então o doador nem foi procurado;
        // e nada foi gravado
        verify(doadorRepository, never()).findById(anyLong());
        verify(repository, never()).save(any(Doacao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção e manter a doação quando o PUT traz doador inexistente")
    void deveLancarExcecaoQuandoPutComDoadorInexistente() {
        // ARRANGE: a doação existe, o doador 999 não
        Doacao existente = doacaoSalva(1L, "Cestas básicas");
        DoacaoRequestDTO dto = new DoacaoRequestDTO(
                999L, LocalDate.of(2026, 11, 5), null, TipoDoacao.ROUPA, null);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(doadorRepository.findById(999L)).thenReturn(Optional.empty());

        // ACT + ASSERT: 422 no Controller (ADR 0001)
        DoadorInexistenteException erro =
                assertThrows(DoadorInexistenteException.class, () -> service.atualizar(1L, dto));
        assertThat(erro.getIdDoador()).isEqualTo(999L);

        // ASSERT: nada foi gravado, e a entidade continua como estava, porque
        // a busca do doador vem antes dos setters
        verify(repository, never()).save(any(Doacao.class));
        assertThat(existente.getDoador().getIdDoador()).isEqualTo(1L);
        assertThat(existente.getDataDoacao()).isEqualTo(DATA);
        assertThat(existente.getTipo()).isEqualTo(TipoDoacao.ALIMENTO);
        assertThat(existente.getValor()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(existente.getDescricao()).isEqualTo("Cestas básicas");
    }

    // ===================== PATCH (atualizarParcial) =====================

    @Test
    @DisplayName("Deve alterar só os campos enviados quando o PATCH é válido")
    void devePreservarCamposNaoEnviadosQuandoPatch() {
        // ARRANGE: PATCH só com o valor; os outros campos vêm nulos
        Doacao existente = doacaoSalva(1L, "Cestas básicas");
        DoacaoPatchDTO dto = new DoacaoPatchDTO(null, null, new BigDecimal("80.50"), null, null);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        // ACT
        DoacaoResponseDTO resposta = service.atualizarParcial(1L, dto);

        // ASSERT: o valor mudou
        assertThat(resposta.valor()).isEqualByComparingTo(new BigDecimal("80.50"));

        // ASSERT: null no PATCH quer dizer "não mexer"
        assertThat(resposta.idDoador()).isEqualTo(1L);
        assertThat(resposta.dataDoacao()).isEqualTo(DATA);
        assertThat(resposta.tipo()).isEqualTo(TipoDoacao.ALIMENTO);
        assertThat(resposta.descricao()).isEqualTo("Cestas básicas");

        // ASSERT: sem idDoador no PATCH, o doador nem é consultado
        verify(doadorRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Deve trocar o doador quando o PATCH traz o idDoador de um doador cadastrado")
    void deveTrocarDoadorQuandoPatchTrazIdDoador() {
        // ARRANGE: PATCH só com o idDoador do doador 2
        Doacao existente = doacaoSalva(1L, "Cestas básicas");
        Doador outroDoador = doador(2L);
        DoacaoPatchDTO dto = new DoacaoPatchDTO(2L, null, null, null, null);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(doadorRepository.findById(2L)).thenReturn(Optional.of(outroDoador));
        when(repository.save(existente)).thenReturn(existente);

        // ACT
        DoacaoResponseDTO resposta = service.atualizarParcial(1L, dto);

        // ASSERT: o doador mudou, e a doação aponta para o doador encontrado
        assertThat(resposta.idDoador()).isEqualTo(2L);
        assertThat(existente.getDoador()).isSameAs(outroDoador);

        // ASSERT: o resto não mudou
        assertThat(resposta.valor()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(resposta.descricao()).isEqualTo("Cestas básicas");
    }

    @Test
    @DisplayName("Deve lançar exceção e manter a doação quando o PATCH traz doador inexistente")
    void deveLancarExcecaoQuandoPatchComDoadorInexistente() {
        // ARRANGE: o doador 999 não existe; os outros campos são válidos
        Doacao existente = doacaoSalva(1L, "Cestas básicas");
        DoacaoPatchDTO dto = new DoacaoPatchDTO(
                999L, LocalDate.of(2026, 11, 5), new BigDecimal("80.50"), TipoDoacao.ROUPA, "Agasalhos");
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(doadorRepository.findById(999L)).thenReturn(Optional.empty());

        // ACT + ASSERT: 422 no Controller, como no POST e no PUT
        assertThatThrownBy(() -> service.atualizarParcial(1L, dto))
                .isInstanceOf(DoadorInexistenteException.class)
                .hasMessageContaining("999");

        // ASSERT: nada foi gravado, e nenhum campo mudou, nem os que vieram
        // válidos: o doador é o primeiro campo aplicado
        verify(repository, never()).save(any(Doacao.class));
        assertThat(existente.getDoador().getIdDoador()).isEqualTo(1L);
        assertThat(existente.getDataDoacao()).isEqualTo(DATA);
        assertThat(existente.getValor()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(existente.getTipo()).isEqualTo(TipoDoacao.ALIMENTO);
        assertThat(existente.getDescricao()).isEqualTo("Cestas básicas");
    }

    @Test
    @DisplayName("Deve apagar a descrição quando o PATCH a envia em branco")
    void deveApagarDescricaoQuandoPatchEnviaDescricaoEmBranco() {
        // ARRANGE: "   " passa na validação (@Size), como no POST
        Doacao existente = doacaoSalva(1L, "Cestas básicas");
        DoacaoPatchDTO dto = new DoacaoPatchDTO(null, null, null, null, "   ");
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        // ACT
        DoacaoResponseDTO resposta = service.atualizarParcial(1L, dto);

        // ASSERT: texto em branco vira null, como no POST e no PUT
        assertThat(resposta.descricao()).isNull();
        assertThat(existente.getDescricao()).isNull();

        // ASSERT: o resto não mudou
        assertThat(resposta.valor()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(resposta.tipo()).isEqualTo(TipoDoacao.ALIMENTO);
    }

    // ===================== DELETE =====================

    @Test
    @DisplayName("Deve excluir a doação quando o id existe")
    void deveExcluirDoacaoQuandoIdExistir() {
        // ARRANGE
        Doacao existente = doacaoSalva(1L, "Cestas básicas");
        when(repository.findById(1L)).thenReturn(Optional.of(existente));

        // ACT
        service.deletar(1L);

        // ASSERT: o DELETE recebeu a entidade encontrada, e o doador não foi tocado
        verify(repository).delete(existente);
        verify(doadorRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir doação inexistente")
    void deveLancarExcecaoQuandoExcluirIdInexistente() {
        // ARRANGE
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // ACT + ASSERT: 404, e não o 204 silencioso do deleteById
        assertThatThrownBy(() -> service.deletar(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Doação")
                .hasMessageContaining("99");

        // ASSERT: nada foi apagado
        verify(repository, never()).delete(any(Doacao.class));
    }
}
