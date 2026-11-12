package br.com.socialconnect.api.doacoes.model;

import br.com.socialconnect.api.doadores.model.Doador;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Espelho da tabela doacoes, criada pela migration V4.
 * <p>
 * Mesmo cuidado do Doador: o ddl-auto=validate confere se a tabela e as
 * colunas existem e se os tipos são compatíveis, mas não compara tamanho,
 * NOT NULL nem as restrições (CHECK, FK). Por isso length, precision, scale
 * e nullable abaixo foram conferidos à mão contra o SQL da V4.
 * <p>
 * Diferença em relação ao repositório do professor: lá a doação guarda só o
 * número do doador (Long idDoador). Aqui ela aponta para a entidade Doador
 * (@ManyToOne): a FK do banco vira uma referência entre objetos no Java.
 * <p>
 * Sem @Data, @ToString e @EqualsAndHashCode do Lombok: eles leriam o doador
 * (que é LAZY) sem ninguém pedir, fora de uma transação.
 */
@Entity
@Table(name = "doacoes")
@Getter
@Setter
@NoArgsConstructor  // obrigatório: o JPA instancia a entidade por reflexão
@AllArgsConstructor // exigido pelo @Builder
@Builder            // usado no criar do DoacaoService
public class Doacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_doacao")
    private Long idDoacao;

    // Muitas doações para um doador. A coluna id_doador é a FK da V4.
    // - LAZY: carregar uma doação não busca o doador junto. O padrão do
    //   @ManyToOne é EAGER, e a listagem faria um SELECT a mais por doador.
    // - optional = false: toda doação tem doador (NOT NULL na V4).
    // - O nome da FK aqui só documenta: com ddl-auto=validate, quem cria a FK
    //   é a migration, e o Hibernate não confere restrições.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_doador", nullable = false,
            foreignKey = @ForeignKey(name = "fk_doacoes_doador"))
    private Doador doador;

    @Column(name = "data_doacao", nullable = false)
    private LocalDate dataDoacao;

    // BigDecimal, e não double: dinheiro com centavos exatos, como o DECIMAL(10,2).
    @Column(precision = 10, scale = 2)
    private BigDecimal valor;

    // STRING grava o nome da constante ("ALIMENTO"), como no Doador: combina
    // com a coluna VARCHAR(20) e com o CHECK da V4.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoDoacao tipo;

    @Column(length = 500)
    private String descricao;
}
