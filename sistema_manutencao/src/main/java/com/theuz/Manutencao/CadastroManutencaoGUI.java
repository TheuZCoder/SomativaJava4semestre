package com.theuz.Manutencao;

import java.awt.*;
import java.io.IOException;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

public class CadastroManutencaoGUI extends JFrame {
    private final JComboBox<String> comboMaquinas;
    private final JTextField txtDataManutencao;
    private final JComboBox<String> comboTipoManutencao;
    private final JTextField txtPecasTrocadas;
    private final JTextField txtTempoParado;
    private final JComboBox<String> comboTecnicos;
    private final JTextField txtObservacoes;
    private final JButton btnSalvar, btnVoltar;
    private final JButton btnEditar, btnExcluir;
    private final JTable tableManutencoes;
    private final DefaultTableModel tableModel;
    private int manutencaoSelecionadaId = -1; // ID da manutenção selecionada

    public CadastroManutencaoGUI() {
        setTitle("Cadastro de Manutenção - CRUD");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel superior (Formulário)
        JPanel painelFormulario = new JPanel(new GridLayout(10, 2));
        painelFormulario.setBorder(BorderFactory.createTitledBorder("Formulário de Cadastro"));
        
        // Adiciona componentes ao painel do formulário
        painelFormulario.add(new JLabel("Máquina:"));
        comboMaquinas = new JComboBox<>();
        painelFormulario.add(comboMaquinas);

        painelFormulario.add(new JLabel("Data da Manutenção:"));
        txtDataManutencao = new JTextField();
        painelFormulario.add(txtDataManutencao);

        painelFormulario.add(new JLabel("Tipo:"));
        comboTipoManutencao = new JComboBox<>(new String[]{"Preventiva", "Corretiva", "Predictiva"});
        painelFormulario.add(comboTipoManutencao);

        painelFormulario.add(new JLabel("Peças Trocadas:"));
        txtPecasTrocadas = new JTextField();
        painelFormulario.add(txtPecasTrocadas);

        painelFormulario.add(new JLabel("Tempo Parado (horas):"));
        txtTempoParado = new JTextField();
        painelFormulario.add(txtTempoParado);

        painelFormulario.add(new JLabel("Técnico:"));
        comboTecnicos = new JComboBox<>();
        painelFormulario.add(comboTecnicos);

        painelFormulario.add(new JLabel("Observações:"));
        txtObservacoes = new JTextField();
        painelFormulario.add(txtObservacoes);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        JButton btnSalvar = new JButton("Salvar");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnLimpar = new JButton("Limpar");
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnLimpar);
        this.btnSalvar = new JButton();
        this.btnVoltar = new JButton();
        this.btnEditar = new JButton();
        this.btnExcluir = new JButton();

