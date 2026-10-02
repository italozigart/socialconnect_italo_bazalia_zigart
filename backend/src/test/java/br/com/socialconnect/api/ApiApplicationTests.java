package br.com.socialconnect.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Sem o @Import(TestcontainersConfiguration.class) gerado pelo Initializr:
// o contexto sobe com o H2 do application.properties, sem precisar de Docker.
@SpringBootTest
class ApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
