package br.com.socialconnect.api.beneficiarios.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * Espelho da tabela beneficiarios, criada pela migration V1.
 * <p>
 * O ddl-auto=validate confere se a tabela e as colunas existem e se os tipos
 * são compatíveis. Tamanho e NOT NULL ele não compara (e UNIQUE só com uma
 * configuração extra, desligada por padrão), então length, nullable e unique
 * abaixo precisam ser conferidos à mão contra o SQL da V1.
 */
@Entity
@Table(name = "beneficiarios")
@Getter
@Setter
@NoArgsConstructor  // obrigatório: o JPA instancia a entidade por reflexão
@AllArgsConstructor // exigido pelo @Builder
@Builder            // usado no toEntity do BeneficiarioService
public class Beneficiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_beneficiario")
    private Long idBeneficiario;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(length = 20)
    private String telefone;

    @Column(length = 255)
    private String endereco;

    @Column(name = "situacao_vulnerabilidade", length = 500)
    private String situacaoVulnerabilidade;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDate dataCadastro;
}
