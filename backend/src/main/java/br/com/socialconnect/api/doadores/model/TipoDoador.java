package br.com.socialconnect.api.doadores.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Tipo de doador, com os valores da dinâmica da Aula 04 e do contrato.
 * <p>
 * O Doador grava o nome da constante como texto (@Enumerated(EnumType.STRING)),
 * e o CHECK da V3 aceita só estes dois nomes. Renomear uma constante exige
 * uma migration nova, senão os registros antigos deixam de ser lidos.
 * <p>
 * Swagger: com enumAsRef, o springdoc publica um schema TipoDoador separado
 * e o referencia nos DTOs, como o docs/openapi.yaml. Sem ele, a lista de
 * valores sairia repetida dentro de cada DTO.
 */
@Schema(name = "TipoDoador", description = "Pessoa física ou jurídica", enumAsRef = true)
public enum TipoDoador {
    PESSOA_FISICA,
    PESSOA_JURIDICA
}
