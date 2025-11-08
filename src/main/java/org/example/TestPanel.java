package org.example;

import org.example.core.Automaton;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import org.example.core.State;

public class TestPanel extends JPanel {
    private Automaton automaton;
    private JTextField inputField;
    private JLabel resultLabel;
    private JTextArea stepsArea;

    public TestPanel(Automaton automaton) {
        this.automaton = automaton;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(0, 150));

        setupUI();
    }

    private void setupUI() {
        // Left panel - Input
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel inputLabel = new JLabel("Testzeichenfolge:");
        inputLabel.setFont(new Font("Arial", Font.BOLD, 12));
        inputPanel.add(inputLabel);

        inputField = new JTextField(20);
        inputField.setFont(new Font("Courier", Font.PLAIN, 14));
        inputPanel.add(inputField);

        JButton testButton = new JButton("Testen");
        testButton.addActionListener(e -> testString());
        inputPanel.add(testButton);

        resultLabel = new JLabel("");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 14));
        inputPanel.add(resultLabel);

        add(inputPanel, BorderLayout.NORTH);

        // Center panel - Steps
        JPanel stepsPanel = new JPanel(new BorderLayout());
        stepsPanel.setBorder(BorderFactory.createTitledBorder("Ausführungsschritte"));

        stepsArea = new JTextArea(3, 50);
        stepsArea.setEditable(false);
        stepsArea.setFont(new Font("Courier", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(stepsArea);
        stepsPanel.add(scrollPane, BorderLayout.CENTER);

        add(stepsPanel, BorderLayout.CENTER);

        // Add Enter key listener
        inputField.addActionListener(e -> testString());
    }

    private void testString() {
        String input = inputField.getText();

        if (automaton.getInitialState() == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Bitte setzen Sie zuerst einen Anfangszustand!",
                    "Kein Anfangszustand",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Test the string
        boolean accepted = automaton.simulate(input);

        // Get step-by-step execution
        List<State> steps = automaton.simulateSteps(input);

        // Update result label
        if (accepted) {
            resultLabel.setText("✓ AKZEPTIERT");
            resultLabel.setForeground(new Color(0, 150, 0));
        } else {
            resultLabel.setText("✗ ABGELEHNT");
            resultLabel.setForeground(Color.RED);
        }

        // Show steps
        StringBuilder stepsText = new StringBuilder();
        if (steps.isEmpty()) {
            stepsText.append("Keine Ausführung (kein Anfangszustand)");
        } else {
            stepsText.append("Start: ").append(steps.get(0).getId());

            if (input.isEmpty()) {
                stepsText.append("\n(Leere Zeichenfolge)");
            } else {
                for (int i = 0; i < input.length(); i++) {
                    if (i + 1 < steps.size()) {
                        stepsText.append("\n");
                        stepsText.append("Lesen '").append(input.charAt(i))
                                .append("' → ").append(steps.get(i + 1).getId());
                    } else {
                        stepsText.append("\n");
                        stepsText.append("Lesen '").append(input.charAt(i))
                                .append("' → (kein Übergang - BLOCKIERT)");
                        break;
                    }
                }
            }

            stepsText.append("\n\nEndzustand: ");
            State finalState = steps.get(steps.size() - 1);
            stepsText.append(finalState.getId());
            stepsText.append(finalState.isFinal() ? " (akzeptierend)" : " (nicht akzeptierend)");
        }

        stepsArea.setText(stepsText.toString());
    }
}