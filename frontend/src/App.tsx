import { AppBar, Container, Toolbar, Typography } from '@mui/material'
import { BeneficiarioList } from './components/BeneficiarioList'

// Layout com o MUI (Etapa 11): a barra do topo (o AppBar vira um <header>) e o
// conteúdo num Container centralizado, que vira o <main> da página. A
// listagem continua em HTML até a Etapa 12, com o estilo do index.css.
function App() {
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
        <Typography variant="h4" component="h1" gutterBottom>
          Beneficiários
        </Typography>
        <BeneficiarioList />
      </Container>
    </>
  )
}

export default App
