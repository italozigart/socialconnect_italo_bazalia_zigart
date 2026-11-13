// Corpo de toda resposta de erro da API (Problem Details, RFC 7807): igual ao
// schema ProblemDetail do docs/openapi.yaml. O detail já vem em português.
export interface ErroCampo {
  field: string
  message: string
}

export interface ProblemDetail {
  type: string
  title: string
  status: number
  detail: string
  instance: string
  timestamp: string
  errors: ErroCampo[] // preenchida só nos 400 de validação
}
