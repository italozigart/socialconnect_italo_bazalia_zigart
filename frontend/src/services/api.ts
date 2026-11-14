import axios from 'axios'
import type { ProblemDetail } from '../types/problemDetail'

// Endereço da API em desenvolvimento. O front (Vite, porta 5173) e a API
// (Spring Boot, porta 8080) são origens diferentes: por isso a API tem o
// CorsConfig liberando http://localhost:5173.
const API_URL = 'http://localhost:8080/api/v1'

// Cliente HTTP único: as telas chamam api.get('/beneficiarios'), e o Axios
// junta com o baseURL.
const api = axios.create({
  baseURL: API_URL,
  headers: {
    // Como na aula. O Axios só manda este cabeçalho quando a requisição tem
    // corpo (POST, PUT e PATCH). No GET da listagem ele fica de fora, e o GET
    // continua sendo uma requisição simples, sem preflight. No POST do
    // cadastro ele vai, e o navegador faz o preflight (OPTIONS) antes.
    'Content-Type': 'application/json',
  },
  // Parâmetro com lista (Etapa 12). A listagem manda o sort duas vezes,
  // ?sort=nome,asc&sort=idBeneficiario,asc, que é o formato que o Spring lê.
  // Sem esta linha, o Axios mandaria sort[]=nome,asc&sort[]=..., o Spring não
  // acharia nenhum parâmetro "sort" e usaria a ordem padrão (nome), em
  // silêncio: o clique no cabeçalho não mudaria nada.
  paramsSerializer: { indexes: null },
})

export default api

// Transforma o erro do Axios no texto que aparece na tela.
// - A API respondeu (400, 404, 500...): o corpo é Problem Details, e o detail
//   já explica o problema em português.
// - Sem resposta: API parada, endereço errado ou bloqueio de CORS. Nos três
//   casos o Axios só recebe "Network Error", porque o navegador não entrega
//   nada ao JavaScript; o motivo verdadeiro fica no console (F12).
export function mensagemDeErro(erro: unknown): string {
  if (axios.isAxiosError<ProblemDetail>(erro)) {
    if (erro.response) {
      return erro.response.data?.detail ?? `A API respondeu com o status ${erro.response.status}.`
    }
    return `Não foi possível acessar a API (${API_URL}). Verifique se o back-end está rodando.`
  }
  return 'Erro inesperado ao acessar a API.'
}

// Corpo Problem Details da resposta de erro (Etapa 11). O formulário usa o
// status para separar o 400 (erros em errors, campo a campo) do 409 (CPF já
// cadastrado). Devolve null quando não houve resposta (API parada, CORS) ou
// quando o corpo não tem o formato do Problem Details (status numérico e a
// lista errors), e aí vale a mensagemDeErro.
export function problemDetailDe(erro: unknown): ProblemDetail | null {
  if (axios.isAxiosError<ProblemDetail>(erro)) {
    const corpo = erro.response?.data
    if (typeof corpo?.status === 'number' && Array.isArray(corpo.errors)) {
      return corpo
    }
  }
  return null
}
