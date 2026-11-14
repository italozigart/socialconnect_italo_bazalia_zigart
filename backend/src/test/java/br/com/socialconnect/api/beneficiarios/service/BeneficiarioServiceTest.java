package br.com.socialconnect.api.beneficiarios.service;

import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioPatchDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioRequestDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioResponseDTO;
import br.com.socialconnect.api.beneficiarios.model.Beneficiario;
import br.com.socialconnect.api.beneficiarios.repository.BeneficiarioRepository;
import br.com.socialconnect.api.exception.CpfDuplicadoException;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do BeneficiarioService: sem Spring e sem banco. O Repository
 * é um mock do Mockito, injetado pelo construtor do Service (@InjectMocks).
 * Cada teste segue o padrão AAA: ARRANGE (prepara), ACT (executa), ASSERT (confere).
 * <p>
 * O MockitoExtension trabalha no modo estrito: um "when" que o Service não
 * chegar a usar faz o teste falhar (UnnecessaryStubbingException). Por isso
 * cada teste só prepara as chamadas que aquele caminho do Service faz.
 */
@ExtendWith(MockitoExtension.class)
class BeneficiarioServiceTest {

    private static final String CPF = "52998224725";
    private static final String OUTRO_CPF = "12345678909";
    private static final LocalDate DATA_CADASTRO = LocalDate.of(2026, 9, 1);

    @Mock
    private BeneficiarioRepository repository;

    @InjectMocks
    private BeneficiarioService service;

    // Beneficiário como viria do banco: com id e data de cadastro.
    // Um objeto novo por chamada, para um teste não alterar o de outro.
    private Beneficiario beneficiarioSalvo() {
        return Beneficiario.builder()
                .idBeneficiario(1L)
                .nome("Maria da Silva")
                .cpf(CPF)
                .telefone("11999999999")
                .endereco("Rua das Flores, 123 - Centro")
                .situacaoVulnerabilidade("Renda familiar baixa")
                .dataCadastro(DATA_CADASTRO)
                .build();
    }

    private BeneficiarioRequestDTO requestValido() {
        return new BeneficiarioRequestDTO(
                "Maria da Silva",
                CPF,
                "11999999999",
                "Rua das Flores, 123 - Centro",
                "Renda familiar baixa"
        );
    }

    // ===================== POST (criar) =====================

