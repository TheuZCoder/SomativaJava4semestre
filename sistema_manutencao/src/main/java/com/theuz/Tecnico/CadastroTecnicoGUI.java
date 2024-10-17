package com.theuz.Tecnico;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JButton;
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

public class CadastroTecnicoGUI extends JFrame {

    private final JTextField txtNome;
    private final JTextField txtEspecialidade;
    private final JTextField txtDisponibilidade;
    private final JButton btnSalvar, btnVoltar, btnExcluir, btnEditar;
    private final JTable tabelaTecnicos;
    private final DefaultTableModel modeloTabelaTecnicos;

    private int tecnicoSelecionadoId = -1;

    public CadastroTecnicoGUI() {
        setTitle("Cadastro de Técnico");
        setSize(800, 600); // Aumentando o tamanho para se alinhar à tela de máquinas
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel de formulário
        JPanel painelFormulario = new JPanel(new GridLayout(4, 2)); // Ajustando o espaçamento para ficar mais
                                                                            // organizado

        // Labels e campos
        JLabel lblNome = new JLabel("Nome:");
        txtNome = new JTextField();

        JLabel lblEspecialidade = new JLabel("Especialidade:");
        txtEspecialidade = new JTextField();

        JLabel lblDisponibilidade = new JLabel("Disponibilidade:");
        txtDisponibilidade = new JTextField();

        painelFormulario.add(lblNome);
        painelFormulario.add(txtNome);
        painelFormulario.add(lblEspecialidade);
        painelFormulario.add(txtEspecialidade);
        painelFormulario.add(lblDisponibilidade);
        painelFormulario.add(txtDisponibilidade);

        add(painelFormulario, BorderLayout.NORTH); // Formulário no topo

        // Tabela para exibir os técnicos cadastrados
        String[] colunas = { "ID", "Nome", "Especialidade", "Disponibilidade" };
        modeloTabelaTecnicos = new DefaultTableModel(colunas, 0);
        tabelaTecnicos = new JTable(modeloTabelaTecnicos);
        JScrollPane scrollPane = new JScrollPane(tabelaTecnicos);
        add(scrollPane, BorderLayout.CENTER); // Tabela no centro

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout()); // Organizando os botões em linha
        btnSalvar = new JButton("Salvar");
        btnVoltar = new JButton("Voltar");
        btnExcluir = new JButton("Excluir");
        btnEditar = new JButton("Editar");

