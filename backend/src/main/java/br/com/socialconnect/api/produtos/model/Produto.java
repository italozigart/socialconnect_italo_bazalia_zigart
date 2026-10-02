package br.com.socialconnect.api.produtos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Espelho da tabela produtos, criada pela migration V2.
 * <p>
 * O ddl-auto=validate confere tabela, colunas e tipos na subida. Tamanho,
 * NOT NULL e UNIQUE ele não compara, então length, nullable e unique abaixo
 * foram conferidos à mão contra o SQL da V2.
 */
@Entity
@Table(name = "produtos")
@Getter
@Setter
@NoArgsConstructor  // obrigatório: o JPA instancia a entidade por reflexão
@AllArgsConstructor // exigido pelo @Builder
@Builder            // usado no criar do ProdutoServiceImpl
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    private Long idProduto;

    @Column(nullable = false, unique = true, length = 150)
    private String nome;

    // STRING grava o nome da constante ("ALIMENTO"). Sem isso, o padrão seria
    // ORDINAL (0, 1, 2...), que muda de significado se a ordem do enum mudar
    // e não combina com a coluna VARCHAR(20) da V2.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoriaProduto categoria;

    @Column(name = "estoque_atual", nullable = false)
    private Integer estoqueAtual;

    @Column(name = "estoque_minimo", nullable = false)
    private Integer estoqueMinimo;

    @Column(name = "unidade_medida", nullable = false, length = 20)
    private String unidadeMedida;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDate dataCadastro;
}
