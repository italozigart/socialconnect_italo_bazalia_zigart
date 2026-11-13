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
    // continua sendo uma requisição simples, sem preflight.
    'Content-Type': 'application/json',
  },
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
