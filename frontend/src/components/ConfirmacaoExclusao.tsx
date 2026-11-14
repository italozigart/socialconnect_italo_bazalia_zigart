import { useEffect, useRef, useState } from 'react'
import {
  Alert,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from '@mui/material'
import api, { mensagemDeErro, problemDetailDe } from '../services/api'
import { formatarCpf } from '../utils/formatacao'
import type { Beneficiario } from '../types/beneficiario'

interface ConfirmacaoExclusaoProps {
  beneficiario: Beneficiario
  aberta: boolean
  onCancelar: () => void
  // jaNaoExistia: a API respondeu 404, porque alguém excluiu antes.
  onExcluido: (beneficiario: Beneficiario, jaNaoExistia: boolean) => void
  // Fim da animação de saída: a lista tira o componente da tela.
  onFechada: () => void
}

// Confirmação da exclusão (Etapa 12): o DELETE só sai depois do clique em
// Excluir. O Dialog do MUI cuida do teclado:
// - o foco entra no diálogo e fica preso nele enquanto ele está aberto (o Tab
//   circula entre os botões);
// - Esc fecha, como o Cancelar.
//
// disableRestoreFocus: quem devolve o foco ao fechar é a BeneficiarioList
// (para a lixeira, se a pessoa cancelou) e o App (para o título, se excluiu,
// porque a linha e a lixeira somem da tabela). Sem ele, o MUI devolveria o
// foco à lixeira ao fechar, e também no meio da abertura em desenvolvimento:
// o StrictMode desmonta e monta o diálogo de novo, o MUI devolve o foco à
// lixeira na desmontagem e, na nova montagem, põe o foco na caixa do
// diálogo, e não no Cancelar.
//
// role="alertdialog": um diálogo que interrompe e pede uma decisão. O título é
// o nome acessível (aria-labelledby, ligado sozinho pelo DialogTitle) e o
// texto é a descrição (aria-describedby): o leitor de tela lê os dois ao abrir.
export function ConfirmacaoExclusao({
  beneficiario,
  aberta,
  onCancelar,
  onExcluido,
  onFechada,
}: ConfirmacaoExclusaoProps) {
  const [excluindo, setExcluindo] = useState(false)
  // Objeto, e não só o texto: o mesmo erro duas vezes seguidas (API parada)
  // é um objeto novo, e o efeito abaixo roda de novo.
  const [erro, setErro] = useState<{ mensagem: string } | null>(null)
  const alertaRef = useRef<HTMLDivElement>(null)
  const idDescricao = `exclusao-descricao-${beneficiario.idBeneficiario}`

  // Durante o DELETE, o botão Excluir fica desabilitado e perde o foco. Num
  // erro, o foco vai para o alerta: o leitor de tela lê a mensagem, e o Tab
  // seguinte volta aos botões.
  useEffect(() => {
    if (erro !== null) {
      alertaRef.current?.focus()
    }
  }, [erro])

  async function excluir() {
    setExcluindo(true)
    setErro(null)
    try {
      // 204, sem corpo: não há nada para ler na resposta.
      await api.delete(`/beneficiarios/${beneficiario.idBeneficiario}`)
      onExcluido(beneficiario, false)
    } catch (e) {
      // 404: alguém excluiu antes. O resultado é o mesmo (o beneficiário não
      // existe mais), então a lista só avisa e se atualiza.
      if (problemDetailDe(e)?.status === 404) {
        onExcluido(beneficiario, true)
        return
      }
      // API parada, 500...: o diálogo continua aberto, com o erro, para a
      // pessoa tentar de novo ou cancelar.
      console.error('Erro ao excluir beneficiário:', e)
      setErro({ mensagem: mensagemDeErro(e) })
      setExcluindo(false)
    }
  }

  // Durante o DELETE, o Esc e o clique fora do diálogo não fecham nada.
  function fechar() {
    if (!excluindo) {
      onCancelar()
    }
  }

  return (
    <Dialog
      open={aberta}
      onClose={fechar}
      role="alertdialog"
      aria-describedby={idDescricao}
      disableRestoreFocus
      slotProps={{ transition: { onExited: onFechada } }}
    >
      <DialogTitle>Excluir beneficiário?</DialogTitle>
      <DialogContent>
        <DialogContentText id={idDescricao}>
          O cadastro de {beneficiario.nome} (CPF {formatarCpf(beneficiario.cpf)}) será excluído. Esta ação não
          pode ser desfeita.
        </DialogContentText>
        {/* role="alert" (padrão do Alert): o leitor de tela anuncia o erro.
            tabIndex={-1}: recebe o foco pelo código, fora da ordem do Tab. */}
        {erro !== null && (
          <Alert ref={alertaRef} tabIndex={-1} severity="error" sx={{ mt: 2 }}>
            {erro.mensagem}
          </Alert>
        )}
      </DialogContent>
      <DialogActions>
        {/* autoFocus no Cancelar: ao abrir, o foco vai para a opção que não
            apaga nada, e um Enter apressado não exclui ninguém. */}
        <Button onClick={fechar} disabled={excluindo} autoFocus>
          Cancelar
        </Button>
        <Button onClick={excluir} variant="contained" color="error" loading={excluindo} loadingPosition="start">
          {excluindo ? 'Excluindo...' : 'Excluir'}
        </Button>
      </DialogActions>
    </Dialog>
  )
}
