package br.com.socialconnect.api.produtos.model;

/**
 * Categorias aceitas para um produto. Gravada no banco como texto
 * (@Enumerated(EnumType.STRING) na Entity), e a V2 tem um CHECK com estes
 * mesmos valores: incluir uma categoria nova exige uma migration nova.
 */
public enum CategoriaProduto {
    ALIMENTO, ROUPA, HIGIENE, OUTROS
}
