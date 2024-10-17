package com.theuz.Falha;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

public class CadastroFalhaGUI extends JFrame {

    HttpGet request = new HttpGet("http://localhost:8080/maquinas");
    HttpPost post = new HttpPost("http://localhost:8080/falha");
    HttpGet requestTecnicos = new HttpGet("http://localhost:8080/tecnicos");

    private final JComboBox<String> comboMaquinas;
    private final JTextField txtDataFalha;
    private final JTextField txtProblema;
    private final JComboBox<String> comboPrioridade;
    private final JComboBox<String> comboTecnicos;
    private final JButton btnSalvar, btnEditar, btnExcluir, btnVoltar, btnCancelar;
    private final JLabel lblMaquina;
    private final JLabel lblDataFalha;
    private final JLabel lblProblema;
    private final JLabel lblPrioridade;
    private final JLabel lblTecnico;
    private final JTable tableFalhas;
    private final DefaultTableModel tableModel;
    private int falhaSelecionadaId = -1; // ID da manutenção selecionada

    public CadastroFalhaGUI() {
        setTitle("Cadastro de Falha");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel superior para os campos de cadastro
        JPanel panelCadastro = new JPanel(new GridLayout(7, 2));

        lblMaquina = new JLabel("Máquina:");
        comboMaquinas = new JComboBox<>(new String[] {});

        lblDataFalha = new JLabel("Data da Falha:");
        txtDataFalha = new JTextField();

        lblProblema = new JLabel("Problema:");
        txtProblema = new JTextField();

        lblPrioridade = new JLabel("Prioridade:");
        comboPrioridade = new JComboBox<>(new String[] { "Baixa", "Média", "Alta" });

        lblTecnico = new JLabel("Técnico:");
        comboTecnicos = new JComboBox<>(new String[] {});

        panelCadastro.add(lblMaquina);
        panelCadastro.add(comboMaquinas);
        panelCadastro.add(lblDataFalha);
        panelCadastro.add(txtDataFalha);
        panelCadastro.add(lblProblema);
        panelCadastro.add(txtProblema);
        panelCadastro.add(lblPrioridade);
        panelCadastro.add(comboPrioridade);
        panelCadastro.add(lblTecnico);
        panelCadastro.add(comboTecnicos);

        // Painel inferior com botões
        JPanel panelBotoes = new JPanel(new FlowLayout());
        btnSalvar = new JButton("Salvar");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        btnVoltar = new JButton("Voltar");
        btnCancelar = new JButton("Cancelar");

        panelBotoes.add(btnVoltar);
        panelBotoes.add(btnSalvar);
        panelBotoes.add(btnEditar);
        panelBotoes.add(btnExcluir);
        panelBotoes.add(btnCancelar);

        // Adicionando tabela para exibir falhas cadastradas
        tableModel = new DefaultTableModel(
                new Object[] { "ID", "Máquina", "Data Falha", "Problema", "Prioridade", "Técnico" }, 0);
        tableFalhas = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableFalhas);

        // Adicionando tudo ao frame
        add(panelCadastro, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBotoes, BorderLayout.SOUTH);

        // Ação do botão salvar
        btnSalvar.addActionListener(e -> {
            if (falhaSelecionadaId == -1) {
                salvarFalha();
            } else {
                editarFalha(falhaSelecionadaId);
            }
        });

        btnCancelar.addActionListener(e -> {
            limparCampos();
        });

        // Ação do botão editar
        btnEditar.addActionListener((ActionEvent e) -> {
            carregarDadosParaEdicao();
        });

        // Ação do botão excluir
        btnExcluir.addActionListener((ActionEvent e) -> {
            carregarDadosParaEdicao();
            excluirFalha();
        });

        btnVoltar.addActionListener((ActionEvent e) -> {
            dispose();
        });

