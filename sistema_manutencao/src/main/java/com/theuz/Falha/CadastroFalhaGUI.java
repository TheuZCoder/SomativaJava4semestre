package com.theuz.Falha;


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

public class CadastroFalhaGUI extends JFrame {

    HttpGet request = new HttpGet("http://localhost:8080/maquinas");
    HttpPost post = new HttpPost("http://localhost:8080/falha");
    HttpGet requestTecnicos = new HttpGet("http://localhost:8080/tecnicos");

    private final JComboBox<String> comboMaquinas;
    private final JTextField txtDataFalha;
    private final JTextField txtProblema;
    private final JComboBox<String> comboPrioridade;
    private final JComboBox<String> comboTecnicos;
    private final JButton btnSalvar, btnVoltar;
    private final JLabel lblMaquina;
    private final JLabel lblDataFalha;
    private final JLabel lblProblema;
    private final JLabel lblPrioridade;
    private final JLabel lblTecnico;

    public CadastroFalhaGUI() {
        setTitle("Cadastro de Falha");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(7, 2));

        // Labels e campos
        lblMaquina = new JLabel("Máquina:");
        comboMaquinas = new JComboBox<>(new String[]{}); // Aqui você pode buscar as máquinas da API

        lblDataFalha = new JLabel("Data da Falha:");
        txtDataFalha = new JTextField();

        lblProblema = new JLabel("Problema:");
        txtProblema = new JTextField();

        lblPrioridade = new JLabel("Prioridade:");
        comboPrioridade = new JComboBox<>(new String[]{"Baixa", "Média", "Alta"});

        lblTecnico = new JLabel("Técnico:");
        comboTecnicos = new JComboBox<>(new String[]{}); // Aqui você pode buscar os técnicos da API

        btnSalvar = new JButton("Salvar");
        btnVoltar = new JButton("Voltar");

        // Adicionando componentes à tela
        add(lblMaquina);
        add(comboMaquinas);
        add(lblDataFalha);
        add(txtDataFalha);
        add(lblProblema);
        add(txtProblema);
        add(lblPrioridade);
        add(comboPrioridade);
        add(lblTecnico);
        add(comboTecnicos);
        add(new JLabel());  
        add(new JLabel()); 
        add(btnVoltar); 
        add(btnSalvar);

        // Ação do botão salvar
        btnSalvar.addActionListener((ActionEvent e) -> {
            salvarFalha();
        });

        btnVoltar.addActionListener((ActionEvent e) -> {
            dispose();
        });

        // Carregar máquinas e técnicos
        carregarMaquinas();
        carregarTecnicos();
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
            // Define o endpoint da sua API para cadastro de falha
            

            // Cria o objeto JSON com os dados da falha
            JSONObject json = new JSONObject();
            json.put("maquinaId", maquina.split(" - ")[0]); // Extraindo o ID da máquina
            json.put("dataFalha", dataFalha);
            json.put("problema", problema);
            json.put("prioridade", prioridade);
            json.put("tecnicoId", tecnico.split(" - ")[0]); // Extraindo o ID do técnico

            // Configura a requisição HTTP
            StringEntity entity = new StringEntity(json.toString());
            post.setEntity(entity);
            post.setHeader("Content-type", "application/json");

            // Envia a requisição e recebe a resposta
            client.execute(post, response ->  {
                if (response.getCode() == 200) {
                    JOptionPane.showMessageDialog(null, "Falha cadastrada com sucesso!");
                } else {
                    JOptionPane.showMessageDialog(null, "Erro ao cadastrar a falha: " + response.getCode());
                }
                return null;
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro: " + ex.getMessage());
        }
    }

    private void carregarMaquinas() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
          
    
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
    
            client.execute(requestTecnicos, response -> {
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
}
