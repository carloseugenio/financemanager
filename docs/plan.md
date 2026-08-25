# Plan

## Intent & Goal

App pessoal de gestão de gastos que permite ao usuário controlar suas finanças mensais com painel visual atualizado diariamente, importação e conciliação de extratos bancários (OFX, CSV, PDF, texto), leitura de SMS bancários, escaneamento de comprovantes e planejamento de projetos com orçamento. O objetivo é dar visibilidade total sobre gastos realizados e planejados, facilitando o controle financeiro pessoal com comparativo previsto x realizado.

## Audience & Roles

Usuário único (uso pessoal). Um único papel: o próprio usuário autenticado, que tem acesso completo a todas as funcionalidades do app — painel, importação, conciliação, contas, categorias, projetos e exportações.

## Core Flows
- These flows must work end-to-end:

### Painel mensal: 

- usuário abre o app → vê painel com total gasto no mês atual, gráfico donut por categoria com percentuais e ícones coloridos, lista de despesas já realizadas agrupadas por categoria, e seção de próximas despesas planejadas (com data e valor).

### Importação de extrato: 

- usuário acessa tela de importação → seleciona tipo de conta (conta corrente, cartão de crédito, outra) → faz upload de arquivo do dispositivo (OFX, CSV, PDF, TXT) ou informa uma URL para o sistema baixar automaticamente → sistema processa e arquiva os dados importados no banco.

### Conciliação por SMS: 

- usuário acessa conciliação → sistema exibe SMS bancários pendentes de leitura detectados no aparelho → usuário também pode colar/digitar manualmente texto de SMS → sistema extrai valor, data e estabelecimento do SMS e cria rascunho de despesa.

### Conciliação por comprovante: 

- usuário acessa conciliação → seleciona comprovante PDF já armazenado ou escaneia um comprovante com a câmera do dispositivo → sistema extrai os dados do comprovante → gera rascunho de despesa para revisão.

### Tela de conciliação e confirmação: 

- usuário vê lista de despesas importadas/extraídas em rascunho → pode editar categoria, valor, data, descrição de cada item → confirma individualmente ou em lote → despesas confirmadas atualizam o controle mensal no painel principal.

### Gestão de contas: 

- usuário acessa configurações de contas → pode adicionar conta bancária (banco, agência, conta), cartão de crédito (bandeira, limite, vencimento) ou carteira digital/online → cada conta fica disponível para associar às despesas e para importação de extratos.

### Análise por categoria: 

- usuário acessa tela de categorias → vê gráfico donut + lista detalhada de gastos por categoria no mês selecionado → pode navegar por períodos (mensal) → cada categoria exibe ícone colorido, total gasto e percentual do total.

### Planejamento de projeto: 

- usuário cria um projeto → define nome, período (data início e fim), orçamento total → adiciona itens de despesa planejada por categoria com valor estimado e data prevista → acompanha comparativo previsto x realizado conforme despesas são confirmadas.

### Exportação: 

- na tela de projeto ou no painel mensal, usuário pode exportar o planejamento ou extrato → escolhe formato PDF (formatado para impressão) ou CSV (dados brutos) → arquivo é gerado e disponibilizado para download/compartilhamento.

## Technical Requirements

- Entidades principais: 
  - Conta (tipo, banco, nome, dados)
  - Despesa (valor, data, categoria, conta, status: rascunho/confirmada, origem: manual/importação/SMS/comprovante), 
  - Categoria (nome, ícone, cor)
  - ProjetoPlanejamento (nome, período, orçamento)
  - ItemProjeto (categoria, valor previsto, valor realizado, data prevista).
  
- Importação de arquivos: 
  - upload de OFX, CSV, PDF, TXT do dispositivo ou via URL. 

- Conciliação SMS: 
  - leitura de SMS do dispositivo (Web API onde disponível) + entrada manual de texto SMS. 

- Conciliação por comprovante: 
  - upload de PDF ou captura via câmera (input type=file accept=image/*,capture). 
   
- Exportação: 
  - geração de PDF via jsPDF (já instalado) e CSV nativo. 
  - 
- Armazenamento de arquivos importados no banco.

## Design Preferences

- Visual colorido e amigável, estilo app de finanças pessoais (referência: Mobills, Organizze). 
- Cada categoria tem ícone SVG próprio e cor distinta (alimentação: laranja, transporte: azul, saúde: verde, lazer: roxo, etc.). 
- Fundo claro com cards brancos arredondados e sombras suaves. 
- Gráficos donut com cores vivas. 
- Tipografia sans-serif moderna com hierarquia clara de tamanhos. 
- Botões com cor primária vibrante (ex: #4F46E5 índigo ou #10B981 verde). 
- Navegação inferior com 5 ícones: Painel, Importar, Conciliar, Categorias, Projetos. 
- Microinterações suaves nos cards. Layout responsivo mobile-first.