package br.com.socialconnect.api.beneficiarios.controller;

import br.com.socialconnect.api.beneficiarios.repository.BeneficiarioRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração: sobe a aplicação inteira numa porta aleatória, com o
 * H2 em memória (sem Docker) e o perfil "test". O Flyway roda a V1 nesse banco,
 * e as requisições passam por HTTP real: Controller, @Valid, Service,
 * Repository, banco e GlobalExceptionHandler.
 * <p>
 * Cada teste começa com a tabela vazia (limparBanco) e cria os próprios dados
 * pela API. Assim nenhum teste depende da ordem nem dos dados de outro.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BeneficiarioControllerIntegrationTest {

    private static final String URL = "/api/v1/beneficiarios";
    private static final String ERROS = "https://socialconnect.api/errors/";

    // CPFs com dígitos verificadores válidos (passam na @CPF)
    private static final String CPF_MARIA = "52998224725";
    private static final String CPF_MARIANA = "12345678909";
    private static final String CPF_JOAO = "11144477735";

    private static final ParameterizedTypeReference<Map<String, Object>> JSON_OBJETO =
            new ParameterizedTypeReference<>() {
            };

    @LocalServerPort
    private int porta;

    // Em classe de teste, @Autowired no atributo é o padrão (o teste do professor
    // faz igual): quem cria a instância é o JUnit. A regra da injeção pelo
    // construtor vale para o código de produção.
    @Autowired
    private BeneficiarioRepository repository;

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

    @BeforeEach
    void limparBanco() {
        // O H2 em memória é o mesmo durante todo o ./mvnw test (DB_CLOSE_DELAY=-1),
        // e o Spring reaproveita o contexto entre os testes. Sem esta limpeza, o
        // CPF gravado por um teste daria 409 no seguinte.
        repository.deleteAll();
    }

    // ===================== Apoio =====================

    private String beneficiarioJson(String nome, String cpf) {
        return """
                {
                  "nome": "%s",
                  "cpf": "%s",
                  "telefone": "11999999999",
                  "endereco": "Rua das Flores, 123 - Centro",
                  "situacaoVulnerabilidade": "Renda familiar baixa"
                }
                """.formatted(nome, cpf);
    }

    private ResponseEntity<Map<String, Object>> post(String json) {
        return cliente.post()
                .uri(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .retrieve()
                .toEntity(JSON_OBJETO);
    }

    private ResponseEntity<Map<String, Object>> get(Object idBeneficiario) {
        return cliente.get()
                .uri(URL + "/{id}", idBeneficiario)
                .retrieve()
                .toEntity(JSON_OBJETO);
    }

    // Cria pela API e devolve o id gerado pelo banco.
    private Object criar(String nome, String cpf) {
        ResponseEntity<Map<String, Object>> resposta = post(beneficiarioJson(nome, cpf));
        assertThat(resposta.getStatusCode().value()).isEqualTo(201);
        return resposta.getBody().get("idBeneficiario");
    }

    // ===================== POST =====================

    @Test
    @DisplayName("POST - deve criar beneficiário e retornar 201 com Location")
    void deveCriarBeneficiarioQuandoDadosValidos() {
        // ARRANGE
        String json = beneficiarioJson("Maria da Silva", CPF_MARIA);

        // ACT
        ResponseEntity<Map<String, Object>> resposta = post(json);

        // ASSERT: 201 com o endereço do recurso novo
        assertThat(resposta.getStatusCode().value()).isEqualTo(201);
        Map<String, Object> corpo = resposta.getBody();
        assertThat(corpo).isNotNull();
        assertThat(corpo.get("idBeneficiario")).isNotNull();
        assertThat(resposta.getHeaders().getLocation()).isNotNull();
        assertThat(resposta.getHeaders().getLocation().toString())
                .isEqualTo(URL + "/" + corpo.get("idBeneficiario"));

        // ASSERT: dados enviados e data de cadastro gerada pelo servidor
        assertThat(corpo.get("nome")).isEqualTo("Maria da Silva");
        assertThat(corpo.get("cpf")).isEqualTo(CPF_MARIA);
        assertThat(corpo.get("dataCadastro")).isEqualTo(LocalDate.now().toString());
    }

    @Test
    @DisplayName("POST - deve retornar 409 quando o CPF já está cadastrado")
    void deveRetornar409QuandoCpfDuplicado() {
        // ARRANGE: o primeiro cadastro passa
        criar("Maria da Silva", CPF_MARIA);

        // ACT: outro nome, mesmo CPF
        ResponseEntity<Map<String, Object>> resposta = post(beneficiarioJson("Maria Souza", CPF_MARIA));

        // ASSERT: 409 em Problem Details, com a mensagem em português
        assertThat(resposta.getStatusCode().value()).isEqualTo(409);
        Map<String, Object> corpo = resposta.getBody();
        assertThat(corpo).isNotNull();
        assertThat(corpo.get("status")).isEqualTo(409);
        assertThat(corpo.get("type")).isEqualTo(ERROS + "cpf-duplicado");
        assertThat(corpo.get("detail")).isEqualTo("Já existe um beneficiário com o CPF " + CPF_MARIA + ".");
        assertThat(corpo.get("instance")).isEqualTo(URL);

        // ASSERT: o segundo cadastro não foi gravado
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("POST - deve retornar 400 listando os campos inválidos")
    void deveRetornar400QuandoDadosInvalidos() {
        // ARRANGE: nome vazio e CPF com máscara (a API aceita só os 11 dígitos)
        String json = """
                {"nome": "", "cpf": "529.982.247-25"}
                """;

        // ACT
        ResponseEntity<Map<String, Object>> resposta = post(json);

        // ASSERT: 400 em Problem Details, um item em errors por campo, em ordem alfabética
        assertThat(resposta.getStatusCode().value()).isEqualTo(400);
        Map<String, Object> corpo = resposta.getBody();
        assertThat(corpo).isNotNull();
        assertThat(corpo.get("type")).isEqualTo(ERROS + "validacao");
        assertThat(corpo.get("errors")).isEqualTo(List.of(
                Map.of("field", "cpf", "message", "CPF inválido. Informe os 11 dígitos, sem pontos nem traço."),
                Map.of("field", "nome", "message", "O nome é obrigatório.")
        ));

        // ASSERT: nada foi gravado
        assertThat(repository.count()).isZero();
    }

    // ===================== GET =====================

    @Test
    @DisplayName("GET - deve retornar 404 quando o beneficiário não existe")
    void deveRetornar404QuandoBeneficiarioNaoExiste() {
        // ACT: tabela vazia, então o id 99999 não existe
        ResponseEntity<Map<String, Object>> resposta = get(99999);

        // ASSERT
        assertThat(resposta.getStatusCode().value()).isEqualTo(404);
        Map<String, Object> corpo = resposta.getBody();
        assertThat(corpo).isNotNull();
        assertThat(corpo.get("type")).isEqualTo(ERROS + "recurso-nao-encontrado");
        assertThat(corpo.get("detail")).isEqualTo("Beneficiário com id 99999 não encontrado.");
    }

    @Test
    @DisplayName("GET - deve filtrar por parte do nome e paginar o resultado")
    void deveFiltrarPorNomeEPaginarQuandoListar() {
        // ARRANGE: dois nomes com "mari" e um sem
        criar("Maria da Silva", CPF_MARIA);
        criar("Mariana Souza", CPF_MARIANA);
        criar("João Pereira", CPF_JOAO);

        // ACT: "MARI" em maiúsculas, uma pessoa por página, ordem por nome
        ResponseEntity<Map<String, Object>> resposta = cliente.get()
                .uri(URL + "?nome={nome}&page=0&size=1&sort=nome,asc", "MARI")
                .retrieve()
                .toEntity(JSON_OBJETO);

        // ASSERT: dois encontrados, sem diferenciar maiúsculas, em duas páginas
        assertThat(resposta.getStatusCode().value()).isEqualTo(200);
        Map<String, Object> corpo = resposta.getBody();
        assertThat(corpo).isNotNull();
        assertThat(corpo.get("totalElements")).isEqualTo(2);
        assertThat(corpo.get("totalPages")).isEqualTo(2);

        // ASSERT: a primeira página traz só a Maria, primeira em ordem alfabética
        List<?> conteudo = (List<?>) corpo.get("content");
        assertThat(conteudo).hasSize(1);
        Map<?, ?> primeiro = (Map<?, ?>) conteudo.get(0);
        assertThat(primeiro.get("nome")).isEqualTo("Maria da Silva");
    }

    // ===================== PUT =====================

    @Test
    @DisplayName("PUT - deve substituir os dados e manter id e data de cadastro")
    void deveSubstituirDadosQuandoPutValido() {
        // ARRANGE
        Object id = criar("Maria da Silva", CPF_MARIA);
        String json = """
                {"nome": "Maria da Silva Souza", "cpf": "%s", "endereco": "Rua Nova, 456 - Centro"}
                """.formatted(CPF_MARIA);

        // ACT: PUT sem telefone e sem situação de vulnerabilidade
        ResponseEntity<Map<String, Object>> resposta = cliente.put()
                .uri(URL + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .retrieve()
                .toEntity(JSON_OBJETO);

        // ASSERT: substituição total, então os campos ausentes viram null
        assertThat(resposta.getStatusCode().value()).isEqualTo(200);
        Map<String, Object> corpo = resposta.getBody();
        assertThat(corpo).isNotNull();
        assertThat(corpo.get("nome")).isEqualTo("Maria da Silva Souza");
        assertThat(corpo.get("endereco")).isEqualTo("Rua Nova, 456 - Centro");
        assertThat(corpo.get("telefone")).isNull();
        assertThat(corpo.get("situacaoVulnerabilidade")).isNull();

        // ASSERT: o PUT manteve o próprio CPF sem 409, e o id e a data não mudaram
        assertThat(corpo.get("cpf")).isEqualTo(CPF_MARIA);
        assertThat(corpo.get("idBeneficiario")).isEqualTo(id);
        assertThat(corpo.get("dataCadastro")).isEqualTo(LocalDate.now().toString());
    }

    // ===================== PATCH =====================

    @Test
    @DisplayName("PATCH - deve alterar só o telefone e preservar os outros campos")
    void devePreservarCamposNaoEnviadosQuandoPatch() {
        // ARRANGE
        Object id = criar("Maria da Silva", CPF_MARIA);

        // ACT: PATCH só com o telefone
        ResponseEntity<Map<String, Object>> resposta = cliente.patch()
                .uri(URL + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {"telefone": "11988887777"}
                        """)
                .retrieve()
                .toEntity(JSON_OBJETO);

        // ASSERT
        assertThat(resposta.getStatusCode().value()).isEqualTo(200);

        // ASSERT: relido do banco por um GET, para provar que a mudança foi gravada
        Map<String, Object> corpo = get(id).getBody();
        assertThat(corpo).isNotNull();
        assertThat(corpo.get("telefone")).isEqualTo("11988887777");
        assertThat(corpo.get("nome")).isEqualTo("Maria da Silva");
        assertThat(corpo.get("cpf")).isEqualTo(CPF_MARIA);
        assertThat(corpo.get("endereco")).isEqualTo("Rua das Flores, 123 - Centro");
        assertThat(corpo.get("situacaoVulnerabilidade")).isEqualTo("Renda familiar baixa");
    }

    // ===================== DELETE =====================

    @Test
    @DisplayName("DELETE - deve retornar 204 e o GET seguinte deve retornar 404")
    void deveRetornar204EDepois404QuandoExcluir() {
        // ARRANGE
        Object id = criar("Maria da Silva", CPF_MARIA);

        // ACT
        ResponseEntity<Void> resposta = cliente.delete()
                .uri(URL + "/{id}", id)
                .retrieve()
                .toBodilessEntity();

        // ASSERT: 204 sem corpo, e o recurso não existe mais
        assertThat(resposta.getStatusCode().value()).isEqualTo(204);
        assertThat(get(id).getStatusCode().value()).isEqualTo(404);
        assertThat(repository.count()).isZero();
    }
}