        // Painel da tabela
        tableModel = new DefaultTableModel(new String[]{"ID", "Máquina", "Data", "Tipo", "Peças", "Tempo", "Técnico", "Observações"}, 0);
        tableManutencoes = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableManutencoes);
        tableManutencoes.setFillsViewportHeight(true);
        
        // Listeners
        tableManutencoes.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    int row = tableManutencoes.getSelectedRow();
                    if (row != -1) {
                        manutencaoSelecionadaId = (int) tableModel.getValueAt(row, 0);
                        comboMaquinas.setSelectedItem(tableModel.getValueAt(row, 1).toString());
                        txtDataManutencao.setText(tableModel.getValueAt(row, 2).toString());
                        comboTipoManutencao.setSelectedItem(tableModel.getValueAt(row, 3).toString());
                        txtPecasTrocadas.setText(tableModel.getValueAt(row, 4).toString());
                        txtTempoParado.setText(tableModel.getValueAt(row, 5).toString());
                        comboTecnicos.setSelectedItem(tableModel.getValueAt(row, 6).toString());
                        txtObservacoes.setText(tableModel.getValueAt(row, 7).toString());
                    }
                }
            }
        });

        // Adiciona os componentes ao JFrame
        add(painelFormulario, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        // Carregar máquinas e técnicos ao iniciar
        
        carregarMaquinas();
        carregarTecnicos();
        // Ações dos botões
        btnSalvar.addActionListener(e -> salvarManutencao());
        btnEditar.addActionListener(e -> editarManutencao());
        btnExcluir.addActionListener(e -> excluirManutencao());
        btnLimpar.addActionListener(e -> limparCampos());
    

        // Ação para selecionar uma manutenção na tabela
        tableManutencoes.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                int row = tableManutencoes.getSelectedRow();
                if (row != -1) {
                    // Pega o ID da manutenção selecionada
                    manutencaoSelecionadaId = (int) tableModel.getValueAt(row, 0);
                    // Preenche os campos com os dados da manutenção selecionada
                    comboMaquinas.setSelectedItem(tableModel.getValueAt(row, 1).toString());
                    txtDataManutencao.setText(tableModel.getValueAt(row, 2).toString());
                    comboTipoManutencao.setSelectedItem(tableModel.getValueAt(row, 3).toString());
                    txtPecasTrocadas.setText(tableModel.getValueAt(row, 4).toString());
                    txtTempoParado.setText(tableModel.getValueAt(row, 5).toString());
                    comboTecnicos.setSelectedItem(tableModel.getValueAt(row, 6).toString());
                    txtObservacoes.setText(tableModel.getValueAt(row, 7).toString());
                }
            }
        });
    }

    // Método para carregar as máquinas da API e preencher o comboMaquinas
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
    
    // Método para carregar os técnicos da API e preencher o comboTecnicos
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

    // Método para listar as manutenções cadastradas
    private void listarManutencoes() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:8080/manutencoes");
    
            client.execute(request, response -> {
                if (response.getCode() == 200) {
                    String result = EntityUtils.toString(response.getEntity());
                    JSONArray manutencoes = new JSONArray(result);
                    tableModel.setRowCount(0); // Limpa a tabela antes de preencher

                    for (int i = 0; i < manutencoes.length(); i++) {
                        JSONObject manutencao = manutencoes.getJSONObject(i);
                        tableModel.addRow(new Object[]{
                            manutencao.getInt("id"),
                            manutencao.getString("maquinaNome"),
                            manutencao.getString("dataManutencao"),
                            manutencao.getString("tipo"),
                            manutencao.getString("pecasTrocadas"),
                            manutencao.getInt("tempoParado"),
                            manutencao.getString("tecnicoNome"),
                            manutencao.getString("observacoes")
                        });
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao carregar manutenções: " + response.getCode());
                }
                return null;
            });
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar manutenções: " + e.getMessage());
        }
    }

    // Método para salvar uma nova manutenção
    private void salvarManutencao() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost("http://localhost:8080/manutencoes");
            JSONObject manutencao = new JSONObject();
    
            // Extrai os dados dos campos
            String maquina = comboMaquinas.getSelectedItem().toString().split(" - ")[0]; // ID da máquina
            String tecnico = comboTecnicos.getSelectedItem().toString().split(" - ")[0]; // ID do técnico
    
            manutencao.put("maquinaId", maquina);
            manutencao.put("dataManutencao", txtDataManutencao.getText());
            manutencao.put("tipo", comboTipoManutencao.getSelectedItem());
            manutencao.put("pecasTrocadas", txtPecasTrocadas.getText());
            manutencao.put("tempoParado", Integer.parseInt(txtTempoParado.getText()));
            manutencao.put("tecnicoId", tecnico);
            manutencao.put("observacoes", txtObservacoes.getText());
    
            StringEntity entity = new StringEntity(manutencao.toString());
            request.setEntity(entity);
            request.setHeader("Content-type", "application/json");
    
            client.execute(request, response -> {
                if (response.getCode() == 201) {
                    JOptionPane.showMessageDialog(this, "Manutenção salva com sucesso!");
                    listarManutencoes(); // Atualiza a tabela
                    limparCampos();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao salvar manutenção: " + response.getCode());
                }
                return null;
            });
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar manutenção: " + e.getMessage());
        }
    }

    // Método para editar a manutenção selecionada
    private void editarManutencao() {
        if (manutencaoSelecionadaId == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma manutenção para editar.");
            return;
        }

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPut request = new HttpPut("http://localhost:8080/manutencoes/" + manutencaoSelecionadaId);
            JSONObject manutencao = new JSONObject();

            String maquina = comboMaquinas.getSelectedItem().toString().split(" - ")[0];
            String tecnico = comboTecnicos.getSelectedItem().toString().split(" - ")[0];

            manutencao.put("maquinaId", maquina);
            manutencao.put("dataManutencao", txtDataManutencao.getText());
            manutencao.put("tipo", comboTipoManutencao.getSelectedItem());
            manutencao.put("pecasTrocadas", txtPecasTrocadas.getText());
            manutencao.put("tempoParado", Integer.parseInt(txtTempoParado.getText()));
            manutencao.put("tecnicoId", tecnico);
            manutencao.put("observacoes", txtObservacoes.getText());

            StringEntity entity = new StringEntity(manutencao.toString());
            request.setEntity(entity);
            request.setHeader("Content-type", "application/json");

            client.execute(request, response -> {
                if (response.getCode() == 200) {
                    JOptionPane.showMessageDialog(this, "Manutenção editada com sucesso!");
                    listarManutencoes();
                    limparCampos();
                    manutencaoSelecionadaId = -1; // Reseta a seleção
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao editar manutenção: " + response.getCode());
                }
                return null;
            });
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao editar manutenção: " + e.getMessage());
        }
    }

    // Método para excluir a manutenção selecionada
    private void excluirManutencao() {
        if (manutencaoSelecionadaId == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma manutenção para excluir.");
            return;
        }

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpDelete request = new HttpDelete("http://localhost:8080/manutencoes/" + manutencaoSelecionadaId);

            client.execute(request, response -> {
                if (response.getCode() == 204) {
                    JOptionPane.showMessageDialog(this, "Manutenção excluída com sucesso!");
                    listarManutencoes();
                    limparCampos();
                    manutencaoSelecionadaId = -1; // Reseta a seleção
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao excluir manutenção: " + response.getCode());
                }
                return null;
            });
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir manutenção: " + e.getMessage());
        }
    }

    // Método para limpar os campos
    private void limparCampos() {
        comboMaquinas.setSelectedIndex(0);
        txtDataManutencao.setText("");
        comboTipoManutencao.setSelectedIndex(0);
        txtPecasTrocadas.setText("");
        txtTempoParado.setText("");
        comboTecnicos.setSelectedIndex(0);
        txtObservacoes.setText("");
    }
}
