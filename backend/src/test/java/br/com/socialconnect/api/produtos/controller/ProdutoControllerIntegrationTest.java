package br.com.socialconnect.api.produtos.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração: sobe a aplicação inteira numa porta aleatória, com o
 * H2 em memória do application.properties (sem Docker). O Flyway roda as
 * migrations V1 e V2 nesse banco, e as requisições passam por HTTP real:
 * Controller, @Valid, Service, Repository, banco e GlobalExceptionHandler.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProdutoControllerIntegrationTest {

    private static final ParameterizedTypeReference<Map<String, Object>> JSON_OBJETO =
            new ParameterizedTypeReference<>() {
            };

    @LocalServerPort
    private int porta;

    private RestClient cliente;

    @BeforeEach
    void configurarCliente() {
        // Por padrão o RestClient lança exceção em 4xx/5xx. Este handler vazio
        // desliga isso, para o teste conferir o status e o corpo do erro.
        cliente = RestClient.builder()
                .baseUrl("http://localhost:" + porta)
                .defaultStatusHandler(HttpStatusCode::isError, (requisicao, resposta) -> {
                })
                .build();
    }

    // O banco é o mesmo para todos os testes, então cada um usa um nome próprio.
    private String produtoJson(String nome, int estoqueAtual, int estoqueMinimo) {
        return """
                {
                  "nome": "%s",
                  "categoria": "ALIMENTO",
                  "estoqueAtual": %d,
                  "estoqueMinimo": %d,
                  "unidadeMedida": "kg"
                }
                """.formatted(nome, estoqueAtual, estoqueMinimo);
    }

    private ResponseEntity<Map<String, Object>> post(String json) {
        return cliente.post()
                .uri("/api/v1/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .retrieve()
                .toEntity(JSON_OBJETO);
    }

    @Test
    @DisplayName("POST - deve criar produto e retornar 201 com Location")
    void deveCriarProdutoQuandoDadosValidos() {
        // Arrange
        String json = produtoJson("Feijao Carioca 1kg", 3, 10);

        // Act
        ResponseEntity<Map<String, Object>> resposta = post(json);

        // Assert
        assertThat(resposta.getStatusCode().value()).isEqualTo(201);
        assertThat(resposta.getHeaders().getLocation()).isNotNull();
        assertThat(resposta.getHeaders().getLocation().toString()).startsWith("/api/v1/produtos/");
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().get("idProduto")).isNotNull();
        assertThat(resposta.getBody().get("nome")).isEqualTo("Feijao Carioca 1kg");
        assertThat(resposta.getBody().get("estoqueBaixo")).isEqualTo(true); // 3 < 10
    }

    @Test
    @DisplayName("POST - deve retornar 409 quando o nome já existe")
    void deveRetornar409QuandoNomeDuplicado() {
        // Arrange: o primeiro cadastro passa
        assertThat(post(produtoJson("Macarrao 500g", 20, 5)).getStatusCode().value()).isEqualTo(201);

        // Act: o mesmo nome, com outras maiúsculas
        ResponseEntity<Map<String, Object>> resposta = post(produtoJson("MACARRAO 500G", 1, 5));

        // Assert: 409 em Problem Details
        assertThat(resposta.getStatusCode().value()).isEqualTo(409);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().get("status")).isEqualTo(409);
        assertThat(resposta.getBody().get("type")).isEqualTo("https://socialconnect.api/errors/nome-duplicado");
        assertThat(resposta.getBody().get("instance")).isEqualTo("/api/v1/produtos");
    }

    @Test
    @DisplayName("POST - deve retornar 422 quando o estoque atual é negativo")
    void deveRetornar422QuandoEstoqueNegativo() {
        // Arrange
        String json = produtoJson("Leite Integral 1l", -1, 5);

        // Act
        ResponseEntity<Map<String, Object>> resposta = post(json);

        // Assert: 422 em Problem Details, com a mensagem em português
        assertThat(resposta.getStatusCode().value()).isEqualTo(422);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().get("status")).isEqualTo(422);
        assertThat(resposta.getBody().get("title")).isEqualTo("Estoque negativo");
    }

    @Test
    @DisplayName("POST - deve retornar 400 listando os campos inválidos")
    void deveRetornar400QuandoDadosInvalidos() {
        // Arrange: nome vazio e unidade fora da lista
        String json = """
                {"nome": "", "categoria": "ALIMENTO", "estoqueAtual": 1, "estoqueMinimo": 1, "unidadeMedida": "tonelada"}
                """;

        // Act
        ResponseEntity<Map<String, Object>> resposta = post(json);

        // Assert
        assertThat(resposta.getStatusCode().value()).isEqualTo(400);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().get("errors").toString()).contains("nome", "unidadeMedida");
    }

    @Test
    @DisplayName("GET - deve retornar 404 quando o produto não existe")
    void deveRetornar404QuandoProdutoNaoExiste() {
        // Act
        ResponseEntity<Map<String, Object>> resposta = cliente.get()
                .uri("/api/v1/produtos/{id}", 999999)
                .retrieve()
                .toEntity(JSON_OBJETO);

        // Assert
        assertThat(resposta.getStatusCode().value()).isEqualTo(404);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().get("type")).isEqualTo("https://socialconnect.api/errors/recurso-nao-encontrado");
    }
}
