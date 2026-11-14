// Formatação dos dados do beneficiário para a tela. Saiu da BeneficiarioList
// na Etapa 12, porque a confirmação de exclusão também mostra o CPF.

// O CPF chega com 11 dígitos; na tela, com a máscara 000.000.000-00.
export function formatarCpf(cpf: string): string {
  return cpf.replace(/^(\d{3})(\d{3})(\d{3})(\d{2})$/, '$1.$2.$3-$4')
}

// A data chega como aaaa-mm-dd e vai para a tela como dd/mm/aaaa. Sem new
// Date(): ele leria "2026-11-12" como meia-noite em UTC, e no Brasil (UTC-3)
// a tela mostraria 11/11.
export function formatarData(data: string): string {
  const [ano, mes, dia] = data.split('-')
  return `${dia}/${mes}/${ano}`
}
