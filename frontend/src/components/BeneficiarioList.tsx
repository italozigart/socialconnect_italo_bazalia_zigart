import { useEffect, useState } from 'react'
import api, { mensagemDeErro } from '../services/api'
import type { Beneficiario } from '../types/beneficiario'
import type { Page } from '../types/page'

// Com o verbatimModuleSyntax ligado no tsconfig do Vite, tipo se importa com
// "import type". O import da aula (import { Beneficiario, Page }) quebra o
// npm run build.

// O CPF chega com 11 dígitos; na tela, com a máscara 000.000.000-00.
function formatarCpf(cpf: string): string {
  return cpf.replace(/^(\d{3})(\d{3})(\d{3})(\d{2})$/, '$1.$2.$3-$4')
}

// Telefone: null quando não foi informado. A API de beneficiário grava o texto
// como veio, então "" ou "   " também são possíveis. O ?.trim() devolve
// undefined para null e "" para texto em branco; nos dois casos, o || mostra
// "não informado".

// A data chega como aaaa-mm-dd e vai para a tela como dd/mm/aaaa. Sem new
// Date(): ele leria "2026-11-12" como meia-noite em UTC, e no Brasil (UTC-3)
// a tela mostraria 11/11.
function formatarData(data: string): string {
  const [ano, mes, dia] = data.split('-')
  return `${dia}/${mes}/${ano}`
}

// Primeira página da listagem (GET /beneficiarios?page=0&size=10), com três
// estados além da tabela: carregando, erro e lista vazia.
export function BeneficiarioList() {
  const [pagina, setPagina] = useState<Page<Beneficiario> | null>(null)
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState<string | null>(null)
  // Muda a cada clique em "Tentar de novo" e faz o useEffect buscar de novo.
  const [tentativa, setTentativa] = useState(0)

  useEffect(() => {
    // Se o componente sair da tela (ou o efeito rodar de novo) antes de a
    // resposta chegar, ela é descartada. Em desenvolvimento, o StrictMode roda
    // o efeito duas vezes de propósito (monta, desmonta e monta de novo): a
    // aba Network mostra dois GETs, e só a resposta do segundo chega à tela.
    let ignorar = false

    // O Axios monta a URL: baseURL + /beneficiarios?page=0&size=10. O tipo
    // entre < > diz ao TypeScript o formato de resposta.data.
    api
      .get<Page<Beneficiario>>('/beneficiarios', { params: { page: 0, size: 10 } })
      .then((resposta) => {
        if (!ignorar) {
          setPagina(resposta.data)
        }
      })
      .catch((e: unknown) => {
        if (!ignorar) {
          console.error('Erro ao buscar beneficiários:', e)
          setErro(mensagemDeErro(e))
        }
      })
      .finally(() => {
        if (!ignorar) {
          setCarregando(false)
        }
      })

    return () => {
      ignorar = true
    }
  }, [tentativa])

  function tentarDeNovo() {
    setErro(null)
    setCarregando(true)
    setTentativa((anterior) => anterior + 1)
  }

  // role="status" e role="alert" fazem o leitor de tela anunciar a mudança.
  if (carregando) {
    return <p role="status">Carregando beneficiários...</p>
  }

  if (erro !== null) {
    return (
      <div role="alert" className="erro">
        <p>{erro}</p>
        <button type="button" onClick={tentarDeNovo}>
          Tentar de novo
        </button>
      </div>
    )
  }

  if (pagina === null || pagina.content.length === 0) {
    return <p>Nenhum beneficiário cadastrado.</p>
  }

  return (
    <table>
      <caption>
        Beneficiários em ordem de nome: {pagina.content.length} de {pagina.totalElements}
      </caption>
      <thead>
        <tr>
          <th scope="col">Nome</th>
          <th scope="col">CPF</th>
          <th scope="col">Telefone</th>
          <th scope="col">Data de cadastro</th>
        </tr>
      </thead>
      <tbody>
        {pagina.content.map((beneficiario) => (
          <tr key={beneficiario.idBeneficiario}>
            <td>{beneficiario.nome}</td>
            <td>{formatarCpf(beneficiario.cpf)}</td>
            <td>{beneficiario.telefone?.trim() || <span className="nao-informado">não informado</span>}</td>
            <td>{formatarData(beneficiario.dataCadastro)}</td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}
