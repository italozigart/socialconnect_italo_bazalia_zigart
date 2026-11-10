package br.com.socialconnect.api.doadores.model;

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

/**
 * Espelho da tabela doadores, criada pela migration V3.
 * <p>
 * Mesmo cuidado do Beneficiario: o ddl-auto=validate confere se a tabela e as
 * colunas existem e se os tipos são compatíveis, mas não compara tamanho nem
 * NOT NULL. Por isso length e nullable abaixo foram conferidos à mão contra
 * o SQL da V3.
 */
@Entity
@Table(name = "doadores")
@Getter
@Setter
@NoArgsConstructor  // obrigatório: o JPA instancia a entidade por reflexão
@AllArgsConstructor // exigido pelo @Builder
@Builder            // usado no criar do DoadorService
public class Doador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_doador")
    private Long idDoador;

    @Column(nullable = false, length = 200)
    private String nome;

    // STRING grava o nome da constante ("PESSOA_FISICA"). Sem isso, o padrão
    // seria ORDINAL (0, 1), que muda de sentido se alguém reordenar o enum e
    // não combina com a coluna VARCHAR(20) nem com o CHECK da V3.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoDoador tipo;

    @Column(length = 254)
    private String email;

    @Column(length = 20)
    private String telefone;
}
