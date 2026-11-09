package br.com.socialconnect.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// Sem o @Import(TestcontainersConfiguration.class) gerado pelo Initializr: ele
// subia um PostgreSQL no Docker, e o PC do laboratório não tem Docker. O
// contexto sobe com o H2 em memória do application.properties, ajustado pelo
// perfil "test" (src/test/resources/application-test.properties).
@SpringBootTest
@ActiveProfiles("test")
class ApiApplicationTests {

    // Passa se o contexto inteiro sobe: beans, Flyway (V1 aplicada no H2) e a
    // validação do Hibernate (ddl-auto=validate) conferindo a Entity com a tabela.
    @Test
    void contextLoads() {
    }

}
