package com.theuz.Manutencao;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
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
    private final JButton btnSalvar;

    public CadastroManutencaoGUI() {
        setTitle("Cadastro de Manutenção");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(8, 2));

        // Labels e campos
        JLabel lblMaquina = new JLabel("Máquina:");
        comboMaquinas = new JComboBox<>(); // ComboBox vazio que será preenchido pela API

        JLabel lblDataManutencao = new JLabel("Data Manutenção:");
        txtDataManutencao = new JTextField();

        JLabel lblTipo = new JLabel("Tipo de Manutenção:");
        comboTipoManutencao = new JComboBox<>(new String[]{"Preventiva", "Corretiva"});

        JLabel lblPecasTrocadas = new JLabel("Peças Trocadas:");
        txtPecasTrocadas = new JTextField();

        JLabel lblTempoParado = new JLabel("Tempo Parado (horas):");
        txtTempoParado = new JTextField();

        JLabel lblTecnico = new JLabel("Técnico:");
        comboTecnicos = new JComboBox<>(); // ComboBox vazio que será preenchido pela API

        JLabel lblObservacoes = new JLabel("Observações:");
        txtObservacoes = new JTextField();

        btnSalvar = new JButton("Salvar");

        // Adicionando componentes à tela
        add(lblMaquina);
        add(comboMaquinas);
        add(lblDataManutencao);
        add(txtDataManutencao);
        add(lblTipo);
        add(comboTipoManutencao);
        add(lblPecasTrocadas);
        add(txtPecasTrocadas);
        add(lblTempoParado);
        add(txtTempoParado);
        add(lblTecnico);
        add(comboTecnicos);
        add(lblObservacoes);
        add(txtObservacoes);
        add(new JLabel());  // Espaço vazio
        add(btnSalvar);

        // Carrega os dados das máquinas e técnicos
        carregarMaquinas();
        carregarTecnicos();

        // Ação do botão salvar
        btnSalvar.addActionListener((ActionEvent e) -> {  
            salvarManutencao();
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
    
    

    // Método para salvar a manutenção
    private void salvarManutencao() {
        // Captura os dados dos campos
        String maquina = (String) comboMaquinas.getSelectedItem();
        String dataManutencao = txtDataManutencao.getText();
        String tipoManutencao = (String) comboTipoManutencao.getSelectedItem();
        String pecasTrocadas = txtPecasTrocadas.getText();
        String tempoParado = txtTempoParado.getText();
        String tecnico = (String) comboTecnicos.getSelectedItem();
        String observacoes = txtObservacoes.getText();

        // Monta o JSON com os dados da manutenção
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // Define o endpoint da sua API para cadastro de manutenção
            HttpPost post = new HttpPost("http://localhost:8080/manutencao");

            // Cria o objeto JSON com os dados da manutenção
            JSONObject json = new JSONObject();
            json.put("maquinaId", maquina.split(" - ")[0]); // Pega o ID da máquina
            json.put("dataManutencao", dataManutencao);
            json.put("tipo", tipoManutencao);
            json.put("pecasTrocadas", pecasTrocadas);
            json.put("tempoParado", tempoParado);
            json.put("tecnicoId", tecnico.split(" - ")[0]); // Pega o ID do técnico
            json.put("observacoes", observacoes);

            // Configura a requisição HTTP
            StringEntity entity = new StringEntity(json.toString());
            post.setEntity(entity);
            post.setHeader("Content-type", "application/json");

            // Envia a requisição e recebe a resposta
            client.execute(post, response -> { 
                if (response.getCode() == 200) {
                    JOptionPane.showMessageDialog(null, "Manutenção cadastrada com sucesso!");
                } else {
                    JOptionPane.showMessageDialog(null, "Erro ao cadastrar a manutenção: " + response.getCode());
                }
                return null;
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro: " + ex.getMessage());
        }
    }
}
