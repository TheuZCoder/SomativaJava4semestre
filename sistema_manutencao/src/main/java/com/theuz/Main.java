package com.theuz;

import javax.swing.SwingUtilities;

import com.theuz.TelaPrincipal.TelaPrincipal;

public class Main {
     public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TelaPrincipal().setVisible(true);
        });
    }
}