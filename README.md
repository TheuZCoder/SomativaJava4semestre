Sistema de Manutenção Preventiva e Corretiva
Descrição do Projeto
O Sistema de Manutenção Preventiva e Corretiva é um software destinado ao gerenciamento do ciclo de vida de máquinas e equipamentos industriais, focado em reduzir o tempo de inatividade e otimizar a performance operacional. O sistema oferece o controle tanto de manutenções preventivas (realizadas para evitar falhas) quanto corretivas (realizadas após uma falha), além de permitir o gerenciamento de técnicos e falhas ocorridas, e gerar relatórios de desempenho.

Funcionalidades Principais
1. Gerenciamento de Máquinas e Equipamentos
Cadastro de máquinas com especificações técnicas (modelo, fabricante, data de aquisição, etc.).
Visualização e edição das informações das máquinas.
2. Registro e Controle de Manutenções
Registro de manutenções preventivas e corretivas.
Histórico completo de manutenções por máquina.
Registro de peças trocadas e tempo de inatividade.
3. Gerenciamento de Falhas
Registro de falhas ocorridas, incluindo a severidade e operador responsável.
Controle de falhas por máquina, com rastreamento de sua resolução.
4. Gerenciamento de Técnicos
Cadastro de técnicos com suas especialidades e disponibilidade.
5. Relatórios e Indicadores de Desempenho
Geração de relatórios detalhados de manutenções, tempo de inatividade e peças substituídas.
Cálculo de indicadores de performance, como:
MTTR (Mean Time to Repair - Tempo Médio de Reparo).
MTBF (Mean Time Between Failures - Tempo Médio Entre Falhas).
Tecnologias Utilizadas
Java: Linguagem principal do projeto.
Swing: Interface gráfica para a aplicação.
JSON-Server: API REST para armazenamento e recuperação de dados.
Apache HttpClient: Para realizar requisições à API.
Requisitos Funcionais
O sistema deve permitir o cadastro de máquinas e equipamentos industriais com suas especificações técnicas.
O sistema deve registrar manutenções preventivas e corretivas, associando técnicos e peças substituídas.
Geração de relatórios com indicadores de performance, como MTTR e MTBF.
Interface gráfica intuitiva e fácil de usar.
Requisitos Não Funcionais
O sistema deve ser responsivo, garantindo um tempo de resposta rápido para operações.
A interface deve ser amigável e proporcionar fácil navegação entre as funcionalidades.
O sistema deve garantir o armazenamento seguro e eficiente dos dados.
Como Rodar o Projeto
Pré-requisitos
Java Development Kit (JDK) instalado.
Maven para gerenciamento de dependências.
JSON-Server para emulação da API REST.
Editor de código, como IntelliJ ou Eclipse.
Passos para Rodar
Clone o repositório:
bash
Copiar código
git clone https://github.com/seu-usuario/sistema-manutencao.git
Navegue até a pasta do projeto:
bash
Copiar código
cd sistema-manutencao
Instale as dependências necessárias:
bash
Copiar código
mvn install
Execute o JSON-Server para fornecer a API de dados:
bash
Copiar código
json-server --watch db.json
Compile e execute o projeto Java:
bash
Copiar código
mvn compile
mvn exec:java -Dexec.mainClass="com.sistemamanutencao.Main"
Diagrama de Classes
O diagrama de classes abaixo modela as principais entidades do sistema:

Máquinas: Representa os equipamentos industriais a serem gerenciados.
Manutenções: Registra as manutenções preventivas e corretivas realizadas nas máquinas.
Falhas: Registra os problemas ocorridos nas máquinas.
Técnicos: Representa os técnicos responsáveis por realizar as manutenções.
json
Copiar código
{
  "maquinas": [
    {
      "id": 1,
      "codigo": "M001",
      "nome": "Torno CNC",
      "modelo": "CNC 3000",
      "fabricante": "Siemens",
      "dataAquisicao": "2020-01-10",
      "tempoVidaEstimado": 10,
      "localizacao": "Linha 1",
      "detalhes": "Operação em alta precisão",
      "manual": "URL do manual"
    }
  ],
  "historicoManutencao": [
    {
      "id": 1,
      "maquinaId": 1,
      "data": "2024-10-07",
      "tipo": "Preventiva",
      "pecasTrocadas": "Correia",
      "tempoDeParada": 4,
      "tecnicoId": "João Silva",
      "observacoes": "Substituição preventiva da correia."
    }
  ],
  "falhas": [
    {
      "id": 1,
      "maquinaId": 1,
      "data": "2024-09-28",
      "problema": "Falha no motor",
      "prioridade": "Alta",
      "operador": "Carlos Lima"
    }
  ],
  "tecnicos": [
    {
      "id": 1,
      "nome": "João Silva",
      "especialidade": "Mecânica",
      "disponibilidade": "Livre"
    }
  ]
}
Diagrama de Sequência
Fluxo de Adição de Nova Manutenção
O usuário clica no botão "Adicionar Manutenção".
A interface gráfica exibe o formulário de preenchimento de dados.
O usuário preenche os campos (máquina, técnico, peças trocadas, etc.) e envia os dados.
O controller valida as informações e envia uma requisição à API JSON-Server.
A API armazena a nova manutenção e retorna uma resposta de sucesso.
O sistema exibe uma mensagem de sucesso ao usuário.
Contribuição
Se desejar contribuir com o projeto, siga os passos abaixo:

Faça um fork do projeto.
Crie uma nova branch:
bash
Copiar código
git checkout -b feature/nova-funcionalidade
Faça suas alterações e commit:
bash
Copiar código
git commit -m "Adiciona nova funcionalidade"
Envie suas alterações:
bash
Copiar código
git push origin feature/nova-funcionalidade
Abra um Pull Request.
Licença
Este projeto está licenciado sob a Licença MIT - veja o arquivo LICENSE para mais detalhes.

