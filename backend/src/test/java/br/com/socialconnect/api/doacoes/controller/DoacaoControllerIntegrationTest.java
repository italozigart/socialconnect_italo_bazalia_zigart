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
 * gravado, porque toda doação precisa de um (FK). As doações de cada teste
 * são criadas pela API.
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

    // GET da lista; a query vai com variáveis, como no teste de beneficiário.
    private ResponseEntity<Map<String, Object>> listar(String query, Object... variaveis) {
        return cliente.get()
                .uri(URL + query, variaveis)
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

    // Cria pela API uma doação do doador gravado. A descrição identifica a
    // doação nas listagens.
    private void criar(String dataDoacao, String tipo, String descricao) {
        ResponseEntity<Map<String, Object>> resposta = post(doacaoJson(dataDoacao, tipo, descricao));
        assertThat(resposta.getStatusCode().value()).isEqualTo(201);
    }

    // Cinco doações cadastradas fora de ordem. As pontas de outubro (01/10 e
    // 31/10) mostram que as datas-limite do período entram; 30/09 e 01/11
    // ficam de fora.
    private void criarDoacoesEmVoltaDeOutubro() {
        criar("2026-10-31", "ALIMENTO", "Leite em pó");
        criar("2026-09-30", "ALIMENTO", "Arroz e feijão");
        criar("2026-11-01", "FINANCEIRA", "Conta de luz");
        criar("2026-10-01", "ROUPA", "Agasalhos");
        criar("2026-10-15", "ALIMENTO", "Cestas básicas");
    }

    // Descrições do content da página, na ordem em que vieram.
    private static List<String> descricoes(Map<String, Object> pagina) {
        return ((List<?>) pagina.get("content")).stream()
                .map(item -> (String) ((Map<?, ?>) item).get("descricao"))
                .toList();
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

    @Test
    @DisplayName("POST - deve retornar 400 listando os campos inválidos")
    void deveRetornar400QuandoDadosInvalidos() {
        // ARRANGE: sem doador, sem data e sem tipo; valor negativo e descrição
        // com 501 caracteres
        String json = """
                {"valor": -5, "descricao": "%s"}
                """.formatted("x".repeat(501));

        // ACT
        ResponseEntity<Map<String, Object>> resposta = post(json);

        // ASSERT: 400 em Problem Details, um item por campo, em ordem alfabética
        assertThat(resposta.getStatusCode().value()).isEqualTo(400);
        Map<String, Object> corpo = resposta.getBody();
        assertThat(corpo).isNotNull();
        assertThat(corpo.get("type")).isEqualTo(ERROS + "validacao");
        assertThat(corpo.get("errors")).isEqualTo(List.of(
                Map.of("field", "dataDoacao", "message", "A data da doação é obrigatória."),
                Map.of("field", "descricao", "message", "A descrição deve ter no máximo 500 caracteres."),
                Map.of("field", "idDoador", "message", "O doador é obrigatório: informe o idDoador."),
                Map.of("field", "tipo", "message", "O tipo é obrigatório: ALIMENTO, ROUPA ou FINANCEIRA."),
                Map.of("field", "valor", "message", "O valor deve ser maior que zero.")
        ));

        // ASSERT: nada foi gravado
        assertThat(repository.count()).isZero();
    }

    @Test
    @DisplayName("POST - deve retornar 400 quando o valor tem mais de duas casas decimais")
    void deveRetornar400QuandoValorTemMaisDeDuasCasas() {
        // ARRANGE: sem o @Digits, o banco gravaria 10.56 sem avisar
        String json = """
                {"idDoador": %d, "dataDoacao": "2026-11-10", "valor": 10.555, "tipo": "FINANCEIRA"}
                """.formatted(idDoador);

        // ACT
        ResponseEntity<Map<String, Object>> resposta = post(json);

        // ASSERT: só o valor é recusado, com os limites do DECIMAL(10,2)
        assertThat(resposta.getStatusCode().value()).isEqualTo(400);
        Map<String, Object> corpo = resposta.getBody();
        assertThat(corpo).isNotNull();
        assertThat(corpo.get("errors")).isEqualTo(List.of(
                Map.of("field", "valor", "message", "O valor aceita até 8 dígitos inteiros e 2 casas decimais.")
        ));
        assertThat(repository.count()).isZero();
    }

    @Test
    @DisplayName("POST - deve retornar 422 quando o doador não existe")
    void deveRetornar422QuandoDoadorNaoExistir() {
        // ARRANGE: JSON válido, mas com um doador que não está cadastrado
        String json = """
                {"idDoador": 99999, "dataDoacao": "2026-11-10", "tipo": "ROUPA"}
                """;

        // ACT
        ResponseEntity<Map<String, Object>> resposta = post(json);

        // ASSERT: 422 em Problem Details (o porquê do 422 está no ADR 0001)
        assertThat(resposta.getStatusCode().value()).isEqualTo(422);
        Map<String, Object> corpo = resposta.getBody();
        assertThat(corpo).isNotNull();
        assertThat(corpo.get("status")).isEqualTo(422);
        assertThat(corpo.get("type")).isEqualTo(ERROS + "doador-inexistente");
        assertThat(corpo.get("title")).isEqualTo("Doador inexistente");
        assertThat(corpo.get("detail"))
                .isEqualTo("Não existe doador com id 99999. Cadastre o doador antes de registrar a doação.");
        assertThat(corpo.get("instance")).isEqualTo(URL);

        // ASSERT: nada foi gravado
        assertThat(repository.count()).isZero();
    }

    // ===================== GET por id =====================

    @Test
    @DisplayName("GET - deve retornar 404 quando a doação não existe")
    void deveRetornar404QuandoDoacaoNaoExiste() {
        // ACT: tabela vazia, então o id 99999 não existe
        ResponseEntity<Map<String, Object>> resposta = get(99999);

        // ASSERT: o texto do 404 não tem gênero e serve para "Doação"
        assertThat(resposta.getStatusCode().value()).isEqualTo(404);
        Map<String, Object> corpo = resposta.getBody();
        assertThat(corpo).isNotNull();
        assertThat(corpo.get("type")).isEqualTo(ERROS + "recurso-nao-encontrado");
        assertThat(corpo.get("detail")).isEqualTo("Doação com id 99999 não existe.");
        assertThat(corpo.get("instance")).isEqualTo(URL + "/99999");
    }

    // ===================== GET da lista =====================

    @Test
    @DisplayName("GET - deve listar as mais recentes primeiro, em páginas de 10, sem filtros")
    void deveListarMaisRecentesPrimeiroQuandoSemFiltros() {
        // ARRANGE: duas doações no mesmo dia e uma mais antiga, fora de ordem
        criar("2026-11-05", "ALIMENTO", "Mesmo dia, registrada antes");
        criar("2026-10-05", "ROUPA", "Mais antiga");
        criar("2026-11-05", "FINANCEIRA", "Mesmo dia, registrada depois");

        // ACT: sem parâmetros (vale o padrão do @PageableDefault)
        ResponseEntity<Map<String, Object>> resposta = listar("");

        // ASSERT: página de 10, com as três doações
        assertThat(resposta.getStatusCode().value()).isEqualTo(200);
        Map<String, Object> corpo = resposta.getBody();
        assertThat(corpo).isNotNull();
        assertThat(corpo.get("size")).isEqualTo(10);
        assertThat(corpo.get("totalElements")).isEqualTo(3);

        // ASSERT: data decrescente; no mesmo dia, a registrada por último
        // (maior idDoacao) vem primeiro
        assertThat(descricoes(corpo)).containsExactly(
                "Mesmo dia, registrada depois", "Mesmo dia, registrada antes", "Mais antiga");
    }

    @Test
    @DisplayName("GET - deve filtrar só pelo tipo quando as datas não vêm")
    void deveFiltrarSoPorTipoQuandoSemDatas() {
        // ARRANGE
        criarDoacoesEmVoltaDeOutubro();

        // ACT: um filtro sozinho (no repositório do professor, só os três juntos)
        ResponseEntity<Map<String, Object>> resposta = listar("?tipo={tipo}", "ALIMENTO");

        // ASSERT: as três de alimento, das mais recentes para as mais antigas
        assertThat(resposta.getStatusCode().value()).isEqualTo(200);
        Map<String, Object> corpo = resposta.getBody();
        assertThat(corpo).isNotNull();
        assertThat(corpo.get("totalElements")).isEqualTo(3);
        assertThat(descricoes(corpo)).containsExactly("Leite em pó", "Cestas básicas", "Arroz e feijão");
    }

    @Test
    @DisplayName("GET - deve filtrar pelo período, com as duas datas incluídas")
    void deveFiltrarPorPeriodoQuandoSemTipo() {
        // ARRANGE
        criarDoacoesEmVoltaDeOutubro();

        // ACT: outubro inteiro, de qualquer tipo
        ResponseEntity<Map<String, Object>> resposta =
                listar("?dataInicio={inicio}&dataFim={fim}", "2026-10-01", "2026-10-31");

        // ASSERT: 01/10 e 31/10 entram; 30/09 e 01/11 ficam de fora
        assertThat(resposta.getStatusCode().value()).isEqualTo(200);
        Map<String, Object> corpo = resposta.getBody();
        assertThat(corpo).isNotNull();
        assertThat(corpo.get("totalElements")).isEqualTo(3);
        assertThat(descricoes(corpo)).containsExactly("Leite em pó", "Cestas básicas", "Agasalhos");
    }

    @Test
    @DisplayName("GET - deve combinar período e tipo quando os três filtros vêm")
    void deveCombinarPeriodoETipoQuandoTodosOsFiltros() {
        // ARRANGE
        criarDoacoesEmVoltaDeOutubro();

        // ACT: outubro inteiro, só alimentos
        ResponseEntity<Map<String, Object>> resposta = listar(
                "?dataInicio={inicio}&dataFim={fim}&tipo={tipo}", "2026-10-01", "2026-10-31", "ALIMENTO");

        // ASSERT: o arroz (30/09) e os agasalhos (roupa) ficam de fora
        assertThat(resposta.getStatusCode().value()).isEqualTo(200);
        Map<String, Object> corpo = resposta.getBody();
        assertThat(corpo).isNotNull();
        assertThat(corpo.get("totalElements")).isEqualTo(2);
        assertThat(descricoes(corpo)).containsExactly("Leite em pó", "Cestas básicas");
    }

    @Test
    @DisplayName("GET - deve retornar 400 quando dataInicio é depois de dataFim")
    void deveRetornar400QuandoDataInicioDepoisDaDataFim() {
        // ACT
        ResponseEntity<Map<String, Object>> resposta =
                listar("?dataInicio={inicio}&dataFim={fim}", "2026-11-10", "2026-11-01");

        // ASSERT
        assertThat(resposta.getStatusCode().value()).isEqualTo(400);
        Map<String, Object> corpo = resposta.getBody();
        assertThat(corpo).isNotNull();
        assertThat(corpo.get("type")).isEqualTo(ERROS + "periodo-invalido");
        assertThat(corpo.get("detail"))
                .isEqualTo("A dataInicio (2026-11-10) não pode ser depois da dataFim (2026-11-01).");
        assertThat(corpo.get("instance")).isEqualTo(URL);
    }

    @Test
    @DisplayName("GET - deve retornar 400 com os valores aceitos quando o tipo é inválido")
    void deveRetornar400ComValoresAceitosQuandoTipoInvalido() {
        // ACT
        ResponseEntity<Map<String, Object>> resposta = listar("?tipo={tipo}", "DINHEIRO");

        // ASSERT: o handler de parâmetro inválido lista os valores do enum
        assertThat(resposta.getStatusCode().value()).isEqualTo(400);
        Map<String, Object> corpo = resposta.getBody();
        assertThat(corpo).isNotNull();
        assertThat(corpo.get("type")).isEqualTo(ERROS + "parametro-invalido");
        assertThat(corpo.get("detail")).isEqualTo("O parâmetro \"tipo\" não aceita o valor \"DINHEIRO\". "
                + "Valores aceitos: ALIMENTO, ROUPA, FINANCEIRA.");
    }
}
