import { useEffect, useRef, useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Alert, Box, Button, Stack, TextField, Typography } from '@mui/material'
import api, { mensagemDeErro, problemDetailDe } from '../services/api'
import { beneficiarioSchema } from '../schemas/beneficiarioSchema'
import type { BeneficiarioFormData, BeneficiarioFormValues } from '../schemas/beneficiarioSchema'
import type { Beneficiario } from '../types/beneficiario'

// Com o verbatimModuleSyntax ligado, tipo se importa com "import type", em
// linha separada. O import da aula, { beneficiarioSchema, BeneficiarioFormData }
// numa linha só, quebra o npm run build.

// Campos na ordem da tela. Servem para ligar os erros do Problem Details
// (errors[].field) aos campos e para dar foco ao primeiro campo com erro.
const CAMPOS = ['nome', 'cpf', 'telefone', 'endereco', 'situacaoVulnerabilidade'] as const
type Campo = (typeof CAMPOS)[number]

function ehCampoDoFormulario(nome: string): nome is Campo {
  return (CAMPOS as readonly string[]).includes(nome)
}

// Todos os campos começam com "", e não undefined: assim o TextField já nasce
// controlado pelo valor inicial, e o schema recebe sempre texto.
const VALORES_INICIAIS: BeneficiarioFormValues = {
  nome: '',
  cpf: '',
  telefone: '',
  endereco: '',
  situacaoVulnerabilidade: '',
}

// Edição (Etapa 12): os campos começam com os dados da tabela. O que a API
// devolve como null (não informado) vira "", pelo mesmo motivo de cima.
function valoresDe(beneficiario: Beneficiario): BeneficiarioFormValues {
  return {
    nome: beneficiario.nome,
    cpf: beneficiario.cpf,
    telefone: beneficiario.telefone ?? '',
    endereco: beneficiario.endereco ?? '',
    situacaoVulnerabilidade: beneficiario.situacaoVulnerabilidade ?? '',
  }
}

// Erro que não é de um campo (API parada, 500...), mostrado no alerta do topo.
// focar: se o alerta recebe o foco (quando nenhum campo recebeu).
interface ErroGeral {
  mensagem: string
  focar: boolean
}

interface BeneficiarioFormProps {
  // Sem beneficiário, o formulário abre vazio e cadastra (POST); com ele,
  // abre preenchido e salva as alterações (PUT).
  beneficiario?: Beneficiario
  onSalvo: (beneficiario: Beneficiario) => void
  onCancelar: () => void
}