        // Carregar máquinas e técnicos
        carregarMaquinas();
        carregarTecnicos();
        listarFalhas();
    }

    // Método para salvar a falha
    private void salvarFalha() {
        // Captura os dados dos campos
        String maquina = (String) comboMaquinas.getSelectedItem();
        String dataFalha = txtDataFalha.getText();
        String problema = txtProblema.getText();
        String prioridade = (String) comboPrioridade.getSelectedItem();
        String tecnico = (String) comboTecnicos.getSelectedItem();

        // Monta o JSON com os dados da falha
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            JSONObject json = new JSONObject();
            json.put("maquinaId", maquina.split(" - ")[0]);
            json.put("dataFalha", dataFalha);
            json.put("problema", problema);
            json.put("prioridade", prioridade);
            json.put("tecnicoId", tecnico.split(" - ")[0]);

            StringEntity entity = new StringEntity(json.toString());
            post.setEntity(entity);
            post.setHeader("Content-type", "application/json");

            client.execute(post, response -> {
                if (response.getCode() == 200) {
                    JOptionPane.showMessageDialog(null, "Falha cadastrada com sucesso!");
                    limparCampos();
                    listarFalhas(); // Atualiza a tabela após salvar
                } else {
                    JOptionPane.showMessageDialog(null, "Erro ao cadastrar a falha: " + response.getCode());
                }
                return null;
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro: " + ex.getMessage());
        }
    }

    // Método para carregar falhas cadastradas na tabela
    private void listarFalhas() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:8080/falha");

            client.execute(request, response -> {
                if (response.getCode() == 200) {
                    String result = EntityUtils.toString(response.getEntity());
                    JSONArray falhas = new JSONArray(result);
                    tableModel.setRowCount(0); // Limpa a tabela antes de preencher

                    for (int i = 0; i < falhas.length(); i++) {
                        JSONObject falha = falhas.getJSONObject(i);

                        // Acessando o nome da máquina e do técnico
                        String maquinaNome = falha.getJSONObject("maquina").getString("nome");
                        String tecnicoNome = falha.getJSONObject("tecnico").getString("nome");

                        // Adicionando a linha com os dados da falha
                        tableModel.addRow(new Object[] {
                                falha.getInt("id"),
                                maquinaNome, // Nome da máquina
                                falha.getString("dataFalha"),
                                falha.getString("problema"),
                                falha.getString("prioridade"),
                                tecnicoNome // Nome do técnico
                        });
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao carregar falhas: " + response.getCode());
                }
                return null;
            });
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar falhas: " + e.getMessage());
        }
    }

    // Método para excluir uma falha
    private void excluirFalha() {
        // Verifica se há uma falha selecionada
        if (falhaSelecionadaId == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma falha para excluir.");
            return;
        }

        // Confirmação antes de excluir
        // Confirmação antes de excluir
        int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o campo selecionado?",
                "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirmacao != JOptionPane.YES_OPTION) {
            limparCampos();
            return;
        }

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // Cria a requisição DELETE para excluir a falha
            HttpDelete delete = new HttpDelete("http://localhost:8080/falha/" + falhaSelecionadaId);

            // Executa a requisição
            client.execute(delete, response -> {
                if (response.getCode() == 204) {
                    JOptionPane.showMessageDialog(this, "Falha excluída com sucesso!");
                    listarFalhas(); // Atualizar a tabela após exclusão
                    limparCampos(); // Limpar campos após exclusão
                    falhaSelecionadaId = -1; // Limpar seleção
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao excluir falha: " + response.getCode());
                }
                return null;
            });
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir falha: " + e.getMessage());
        }
    }

    private void editarFalha(int id) {
        // Verifica se há uma falha selecionada
        if (falhaSelecionadaId == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma falha para editar.");
            return;
        }

        // Obtendo os dados dos campos do formulário
        String maquina = comboMaquinas.getSelectedItem().toString().split(" - ")[0];
        String dataFalha = txtDataFalha.getText();
        String problema = txtProblema.getText();
        String prioridade = comboPrioridade.getSelectedItem().toString();
        String tecnico = comboTecnicos.getSelectedItem().toString().split(" - ")[0];

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // Cria a requisição PUT para atualizar a falha
            HttpPut put = new HttpPut("http://localhost:8080/falha/" + id);
            JSONObject json = new JSONObject();

            // Preenchendo o objeto JSON com os dados
            json.put("maquinaId", maquina);
            json.put("dataFalha", dataFalha);
            json.put("problema", problema);
            json.put("prioridade", prioridade);
            json.put("tecnicoId", tecnico);

            // Configura o corpo da requisição
            StringEntity entity = new StringEntity(json.toString());
            put.setEntity(entity);
            put.setHeader("Content-type", "application/json");

            // Executa a requisição
            client.execute(put, response -> {
                if (response.getCode() == 200) {
                    JOptionPane.showMessageDialog(this, "Falha editada com sucesso!");
                    listarFalhas(); // Atualizar a tabela após edição
                    limparCampos(); // Limpar campos após editar
                    falhaSelecionadaId = -1; // Limpar seleção
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao editar falha: " + response.getCode());
                }
                return null;
            });
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao editar falha: " + e.getMessage());
        }
    }

    // Métodos para carregar máquinas e técnicos (mesma lógica que já foi
    // implementada)
    private void carregarMaquinas() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:8080/maquinas");

            client.execute(request, response -> {
                if (response.getCode() == 200) {
                    String result = EntityUtils.toString(response.getEntity());
                    JSONArray maquinas = new JSONArray(result);

                    for (int i = 0; i < maquinas.length(); i++) {
                        JSONObject maquina = maquinas.getJSONObject(i);
                        comboMaquinas.addItem(maquina.getInt("id") + " - " + maquina.getString("nome"));
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao carregar máquinas: " + response.getCode());
                }
                return null;
            });
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar máquinas: " + e.getMessage());
        }
    }

    private void carregarTecnicos() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:8080/tecnicos");

            client.execute(request, response -> {
                if (response.getCode() == 200) {
                    String result = EntityUtils.toString(response.getEntity());
                    JSONArray tecnicos = new JSONArray(result);

                    for (int i = 0; i < tecnicos.length(); i++) {
                        JSONObject tecnico = tecnicos.getJSONObject(i);
                        comboTecnicos.addItem(tecnico.getInt("id") + " - " + tecnico.getString("nome"));
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao carregar técnicos: " + response.getCode());
                }
                return null;
            });
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar técnicos: " + e.getMessage());
        }
    }

    private void carregarDadosParaEdicao() {
        int selectedRow = tableFalhas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma falha para editar.");
            return;
        }

        // Obtendo o ID da falha selecionada
        falhaSelecionadaId = (int) tableModel.getValueAt(selectedRow, 0);

        // Carregando os dados da falha nos campos de texto e JComboBox
        comboMaquinas.setSelectedItem((String) tableModel.getValueAt(selectedRow, 1)); // Nome da máquina
        txtDataFalha.setText((String) tableModel.getValueAt(selectedRow, 2)); // Data da falha
        txtProblema.setText((String) tableModel.getValueAt(selectedRow, 3)); // Descrição do problema
        comboPrioridade.setSelectedItem((String) tableModel.getValueAt(selectedRow, 4)); // Prioridade
        comboTecnicos.setSelectedItem((String) tableModel.getValueAt(selectedRow, 5)); // Nome do técnico

    }

    private void limparCampos() {
        comboMaquinas.setSelectedIndex(0);
        txtDataFalha.setText("");
        txtProblema.setText("");
        comboPrioridade.setSelectedIndex(0);
        comboTecnicos.setSelectedIndex(0);
        falhaSelecionadaId = -1;
    }

}
