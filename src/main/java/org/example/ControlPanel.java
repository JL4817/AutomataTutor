package org.example;

import org.example.core.Automaton;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {
    private CanvasPanel canvas;
    private Automaton automaton;
    private ButtonGroup modeGroup;
    private JCheckBox nfaCheckBox;

    public ControlPanel(CanvasPanel canvas, Automaton automaton) {
        this.canvas = canvas;
        this.automaton = automaton;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(250, 0));

        setupUI();
    }

    private void setupUI() {
        // Title
        JLabel title = new JLabel("Controls");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(title);
        add(Box.createVerticalStrut(20));

        // Mode selection radio buttons
        modeGroup = new ButtonGroup();

        addModeButton("Bewegen", CanvasPanel.EditorMode.SELECT, true);
        addModeButton("Zustand hinzufügen", CanvasPanel.EditorMode.ADD_STATE, false);
        addModeButton("Übergang hinzufügen", CanvasPanel.EditorMode.ADD_TRANSITION, false);
        addModeButton("Anfangszustand setzen", CanvasPanel.EditorMode.SET_INITIAL, false);
        addModeButton("Endzustand setzen", CanvasPanel.EditorMode.SET_FINAL, false);
        addModeButton("Löschen", CanvasPanel.EditorMode.DELETE, false);

        add(Box.createVerticalStrut(20));

        // Clear All button
        JButton clearButton = new JButton("Clear All");
        clearButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearButton.setMaximumSize(new Dimension(200, 30));
        clearButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Alle Zustände und Übergänge löschen?",
                    "Bestätigung",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                automaton.clear();
                canvas.repaint();
            }
        });
        add(clearButton);

        add(Box.createVerticalStrut(20));

        // Separator
        JSeparator separator = new JSeparator();
        separator.setForeground(Color.DARK_GRAY);
        separator.setBackground(Color.DARK_GRAY);
        add(separator);
        add(Box.createVerticalStrut(15));

        // NEA/DEA Mode Selection
        JLabel modeLabel = new JLabel("Automatentyp:");
        modeLabel.setFont(new Font("Arial", Font.BOLD, 13));
        modeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(modeLabel);
        add(Box.createVerticalStrut(8));

        nfaCheckBox = new JCheckBox("NEA");
        nfaCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        nfaCheckBox.setSelected(false); // Default to DEA
        nfaCheckBox.addActionListener(e -> {
            automaton.setNFA(nfaCheckBox.isSelected());
            updateModeLabel();
        });
        add(nfaCheckBox);

        add(Box.createVerticalStrut(10));

        // Info label showing current mode
        JLabel infoLabel = new JLabel("<html><i>Aktuell: DEA<br>(Deterministisch)</i></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        infoLabel.setForeground(Color.DARK_GRAY);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(infoLabel);

        // Update info label when checkbox changes
        nfaCheckBox.addActionListener(e -> {
            if (nfaCheckBox.isSelected()) {
                infoLabel.setText("<html><i>Aktuell: NEA<br>(Nichtdeterministisch)</i></html>");
            } else {
                infoLabel.setText("<html><i>Aktuell: DEA<br>(Deterministisch)</i></html>");
            }
        });

        add(Box.createVerticalStrut(15));

        add(Box.createVerticalGlue());
    }

    private void addModeButton(String text, CanvasPanel.EditorMode mode, boolean selected) {
        JRadioButton button = new JRadioButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setSelected(selected);
        button.addActionListener(e -> canvas.setMode(mode));

        modeGroup.add(button);
        add(button);
        add(Box.createVerticalStrut(5));

        if (selected) {
            canvas.setMode(mode);
        }
    }

    private void updateModeLabel() {
        canvas.repaint();
    }
}