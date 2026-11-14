# Acessibilidade e usabilidade da listagem de beneficiários (Etapa 12, issue #21)

Avaliação da tela de beneficiários do front-end (`frontend/`), depois da Etapa 12: tabela do MUI com ordenação, paginação, Skeleton, lista vazia com ícone e os botões Editar (PUT) e Excluir (DELETE, com confirmação). Base: a Aula 10 (WCAG 2.1, Design Studio e teste de usabilidade).

## Relatório do Lighthouse

- **Arquivo:** [`lighthouse-acessibilidade-celular.html`](lighthouse-acessibilidade-celular.html) (abra no navegador; é o relatório salvo pelo Chrome, sem alteração).
- **Data:** 14/11/2026, 15:51, no LAB03-PC03.
- **Ferramenta:** Lighthouse 13.5.0, no Chrome 157 (F12 → aba Lighthouse), numa janela anônima.
- **Configuração:** modo Navegação, dispositivo Dispositivos móveis, só a categoria Acessibilidade.
- **Página avaliada:** http://localhost:5173, com o build de produção (`npm run build && npm run preview`), a API rodando e 12 beneficiários cadastrados (tabela na página 1 de 2).
- **Resultado:** **100** em Acessibilidade, com 24 auditorias aprovadas e nenhuma falha.

O Lighthouse só confere o que dá para medir automaticamente (contraste, nomes dos botões, `lang`, ordem dos títulos, cabeçalhos da tabela, tamanho dos alvos de toque etc.). Outros 10 itens ele lista para **verificação manual**:

| Item do Lighthouse | Como foi conferido |
|---|---|
| Controles interativos recebem foco pelo teclado | Itens 6 e 7 da autoavaliação |
| Elementos interativos indicam a finalidade e o estado | Seta e `aria-sort` na coluna ordenada; página atual marcada na paginação |
| A ordem do Tab é lógica | Item 6 |
| A ordem visual segue a ordem do DOM | Item 6 |
| O foco não fica preso por engano | Item 6 (o foco só fica preso de propósito no diálogo de exclusão, e o Esc sai) |
| O foco vai para o conteúdo novo | Título da tela ao trocar de tela, campo com erro e alertas |
| Usa elementos de marco (landmarks) | `header` (AppBar) e `main` (Container) |
| Conteúdo fora da tela fica escondido da tecnologia assistiva | Linhas do Skeleton com `aria-hidden` |
| Controles personalizados têm rótulo | `aria-label` "Editar <nome>" e "Excluir <nome>" |
| Controles personalizados têm papel ARIA | Títulos de coluna com `role="button"`; diálogo com `role="alertdialog"` |

## Teste de usabilidade

**Autoavaliação, e não o teste cruzado da issue.** Não houve um colega disponível para o teste cruzado (Aula 10: a dupla B testa sem ver o código). O checklist abaixo foi feito por mim, em 14/11/2026, no LAB03-PC03, com o Chrome e a API rodando. O teste com um colega fica pendente; quando acontecer, os resultados entram neste arquivo.

| # | Tarefa ou critério (Aula 10) | Meta | Resultado |
|---|---|---|---|
| 1 | Eficiência: cadastrar um beneficiário | menos de 30 s | Funcionou |
| 2 | Ordenar por nome de Z a A e ir para a página 2 | sem ajuda | Funcionou |
| 3 | Editar o telefone de um beneficiário | sem ajuda | Funcionou |
| 4 | Excluir um beneficiário, cancelando uma vez antes | entender a confirmação | Funcionou |
| 5 | Clareza de erros: cadastrar com o CPF `12345678900` | a mensagem explica o erro | Funcionou |
| 6 | Teclado: repetir os itens 1 e 4 só com Tab, Enter e Esc | conseguir sem mouse | Funcionou |
| 7 | Contraste (WebAIM): branco sobre `#007ACC` e `#666666` sobre branco | pelo menos 4,5:1 | Funcionou |
| 8 | Responsividade: modo celular do F12 (iPhone SE) | legível e sem rolagem lateral | Funcionou |

Limite desta avaliação: quem testou conhece a tela e o código, então ela não mostra as dificuldades de quem usa pela primeira vez, que é o objetivo do teste cruzado.
