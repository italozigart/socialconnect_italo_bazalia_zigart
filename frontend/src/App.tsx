import { useEffect, useRef, useState } from 'react'
import { Alert, AppBar, Button, Container, Stack, Toolbar, Typography } from '@mui/material'
import { BeneficiarioForm } from './components/BeneficiarioForm'
import { BeneficiarioList } from './components/BeneficiarioList'
import { CONSULTA_INICIAL } from './types/consulta'
import type { Beneficiario } from './types/beneficiario'
import type { ConsultaBeneficiarios } from './types/consulta'

// Três telas, trocadas por estado: a listagem, o cadastro (Etapa 11) e a
// edição (Etapa 12). Continua sem roteador: nenhuma tela precisa de endereço
// próprio. O custo é que o F5 e o "voltar" do navegador não voltam para a
// edição; se um dia isso for preciso, aí o react-router passa a valer a pena.
// A edição carrega o beneficiário escolhido na tabela, e o TypeScript só
// deixa abrir essa tela com ele.
type Tela = { nome: 'listagem' } | { nome: 'cadastro' } | { nome: 'edicao'; beneficiario: Beneficiario }

interface Mensagem {
  texto: string
  severidade: 'success' | 'info'
}

// Layout com o MUI: a barra do topo (o AppBar vira um <header>) e o conteúdo
// num Container centralizado, que vira o <main> da página.
function App() {
  const [tela, setTela] = useState<Tela>({ nome: 'listagem' })
  // Página e ordem da listagem, guardadas aqui para a lista voltar igual
  // depois do cadastro ou da edição.
  const [consulta, setConsulta] = useState<ConsultaBeneficiarios>(CONSULTA_INICIAL)
  const [mensagem, setMensagem] = useState<Mensagem | null>(null)
  const tituloRef = useRef<HTMLHeadingElement>(null)
  // Pedidos de foco no título: cada troca de tela e cada exclusão somam 1.
  // Começa em 0, e na abertura da página o foco fica onde o navegador deixa
  // (o StrictMode roda o efeito duas vezes na montagem, as duas com 0).
  const [pedidosDeFoco, setPedidosDeFoco] = useState(0)

  // Foco no título: o leitor de tela anuncia onde a pessoa está, e o próximo
  // Tab começa dali. Sem isso, o foco ficaria no botão que sumiu (o da tela
  // anterior ou o da linha excluída) e cairia no body.
  useEffect(() => {
    if (pedidosDeFoco > 0) {
      tituloRef.current?.focus()
    }
  }, [pedidosDeFoco])

  function irPara(novaTela: Tela) {
    setTela(novaTela)
    setPedidosDeFoco((anterior) => anterior + 1)
  }

  function abrirCadastro() {
    setMensagem(null)
    irPara({ nome: 'cadastro' })
  }

  function abrirEdicao(beneficiario: Beneficiario) {
    setMensagem(null)
    irPara({ nome: 'edicao', beneficiario })
  }

  // 201 do POST e 200 do PUT: a mensagem aparece na listagem, que monta de
  // novo e busca a página (a mesma de antes) já atualizada.
  function aoCadastrar(beneficiario: Beneficiario) {
    setMensagem({ texto: `Beneficiário ${beneficiario.nome} cadastrado com sucesso.`, severidade: 'success' })
    irPara({ nome: 'listagem' })
  }

  function aoAtualizar(beneficiario: Beneficiario) {
    setMensagem({ texto: `Beneficiário ${beneficiario.nome} atualizado com sucesso.`, severidade: 'success' })
    irPara({ nome: 'listagem' })
  }

  function fecharMensagem() {
    setMensagem(null)
    setPedidosDeFoco((anterior) => anterior + 1)
  }

  // 204 do DELETE (ou 404, se alguém excluiu antes). A lista já está na tela
  // e se recarrega sozinha; aqui ficam a mensagem e o foco. A linha excluída
  // sai da tabela com o botão de lixeira, para onde o diálogo devolveria o
  // foco: por isso ele vai para o título.
  function aoExcluir(beneficiario: Beneficiario, jaNaoExistia: boolean) {
    setMensagem(
      jaNaoExistia
        ? { texto: `O beneficiário ${beneficiario.nome} já tinha sido excluído. A lista foi atualizada.`, severidade: 'info' }
        : { texto: `Beneficiário ${beneficiario.nome} excluído com sucesso.`, severidade: 'success' },
    )
    setPedidosDeFoco((anterior) => anterior + 1)
  }

  return (
    <>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" component="p">
            SocialConnect
          </Typography>
        </Toolbar>
      </AppBar>

      <Container component="main" maxWidth="md" sx={{ py: 3 }}>
        {tela.nome === 'listagem' && (
          <>
            {/* Mobile-first: no celular, o título e o botão ficam um embaixo
                do outro, e o botão ocupa a largura toda; a partir de 600px
                (sm), lado a lado. No MUI 9, justifyContent e alignItems vão
                no sx, e não como props. */}
            <Stack
              direction={{ xs: 'column', sm: 'row' }}
              spacing={2}
              sx={{ justifyContent: 'space-between', alignItems: { xs: 'stretch', sm: 'center' }, mb: 2 }}
            >
              <Typography variant="h4" component="h1" ref={tituloRef} tabIndex={-1}>
                Beneficiários
              </Typography>
              <Button variant="contained" onClick={abrirCadastro}>
                Novo beneficiário
              </Button>
            </Stack>

            {/* role="alert" (padrão do Alert): o leitor de tela anuncia a
                mensagem. O botão de fechar vem com o texto "Fechar" (ptBR) e
                some junto com o aviso; por isso o foco vai para o título. */}
            {mensagem !== null && (
              <Alert severity={mensagem.severidade} onClose={fecharMensagem} sx={{ mb: 2 }}>
                {mensagem.texto}
              </Alert>
            )}

            <BeneficiarioList
              consulta={consulta}
              onMudarConsulta={setConsulta}
              onEditar={abrirEdicao}
              onExcluido={aoExcluir}
            />
          </>
        )}

        {tela.nome === 'cadastro' && (
          <>
            <Typography variant="h4" component="h1" ref={tituloRef} tabIndex={-1} sx={{ mb: 1 }}>
              Novo beneficiário
            </Typography>
            <BeneficiarioForm key="cadastro" onSalvo={aoCadastrar} onCancelar={() => irPara({ nome: 'listagem' })} />
          </>
        )}

        {tela.nome === 'edicao' && (
          <>
            <Typography variant="h4" component="h1" ref={tituloRef} tabIndex={-1} sx={{ mb: 1 }}>
              Editar beneficiário
            </Typography>
            {/* A key troca o formulário a cada beneficiário: o RHF só lê os
                valores iniciais (defaultValues) quando o formulário monta. */}
            <BeneficiarioForm
              key={`edicao-${tela.beneficiario.idBeneficiario}`}
              beneficiario={tela.beneficiario}
              onSalvo={aoAtualizar}
              onCancelar={() => irPara({ nome: 'listagem' })}
            />
          </>
        )}
      </Container>
    </>
  )
}

export default App
