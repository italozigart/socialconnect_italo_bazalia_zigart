import { useEffect, useRef, useState } from 'react'
import type { Dispatch, SetStateAction } from 'react'
import {
  Alert,
  Box,
  Button,
  Pagination,
  Paper,
  Skeleton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TableSortLabel,
  Typography,
  useMediaQuery,
} from '@mui/material'
import { useTheme } from '@mui/material/styles'
import type { SxProps, Theme } from '@mui/material/styles'
import PersonOffOutlinedIcon from '@mui/icons-material/PersonOffOutlined'
import api, { mensagemDeErro } from '../services/api'
import { somenteLeitorDeTela } from '../utils/acessibilidade'
import { formatarCpf, formatarData } from '../utils/formatacao'
import type { Beneficiario } from '../types/beneficiario'
import type { CampoOrdenavel, ConsultaBeneficiarios, Direcao, Ordenacao } from '../types/consulta'
import type { Page } from '../types/page'

// Ícone importado pelo caminho (@mui/icons-material/PersonOffOutlined), e não
// pela lista do pacote, como na aula ({ Edit } from '@mui/icons-material'): o
// resultado é o mesmo, mas o npm run dev não precisa processar os mais de 2
// mil ícones do pacote para achar um.

// Itens por página: o size da API.
const TAMANHO_PAGINA = 10

// Linhas cinza (Skeleton) enquanto a página carrega.
const LINHAS_DO_ESQUELETO = 5

// Mobile-first: no celular (menos de 600px), a tabela mostra só Nome e CPF,
// e cabe na tela sem rolar para o lado. O Telefone aparece a partir de
// 600px (sm), e a Data de cadastro a partir de 900px (md). Os dois continuam
// na tela de edição.
const SO_A_PARTIR_DO_SM = { display: { xs: 'none', sm: 'table-cell' } } as const
const SO_A_PARTIR_DO_MD = { display: { xs: 'none', md: 'table-cell' } } as const

const DESCRICAO_DA_ORDEM: Record<CampoOrdenavel, Record<Direcao, string>> = {
  nome: { asc: 'nome, de A a Z', desc: 'nome, de Z a A' },
  cpf: { asc: 'CPF, do menor para o maior', desc: 'CPF, do maior para o menor' },
  dataCadastro: {
    asc: 'data de cadastro, dos mais antigos para os mais novos',
    desc: 'data de cadastro, dos mais novos para os mais antigos',
  },
}

// Identifica uma busca: muda quando muda a página, a ordem ou a recarga.
function chaveDaBusca(pagina: number, ordenacao: Ordenacao, recarga: number): string {
  return `${pagina}|${ordenacao.campo}|${ordenacao.direcao}|${recarga}`
}

// O que a última busca trouxe, com a chave da busca que o pediu.
interface Resultado {
  chave: string
  pagina: Page<Beneficiario> | null // null quando deu erro
  erro: string | null
}

interface BeneficiarioListProps {
  consulta: ConsultaBeneficiarios
  onMudarConsulta: Dispatch<SetStateAction<ConsultaBeneficiarios>>
}

