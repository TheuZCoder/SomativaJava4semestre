package com.theuz.Relatorio;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class RelatorioPDF extends JFrame {

    private JFrame frame;

    public RelatorioPDF() {
        frame = new JFrame("Gerar Relatório PDF");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(null);

        JButton btnGerar = new JButton("Gerar Relatório");
        btnGerar.setBounds(30, 80, 150, 30);
        frame.add(btnGerar);

        btnGerar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gerarRelatorio();
            }
        });

        frame.setVisible(true);
    }

    private void gerarRelatorio() {
        // Usando JFileChooser para selecionar o local de salvamento
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Relatório PDF");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File("relatorio.pdf"));

        int userSelection = fileChooser.showSaveDialog(frame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String nomeArquivo = fileToSave.getAbsolutePath();

            try {
                // Criando o PDF
                PdfWriter writer = new PdfWriter(nomeArquivo);
                PdfDocument pdfDocument = new PdfDocument(writer);
                Document document = new Document(pdfDocument);

                // Adicionando conteúdo ao PDF
                document.add(new Paragraph("Relatório de Manutenções e Falhas"));
                document.add(new Paragraph("==================================="));
                document.add(new Paragraph("Manutenções:"));

                // Adicionando informações de manutenções
                listarManutencoes(document);

                document.add(new Paragraph("==================================="));
                document.add(new Paragraph("Falhas:"));

                // Adicionando informações de falhas
                listarFalhas(document);

                document.close();
                JOptionPane.showMessageDialog(frame, "Relatório gerado com sucesso!\nCaminho: " + nomeArquivo);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Erro ao gerar o relatório: " + ex.getMessage());
            }
        }
    }

    private void listarManutencoes(Document document) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:8080/manutencao");
            client.execute(request, response -> {
                if (response.getCode() == 200) {
                    String result = EntityUtils.toString(response.getEntity());
                    JSONArray manutencoes = new JSONArray(result);

                    for (int i = 0; i < manutencoes.length(); i++) {
                        JSONObject manutencao = manutencoes.getJSONObject(i);
                        String maquinaNome = manutencao.getJSONObject("maquina").getString("nome");
                        String tecnicoNome = manutencao.getJSONObject("tecnico").getString("nome");

                        // Adicionando dados da manutenção ao PDF
                        document.add(new Paragraph("ID: " + manutencao.getInt("id")));
                        document.add(new Paragraph("Máquina: " + maquinaNome));
                        document.add(new Paragraph("Data: " + manutencao.getString("dataManutencao")));
                        document.add(new Paragraph("Tipo: " + manutencao.getString("tipo")));
                        document.add(new Paragraph("Peças Trocadas: " + manutencao.getString("pecasTrocadas")));
                        document.add(new Paragraph("Tempo Parado: " + manutencao.getInt("tempoParado") + " minutos"));
                        document.add(new Paragraph("Técnico: " + tecnicoNome));
                        document.add(new Paragraph("Observações: " + manutencao.getString("observacoes")));
                        document.add(new Paragraph("==================================="));
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

    private void listarFalhas(Document document) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:8080/falha");
            client.execute(request, response -> {
                if (response.getCode() == 200) {
                    String result = EntityUtils.toString(response.getEntity());
                    JSONArray falhas = new JSONArray(result);

                    for (int i = 0; i < falhas.length(); i++) {
                        JSONObject falha = falhas.getJSONObject(i);
                        String maquinaNome = falha.getJSONObject("maquina").getString("nome");
                        String tecnicoNome = falha.getJSONObject("tecnico").getString("nome");

                        // Adicionando dados da falha ao PDF
                        document.add(new Paragraph("ID: " + falha.getInt("id")));
                        document.add(new Paragraph("Máquina: " + maquinaNome));
                        document.add(new Paragraph("Data: " + falha.getString("dataFalha")));
                        document.add(new Paragraph("Problema: " + falha.getString("problema")));
                        document.add(new Paragraph("Prioridade: " + falha.getString("prioridade")));
                        document.add(new Paragraph("Técnico: " + tecnicoNome));
                        document.add(new Paragraph("==================================="));
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao carregar falhas: " + response.getCode());
                }
                return null;
            });
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar falhas: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new RelatorioPDF();
    }
}
