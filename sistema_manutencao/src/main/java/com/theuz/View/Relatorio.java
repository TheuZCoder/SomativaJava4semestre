package com.theuz.View;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Relatorio extends JFrame {

    private final JTable tabelaFalhas;
    private final DefaultTableModel modeloTabelaFalhas;
    private final JTable tabelaManutencoes;
    private final DefaultTableModel modeloTabelaManutencoes;

    public Relatorio() {
        setTitle("Relatório de Manutenções e Falhas Cadastradas");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Criar modelo e tabela para falhas
        String[] columnNamesFalhas = { "Máquina", "Data da Falha", "Problema", "Prioridade", "Técnico" };
        modeloTabelaFalhas = new DefaultTableModel(columnNamesFalhas, 0);
        tabelaFalhas = new JTable(modeloTabelaFalhas);

        // Criar modelo e tabela para manutenções
        String[] columnNamesManutencoes = { "Máquina", "Data da Manutenção", "Tipo", "Peças Trocadas", "Técnico" };
        modeloTabelaManutencoes = new DefaultTableModel(columnNamesManutencoes, 0);
        tabelaManutencoes = new JTable(modeloTabelaManutencoes);

        // Adicionar títulos para cada tabela
        JLabel tituloFalhas = new JLabel("Relatório de Falhas");
        JLabel tituloManutencoes = new JLabel("Relatório de Manutenção");

        // Adicionar tabelas a JScrollPane
        JScrollPane scrollPaneFalhas = new JScrollPane(tabelaFalhas);
        JScrollPane scrollPaneManutencoes = new JScrollPane(tabelaManutencoes);

        // Adicionar os títulos e tabelas ao layout
        add(tituloFalhas, BorderLayout.NORTH);
        add(scrollPaneFalhas, BorderLayout.CENTER);
        add(tituloManutencoes, BorderLayout.SOUTH);
        add(scrollPaneManutencoes, BorderLayout.SOUTH);

        // Carregar os dados nas tabelas
        carregarManutencoesFalhas();
    }

    private void carregarManutencoesFalhas() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // Requisição para obter manutenções
            HttpGet requestManutencao = new HttpGet("http://localhost:8080/manutencao");
            client.execute(requestManutencao, response -> {
                if (response.getCode() == 200) {
                    String result = EntityUtils.toString(response.getEntity());
                    System.out.println("Resposta de manutenções: " + result); // Log para verificar a resposta

                    // Criar o JSONArray a partir da resposta
                    JSONArray manutencoes = new JSONArray(result);
                    for (int i = 0; i < manutencoes.length(); i++) {
                        JSONObject manutencao = manutencoes.getJSONObject(i);
                        String nomeMaquina = manutencao.getJSONObject("maquina").getString("nome");
                        String nomeTecnico = manutencao.getJSONObject("tecnico").getString("nome");

                        Object[] rowData = {
                                nomeMaquina,
                                manutencao.getString("dataManutencao"),
                                manutencao.getString("tipo"),
                                manutencao.getString("pecasTrocadas"),
                                nomeTecnico
                        };
                        modeloTabelaManutencoes.addRow(rowData);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao carregar manutenções: " + response.getCode());
                }
                return null;
            });

            // Requisição para obter falhas
            HttpGet requestFalha = new HttpGet("http://localhost:8080/falha");
            client.execute(requestFalha, response -> {
                if (response.getCode() == 200) {
                    String result = EntityUtils.toString(response.getEntity());
                    System.out.println("Resposta de falhas: " + result); // Log para verificar a resposta

                    JSONArray falhas = new JSONArray(result);
                    for (int i = 0; i < falhas.length(); i++) {
                        JSONObject falha = falhas.getJSONObject(i);
                        String nomeMaquina = falha.getJSONObject("maquina").getString("nome");
                        String nomeTecnico = falha.getJSONObject("tecnico").getString("nome");

                        Object[] rowData = {
                                nomeMaquina,
                                falha.getString("dataFalha"),
                                falha.getString("problema"),
                                falha.getString("prioridade"),
                                nomeTecnico
                        };
                        modeloTabelaFalhas.addRow(rowData);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao carregar falhas: " + response.getCode());
                }
                return null;
            });

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + e.getMessage());
        } catch (JSONException e) {
            JOptionPane.showMessageDialog(this, "Erro no formato da resposta: " + e.getMessage());
        }
    }
}
