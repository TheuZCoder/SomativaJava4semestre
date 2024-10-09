package com.theuz.Maquina;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONObject;

public class CadastroMaquinaGUI extends JFrame {

    private JTextField tfManual,tfCodigo, tfNome, tfModelo, tfFabricante, tfDataAquisicao, tfTempoVida, tfLocalizacao, tfDetalhes;

    public CadastroMaquinaGUI() {
        setTitle("Cadastro de Máquina");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(10, 2));

        // Criação dos componentes
        JLabel lblCodigo = new JLabel("Código:");
        tfCodigo = new JTextField();

        JLabel lblNome = new JLabel("Nome:");
        tfNome = new JTextField();

        JLabel lblModelo = new JLabel("Modelo:");
        tfModelo = new JTextField();

        JLabel lblFabricante = new JLabel("Fabricante:");
        tfFabricante = new JTextField();

        JLabel lblDataAquisicao = new JLabel("Data Aquisição:");
        tfDataAquisicao = new JTextField();

        JLabel lblTempoVida = new JLabel("Tempo Vida Estimado:");
        tfTempoVida = new JTextField();

        JLabel lblLocalizacao = new JLabel("Localização:");
        tfLocalizacao = new JTextField();

        JLabel lblDetalhes = new JLabel("Detalhes:");
        tfDetalhes = new JTextField();

        JLabel lblManual = new JLabel("Manual:");
        tfManual = new JTextField();

        JButton btnCadastrar = new JButton("Cadastrar");

        // Adiciona os componentes ao JFrame
        add(lblCodigo);
        add(tfCodigo);
        add(lblNome);
        add(tfNome);
        add(lblModelo);
        add(tfModelo);
        add(lblFabricante);
        add(tfFabricante);
        add(lblDataAquisicao);
        add(tfDataAquisicao);
        add(lblTempoVida);
        add(tfTempoVida);
        add(lblLocalizacao);
        add(tfLocalizacao);
        add(lblDetalhes);
        add(tfDetalhes);
        add(lblManual);
        add(tfManual);
        add(btnCadastrar);
        

        // Ação do botão de cadastro
        btnCadastrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cadastrarMaquina();
            }
        });
    }

    private void cadastrarMaquina() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // Define o endpoint da sua API
            HttpPost post = new HttpPost("http://localhost:8080/maquinas");

            // Cria o objeto JSON com os dados da máquina
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

            // Configura a requisição HTTP
            StringEntity entity = new StringEntity(json.toString());
            post.setEntity(entity);
            post.setHeader("Content-type", "application/json");

            // Envia a requisição e recebe a resposta
            client.execute(post, response -> {
                if (response.getCode() == 200) {
                    JOptionPane.showMessageDialog(null, "Máquina cadastrada com sucesso!");
                } else {
                    JOptionPane.showMessageDialog(null, "Erro ao cadastrar a máquina: " + response.getCode());
                }
                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro: " + ex.getMessage());
        }
    }
}

