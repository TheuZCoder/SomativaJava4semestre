package com.theuz.Maquina;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class CadastroMaquinaGUI extends JFrame {

    private JTextField tfCodigo, tfNome, tfModelo, tfFabricante, tfDataAquisicao, tfTempoVida, tfLocalizacao, tfDetalhes, tfManual;
    private JTable tabelaMaquinas;
    private DefaultTableModel modeloTabela;
    private JButton btnCadastrar, btnEditar, btnExcluir, btnVoltar;

    public CadastroMaquinaGUI() {
        setTitle("Cadastro de Máquina - CRUD");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel superior (Formulário)
        JPanel painelFormulario = new JPanel(new GridLayout(10, 2));
        
        painelFormulario.add(new JLabel("Código:"));
        tfCodigo = new JTextField();
        painelFormulario.add(tfCodigo);

        painelFormulario.add(new JLabel("Nome:"));
        tfNome = new JTextField();
        painelFormulario.add(tfNome);

        painelFormulario.add(new JLabel("Modelo:"));
        tfModelo = new JTextField();
        painelFormulario.add(tfModelo);

        painelFormulario.add(new JLabel("Fabricante:"));
        tfFabricante = new JTextField();
        painelFormulario.add(tfFabricante);

        painelFormulario.add(new JLabel("Data Aquisição:"));
        tfDataAquisicao = new JTextField();
        painelFormulario.add(tfDataAquisicao);

        painelFormulario.add(new JLabel("Tempo de Vida Estimado:"));
        tfTempoVida = new JTextField();
        painelFormulario.add(tfTempoVida);

        painelFormulario.add(new JLabel("Localização:"));
        tfLocalizacao = new JTextField();
        painelFormulario.add(tfLocalizacao);

        painelFormulario.add(new JLabel("Detalhes:"));
        tfDetalhes = new JTextField();
        painelFormulario.add(tfDetalhes);

        painelFormulario.add(new JLabel("Manual:"));
        tfManual = new JTextField();
        painelFormulario.add(tfManual);

        // Painel de botões
        JPanel painelBotoes = new JPanel();
        btnCadastrar = new JButton("Cadastrar");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        btnVoltar = new JButton("Voltar");

        painelBotoes.add(btnCadastrar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnVoltar);

        // Tabela de máquinas
        String[] colunas = {"ID", "Código", "Nome", "Modelo", "Fabricante", "Data Aquisição", "Tempo Vida", "Localização", "Detalhes", "Manual"};
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaMaquinas = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaMaquinas);

        // Adicionando componentes ao JFrame
        add(painelFormulario, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        // Carregar máquinas cadastradas
        carregarMaquinas();

        // Ações dos botões
        btnCadastrar.addActionListener(e -> cadastrarMaquina());
        btnEditar.addActionListener(e -> editarMaquina());
        btnExcluir.addActionListener(e -> excluirMaquina());
        btnVoltar.addActionListener(e -> dispose());
    }

    private void carregarMaquinas() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet("http://localhost:8080/maquinas");
            client.execute(get, response -> {
                if (response.getCode() == 200) {
                    String result = EntityUtils.toString(response.getEntity());
                    JSONArray maquinas = new JSONArray(result);
                    for (int i = 0; i < maquinas.length(); i++) {
                        JSONObject maquina = maquinas.getJSONObject(i);
                        Object[] rowData = {
                            maquina.getInt("id"),
                            maquina.getString("codigo"),
                            maquina.getString("nome"),
                            maquina.getString("modelo"),
                            maquina.getString("fabricante"),
                            maquina.getString("dataAquisicao"),
                            maquina.getInt("tempoVidaEstimado"),
                            maquina.getString("localizacao"),
                            maquina.getString("detalhes"),
                            maquina.getString("manual")
                        };
                        modeloTabela.addRow(rowData);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Erro ao carregar máquinas: " + response.getCode());
                }
                return null;
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void cadastrarMaquina() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("http://localhost:8080/maquinas");
            JSONObject json = new JSONObject();
            json.put("codigo", tfCodigo.getText());
            json.put("nome", tfNome.getText());
            json.put("modelo", tfModelo.getText());
            json.put("fabricante", tfFabricante.getText());
            json.put("dataAquisicao", tfDataAquisicao.getText());
            json.put("tempoVidaEstimado", Integer.parseInt(tfTempoVida.getText()));
            json.put("localizacao", tfLocalizacao.getText());
            json.put("detalhes", tfDetalhes.getText());
            json.put("manual", tfManual.getText());

            StringEntity entity = new StringEntity(json.toString());
            post.setEntity(entity);
            post.setHeader("Content-type", "application/json");

            client.execute(post, response -> {
                if (response.getCode() == 200) {
                    JOptionPane.showMessageDialog(null, "Máquina cadastrada com sucesso!");
                    modeloTabela.setRowCount(0); // Limpa a tabela
                    carregarMaquinas(); // Atualiza a tabela
                } else {
                    JOptionPane.showMessageDialog(null, "Erro ao cadastrar máquina: " + response.getCode());
                }
                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro: " + ex.getMessage());
        }
    }

    private void editarMaquina() {
        int selectedRow = tabelaMaquinas.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tabelaMaquinas.getValueAt(selectedRow, 0);
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = new HttpPost("http://localhost:8080/maquinas/" + id); // Use o método PUT em vez de POST na sua API
                JSONObject json = new JSONObject();
                json.put("codigo", tfCodigo.getText());
                json.put("nome", tfNome.getText());
                json.put("modelo", tfModelo.getText());
                json.put("fabricante", tfFabricante.getText());
                json.put("dataAquisicao", tfDataAquisicao.getText());
                json.put("tempoVidaEstimado", Integer.parseInt(tfTempoVida.getText()));
                json.put("localizacao", tfLocalizacao.getText());
                json.put("detalhes", tfDetalhes.getText());
                json.put("manual", tfManual.getText());

                StringEntity entity = new StringEntity(json.toString());
                post.setEntity(entity);
                post.setHeader("Content-type", "application/json");

                client.execute(post, response -> {
                    if (response.getCode() == 200) {
                        JOptionPane.showMessageDialog(null, "Máquina atualizada com sucesso!");
                        modeloTabela.setRowCount(0); // Limpa a tabela
                        carregarMaquinas(); // Atualiza a tabela
                    } else {
                        JOptionPane.showMessageDialog(null, "Erro ao atualizar máquina: " + response.getCode());
                    }
                    return null;
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Selecione uma máquina para editar.");
        }
    }

    private void excluirMaquina() {
        int selectedRow = tabelaMaquinas.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tabelaMaquinas.getValueAt(selectedRow, 0);
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpDelete delete = new HttpDelete("http://localhost:8080/maquinas/" + id);
                client.execute(delete, response -> {
                    int statusCode = response.getCode();
                    
                    // Trata tanto o código 200 quanto o 204 como sucesso
                    if (statusCode == 200 || statusCode == 204) {
                        JOptionPane.showMessageDialog(null, "Máquina excluída com sucesso!");
                        modeloTabela.setRowCount(0); // Limpa a tabela
                        carregarMaquinas(); // Atualiza a tabela
                    } else {
                        JOptionPane.showMessageDialog(null, "Erro ao excluir máquina: " + statusCode);
                    }
                    return null;
                });
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Selecione uma máquina para excluir.");
        }
    }
    
}
