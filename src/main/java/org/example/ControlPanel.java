package org.example;

import org.example.core.Automaton;
import org.example.CanvasPanel;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {
    private CanvasPanel canvas;
    private Automaton automaton;
    private ButtonGroup modeButtonGroup;

    public ControlPanel(CanvasPanel canvas, Automaton automaton) {
        this.canvas = canvas;
        this.automaton = automaton;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(250, 0));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        setupUI();
    }

    private void setupUI() {
        setLayout(null); // manual positioning
        setPreferredSize(new Dimension(200, 400));

        // Title
        JLabel title = new JLabel("Controls");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(60, 10, 100, 30); // x, y, width, height
        add(title);

        int x = 10;  // consistent left margin
        int y = 60;  // starting vertical position
        int spacing = 35; // space between buttons

        modeButtonGroup = new ButtonGroup();

        addModeButton("Bewegen", CanvasPanel.EditorMode.SELECT, true, x, y);
        addModeButton("Zustand hinzufügen", CanvasPanel.EditorMode.ADD_STATE, false, x, y += spacing);
        addModeButton("Übergang hinzufügen", CanvasPanel.EditorMode.ADD_TRANSITION, false, x, y += spacing);
        addModeButton("Anfangszustand setzen", CanvasPanel.EditorMode.SET_INITIAL, false, x, y += spacing);
        addModeButton("Endzustand setzen", CanvasPanel.EditorMode.SET_FINAL, false, x, y += spacing);
        addModeButton("Löschen", CanvasPanel.EditorMode.DELETE, false, x, y += spacing);

        // Clear All button
        JButton clearButton = new JButton("Clear All");
        clearButton.setBounds(30, y + 50, 130, 30);
        clearButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Möchten Sie den Automaten wirklich löschen?",
                    "Löschen bestätigen",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                automaton.clear();
                canvas.repaint();
            }
        });
        add(clearButton);
    }

    private void addModeButton(String text, CanvasPanel.EditorMode mode, boolean selected, int x, int y) {
        JRadioButton button = new JRadioButton(text);
        button.setBounds(x, y, 160, 25);
        button.setSelected(selected);
        button.addActionListener(e -> canvas.setMode(mode));
        modeButtonGroup.add(button);
        add(button);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 30));
        return button;
    }

    private JSeparator createSeparator() {
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(180, 1));
        return separator;
    }
}