    @Test
    @DisplayName("Deve criar beneficiário quando o CPF não está cadastrado")
    void deveCriarBeneficiarioQuandoCpfNaoCadastrado() {
        // ARRANGE: CPF livre, e o banco devolve o registro gravado, com o id gerado
        BeneficiarioRequestDTO dto = requestValido();
        Beneficiario gravado = beneficiarioSalvo();
        gravado.setDataCadastro(LocalDate.now());
        when(repository.existsByCpf(CPF)).thenReturn(false);
        when(repository.saveAndFlush(any(Beneficiario.class))).thenReturn(gravado);

        // ACT
        BeneficiarioResponseDTO resposta = service.criar(dto);

        // ASSERT: a resposta é montada com o que o banco devolveu
        assertThat(resposta.idBeneficiario()).isEqualTo(1L);
        assertThat(resposta.nome()).isEqualTo("Maria da Silva");
        assertThat(resposta.cpf()).isEqualTo(CPF);

        // ASSERT: a entidade enviada ao banco não tinha id (INSERT, e não UPDATE)
        // e levava a data de hoje, preenchida pelo servidor
        ArgumentCaptor<Beneficiario> captor = ArgumentCaptor.forClass(Beneficiario.class);
        verify(repository).saveAndFlush(captor.capture());
        Beneficiario enviado = captor.getValue();
        assertThat(enviado.getIdBeneficiario()).isNull();
        assertThat(enviado.getDataCadastro()).isEqualTo(LocalDate.now());
        assertThat(enviado.getCpf()).isEqualTo(CPF);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o CPF já está cadastrado")
    void deveLancarExcecaoQuandoCpfJaCadastrado() {
        // ARRANGE: o banco já tem um beneficiário com este CPF
        BeneficiarioRequestDTO dto = requestValido();
        when(repository.existsByCpf(CPF)).thenReturn(true);

        // ACT + ASSERT: 409 no Controller (CpfDuplicadoException)
        assertThatThrownBy(() -> service.criar(dto))
                .isInstanceOf(CpfDuplicadoException.class)
                .hasMessageContaining(CPF);

        // ASSERT: nada foi gravado
        verify(repository, never()).saveAndFlush(any(Beneficiario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção de CPF duplicado quando o UNIQUE do banco recusar o CPF")
    void deveLancarCpfDuplicadoQuandoBancoRecusarCpfRepetido() {
        // ARRANGE: simula a corrida entre dois POSTs. A consulta não achou o CPF,
        // mas outra requisição gravou o mesmo CPF antes, e o UNIQUE da V1 recusa
        BeneficiarioRequestDTO dto = requestValido();
        when(repository.existsByCpf(CPF)).thenReturn(false);
        when(repository.saveAndFlush(any(Beneficiario.class)))
                .thenThrow(new DataIntegrityViolationException("Unique index or primary key violation"));

        // ACT + ASSERT: o erro do banco vira CpfDuplicadoException (409), e não 500
        assertThatThrownBy(() -> service.criar(dto))
                .isInstanceOf(CpfDuplicadoException.class)
                .hasMessageContaining(CPF);
    }

    @Test
    @DisplayName("Deve gravar o telefone como nulo quando o POST traz telefone em branco")
    void deveGravarTelefoneNuloQuandoPostComTelefoneEmBranco() {
        // ARRANGE: "   " passa na @Telefone, porque o telefone é opcional
        BeneficiarioRequestDTO dto = new BeneficiarioRequestDTO("Maria da Silva", CPF, "   ", null, null);
        when(repository.existsByCpf(CPF)).thenReturn(false);
        when(repository.saveAndFlush(any(Beneficiario.class))).thenReturn(beneficiarioSalvo());

        // ACT
        service.criar(dto);

        // ASSERT: o banco recebeu null, e não o texto em branco
        ArgumentCaptor<Beneficiario> captor = ArgumentCaptor.forClass(Beneficiario.class);
        verify(repository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getTelefone()).isNull();
    }

    // ===================== GET por id =====================

    @Test
    @DisplayName("Deve retornar o beneficiário quando o id existe")
    void deveRetornarBeneficiarioQuandoIdExistir() {
        // ARRANGE
        when(repository.findById(1L)).thenReturn(Optional.of(beneficiarioSalvo()));

        // ACT
        BeneficiarioResponseDTO resposta = service.buscarPorId(1L);

        // ASSERT
        assertThat(resposta.idBeneficiario()).isEqualTo(1L);
        assertThat(resposta.nome()).isEqualTo("Maria da Silva");
        assertThat(resposta.dataCadastro()).isEqualTo(DATA_CADASTRO);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o id não existe")
    void deveLancarExcecaoQuandoIdNaoExistir() {
        // ARRANGE
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // ACT + ASSERT: 404 no Controller (RecursoNaoEncontradoException)
        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("99");
    }

    // ===================== PUT (atualizar) =====================

    @Test
    @DisplayName("Deve substituir todos os campos quando o PUT é válido")
    void deveSubstituirTodosOsCamposQuandoPutValido() {
        // ARRANGE: PUT sem telefone e com outro CPF, livre no banco
        Beneficiario existente = beneficiarioSalvo();
        BeneficiarioRequestDTO dto = new BeneficiarioRequestDTO(
                "Maria da Silva Souza", OUTRO_CPF, null, "Rua Nova, 456 - Centro", "Desempregada");
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.existsByCpfAndIdBeneficiarioNot(OUTRO_CPF, 1L)).thenReturn(false);
        when(repository.saveAndFlush(existente)).thenReturn(existente);

        // ACT
        BeneficiarioResponseDTO resposta = service.atualizar(1L, dto);

        // ASSERT: os cinco campos do cliente foram copiados, inclusive o nulo
        assertThat(resposta.nome()).isEqualTo("Maria da Silva Souza");
        assertThat(resposta.cpf()).isEqualTo(OUTRO_CPF);
        assertThat(resposta.telefone()).isNull(); // substituição total: ausente apaga
        assertThat(resposta.endereco()).isEqualTo("Rua Nova, 456 - Centro");
        assertThat(resposta.situacaoVulnerabilidade()).isEqualTo("Desempregada");

        // ASSERT: id e data de cadastro são do servidor e não mudam
        assertThat(resposta.idBeneficiario()).isEqualTo(1L);
        assertThat(resposta.dataCadastro()).isEqualTo(DATA_CADASTRO);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o PUT usa o CPF de outro beneficiário")
    void deveLancarExcecaoQuandoPutUsarCpfDeOutroBeneficiario() {
        // ARRANGE: OUTRO_CPF já pertence a outro beneficiário
        Beneficiario existente = beneficiarioSalvo();
        BeneficiarioRequestDTO dto = new BeneficiarioRequestDTO(
                "Maria da Silva", OUTRO_CPF, "11999999999", null, null);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.existsByCpfAndIdBeneficiarioNot(OUTRO_CPF, 1L)).thenReturn(true);

        // ACT + ASSERT
        assertThatThrownBy(() -> service.atualizar(1L, dto))
                .isInstanceOf(CpfDuplicadoException.class)
                .hasMessageContaining(OUTRO_CPF);

        // ASSERT: nada foi gravado, e a entidade continua com o CPF antigo,
        // porque a verificação vem antes dos setters
        verify(repository, never()).saveAndFlush(any(Beneficiario.class));
        assertThat(existente.getCpf()).isEqualTo(CPF);
    }

    @Test
    @DisplayName("Deve apagar o telefone quando o PUT traz telefone em branco")
    void deveApagarTelefoneQuandoPutComTelefoneEmBranco() {
        // ARRANGE: o beneficiário tem telefone, e o PUT manda ""
        Beneficiario existente = beneficiarioSalvo();
        BeneficiarioRequestDTO dto = new BeneficiarioRequestDTO(
                "Maria da Silva", CPF, "", "Rua das Flores, 123 - Centro", "Renda familiar baixa");
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.existsByCpfAndIdBeneficiarioNot(CPF, 1L)).thenReturn(false);
        when(repository.saveAndFlush(existente)).thenReturn(existente);

        // ACT
        BeneficiarioResponseDTO resposta = service.atualizar(1L, dto);

        // ASSERT: o telefone virou null, e não ""
        assertThat(existente.getTelefone()).isNull();
        assertThat(resposta.telefone()).isNull();
    }

    // ===================== PATCH (atualizarParcial) =====================

    @Test
    @DisplayName("Deve alterar só os campos enviados quando o PATCH é válido")
    void devePreservarCamposNaoEnviadosQuandoPatch() {
        // ARRANGE: PATCH só com o telefone; os outros campos vêm nulos
        Beneficiario existente = beneficiarioSalvo();
        BeneficiarioPatchDTO dto = new BeneficiarioPatchDTO(null, "11988887777", null, null);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        // ACT
        BeneficiarioResponseDTO resposta = service.atualizarParcial(1L, dto);

        // ASSERT: o telefone mudou
        assertThat(resposta.telefone()).isEqualTo("11988887777");

        // ASSERT: null no PATCH quer dizer "não mexer"
        assertThat(resposta.nome()).isEqualTo("Maria da Silva");
        assertThat(resposta.cpf()).isEqualTo(CPF);
        assertThat(resposta.endereco()).isEqualTo("Rua das Flores, 123 - Centro");
        assertThat(resposta.situacaoVulnerabilidade()).isEqualTo("Renda familiar baixa");
        assertThat(resposta.dataCadastro()).isEqualTo(DATA_CADASTRO);

        // ASSERT: sem CPF no PATCH, não há consulta de duplicidade
        verify(repository, never()).existsByCpfAndIdBeneficiarioNot(anyString(), anyLong());
    }

    @Test
    @DisplayName("Deve apagar só o telefone quando o PATCH traz telefone em branco")
    void deveApagarTelefoneQuandoPatchComTelefoneEmBranco() {
        // ARRANGE: PATCH só com o telefone em branco; os outros campos vêm nulos
        Beneficiario existente = beneficiarioSalvo();
        BeneficiarioPatchDTO dto = new BeneficiarioPatchDTO(null, "", null, null);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        // ACT
        BeneficiarioResponseDTO resposta = service.atualizarParcial(1L, dto);

        // ASSERT: "" apaga o telefone (null), diferente do null, que não mexe
        assertThat(resposta.telefone()).isNull();

        // ASSERT: os campos que vieram nulos não mudaram
        assertThat(resposta.nome()).isEqualTo("Maria da Silva");
        assertThat(resposta.endereco()).isEqualTo("Rua das Flores, 123 - Centro");
        assertThat(resposta.situacaoVulnerabilidade()).isEqualTo("Renda familiar baixa");
    }

    // ===================== DELETE =====================

    @Test
    @DisplayName("Deve excluir o beneficiário quando o id existe")
    void deveExcluirBeneficiarioQuandoIdExistir() {
        // ARRANGE
        Beneficiario existente = beneficiarioSalvo();
        when(repository.findById(1L)).thenReturn(Optional.of(existente));

        // ACT
        service.deletar(1L);

        // ASSERT: o DELETE recebeu a entidade encontrada
        verify(repository).delete(existente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir beneficiário inexistente")
    void deveLancarExcecaoQuandoExcluirIdInexistente() {
        // ARRANGE
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // ACT + ASSERT: 404, e não o 204 silencioso do deleteById
        assertThatThrownBy(() -> service.deletar(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        // ASSERT: nada foi apagado
        verify(repository, never()).delete(any(Beneficiario.class));
    }

    // ===================== GET da lista (filtros) =====================

    @Test
    @DisplayName("Deve filtrar só pelo CPF quando CPF e nome vêm juntos")
    void deveFiltrarPorCpfQuandoCpfENomeInformados() {
        // ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        Page<Beneficiario> pagina = new PageImpl<>(List.of(beneficiarioSalvo()), pageable, 1);
        when(repository.findByCpf(CPF, pageable)).thenReturn(pagina);

        // ACT
        Page<BeneficiarioResponseDTO> resposta = service.listar("maria", CPF, pageable);

        // ASSERT: o CPF tem prioridade, e a página foi convertida para DTO
        assertThat(resposta.getTotalElements()).isEqualTo(1);
        assertThat(resposta.getContent())
                .extracting(BeneficiarioResponseDTO::cpf)
                .containsExactly(CPF);
        verify(repository, never()).findByNomeContainingIgnoreCase(anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve listar todos quando os filtros vêm em branco")
    void deveListarTodosQuandoFiltrosEmBranco() {
        // ARRANGE: filtro em branco conta como ausente
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));

        // ACT
        Page<BeneficiarioResponseDTO> resposta = service.listar("   ", "", pageable);

        // ASSERT
        assertThat(resposta.getTotalElements()).isZero();
        verify(repository, never()).findByCpf(anyString(), any(Pageable.class));
        verify(repository, never()).findByNomeContainingIgnoreCase(anyString(), any(Pageable.class));
    }
}
