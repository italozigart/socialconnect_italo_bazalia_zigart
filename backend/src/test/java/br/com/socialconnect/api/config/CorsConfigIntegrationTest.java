package br.com.socialconnect.api.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração do CorsConfig: sobe a aplicação numa porta aleatória,
 * como os outros testes de integração, e manda pelo RestClient os cabeçalhos
 * que o navegador manda.
 * <p>
 * Preflight é o OPTIONS que o navegador envia antes de um POST com JSON, de um
 * PUT, de um PATCH ou de um DELETE para outra origem. Ele leva o Origin (de
 * onde vem a página) e o Access-Control-Request-Method (o método que vem
 * depois). O Spring responde sozinho, sem chamar o Controller: por isso as
 * URLs com id não precisam existir no banco.
 * <p>
 * O servidor do teste é http://localhost:porta-aleatória. Para o Spring, uma
 * requisição é de CORS quando o Origin é de outra origem (esquema, host ou
 * porta diferentes); é o caso de http://localhost:5173.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CorsConfigIntegrationTest {

    // Escrita aqui, e não lida do CorsConfig: se alguém trocar a origem por
    // engano, o teste falha.
    private static final String ORIGEM_FRONT = "http://localhost:5173";

    private static final ParameterizedTypeReference<Map<String, Object>> JSON_OBJETO =
            new ParameterizedTypeReference<>() {
            };

    @LocalServerPort
    private int porta;

    private RestClient cliente;

    @BeforeEach
    void configurarCliente() {
        // Mesmo cliente dos outros testes: o handler vazio deixa o teste
        // conferir o 403 em vez de o RestClient lançar exceção.
        cliente = RestClient.builder()
                .baseUrl("http://localhost:" + porta)
                .defaultStatusHandler(HttpStatusCode::isError, (requisicao, resposta) -> {
                })
                .build();
    }

    // ===================== Apoio =====================

    // Preflight como o navegador manda. O corpo vem como texto: no 200 ele é
    // vazio, e no 403 é "Invalid CORS request", que não é JSON.
    private ResponseEntity<String> preflight(String url, String origem, String metodo) {
        return cliente.options()
                .uri(url)
                .header(HttpHeaders.ORIGIN, origem)
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, metodo)
                .retrieve()
                .toEntity(String.class);
    }

    // ===================== Origem liberada =====================

    @ParameterizedTest(name = "{0} {1}")
    @CsvSource({
            "GET,    /api/v1/beneficiarios",
            "POST,   /api/v1/doadores",
            "PUT,    /api/v1/doacoes/1",
            "PATCH,  /api/v1/beneficiarios/1",
            "DELETE, /api/v1/doacoes/1"
    })
    @DisplayName("Preflight - deve liberar a origem do front-end nos cinco métodos, em todo /api/**")
    void deveLiberarPreflightDaOrigemDoFrontEnd(String metodo, String url) {
        // ACT: um método por linha, em URLs dos três recursos
        ResponseEntity<String> resposta = preflight(url, ORIGEM_FRONT, metodo);

        // ASSERT: 200 com a origem do front devolvida no Allow-Origin
        assertThat(resposta.getStatusCode().value()).isEqualTo(200);
        HttpHeaders cabecalhos = resposta.getHeaders();
        assertThat(cabecalhos.getAccessControlAllowOrigin()).isEqualTo(ORIGEM_FRONT);

        // ASSERT: o Spring devolve a lista inteira de métodos liberados
        assertThat(cabecalhos.getAccessControlAllowMethods()).containsExactly(
                HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH, HttpMethod.DELETE);

        // ASSERT: o navegador pode guardar este preflight por 30 minutos (padrão do Spring)
        assertThat(cabecalhos.getAccessControlMaxAge()).isEqualTo(1800);

        // ASSERT: sem allowCredentials, o cabeçalho de credenciais não vem
        assertThat(cabecalhos.containsHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS)).isFalse();
    }

    @Test
    @DisplayName("Preflight - deve liberar o Content-Type pedido por um POST com JSON")
    void deveLiberarContentTypeNoPreflightDoPostComJson() {
        // ACT: o POST com JSON (cadastro) faz o navegador avisar no preflight
        // que vai mandar o cabeçalho Content-Type
        ResponseEntity<String> resposta = cliente.options()
                .uri("/api/v1/beneficiarios")
                .header(HttpHeaders.ORIGIN, ORIGEM_FRONT)
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type")
                .retrieve()
                .toEntity(String.class);

        // ASSERT: liberado, porque o padrão do Spring aceita qualquer cabeçalho
        assertThat(resposta.getStatusCode().value()).isEqualTo(200);
        assertThat(resposta.getHeaders().getAccessControlAllowOrigin()).isEqualTo(ORIGEM_FRONT);
        assertThat(resposta.getHeaders().getAccessControlAllowHeaders()).containsExactly("content-type");
    }

    @Test
    @DisplayName("GET da listagem - deve vir com o Allow-Origin, sem precisar de preflight")
    void deveIncluirAllowOriginNoGetDaListagem() {
        // ACT: o GET que a BeneficiarioList faz. Sem corpo e sem cabeçalhos
        // especiais, é uma requisição simples: o navegador manda direto, só
        // com o Origin
        ResponseEntity<Map<String, Object>> resposta = cliente.get()
                .uri("/api/v1/beneficiarios?page=0&size=10")
                .header(HttpHeaders.ORIGIN, ORIGEM_FRONT)
                .retrieve()
                .toEntity(JSON_OBJETO);

        // ASSERT: a página chega normalmente...
        assertThat(resposta.getStatusCode().value()).isEqualTo(200);
        assertThat(resposta.getBody()).containsKeys("content", "totalElements", "totalPages", "first", "last");

        // ...e com o Allow-Origin, que deixa o navegador entregar a resposta ao front
        assertThat(resposta.getHeaders().getAccessControlAllowOrigin()).isEqualTo(ORIGEM_FRONT);
    }

    // ===================== Recusas =====================

    @ParameterizedTest(name = "recusa {0}")
    @ValueSource(strings = {
            "http://localhost:3000",  // outra aplicação
            "http://127.0.0.1:5173",  // mesma máquina, outro host
            "http://localhost:5174"   // o Vite numa porta que não é a 5173
    })
    @DisplayName("Preflight - deve recusar com 403, fora do Problem Details, uma origem não liberada")
    void deveRecusarPreflightDeOrigemNaoLiberada(String origem) {
        // ACT
        ResponseEntity<String> resposta = preflight("/api/v1/beneficiarios", origem, "POST");

        // ASSERT: 403 com o texto do Spring. A recusa sai antes do Controller,
        // então não passa pelo GlobalExceptionHandler: não é Problem Details
        assertThat(resposta.getStatusCode().value()).isEqualTo(403);
        assertThat(resposta.getBody()).isEqualTo("Invalid CORS request");

        // ASSERT: sem Allow-Origin, o navegador bloqueia a requisição de verdade
        assertThat(resposta.getHeaders().getAccessControlAllowOrigin()).isNull();
    }

    @Test
    @DisplayName("Preflight - deve recusar com 403 um método fora da lista, mesmo que a API o atenda")
    void deveRecusarPreflightDeMetodoForaDaLista() {
        // ACT: o Spring atende HEAD em toda rota de GET, mas o HEAD não está
        // na lista do CorsConfig
        ResponseEntity<String> resposta = preflight("/api/v1/beneficiarios", ORIGEM_FRONT, "HEAD");

        // ASSERT: a lista do CorsConfig manda, e não o que o Controller aceita
        assertThat(resposta.getStatusCode().value()).isEqualTo(403);
        assertThat(resposta.getBody()).isEqualTo("Invalid CORS request");
        assertThat(resposta.getHeaders().getAccessControlAllowOrigin()).isNull();
    }
}
