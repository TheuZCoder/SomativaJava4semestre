package com.theuz.Tecnico;


import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONObject;

public class CadastroTecnicoGUI extends JFrame {

    private final JTextField txtNome;
    private final JTextField txtEspecialidade;
    private final JTextField txtDisponibilidade;
    private final JButton btnSalvar, btnVoltar;

    public CadastroTecnicoGUI() {
        setTitle("Cadastro de Técnico");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(5, 2));

        // Labels e campos
        JLabel lblNome = new JLabel("Nome:");
        txtNome = new JTextField();

        JLabel lblEspecialidade = new JLabel("Especialidade:");
        txtEspecialidade = new JTextField();

        JLabel lblDisponibilidade = new JLabel("Disponibilidade:");
        txtDisponibilidade = new JTextField();

        btnVoltar = new JButton("Voltar");

        btnSalvar = new JButton("Salvar");

        // Adicionando componentes à tela
        add(lblNome);
        add(txtNome);
        add(lblEspecialidade);
        add(txtEspecialidade);
        add(lblDisponibilidade);
        add(txtDisponibilidade);
        add(new JLabel());  // Espaço vazio
        add(new JLabel()); 
        add(btnVoltar); 
        add(btnSalvar);

        // Ação do botão salvar
        btnSalvar.addActionListener((ActionEvent e) -> {
            salvarTecnico();
        });

        btnVoltar.addActionListener((ActionEvent e) -> {
            dispose();
        });
    }

    // Método para salvar o técnico
    private void salvarTecnico() {
        // Captura os dados dos campos
        String nome = txtNome.getText();
        String especialidade = txtEspecialidade.getText();
        String disponibilidade = txtDisponibilidade.getText();

        // Monta o JSON com os dados do técnico
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // Define o endpoint da sua API para cadastro de técnico
            HttpPost post = new HttpPost("http://localhost:8080/tecnicos");

            // Cria o objeto JSON com os dados do técnico
            JSONObject json = new JSONObject();
            json.put("nome", nome);
            json.put("especialidade", especialidade);
            json.put("disponibilidade", disponibilidade);

            // Configura a requisição HTTP
            StringEntity entity = new StringEntity(json.toString());
            post.setEntity(entity);
            post.setHeader("Content-type", "application/json");

            // Envia a requisição e recebe a resposta
            client.execute(post, response -> {
                if (response.getCode() == 200) {
                    JOptionPane.showMessageDialog(null, "Técnico cadastrado com sucesso!");
                } else {
                    JOptionPane.showMessageDialog(null, "Erro ao cadastrar o técnico: " + response.getCode());
                }
                return null;
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro: " + ex.getMessage());
        }
    }

}
