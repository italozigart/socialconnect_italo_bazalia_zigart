// Beneficiário como a API devolve: igual ao schema BeneficiarioResponse do
// docs/openapi.yaml, com os nomes em camelCase dos records Java.
//
// Diferença em relação à aula: telefone, endereço e situação são opcionais no
// cadastro e chegam como null quando não foram informados. Com "string", o
// TypeScript deixaria usar o valor sem conferir; com "string | null", ele
// obriga a tratar o null (na tela, "não informado").
export interface Beneficiario {
  idBeneficiario: number
  nome: string
  cpf: string // 11 dígitos, sem máscara
  telefone: string | null
  endereco: string | null
  situacaoVulnerabilidade: string | null
  dataCadastro: string // aaaa-mm-dd (LocalDate no Java)
}
