package org.example;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater((() -> {
            Visualizer visualizer = new Visualizer();
            visualizer.setVisible(true);
        }));
    }
}