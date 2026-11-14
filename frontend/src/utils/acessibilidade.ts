// Estilo (sx) de um texto que só o leitor de tela lê: o elemento continua na
// página e é lido normalmente, mas ocupa 1 pixel e fica invisível. É o mesmo
// CSS do visuallyHidden do MUI, que vem no pacote @mui/utils; ele está aqui
// para o front não importar um pacote que não está no package.json.
export const somenteLeitorDeTela = {
  border: 0,
  clip: 'rect(0 0 0 0)',
  height: '1px',
  margin: '-1px',
  overflow: 'hidden',
  padding: 0,
  position: 'absolute',
  whiteSpace: 'nowrap',
  width: '1px',
} as const