// Cadastro e edição de beneficiário (Aula 10): React Hook Form + Zod + MUI.
// - Antes do envio, o zodResolver valida tudo; com erro, o RHF não chama o
//   enviar, mostra as mensagens e dá foco ao primeiro campo inválido.
// - Depois do envio: 201 (POST) ou 200 (PUT) avisam o App (mensagem e volta
//   para a listagem); 400 mostra cada item do errors no seu campo; 409 mostra
//   o detail no CPF; o resto (404, API parada, 500) aparece num alerta no topo.
export function BeneficiarioForm({ beneficiario, onSalvo, onCancelar }: BeneficiarioFormProps) {
  const {
    register,
    handleSubmit,
    setError,
    setFocus,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(beneficiarioSchema),
    defaultValues: beneficiario ? valoresDe(beneficiario) : VALORES_INICIAIS,
  })
  const [erroGeral, setErroGeral] = useState<ErroGeral | null>(null)
  const alertaRef = useRef<HTMLDivElement>(null)

  // O botão de enviar fica desabilitado durante o envio, e o navegador tira o
  // foco dele (vai para o body). Num erro sem campo, o foco vai para o alerta:
  // o leitor de tela lê a mensagem, e o próximo Tab segue para o Nome. Cada
  // erro é um objeto novo, então o efeito roda uma vez por erro, e não a cada
  // tecla digitada.
  useEffect(() => {
    if (erroGeral?.focar) {
      alertaRef.current?.focus()
    }
  }, [erroGeral])

  // Recebe os dados já validados e transformados pelo schema (sem espaços nas
  // pontas e sem os opcionais vazios). O RHF deixa o isSubmitting em true até
  // esta função terminar, e o botão fica em "Cadastrando..." ou "Salvando..."
  // nesse tempo.
  //
  // PUT (Etapa 12): substituição total, com o mesmo corpo do POST. Campo
  // opcional apagado na tela não vai no JSON, e a API grava null: apagar o
  // endereço na edição apaga o endereço no banco.
  async function enviar(dados: BeneficiarioFormData) {
    setErroGeral(null)
    try {
      const resposta = beneficiario
        ? await api.put<Beneficiario>(`/beneficiarios/${beneficiario.idBeneficiario}`, dados)
        : await api.post<Beneficiario>('/beneficiarios', dados)
      onSalvo(resposta.data)
    } catch (erro) {
      tratarErroDaApi(erro)
    }
  }

  function tratarErroDaApi(erro: unknown) {
    const problema = problemDetailDe(erro)

    // 409: o único conflito do POST e do PUT é o CPF de outro beneficiário.
    // O detail já diz qual.
    if (problema?.status === 409) {
      setError('cpf', { type: 'server', message: problema.detail }, { shouldFocus: true })
      return
    }

    // 400 com a lista errors: cada item vira o erro do seu campo. Acontece com
    // o que só a API confere (dígitos do CPF, DDD e início do telefone). Um
    // campo pode vir mais de uma vez no errors, e o setError guarda uma
    // mensagem só; por isso as mensagens de um mesmo campo vão juntas. O erro
    // some quando a pessoa corrige o campo, porque o RHF valida de novo a
    // cada mudança depois da primeira tentativa de envio.
    if (problema?.status === 400 && problema.errors.length > 0) {
      const mensagensPorCampo = new Map<Campo, string[]>()
      const semCampo: string[] = []
      for (const { field, message } of problema.errors) {
        if (ehCampoDoFormulario(field)) {
          mensagensPorCampo.set(field, [...(mensagensPorCampo.get(field) ?? []), message])
        } else {
          semCampo.push(message)
        }
      }
      for (const [campo, mensagens] of mensagensPorCampo) {
        setError(campo, { type: 'server', message: mensagens.join(' ') })
      }
      // Foco no primeiro campo com erro na ordem da tela (o errors da API vem
      // em ordem alfabética).
      const primeiroComErro = CAMPOS.find((campo) => mensagensPorCampo.has(campo))
      if (primeiroComErro) {
        setFocus(primeiroComErro)
      }
      if (semCampo.length > 0) {
        setErroGeral({ mensagem: semCampo.join(' '), focar: primeiroComErro === undefined })
      }
      return
    }

    // 404 no PUT: alguém excluiu o beneficiário depois que a tabela carregou.
    if (problema?.status === 404) {
      setErroGeral({
        mensagem: `${problema.detail} Ele pode ter sido excluído. Use Cancelar para voltar à lista.`,
        focar: true,
      })
      return
    }

    // API parada, CORS, 500 ou 400 sem errors (JSON malformado).
    console.error('Erro ao salvar beneficiário:', erro)
    setErroGeral({ mensagem: mensagemDeErro(erro), focar: true })
  }

  // Textos do botão de enviar, conforme a tela.
  const textoDoBotao = beneficiario
    ? isSubmitting
      ? 'Salvando...'
      : 'Salvar alterações'
    : isSubmitting
      ? 'Cadastrando...'
      : 'Cadastrar'

  // Acessibilidade:
  // - o label do TextField vira um <label for="..."> ligado ao campo;
  // - com error, o campo ganha aria-invalid="true", e o helperText (a mensagem)
  //   fica ligado a ele pelo aria-describedby: o leitor de tela lê a mensagem
  //   junto com o campo;
  // - required põe o * no label e o atributo required no campo. O noValidate
  //   do formulário desliga os balões do navegador: quem valida é o Zod;
  // - Enter num campo de uma linha envia o formulário, e o Tab chega a todos
  //   os campos e aos dois botões, então dá para cadastrar só com o teclado.
  //
  // O {...register('nome')} passa name, onChange, onBlur e ref. No TextField,
  // o ref cai na <div> que envolve o campo; o RHF procura o <input> dentro
  // dela, e é nele que lê o valor e dá o foco.
  return (
    <Box component="form" onSubmit={handleSubmit(enviar)} noValidate autoComplete="off" sx={{ maxWidth: 600 }}>
      <Typography variant="body2" sx={{ color: 'text.secondary' }}>
        Campos com * são obrigatórios.
      </Typography>

      {/* role="alert" (padrão do Alert): o leitor de tela anuncia o erro.
          tabIndex={-1}: o alerta pode receber o foco pelo código, mas fica
          fora da ordem do Tab. */}
      {erroGeral !== null && (
        <Alert ref={alertaRef} tabIndex={-1} severity="error" sx={{ mt: 2 }}>
          {erroGeral.mensagem}
        </Alert>
      )}

      <TextField
        label="Nome"
        required
        {...register('nome')}
        error={!!errors.nome}
        helperText={errors.nome?.message}
        fullWidth
        margin="normal"
      />

      <TextField
        label="CPF"
        required
        {...register('cpf')}
        error={!!errors.cpf}
        helperText={errors.cpf?.message ?? 'Só os 11 dígitos, sem pontos nem traço.'}
        fullWidth
        margin="normal"
        slotProps={{ htmlInput: { inputMode: 'numeric' } }}
      />

      <TextField
        label="Telefone"
        {...register('telefone')}
        error={!!errors.telefone}
        helperText={errors.telefone?.message ?? 'DDD e número, só dígitos (ex.: 11999999999).'}
        fullWidth
        margin="normal"
        slotProps={{ htmlInput: { inputMode: 'numeric' } }}
      />

      <TextField
        label="Endereço"
        {...register('endereco')}
        error={!!errors.endereco}
        helperText={errors.endereco?.message}
        fullWidth
        margin="normal"
      />

      <TextField
        label="Situação de vulnerabilidade"
        {...register('situacaoVulnerabilidade')}
        error={!!errors.situacaoVulnerabilidade}
        helperText={errors.situacaoVulnerabilidade?.message}
        fullWidth
        margin="normal"
        multiline
        rows={3}
      />

      {/* loading (MUI 9): desabilita o botão e mostra o indicador de progresso
          no lugar do ícone; o texto muda durante o envio, como na aula. */}
      <Stack direction="row" spacing={2} sx={{ mt: 2 }}>
        <Button type="submit" variant="contained" loading={isSubmitting} loadingPosition="start">
          {textoDoBotao}
        </Button>
        <Button type="button" variant="outlined" onClick={onCancelar} disabled={isSubmitting}>
          Cancelar
        </Button>
      </Stack>
    </Box>
  )
}
