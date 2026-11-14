import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import CssBaseline from '@mui/material/CssBaseline'
import { ThemeProvider, createTheme } from '@mui/material/styles'
import { ptBR } from '@mui/material/locale'
import App from './App.tsx'

// Tema do MUI (Aula 10): as cores da aula, com quatro acréscimos.
// - contrastThreshold 4.5: o MUI escolhe a cor do texto (branco ou escuro)
//   dos botões e alertas pelo contraste com o fundo. O padrão é 3; com 4.5, a
//   escolha segue o nível AA da WCAG para texto normal, como a documentação
//   do MUI recomenda. No primary da aula (#007acc), o texto continua branco
//   (4,5:1); no secondary (#ff4081), o branco daria 3,3:1, e o MUI passa a
//   usar texto escuro.
// - fontFamily: a fonte do sistema (Segoe UI no Windows), a mesma do index.css
//   da Etapa 10. A Roboto, padrão do MUI, não vem instalada no Windows, e
//   baixá-la seria mais um pacote.
// - ptBR: os textos prontos dos componentes em português (o "Fechar" do Alert
//   e os nomes dos botões da paginação, como "Ir para a página 2").
// - focusVisible (Etapa 12, novidade do MUI 9): um contorno de 2px na cor
//   primary em volta de todo botão que recebe o foco pelo teclado. Sem ele,
//   cada componente marca o foco do seu jeito, e o título de coluna da tabela
//   (TableSortLabel) só mudava a cor do texto. Com o mouse, o contorno não
//   aparece.
const theme = createTheme(
  {
    palette: {
      primary: { main: '#007acc' },
      secondary: { main: '#ff4081' },
      contrastThreshold: 4.5,
    },
    typography: {
      fontFamily: 'system-ui, "Segoe UI", Roboto, Arial, sans-serif',
    },
    focusVisible: true,
  },
  ptBR,
)

// ThemeProvider: entrega o tema a todos os componentes do MUI.
// CssBaseline: zera as margens do navegador e aplica a fonte e as cores do
// tema ao body. Desde a Etapa 12 não há mais index.css: a tabela também é
// do MUI.
createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <App />
    </ThemeProvider>
  </StrictMode>,
)
