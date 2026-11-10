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

    // Passa se o contexto inteiro sobe: beans, Flyway (todas as migrations
    // aplicadas no H2, V1 e V3 na Etapa 7) e a validação do Hibernate
    // (ddl-auto=validate) conferindo cada Entity com a sua tabela.
    @Test
    void contextLoads() {
    }

}