        painelBotoes.add(btnVoltar);
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);

        add(painelBotoes, BorderLayout.SOUTH); // Botões na parte inferior

        // Ação dos botões
        btnSalvar.addActionListener((ActionEvent e) -> {
            if (tecnicoSelecionadoId == -1) {
                salvarTecnico();
            } else {
                atualizarTecnico(tecnicoSelecionadoId);
            }
        });

        btnVoltar.addActionListener((ActionEvent e) -> {
            dispose();
        });

        btnExcluir.addActionListener((ActionEvent e) -> {
            carregarDadosParaEdicao();
            excluirTecnico();
        });

        btnEditar.addActionListener((ActionEvent e) -> {
            carregarDadosParaEdicao();
        });

        // Carregar técnicos ao iniciar
        carregarTecnicos();
    }

    // Método para salvar o técnico
    private void salvarTecnico() {
        String nome = txtNome.getText();
        String especialidade = txtEspecialidade.getText();
        String disponibilidade = txtDisponibilidade.getText();

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("http://localhost:8080/tecnicos");
            JSONObject json = new JSONObject();
            json.put("nome", nome);
            json.put("especialidade", especialidade);
            json.put("disponibilidade", disponibilidade);

            StringEntity entity = new StringEntity(json.toString());
            post.setEntity(entity);
            post.setHeader("Content-type", "application/json");

            client.execute(post, response -> {
                if (response.getCode() == 200) {
                    JOptionPane.showMessageDialog(null, "Técnico cadastrado com sucesso!");
                    limparCampos();
                    carregarTecnicos(); // Atualizar a tabela após salvar
                } else {
                    JOptionPane.showMessageDialog(null, "Erro ao cadastrar o técnico: " + response.getCode());
                }
                return null;
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro: " + ex.getMessage());
        }
    }

    // Método para carregar os técnicos na tabela
    private void carregarTecnicos() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet("http://localhost:8080/tecnicos");
            client.execute(get, response -> {
                if (response.getCode() == 200) {
                    String result = EntityUtils.toString(response.getEntity());
                    JSONArray tecnicos = new JSONArray(result);

                    modeloTabelaTecnicos.setRowCount(0); // Limpar a tabela
                    for (int i = 0; i < tecnicos.length(); i++) {
                        JSONObject tecnico = tecnicos.getJSONObject(i);
                        Object[] rowData = {
                                tecnico.getInt("id"),
                                tecnico.getString("nome"),
                                tecnico.getString("especialidade"),
                                tecnico.getString("disponibilidade")
                        };
                        modeloTabelaTecnicos.addRow(rowData);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao carregar técnicos: " + response.getCode());
                }
                return null;
            });
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + e.getMessage());
        }
    }

    // Método para excluir o técnico selecionado
    private void excluirTecnico() {
        int selectedRow = tabelaTecnicos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um técnico para excluir.");
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

        int id = (int) modeloTabelaTecnicos.getValueAt(selectedRow, 0);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpDelete delete = new HttpDelete("http://localhost:8080/tecnicos/" + id);
            client.execute(delete, response -> {
                if (response.getCode() == 204) {
                    JOptionPane.showMessageDialog(null, "Técnico excluído com sucesso!");
                    carregarTecnicos(); // Atualizar a tabela após exclusão
                    limparCampos();
                } else {
                    JOptionPane.showMessageDialog(null, "Erro ao excluir o técnico: " + response.getCode());
                }
                return null;
            });
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir técnico: " + e.getMessage());
        }
    }

    // Método para carregar dados do técnico selecionado para edição
    private void carregarDadosParaEdicao() {
        int selectedRow = tabelaTecnicos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um técnico para editar.");
            return;
        }

        tecnicoSelecionadoId = (int) modeloTabelaTecnicos.getValueAt(selectedRow, 0);
        txtNome.setText((String) modeloTabelaTecnicos.getValueAt(selectedRow, 1));
        txtEspecialidade.setText((String) modeloTabelaTecnicos.getValueAt(selectedRow, 2));
        txtDisponibilidade.setText((String) modeloTabelaTecnicos.getValueAt(selectedRow, 3));
    }

    // Método para atualizar os dados do técnico
    private void atualizarTecnico(int id) {
        String nome = txtNome.getText();
        String especialidade = txtEspecialidade.getText();
        String disponibilidade = txtDisponibilidade.getText();

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPut put = new HttpPut("http://localhost:8080/tecnicos/" + id);
            JSONObject json = new JSONObject();
            json.put("nome", nome);
            json.put("especialidade", especialidade);
            json.put("disponibilidade", disponibilidade);

            StringEntity entity = new StringEntity(json.toString());
            put.setEntity(entity);
            put.setHeader("Content-type", "application/json");

            client.execute(put, response -> {
                if (response.getCode() == 200) {
                    JOptionPane.showMessageDialog(null, "Técnico atualizado com sucesso!");
                    limparCampos();
                    carregarTecnicos(); // Atualizar a tabela após edição
                    tecnicoSelecionadoId = -1; // Limpar seleção
                } else {
                    JOptionPane.showMessageDialog(null, "Erro ao atualizar o técnico: " + response.getCode());
                }
                return null;
            });
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar técnico: " + e.getMessage());
        }
    }

    private void limparCampos() {
        txtNome.setText("");
        txtEspecialidade.setText("");
        txtDisponibilidade.setText("");
        tecnicoSelecionadoId = -1;
    }
}
