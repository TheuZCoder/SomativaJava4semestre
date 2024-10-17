package com.theuz.Maquina;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class CadastroMaquinaGUI extends JFrame {

    private JTextField tfCodigo, tfNome, tfModelo, tfFabricante, tfDataAquisicao, tfTempoVida, tfLocalizacao,
            tfDetalhes, tfManual;
    private JTable tabelaMaquinas;
    private DefaultTableModel modeloTabela;
    private JButton btnCadastrar, btnEditar, btnExcluir, btnVoltar, btnCancelar;
    private int maquinaSelecionadaId = -1;

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
        btnCadastrar = new JButton("Salvar");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        btnVoltar = new JButton("Voltar");
        btnCancelar = new JButton("Cancelar");

        painelBotoes.add(btnVoltar);
        painelBotoes.add(btnCadastrar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnCancelar);

        // Tabela de máquinas
        String[] colunas = { "ID", "Código", "Nome", "Modelo", "Fabricante", "Data Aquisição", "Tempo Vida",
                "Localização", "Detalhes", "Manual" };
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
        btnCadastrar.addActionListener(e -> {
            if (maquinaSelecionadaId == -1) {
                cadastrarMaquina();
            } else {
                atualizarMaquina(maquinaSelecionadaId);
            }
        });
        btnEditar.addActionListener(e -> carregarDadosParaEdicao());
        btnExcluir.addActionListener((ActionEvent e) -> {
            carregarDadosParaEdicao();
            excluirMaquina();
        });
        btnVoltar.addActionListener(e -> dispose());
        btnCancelar.addActionListener(e -> limparCampos());
    }

    private void carregarMaquinas() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet("http://localhost:8080/maquinas");
            client.execute(get, response -> {
                if (response.getCode() == 200) {
                    String result = EntityUtils.toString(response.getEntity());
                    JSONArray maquinas = new JSONArray(result);

                    modeloTabela.setRowCount(0);

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
        String tempoParadoText =  tfTempoVida.getText();
        if (!tempoParadoText.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um valor numérico válido para 'Tempo de vida'.",
                    "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                    tfTempoVida.requestFocus(); // Focar no campo para correção
            return; // Não prossegue se a validação falhar
        }

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
                    limparCampos();
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

    private void carregarDadosParaEdicao() {
        int selectedRow = tabelaMaquinas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma máquina para editar.");
            return;
        }

        // Obtendo o ID da máquina selecionada
        maquinaSelecionadaId = (int) modeloTabela.getValueAt(selectedRow, 0);

        // Carregando os dados da máquina nos campos de texto
        tfCodigo.setText((String) modeloTabela.getValueAt(selectedRow, 1));
        tfNome.setText((String) modeloTabela.getValueAt(selectedRow, 2));
        tfModelo.setText((String) modeloTabela.getValueAt(selectedRow, 3));
        tfFabricante.setText((String) modeloTabela.getValueAt(selectedRow, 4));
        tfDataAquisicao.setText((String) modeloTabela.getValueAt(selectedRow, 5));
        tfTempoVida.setText(String.valueOf(modeloTabela.getValueAt(selectedRow, 6))); // Converter para String
        tfLocalizacao.setText((String) modeloTabela.getValueAt(selectedRow, 7));
        tfDetalhes.setText((String) modeloTabela.getValueAt(selectedRow, 8));
        tfManual.setText((String) modeloTabela.getValueAt(selectedRow, 9));
    }

    // Método para atualizar os dados da máquina
    private void atualizarMaquina(int id) {
        // Obtendo os dados dos campos de texto
        String codigo = tfCodigo.getText();
        String nome = tfNome.getText();
        String modelo = tfModelo.getText();
        String fabricante = tfFabricante.getText();
        String dataAquisicao = tfDataAquisicao.getText();
        String tempoVidaText = tfTempoVida.getText();
        String localizacao = tfLocalizacao.getText();
        String detalhes = tfDetalhes.getText();
        String manual = tfManual.getText();

        if (!tempoVidaText.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um valor numérico válido para 'Tempo de vida'.",
                    "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                    tfTempoVida.requestFocus(); // Focar no campo para correção
            return; // Não prossegue se a validação falhar
        }

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPut put = new HttpPut("http://localhost:8080/maquinas/" + id);
            JSONObject json = new JSONObject();
            json.put("codigo", codigo);
            json.put("nome", nome);
            json.put("modelo", modelo);
            json.put("fabricante", fabricante);
            json.put("dataAquisicao", dataAquisicao);
            json.put("tempoVidaEstimado", Integer.parseInt(tempoVidaText)); // Convertendo para Integer
            json.put("localizacao", localizacao);
            json.put("detalhes", detalhes);
            json.put("manual", manual);

            StringEntity entity = new StringEntity(json.toString());
            put.setEntity(entity);
            put.setHeader("Content-type", "application/json");

            client.execute(put, response -> {
                if (response.getCode() == 200) {
                    JOptionPane.showMessageDialog(null, "Máquina atualizada com sucesso!");
                    limparCampos();
                    carregarMaquinas(); // Atualizar a tabela após edição
                    maquinaSelecionadaId = -1; // Limpar seleção
                } else {
                    JOptionPane.showMessageDialog(null, "Erro ao atualizar a máquina: " + response.getCode());
                }
                return null;
            });
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar máquina: " + e.getMessage());
        }
    }

    private void excluirMaquina() {

        // Confirmação antes de excluir
        int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o campo selecionado?",
                "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirmacao != JOptionPane.YES_OPTION) {
            limparCampos();
            return;
        }

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
                        limparCampos();
                    } else if (statusCode == 500) { // Ou qualquer outro código que sua API retorne para chave estrangeira
                        JOptionPane.showMessageDialog(null, "Erro ao excluir máquina: Esta máquina está sendo utilizada em outra tabela.");
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

    private void limparCampos() {
        tfCodigo.setText("");
        tfNome.setText("");
        tfModelo.setText("");
        tfFabricante.setText("");
        tfDataAquisicao.setText("");
        tfTempoVida.setText("");
        tfLocalizacao.setText("");
        tfDetalhes.setText("");
        tfManual.setText("");
        maquinaSelecionadaId = -1;
    }
}
