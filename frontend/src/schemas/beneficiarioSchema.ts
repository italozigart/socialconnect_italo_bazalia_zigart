import { z } from 'zod'

// Schema do formulário de cadastro (Aula 10), com as mesmas regras e as mesmas
// mensagens do BeneficiarioRequestDTO e do messages.properties da API.
//
// Diferenças em relação ao schema da aula:
// - nome sem min(3): a API não tem tamanho mínimo, só @NotBlank e no máximo 150;
// - situacaoVulnerabilidade, que a aula não tem e a API aceita (até 500);
// - campo vazio: o TextField entrega "" (e não undefined) quando a pessoa não
//   digita nada. Com .regex(...).optional(), como na aula, o telefone vazio
//   seria recusado, porque o optional() só deixa passar undefined. Aqui o ""
//   passa nos campos opcionais e vira undefined, e o JSON sai sem o campo,
//   que a API grava como null;
// - trim(): os espaços nas pontas saem antes de validar e de enviar, e um nome
//   só com espaços conta como vazio, como no @NotBlank da API.
//
// O que só a API confere (a diferença que fica registrada no PR): os dígitos
// verificadores do CPF (@CPF) e, no telefone, o DDD (de 11 a 99) e o início
// do número (fixo de 2 a 5, celular com 9). Nesses casos o POST volta 400, e
// o formulário mostra a mensagem do Problem Details embaixo do campo.

const vazioViraUndefined = (valor: string) => (valor === '' ? undefined : valor)

export const beneficiarioSchema = z.object({
  nome: z
    .string()
    .trim()
    .min(1, 'O nome é obrigatório.')
    .max(150, 'O nome deve ter no máximo 150 caracteres.'),

  cpf: z
    .string()
    .trim()
    .min(1, 'O CPF é obrigatório.')
    .regex(/^\d{11}$/, 'CPF inválido. Informe os 11 dígitos, sem pontos nem traço.'),

  // Opcional: vazio passa; preenchido, só dígitos, 10 ou 11 (a regra ^\d{10,11}$
  // da @Telefone e do contrato).
  telefone: z
    .string()
    .trim()
    .refine(
      (valor) => valor === '' || /^\d{10,11}$/.test(valor),
      'Telefone inválido. Informe DDD e número, só dígitos: 10 para fixo ou 11 para celular (ex.: 11999999999).',
    )
    .transform(vazioViraUndefined),

  endereco: z
    .string()
    .trim()
    .max(255, 'O endereço deve ter no máximo 255 caracteres.')
    .transform(vazioViraUndefined),

  situacaoVulnerabilidade: z
    .string()
    .trim()
    .max(500, 'A situação de vulnerabilidade deve ter no máximo 500 caracteres.')
    .transform(vazioViraUndefined),
})

// Dois tipos, porque o schema transforma os valores:
// - BeneficiarioFormValues: o que os campos guardam enquanto a pessoa digita
//   (sempre texto, entrada do schema);
// - BeneficiarioFormData: o que vai no POST (saída do schema), no formato do
//   BeneficiarioRequest do contrato. É o z.infer da aula.
export type BeneficiarioFormValues = z.input<typeof beneficiarioSchema>
export type BeneficiarioFormData = z.output<typeof beneficiarioSchema>
