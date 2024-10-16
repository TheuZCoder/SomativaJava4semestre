package com.theuz.TelaPrincipal;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.theuz.Falha.CadastroFalhaGUI;
import com.theuz.Manutencao.CadastroManutencaoGUI;
import com.theuz.Maquina.CadastroMaquinaGUI;
import com.theuz.Relatorio.RelatorioPDF;
import com.theuz.Tecnico.CadastroTecnicoGUI;

public class TelaPrincipal extends JFrame {

    public TelaPrincipal() {
        setTitle("Sistema de Manutenção Preventiva e Corretiva");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 1));

        // Criação dos botões de escolha
        JButton btnCadastroMaquinas = new JButton("Cadastro de Máquinas");
        JButton btnRegistroManutencao = new JButton("Cadastro de Manutenções");
        JButton btnRegistroFalhas = new JButton("Cadastro de Falhas");
        JButton btnCadastroTecnicos = new JButton("Cadastro de Técnicos");
        JButton btnRelatorio = new JButton("Gerar Relatório");

        // Adicionando os botões ao JFrame
        add(btnCadastroMaquinas);
        add(btnRegistroManutencao);
        add(btnRegistroFalhas);
        add(btnCadastroTecnicos);
        add(btnRelatorio);

        // Ações dos botões
        btnCadastroMaquinas.addActionListener((ActionEvent e) -> {
            // Abre a tela de Cadastro de Máquinas
            new CadastroMaquinaGUI().setVisible(true);
        });

        btnRegistroManutencao.addActionListener((ActionEvent e) -> {
            // Abre a tela de Registro de Manutenções
            new CadastroManutencaoGUI().setVisible(true);
        });

        btnRegistroFalhas.addActionListener((ActionEvent e) -> {
            // Abre a tela de Registro de Falhas
            new CadastroFalhaGUI().setVisible(true);
        });

        btnCadastroTecnicos.addActionListener((ActionEvent e) -> {  
            // Abre a tela de Cadastro de Técnicos
            new CadastroTecnicoGUI().setVisible(true);
        });

        btnRelatorio.addActionListener((ActionEvent e) -> {
            // Abre a tela de Relatório
            new RelatorioPDF().setVisible(true);
        });
    }

   
}
