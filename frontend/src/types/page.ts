// Página da listagem: igual ao schema PageBeneficiarioResponse do
// docs/openapi.yaml. Genérico (T) para servir também às listagens de doações
// e de doadores: Page<Beneficiario>, Page<Doacao>...
//
// Diferença em relação à aula: lá o tipo tem number e size; o contrato tem
// first e last. O JSON da API traz os dois pares e mais alguns campos, porque
// o Spring serializa o PageImpl inteiro (é o WARN do PageImpl no log). O tipo
// fica só com o que o contrato garante; o TypeScript ignora o resto.
export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
}
