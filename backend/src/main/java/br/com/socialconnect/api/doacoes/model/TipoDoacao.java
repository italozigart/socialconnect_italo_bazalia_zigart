package br.com.socialconnect.api.doacoes.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Tipo de doação, com os valores do contrato (docs/openapi.yaml) e do
 * script de referência da Aula 05.
 * <p>
 * A Doacao grava o nome da constante como texto (@Enumerated(EnumType.STRING)),
 * e o CHECK da V4 aceita só estes três nomes. Renomear uma constante exige
 * uma migration nova, senão os registros antigos deixam de ser lidos.
 * <p>
 * Swagger: com enumAsRef, o springdoc publica um schema TipoDoacao separado e
 * o referencia nos DTOs e no filtro da listagem, como o TipoDoador.
 */
@Schema(name = "TipoDoacao", description = "Alimento, roupa ou financeira (dinheiro)", enumAsRef = true)
public enum TipoDoacao {
    ALIMENTO,
    ROUPA,
    FINANCEIRA
}
