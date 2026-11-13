package br.com.socialconnect.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS: libera o front-end (React com Vite, em http://localhost:5173) a chamar
 * a API (http://localhost:8080). Base: Aula 09.
 * <p>
 * Origem é esquema + host + porta. O front na 5173 e a API na 8080 são origens
 * diferentes, e o navegador só entrega ao JavaScript a resposta de outra origem
 * se ela vier com o cabeçalho Access-Control-Allow-Origin. Sem esta classe, o
 * GET da listagem chega à API e volta 200, mas o navegador bloqueia a leitura:
 * o erro aparece no console (F12), e não na resposta.
 * <p>
 * Antes de um POST com JSON, um PUT, um PATCH ou um DELETE, o navegador manda
 * um preflight: OPTIONS com os cabeçalhos Origin e Access-Control-Request-Method.
 * Quem responde é o próprio Spring, com as regras abaixo, sem chamar o
 * Controller. O GET da listagem é uma requisição simples, sem preflight.
 * <p>
 * Regras de /api/**:
 * <ul>
 *   <li>allowedOrigins: só a origem do Vite. http://127.0.0.1:5173 ou
 *       http://localhost:5174 são outras origens e são recusadas;</li>
 *   <li>allowedMethods: os cinco métodos que a API usa. O OPTIONS do preflight
 *       não precisa estar na lista;</li>
 *   <li>cabeçalhos: o padrão do Spring libera todos, como o Content-Type do
 *       JSON e, mais adiante, o Authorization do JWT;</li>
 *   <li>maxAge: o padrão do Spring (1800 s). O navegador guarda a resposta do
 *       preflight de cada URL por até 30 minutos e, nesse tempo, não repete o
 *       OPTIONS para ela.</li>
 * </ul>
 * Diferença em relação à aula: sem allowCredentials(true). A API não usa
 * cookie nem sessão, então não há credencial para o navegador enviar. O JWT
 * vai no cabeçalho Authorization, que não depende dessa opção.
 * <p>
 * Origem ou método fora das regras: o Spring responde 403 com o texto
 * "Invalid CORS request". Essa resposta sai antes do Controller, por isso não
 * passa pelo GlobalExceptionHandler e não vem em Problem Details.
 * <p>
 * O Swagger UI e o console do H2 não dependem desta classe: eles rodam na
 * própria porta 8080, então as requisições são da mesma origem.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE");
    }
}
