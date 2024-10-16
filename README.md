# Sistema de Manutenção Preventiva e Corretiva

## Descrição do Projeto
O **Sistema de Manutenção Preventiva e Corretiva** é um software desenvolvido para gerenciar o ciclo de vida de máquinas e equipamentos industriais, com foco em reduzir o tempo de inatividade e otimizar a performance operacional. O sistema permite o controle de manutenções preventivas (realizadas para evitar falhas) e corretivas (realizadas após uma falha), além de gerenciar técnicos e falhas ocorridas, e gerar relatórios de desempenho.

## Funcionalidades Principais

1. **Gerenciamento de Máquinas e Equipamentos**
   - Cadastro de máquinas com especificações técnicas (modelo, fabricante, data de aquisição, etc.).
   - Visualização e edição das informações das máquinas.

2. **Registro e Controle de Manutenções**
   - Registro de manutenções preventivas e corretivas.
   - Histórico completo de manutenções por máquina.
   - Registro de peças trocadas e tempo de inatividade.

3. **Gerenciamento de Falhas**
   - Registro de falhas ocorridas, incluindo severidade e operador responsável.
   - Controle de falhas por máquina, com rastreamento de sua resolução.

4. **Gerenciamento de Técnicos**
   - Cadastro de técnicos com suas especialidades e disponibilidade.

5. **Relatórios e Indicadores de Desempenho**
   - Geração de relatórios detalhados de manutenções, tempo de inatividade e peças substituídas.
   - Cálculo de indicadores de performance, como:
     - **MTTR** (Mean Time to Repair - Tempo Médio de Reparo).
     - **MTBF** (Mean Time Between Failures - Tempo Médio Entre Falhas).

## Tecnologias Utilizadas
- **Java**: Linguagem principal do projeto.
- **Swing**: Interface gráfica para a aplicação.
- **JSON-Server**: API REST para armazenamento e recuperação de dados.
- **Apache HttpClient**: Para realizar requisições à API.

## Requisitos Funcionais
- O sistema deve permitir o cadastro de máquinas e equipamentos industriais com suas especificações técnicas.
- O sistema deve registrar manutenções preventivas e corretivas, associando técnicos e peças substituídas.
- Geração de relatórios com indicadores de performance, como MTTR e MTBF.
- Interface gráfica intuitiva e fácil de usar.

## Requisitos Não Funcionais
- O sistema deve ser responsivo, garantindo um tempo de resposta rápido para operações.
- A interface deve ser amigável e proporcionar fácil navegação entre as funcionalidades.
- O sistema deve garantir o armazenamento seguro e eficiente dos dados.

## Como Rodar o Projeto

### Pré-requisitos
- **Java Development Kit (JDK)** instalado.
- **Maven** para gerenciamento de dependências.
- **JSON-Server** para emulação da API REST.
- Editor de código, como **IntelliJ** ou **Eclipse**.

### Passos para Rodar
1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/sistema-manutencao.git