// Listagem de beneficiários com o MUI (Aula 10, Design Studio): tabela com
// ordenação pelo cabeçalho, paginação, Skeleton no carregamento e mensagem
// com ícone na lista vazia.
export function BeneficiarioList({ consulta, onMudarConsulta }: BeneficiarioListProps) {
  const { pagina, ordenacao } = consulta
  // Soma 1 a cada "Tentar de novo": busca a mesma página de novo.
  const [recarga, setRecarga] = useState(0)
  const [resultado, setResultado] = useState<Resultado | null>(null)
  const alertaRef = useRef<HTMLDivElement>(null)
  // No celular, a paginação mostra menos números para caber numa linha.
  // noSsr: o valor certo já na primeira renderização (sem servidor, não há
  // por que começar com false e trocar depois).
  const telaPequena = useMediaQuery(useTheme().breakpoints.down('sm'), { noSsr: true })

  // "Carregando" não é um useState: a tela está carregando enquanto o
  // resultado guardado não for o da busca atual. Assim ele nunca fica
  // esquecido em true (um clique na página que já está aberta não muda a
  // chave e não busca nada) nem em false (a busca nova muda a chave antes de
  // começar).
  const carregando = resultado?.chave !== chaveDaBusca(pagina, ordenacao, recarga)
  const { campo, direcao } = ordenacao

  useEffect(() => {
    // A resposta de uma busca que já foi trocada por outra (clique rápido em
    // outra página, ou o StrictMode, que em desenvolvimento roda o efeito duas
    // vezes) é descartada.
    let ignorar = false
    const ordemAtual: Ordenacao = { campo, direcao }

    // Dois sort: o da coluna e o idBeneficiario, na mesma direção, como
    // desempate. Sem ele, beneficiários com a mesma data de cadastro (todos os
    // do dia) ficariam numa ordem que o banco escolhe a cada consulta, e um
    // deles poderia aparecer em duas páginas e outro em nenhuma.
    api
      .get<Page<Beneficiario>>('/beneficiarios', {
        params: {
          page: pagina,
          size: TAMANHO_PAGINA,
          sort: [`${campo},${direcao}`, `idBeneficiario,${direcao}`],
        },
      })
      .then((resposta) => {
        if (ignorar) {
          return
        }
        const dados = resposta.data
        // Página depois da última (ex.: alguém excluiu pelo Swagger o único
        // beneficiário da última página): a busca vai para a última que existe.
        if (dados.content.length === 0 && pagina > 0 && dados.totalPages > 0) {
          onMudarConsulta((anterior) => ({ ...anterior, pagina: dados.totalPages - 1 }))
          return
        }
        setResultado({ chave: chaveDaBusca(pagina, ordemAtual, recarga), pagina: dados, erro: null })
      })
      .catch((e: unknown) => {
        if (!ignorar) {
          console.error('Erro ao buscar beneficiários:', e)
          setResultado({ chave: chaveDaBusca(pagina, ordemAtual, recarga), pagina: null, erro: mensagemDeErro(e) })
        }
      })

    return () => {
      ignorar = true
    }
  }, [pagina, campo, direcao, recarga, onMudarConsulta])

  // Durante o carregamento, a paginação e o total continuam os da página
  // anterior: a Pagination não some da tela, e o foco fica no número clicado.
  const dados = resultado?.pagina ?? null
  const erro = carregando ? null : (resultado?.erro ?? null)
  const vazio = !carregando && dados !== null && dados.content.length === 0

  // Num erro, o foco vai para o alerta (como no formulário da Etapa 11): o
  // botão que a pessoa tinha clicado (página ou cabeçalho) sai da tela junto
  // com a tabela.
  useEffect(() => {
    if (erro !== null) {
      alertaRef.current?.focus()
    }
  }, [erro])

  // Clique no cabeçalho: na coluna que já ordena, inverte a direção; em outra
  // coluna, começa pela ordem crescente. A ordem nova sempre volta para a
  // primeira página.
  function ordenarPor(novoCampo: CampoOrdenavel) {
    const novaDirecao: Direcao = novoCampo === campo && direcao === 'asc' ? 'desc' : 'asc'
    onMudarConsulta({ pagina: 0, ordenacao: { campo: novoCampo, direcao: novaDirecao } })
  }

  // A Pagination numera a partir de 1, e o page da API, a partir de 0.
  function mudarPagina(numero: number) {
    if (numero - 1 !== pagina) {
      onMudarConsulta((anterior) => ({ ...anterior, pagina: numero - 1 }))
    }
  }

  function tentarDeNovo() {
    setRecarga((anterior) => anterior + 1)
  }

  // Texto que o leitor de tela anuncia a cada busca (role="status"), e o
  // resumo que aparece em cima da tabela.
  const resumo =
    dados === null || dados.content.length === 0
      ? ''
      : `${dados.totalElements} ${dados.totalElements === 1 ? 'beneficiário' : 'beneficiários'}, ` +
        `em ordem de ${DESCRICAO_DA_ORDEM[campo][direcao]}. Página ${pagina + 1} de ${dados.totalPages}.`
  let anuncio = resumo
  if (carregando) {
    anuncio = 'Carregando beneficiários...'
  } else if (erro !== null) {
    anuncio = '' // o alerta de erro já é anunciado (role="alert")
  } else if (vazio) {
    anuncio = 'Nenhum beneficiário cadastrado.'
  }

  function cabecalhoOrdenavel(campoDaColuna: CampoOrdenavel, rotulo: string, sx?: SxProps<Theme>) {
    const ativo = campoDaColuna === campo
    // sortDirection põe o aria-sort no <th>: o leitor de tela diz "ordenado
    // em ordem crescente" ao chegar na coluna. O TableSortLabel é o botão
    // (Tab, Enter ou espaço) com a seta, que só aparece na coluna ativa.
    return (
      <TableCell sortDirection={ativo ? direcao : false} sx={sx}>
        <TableSortLabel
          active={ativo}
          direction={ativo ? direcao : 'asc'}
          onClick={() => ordenarPor(campoDaColuna)}
        >
          {rotulo}
        </TableSortLabel>
      </TableCell>
    )
  }

  return (
    <>
      {/* role="status" fica sempre na tela (só o texto muda), para o leitor
          de tela anunciar cada mudança: "Carregando...", e depois a página
          que chegou. */}
      <Box role="status" sx={somenteLeitorDeTela}>
        {anuncio}
      </Box>

      {erro !== null ? (
        // role="alert" (padrão do Alert). tabIndex={-1}: recebe o foco pelo
        // código, fora da ordem do Tab.
        <Alert
          ref={alertaRef}
          tabIndex={-1}
          severity="error"
          action={
            <Button color="inherit" size="small" onClick={tentarDeNovo}>
              Tentar de novo
            </Button>
          }
        >
          {erro}
        </Alert>
      ) : vazio ? (
        // Lista vazia com ícone. O SvgIcon sai com aria-hidden: o ícone é
        // enfeite, e quem explica é o texto.
        <Paper variant="outlined" sx={{ p: 4, textAlign: 'center' }}>
          <PersonOffOutlinedIcon sx={{ fontSize: 56, color: 'text.secondary' }} />
          <Typography variant="h6" component="p" sx={{ mt: 1 }}>
            Nenhum beneficiário cadastrado.
          </Typography>
          <Typography variant="body2" sx={{ color: 'text.secondary' }}>
            Use o botão Novo beneficiário para cadastrar o primeiro.
          </Typography>
        </Paper>
      ) : (
        <>
          {/* TableContainer com Paper: a tabela no cartão branco. Se ainda
              assim não couber (tela de 320px com nomes longos), quem rola
              para o lado é só a tabela, e não a página inteira. */}
          <TableContainer component={Paper}>
            <Table
              size="small"
              aria-busy={carregando}
              sx={{
                // O MUI põe o caption embaixo da tabela; aqui ele fica em
                // cima, como título visível dela.
                '& caption': { captionSide: 'top' },
                // No celular, menos espaço dos lados de cada célula.
                '& th, & td': { px: { xs: 0.75, sm: 2 } },
              }}
            >
              {/* O caption é o nome da tabela para o leitor de tela. */}
              <caption>{carregando ? 'Carregando beneficiários...' : resumo}</caption>
              <TableHead>
                <TableRow>
                  {cabecalhoOrdenavel('nome', 'Nome')}
                  {cabecalhoOrdenavel('cpf', 'CPF')}
                  <TableCell sx={SO_A_PARTIR_DO_SM}>Telefone</TableCell>
                  {cabecalhoOrdenavel('dataCadastro', 'Data de cadastro', SO_A_PARTIR_DO_MD)}
                </TableRow>
              </TableHead>
              <TableBody>
                {carregando
                  ? // aria-hidden: as linhas cinza não têm texto, e o leitor
                    // de tela leria células vazias.
                    Array.from({ length: LINHAS_DO_ESQUELETO }, (_, i) => (
                      <TableRow key={`esqueleto-${i}`} aria-hidden="true">
                        <TableCell>
                          <Skeleton />
                        </TableCell>
                        <TableCell>
                          <Skeleton />
                        </TableCell>
                        <TableCell sx={SO_A_PARTIR_DO_SM}>
                          <Skeleton />
                        </TableCell>
                        <TableCell sx={SO_A_PARTIR_DO_MD}>
                          <Skeleton />
                        </TableCell>
                      </TableRow>
                    ))
                  : dados?.content.map((beneficiario) => (
                      <TableRow key={beneficiario.idBeneficiario} hover>
                        {/* O nome é o cabeçalho da linha (th scope="row"):
                            nas outras células, o leitor de tela diz de quem
                            é o dado. */}
                        <TableCell component="th" scope="row">
                          {beneficiario.nome}
                        </TableCell>
                        <TableCell sx={{ whiteSpace: 'nowrap' }}>{formatarCpf(beneficiario.cpf)}</TableCell>
                        <TableCell sx={SO_A_PARTIR_DO_SM}>
                          {/* Telefone null ou em branco: "não informado". */}
                          {beneficiario.telefone?.trim() || (
                            <Box component="span" sx={{ fontStyle: 'italic', color: 'text.secondary' }}>
                              não informado
                            </Box>
                          )}
                        </TableCell>
                        <TableCell sx={SO_A_PARTIR_DO_MD}>{formatarData(beneficiario.dataCadastro)}</TableCell>
                      </TableRow>
                    ))}
              </TableBody>
            </Table>
          </TableContainer>

          {/* Paginação do MUI: começa em 1, por isso o pagina + 1. O ptBR do
              tema dá os nomes dos botões ("Ir para a página 2"). Com uma
              página só, ela não aparece. */}
          {dados !== null && dados.totalPages > 1 && (
            <Pagination
              count={dados.totalPages}
              page={pagina + 1}
              onChange={(_evento, numero) => mudarPagina(numero)}
              color="primary"
              siblingCount={telaPequena ? 0 : 1}
              aria-label="Páginas da lista de beneficiários"
              sx={{ mt: 2, '& ul': { justifyContent: 'center' } }}
            />
          )}
        </>
      )}

    </>
  )
}
