package br.com.socialconnect.api.doacoes.controller;

import br.com.socialconnect.api.doacoes.repository.DoacaoRepository;
import br.com.socialconnect.api.doadores.model.Doador;
import br.com.socialconnect.api.doadores.model.TipoDoador;
import br.com.socialconnect.api.doadores.repository.DoadorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração dos endpoints de doação, no molde do
 * DoadorControllerIntegrationTest: aplicação inteira numa porta aleatória,
 * H2 em memória e perfil "test". O dojo da Aula 07 pede este teste com o
 * Testcontainers (PostgreSQL no Docker); aqui ele roda no H2, porque o
 * laboratório não tem Docker.
 * <p>
 * Cada teste começa com as tabelas doacoes e doadores vazias e um doador
 * gravado, porque toda doação precisa de um (FK).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class DoacaoControllerIntegrationTest {

    private static final String URL = "/api/v1/doacoes";
    private static final String ERROS = "https://socialconnect.api/errors/";

    private static final ParameterizedTypeReference<Map<String, Object>> JSON_OBJETO =
            new ParameterizedTypeReference<>() {
            };

    @LocalServerPort
    private int porta;

    // @Autowired no atributo: em classe de teste quem cria a instância é o
    // JUnit (mesma explicação do teste de beneficiário).
    @Autowired
    private DoacaoRepository repository;

    @Autowired
    private DoadorRepository doadorRepository;

    private RestClient cliente;

    // Doador gravado antes de cada teste, para as doações apontarem para ele.
    private Long idDoador;

    @BeforeEach
    void configurarCliente() {
        // Status handler vazio: o RestClient não lança exceção em 4xx/5xx, e o
        // teste confere o status e o corpo do erro.
        cliente = RestClient.builder()
                .baseUrl("http://localhost:" + porta)
                .defaultStatusHandler(HttpStatusCode::isError, (requisicao, resposta) -> {
                })
                .build();
    }

    @BeforeEach
    void prepararBanco() {
        // Doações antes dos doadores, por causa da FK. O H2 é o mesmo dos
        // outros testes de integração, e cada classe limpa as tabelas que usa.
        repository.deleteAll();
        doadorRepository.deleteAll();

        // Gravado pelo repository, e não pela API de doadores: este teste é da
        // API de doações, e a de doadores tem os próprios testes.
        idDoador = doadorRepository.save(Doador.builder()
                        .nome("Mercado Solidário Ltda")
                        .tipo(TipoDoador.PESSOA_JURIDICA)
                        .build())
                .getIdDoador();
    }

    // ===================== Apoio =====================

    private ResponseEntity<Map<String, Object>> post(String json) {
        return cliente.post()
                .uri(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .retrieve()
                .toEntity(JSON_OBJETO);
    }

    private ResponseEntity<Map<String, Object>> get(Object idDoacao) {
        return cliente.get()
                .uri(URL + "/{id}", idDoacao)
                .retrieve()
                .toEntity(JSON_OBJETO);
    }

    // JSON de uma doação do doador gravado, com valor de 150,50.
    private String doacaoJson(String dataDoacao, String tipo, String descricao) {
        return """
                {
                  "idDoador": %d,
                  "dataDoacao": "%s",
                  "valor": 150.50,
                  "tipo": "%s",
                  "descricao": "%s"
                }
                """.formatted(idDoador, dataDoacao, tipo, descricao);
    }

    // Número do JSON como Long: o Jackson lê um id pequeno como Integer.
    private static Long numero(Object valor) {
        return ((Number) valor).longValue();
    }

    // Valor do JSON como BigDecimal: o Jackson lê 150.50 como Double (150.5),
    // e o isEqualByComparingTo compara sem olhar as casas decimais.
    private static BigDecimal decimal(Object valor) {
        return new BigDecimal(String.valueOf(valor));
    }

    // ===================== POST =====================

    @Test
    @DisplayName("POST - deve criar doação e retornar 201 com Location")
    void deveCriarDoacaoQuandoDadosValidos() {
        // ARRANGE
        String json = doacaoJson("2026-11-10", "ALIMENTO", "Cestas básicas");

        // ACT
        ResponseEntity<Map<String, Object>> resposta = post(json);

        // ASSERT: 201 com o endereço do recurso novo
        assertThat(resposta.getStatusCode().value()).isEqualTo(201);
        Map<String, Object> corpo = resposta.getBody();
        assertThat(corpo).isNotNull();
        Object id = corpo.get("idDoacao");
        assertThat(id).isNotNull();
        assertThat(resposta.getHeaders().getLocation()).isNotNull();
        assertThat(resposta.getHeaders().getLocation().toString()).isEqualTo(URL + "/" + id);

        // ASSERT: a resposta traz o que foi enviado, inclusive a descrição
        // (o toEntity do repositório do professor perdia a descrição)
        assertThat(numero(corpo.get("idDoador"))).isEqualTo(idDoador);
        assertThat(corpo.get("dataDoacao")).isEqualTo("2026-11-10");
        assertThat(decimal(corpo.get("valor"))).isEqualByComparingTo(new BigDecimal("150.50"));
        assertThat(corpo.get("tipo")).isEqualTo("ALIMENTO");
        assertThat(corpo.get("descricao")).isEqualTo("Cestas básicas");

        // ASSERT: o GET pelo id devolve a doação gravada
        Map<String, Object> gravada = get(id).getBody();
        assertThat(gravada).isNotNull();
        assertThat(numero(gravada.get("idDoador"))).isEqualTo(idDoador);
        assertThat(gravada.get("dataDoacao")).isEqualTo("2026-11-10");
        assertThat(decimal(gravada.get("valor"))).isEqualByComparingTo(new BigDecimal("150.50"));
        assertThat(gravada.get("descricao")).isEqualTo("Cestas básicas");
    }

    @Test
    @DisplayName("POST - deve retornar 400 quando a data da doação está no futuro")
    void deveRetornar400QuandoDataFutura() {
        // ARRANGE: daqui a 10 dias, como no roteiro do dojo
        String dataFutura = LocalDate.now().plusDays(10).toString();
        String json = doacaoJson(dataFutura, "ALIMENTO", "Cestas básicas");

        // ACT
        ResponseEntity<Map<String, Object>> resposta = post(json);

        // ASSERT: 400 de validação, só no campo dataDoacao, com "futuro" na mensagem
        assertThat(resposta.getStatusCode().value()).isEqualTo(400);
        Map<String, Object> corpo = resposta.getBody();
        assertThat(corpo).isNotNull();
        assertThat(corpo.get("type")).isEqualTo(ERROS + "validacao");
        List<?> erros = (List<?>) corpo.get("errors");
        assertThat(erros).hasSize(1);
        Map<?, ?> erro = (Map<?, ?>) erros.get(0);
        assertThat(erro.get("field")).isEqualTo("dataDoacao");
        assertThat((String) erro.get("message")).contains("futuro");

        // ASSERT: nada foi gravado
        assertThat(repository.count()).isZero();
    }
}
