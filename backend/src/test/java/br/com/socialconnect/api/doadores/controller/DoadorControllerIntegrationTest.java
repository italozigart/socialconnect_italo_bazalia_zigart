package br.com.socialconnect.api.doadores.controller;

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

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração dos endpoints de doador, no molde do
 * BeneficiarioControllerIntegrationTest: aplicação inteira numa porta
 * aleatória, H2 em memória (sem Docker) e perfil "test". O Flyway roda a V1 e
 * a V3, e as requisições passam por HTTP real: Controller, @Valid, Service,
 * Repository, banco e GlobalExceptionHandler.
 * <p>
 * Cada teste começa com a tabela doadores vazia e cria os próprios dados pela
 * API. A classe usa o mesmo contexto do Spring (e o mesmo H2) que o teste de
 * beneficiário; cada classe limpa só a tabela que usa.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class DoadorControllerIntegrationTest {

    private static final String URL = "/api/v1/doadores";
    private static final String ERROS = "https://socialconnect.api/errors/";

    private static final ParameterizedTypeReference<Map<String, Object>> JSON_OBJETO =
            new ParameterizedTypeReference<>() {
            };

    @LocalServerPort
    private int porta;

    // @Autowired no atributo: em classe de teste quem cria a instância é o
    // JUnit (mesma explicação do teste de beneficiário).
    @Autowired
    private DoadorRepository repository;

    private RestClient cliente;

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
    void limparBanco() {
        // Na Etapa 8, com doações apontando para doadores (FK), esta limpeza
        // vai precisar apagar as doações antes.
        repository.deleteAll();
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

    private ResponseEntity<Map<String, Object>> get(Object idDoador) {
        return cliente.get()
                .uri(URL + "/{id}", idDoador)
                .retrieve()
                .toEntity(JSON_OBJETO);
    }

    // Cria pela API um doador só com os campos obrigatórios.
    private void criar(String nome, String tipo) {
        ResponseEntity<Map<String, Object>> resposta = post("""
                {"nome": "%s", "tipo": "%s"}
                """.formatted(nome, tipo));
        assertThat(resposta.getStatusCode().value()).isEqualTo(201);
    }

    // ===================== POST =====================

    @Test
    @DisplayName("POST - deve criar doador e retornar 201 com Location")
    void deveCriarDoadorQuandoDadosValidos() {
        // ARRANGE
        String json = """
                {
                  "nome": "Mercado Solidário Ltda",
                  "tipo": "PESSOA_JURIDICA",
                  "email": "contato@example.com",
                  "telefone": "1133334444"
                }
                """;

        // ACT
        ResponseEntity<Map<String, Object>> resposta = post(json);

        // ASSERT: 201 com o endereço do recurso novo
        assertThat(resposta.getStatusCode().value()).isEqualTo(201);
        Map<String, Object> corpo = resposta.getBody();
        assertThat(corpo).isNotNull();
        Object id = corpo.get("idDoador");
        assertThat(id).isNotNull();
        assertThat(resposta.getHeaders().getLocation()).isNotNull();
        assertThat(resposta.getHeaders().getLocation().toString()).isEqualTo(URL + "/" + id);

        // ASSERT: o GET pelo id devolve o doador gravado, com os quatro campos
        Map<String, Object> gravado = get(id).getBody();
        assertThat(gravado).isNotNull();
        assertThat(gravado.get("nome")).isEqualTo("Mercado Solidário Ltda");
        assertThat(gravado.get("tipo")).isEqualTo("PESSOA_JURIDICA");
        assertThat(gravado.get("email")).isEqualTo("contato@example.com");
        assertThat(gravado.get("telefone")).isEqualTo("1133334444");
    }

    @Test
    @DisplayName("POST - deve retornar 400 listando os campos inválidos")
    void deveRetornar400QuandoDadosInvalidos() {
        // ARRANGE: nome vazio, sem tipo, e-mail sem @ e telefone com máscara
        String json = """
                {"nome": "", "email": "contato.example.com", "telefone": "(11) 3333-4444"}
                """;

        // ACT
        ResponseEntity<Map<String, Object>> resposta = post(json);

        // ASSERT: 400 em Problem Details, um item por campo, em ordem alfabética
        assertThat(resposta.getStatusCode().value()).isEqualTo(400);
        Map<String, Object> corpo = resposta.getBody();
        assertThat(corpo).isNotNull();
        assertThat(corpo.get("type")).isEqualTo(ERROS + "validacao");
        assertThat(corpo.get("errors")).isEqualTo(List.of(
                Map.of("field", "email", "message", "E-mail inválido. Use o formato nome@dominio.com."),
                Map.of("field", "nome", "message", "O nome é obrigatório."),
                Map.of("field", "telefone", "message", "Telefone inválido. Informe DDD e número, só dígitos: "
                        + "10 para fixo ou 11 para celular (ex.: 11999999999)."),
                Map.of("field", "tipo", "message", "O tipo é obrigatório: PESSOA_FISICA ou PESSOA_JURIDICA.")
        ));

        // ASSERT: nada foi gravado
        assertThat(repository.count()).isZero();
    }

    @Test
    @DisplayName("POST - deve retornar 400 quando o tipo não existe no enum")
    void deveRetornar400QuandoTipoForaDaLista() {
        // ARRANGE: "ONG" não é PESSOA_FISICA nem PESSOA_JURIDICA
        String json = """
                {"nome": "Associação Amigos do Bairro", "tipo": "ONG"}
                """;

        // ACT
        ResponseEntity<Map<String, Object>> resposta = post(json);

        // ASSERT: o Jackson não converte o texto para o enum, e o handler
        // responde 400 de corpo inválido, e não 500
        assertThat(resposta.getStatusCode().value()).isEqualTo(400);
        Map<String, Object> corpo = resposta.getBody();
        assertThat(corpo).isNotNull();
        assertThat(corpo.get("type")).isEqualTo(ERROS + "corpo-invalido");
        assertThat(repository.count()).isZero();
    }

    // ===================== GET =====================

    @Test
    @DisplayName("GET - deve retornar 404 quando o doador não existe")
    void deveRetornar404QuandoDoadorNaoExiste() {
        // ACT: tabela vazia, então o id 99999 não existe
        ResponseEntity<Map<String, Object>> resposta = get(99999);

        // ASSERT
        assertThat(resposta.getStatusCode().value()).isEqualTo(404);
        Map<String, Object> corpo = resposta.getBody();
        assertThat(corpo).isNotNull();
        assertThat(corpo.get("type")).isEqualTo(ERROS + "recurso-nao-encontrado");
        assertThat(corpo.get("detail")).isEqualTo("Doador com id 99999 não existe.");
        assertThat(corpo.get("instance")).isEqualTo(URL + "/99999");
    }

    @Test
    @DisplayName("GET - deve listar em ordem de nome e paginar quando não há sort")
    void deveOrdenarPorNomeEPaginarQuandoListarSemSort() {
        // ARRANGE: cadastro fora da ordem alfabética
        criar("Carlos Lima", "PESSOA_FISICA");
        criar("Ana Souza", "PESSOA_FISICA");
        criar("Bazar Esperança Ltda", "PESSOA_JURIDICA");

        // ACT: dois por página, sem sort (vale o padrão do @PageableDefault)
        ResponseEntity<Map<String, Object>> resposta = cliente.get()
                .uri(URL + "?page=0&size=2")
                .retrieve()
                .toEntity(JSON_OBJETO);

        // ASSERT: três no total, em duas páginas
        assertThat(resposta.getStatusCode().value()).isEqualTo(200);
        Map<String, Object> corpo = resposta.getBody();
        assertThat(corpo).isNotNull();
        assertThat(corpo.get("totalElements")).isEqualTo(3);
        assertThat(corpo.get("totalPages")).isEqualTo(2);

        // ASSERT: a primeira página traz os dois primeiros em ordem alfabética
        List<?> conteudo = (List<?>) corpo.get("content");
        assertThat(conteudo).hasSize(2);
        assertThat(((Map<?, ?>) conteudo.get(0)).get("nome")).isEqualTo("Ana Souza");
        assertThat(((Map<?, ?>) conteudo.get(1)).get("nome")).isEqualTo("Bazar Esperança Ltda");
    }
}
