import { useEffect, useRef, useState } from 'react'
import { Alert, AppBar, Button, Container, Stack, Toolbar, Typography } from '@mui/material'
import { BeneficiarioForm } from './components/BeneficiarioForm'
import { BeneficiarioList } from './components/BeneficiarioList'
import { CONSULTA_INICIAL } from './types/consulta'
import type { Beneficiario } from './types/beneficiario'
import type { ConsultaBeneficiarios } from './types/consulta'

// Duas telas, trocadas por estado (Etapa 11): a listagem e o cadastro. Sem
// roteador por enquanto: nenhuma tela precisa de endereço próprio, e a troca
// é um useState. Se a edição da Etapa 12 precisar de URL (voltar do navegador,
// F5 na tela de edição), aí o react-router passa a valer a pena.
type Tela = 'listagem' | 'cadastro'

// Layout com o MUI: a barra do topo (o AppBar vira um <header>) e o conteúdo
// num Container centralizado, que vira o <main> da página.
function App() {
  const [tela, setTela] = useState<Tela>('listagem')
  // Página e ordem da listagem (Etapa 12), guardadas aqui para a lista voltar
  // igual depois do cadastro.
  const [consulta, setConsulta] = useState<ConsultaBeneficiarios>(CONSULTA_INICIAL)
  const [mensagem, setMensagem] = useState<string | null>(null)
  const tituloRef = useRef<HTMLHeadingElement>(null)
  const trocouDeTela = useRef(false)

  // Ao trocar de tela, o foco vai para o título da tela nova: o leitor de tela
  // anuncia onde a pessoa está, e o próximo Tab começa dali. Sem isso, o foco
  // ficaria no botão que sumiu e cairia no body. Na abertura da página, o foco
  // fica onde o navegador deixa (por isso a variável trocouDeTela, que só os
  // cliques mudam; o StrictMode roda este efeito duas vezes na montagem).
  useEffect(() => {
    if (trocouDeTela.current) {
      tituloRef.current?.focus()
    }
  }, [tela])

  function irPara(novaTela: Tela) {
    trocouDeTela.current = true
    setTela(novaTela)
  }

  function abrirCadastro() {
    setMensagem(null)
    irPara('cadastro')
  }

  // 201 do POST: a mensagem aparece na listagem, que monta de novo e busca a
  // página (a mesma de antes) já atualizada.
  function aoCadastrar(beneficiario: Beneficiario) {
    setMensagem(`Beneficiário ${beneficiario.nome} cadastrado com sucesso.`)
    irPara('listagem')
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
        {tela === 'listagem' ? (
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
                mensagem. O botão de fechar vem com o texto "Fechar" (ptBR). */}
            {mensagem !== null && (
              <Alert severity="success" onClose={() => setMensagem(null)} sx={{ mb: 2 }}>
                {mensagem}
              </Alert>
            )}

            <BeneficiarioList consulta={consulta} onMudarConsulta={setConsulta} />
          </>
        ) : (
          <>
            <Typography variant="h4" component="h1" ref={tituloRef} tabIndex={-1} sx={{ mb: 1 }}>
              Novo beneficiário
            </Typography>
            <BeneficiarioForm onCadastrado={aoCadastrar} onCancelar={() => irPara('listagem')} />
          </>
        )}
      </Container>
    </>
  )
}

export default App
