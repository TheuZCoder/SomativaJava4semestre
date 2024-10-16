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
    private final JButton btnSalvar, btnEditar, btnExcluir, btnVoltar;
    private final JLabel lblMaquina;
    private final JLabel lblDataFalha;
    private final JLabel lblProblema;
    private final JLabel lblPrioridade;
    private final JLabel lblTecnico;
    private final JTable tableFalhas;
    private final DefaultTableModel tableModel;

    public CadastroFalhaGUI() {
        setTitle("Cadastro de Falha");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel superior para os campos de cadastro
        JPanel panelCadastro = new JPanel(new GridLayout(7, 2));

        lblMaquina = new JLabel("Máquina:");
        comboMaquinas = new JComboBox<>(new String[]{});

        lblDataFalha = new JLabel("Data da Falha:");
        txtDataFalha = new JTextField();

        lblProblema = new JLabel("Problema:");
        txtProblema = new JTextField();

        lblPrioridade = new JLabel("Prioridade:");
        comboPrioridade = new JComboBox<>(new String[]{"Baixa", "Média", "Alta"});

        lblTecnico = new JLabel("Técnico:");
        comboTecnicos = new JComboBox<>(new String[]{});

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

        panelBotoes.add(btnVoltar);
        panelBotoes.add(btnSalvar);
        panelBotoes.add(btnEditar);
        panelBotoes.add(btnExcluir);

        // Adicionando tabela para exibir falhas cadastradas
        tableModel = new DefaultTableModel(new Object[]{"ID", "Máquina", "Data Falha", "Problema", "Prioridade", "Técnico"}, 0);
        tableFalhas = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableFalhas);

        // Adicionando tudo ao frame
        add(panelCadastro, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBotoes, BorderLayout.SOUTH);

        // Ação do botão salvar
        btnSalvar.addActionListener((ActionEvent e) -> {
            salvarFalha();
        });

        // Ação do botão editar
        btnEditar.addActionListener((ActionEvent e) -> {
            editarFalha();
        });

        // Ação do botão excluir
        btnExcluir.addActionListener((ActionEvent e) -> {
            excluirFalha();
        });

        btnVoltar.addActionListener((ActionEvent e) -> {
            dispose();
        });

        // Carregar máquinas e técnicos
        carregarMaquinas();
        carregarTecnicos();
        carregarFalhas();
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
                    carregarFalhas(); // Atualiza a tabela após salvar
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
    private void carregarFalhas() {
        // Lógica para carregar falhas da API e popular a tabela
    }

    // Método para editar uma falha
    private void editarFalha() {
        // Lógica para editar falha selecionada na tabela
    }

    // Método para excluir uma falha
    private void excluirFalha() {
        // Lógica para excluir a falha selecionada na tabela
    }

    // Métodos para carregar máquinas e técnicos (mesma lógica que já foi implementada)
    private void carregarMaquinas() { /* ... */ }
    private void carregarTecnicos() { /* ... */ }

}
