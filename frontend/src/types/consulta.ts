// Consulta da listagem (Etapa 12): a página e a ordem que a tela pede à API.
// Fica no App, e não na BeneficiarioList: a lista sai da tela durante o
// cadastro e a edição, e, ao voltar, aparece na mesma página e na mesma ordem.

// Colunas que a tabela deixa ordenar. O sort da API usa o nome do atributo da
// Entity (dataCadastro), e não o da coluna do banco (data_cadastro). O
// telefone fica de fora: como ele pode ser null, quem não tem telefone iria
// para o começo ou para o fim conforme o banco.
export type CampoOrdenavel = 'nome' | 'cpf' | 'dataCadastro'

export type Direcao = 'asc' | 'desc'

export interface Ordenacao {
  campo: CampoOrdenavel
  direcao: Direcao
}

export interface ConsultaBeneficiarios {
  // Começa em 0, como o page da API. A Pagination do MUI começa em 1, e a
  // BeneficiarioList faz a conversão.
  pagina: number
  ordenacao: Ordenacao
}

// A mesma ordem da API quando o sort não vem: @PageableDefault(sort = "nome").
export const CONSULTA_INICIAL: ConsultaBeneficiarios = {
  pagina: 0,
  ordenacao: { campo: 'nome', direcao: 'asc' },
}
