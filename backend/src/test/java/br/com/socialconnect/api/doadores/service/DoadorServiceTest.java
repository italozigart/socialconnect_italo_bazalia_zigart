package br.com.socialconnect.api.doadores.service;

import br.com.socialconnect.api.doadores.dto.DoadorRequestDTO;
import br.com.socialconnect.api.doadores.dto.DoadorResponseDTO;
import br.com.socialconnect.api.doadores.model.Doador;
import br.com.socialconnect.api.doadores.model.TipoDoador;
import br.com.socialconnect.api.doadores.repository.DoadorRepository;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do DoadorService, no molde do BeneficiarioServiceTest: sem
 * Spring e sem banco, com o Repository mockado pelo Mockito e injetado pelo
 * construtor do Service (@InjectMocks). Padrão AAA em todos os testes.
 * <p>
 * O MockitoExtension está no modo estrito: cada teste prepara só as chamadas
 * que aquele caminho do Service faz.
 */
@ExtendWith(MockitoExtension.class)
class DoadorServiceTest {

    @Mock
    private DoadorRepository repository;

    @InjectMocks
    private DoadorService service;

    // Doador como viria do banco: com id. Um objeto novo por chamada.
    private Doador doadorSalvo(Long idDoador, String nome) {
        return Doador.builder()
                .idDoador(idDoador)
                .nome(nome)
                .tipo(TipoDoador.PESSOA_JURIDICA)
                .email("contato@example.com")
                .telefone("1133334444")
                .build();
    }

    // ===================== POST (criar) =====================

    @Test
    @DisplayName("Deve criar doador quando os dados são válidos")
    void deveCriarDoadorQuandoDadosValidos() {
        // ARRANGE: o save simulado devolve OUTRO objeto, com o id gerado. Assim
        // o captor vê a entidade como o Service a enviou (o molde da Etapa 6).
        DoadorRequestDTO dto = new DoadorRequestDTO(
                "Mercado Solidário Ltda", TipoDoador.PESSOA_JURIDICA, "contato@example.com", "1133334444");
        when(repository.save(any(Doador.class))).thenReturn(doadorSalvo(1L, "Mercado Solidário Ltda"));

        // ACT
        DoadorResponseDTO resposta = service.criar(dto);

        // ASSERT: a resposta é montada com o que o banco devolveu
        assertThat(resposta.idDoador()).isEqualTo(1L);
        assertThat(resposta.nome()).isEqualTo("Mercado Solidário Ltda");
        assertThat(resposta.tipo()).isEqualTo(TipoDoador.PESSOA_JURIDICA);

        // ASSERT: a entidade enviada ao banco não tinha id (INSERT, e não UPDATE)
        // e levava os quatro campos do cliente
        ArgumentCaptor<Doador> captor = ArgumentCaptor.forClass(Doador.class);
        verify(repository).save(captor.capture());
        Doador enviado = captor.getValue();
        assertThat(enviado.getIdDoador()).isNull();
        assertThat(enviado.getNome()).isEqualTo("Mercado Solidário Ltda");
        assertThat(enviado.getTipo()).isEqualTo(TipoDoador.PESSOA_JURIDICA);
        assertThat(enviado.getEmail()).isEqualTo("contato@example.com");
        assertThat(enviado.getTelefone()).isEqualTo("1133334444");
    }

    @Test
    @DisplayName("Deve gravar e-mail e telefone como nulos quando vêm em branco")
    void deveGravarNuloQuandoEmailETelefoneEmBranco() {
        // ARRANGE: pessoa física sem contato; "" e "   " passam na validação
        DoadorRequestDTO dto = new DoadorRequestDTO("Maria da Silva", TipoDoador.PESSOA_FISICA, "", "   ");
        Doador gravado = Doador.builder()
                .idDoador(2L)
                .nome("Maria da Silva")
                .tipo(TipoDoador.PESSOA_FISICA)
                .build();
        when(repository.save(any(Doador.class))).thenReturn(gravado);

        // ACT
        DoadorResponseDTO resposta = service.criar(dto);

        // ASSERT: o banco recebeu null, e não texto em branco
        ArgumentCaptor<Doador> captor = ArgumentCaptor.forClass(Doador.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isNull();
        assertThat(captor.getValue().getTelefone()).isNull();

        // ASSERT: e a resposta também sai com null
        assertThat(resposta.email()).isNull();
        assertThat(resposta.telefone()).isNull();
    }

    // ===================== GET por id =====================

    @Test
    @DisplayName("Deve retornar o doador quando o id existe")
    void deveRetornarDoadorQuandoIdExistir() {
        // ARRANGE
        when(repository.findById(1L)).thenReturn(Optional.of(doadorSalvo(1L, "Mercado Solidário Ltda")));

        // ACT
        DoadorResponseDTO resposta = service.buscarPorId(1L);

        // ASSERT: os cinco campos da entidade foram para o DTO
        assertThat(resposta.idDoador()).isEqualTo(1L);
        assertThat(resposta.nome()).isEqualTo("Mercado Solidário Ltda");
        assertThat(resposta.tipo()).isEqualTo(TipoDoador.PESSOA_JURIDICA);
        assertThat(resposta.email()).isEqualTo("contato@example.com");
        assertThat(resposta.telefone()).isEqualTo("1133334444");
    }

    @Test
    @DisplayName("Deve lançar exceção quando o id não existe")
    void deveLancarExcecaoQuandoIdNaoExistir() {
        // ARRANGE
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // ACT + ASSERT: 404 no Controller (RecursoNaoEncontradoException)
        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Doador")
                .hasMessageContaining("99");
    }

    // ===================== GET da lista =====================

    @Test
    @DisplayName("Deve listar os doadores da página convertidos para DTO")
    void deveConverterPaginaParaDtoQuandoListar() {
        // ARRANGE: a página que o banco devolveria, com 2 de 3 doadores
        Pageable pageable = PageRequest.of(0, 2);
        Page<Doador> pagina = new PageImpl<>(
                List.of(doadorSalvo(1L, "Ana Souza"), doadorSalvo(2L, "Bruno Lima")), pageable, 3);
        when(repository.findAll(pageable)).thenReturn(pagina);

        // ACT
        Page<DoadorResponseDTO> resposta = service.listar(pageable);

        // ASSERT: mesmos totais e o conteúdo convertido, na mesma ordem
        assertThat(resposta.getTotalElements()).isEqualTo(3);
        assertThat(resposta.getTotalPages()).isEqualTo(2);
        assertThat(resposta.getContent())
                .extracting(DoadorResponseDTO::nome)
                .containsExactly("Ana Souza", "Bruno Lima");
    }
}
