package org.example;

import org.example.core.Automaton;

import javax.swing.*;
import java.awt.*;

public class Visualizer extends JFrame {
    private Automaton automaton;
    private CanvasPanel canvas;
    private ControlPanel controlPanel;
    private TestPanel testPanel;

    public Visualizer() {
        setTitle("AutomatenTutor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setupUI();
    }

    private void setupUI() {
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
        ToolTipManager.sharedInstance().setInitialDelay(0);

        // Create automaton model
        automaton = new Automaton();

        // Main layout
        setLayout(new BorderLayout(0, 0));

        // Create canvas (center)
        canvas = new CanvasPanel(automaton);
        canvas.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(canvas, BorderLayout.CENTER);

        // Create control panel (right side)
        controlPanel = new ControlPanel(canvas, automaton);
        controlPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY));
        add(controlPanel, BorderLayout.EAST);

        // Create test panel (bottom)
        testPanel = new TestPanel(automaton);
        testPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        add(testPanel, BorderLayout.SOUTH);

        // Add menu bar
        setJMenuBar(createMenuBar());
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Exit menu item
        JMenu exitMenu = new JMenu("Beenden");
        exitMenu.addMenuListener(new javax.swing.event.MenuListener() {
            @Override
            public void menuSelected(javax.swing.event.MenuEvent e) {
                System.exit(0);
            }
            @Override
            public void menuDeselected(javax.swing.event.MenuEvent e) {}
            @Override
            public void menuCanceled(javax.swing.event.MenuEvent e) {}
        });
        menuBar.add(exitMenu);

        // Clear Automaton menu item
        JMenu clearMenu = new JMenu("Automat löschen");
        clearMenu.addMenuListener(new javax.swing.event.MenuListener() {
            @Override
            public void menuSelected(javax.swing.event.MenuEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        Visualizer.this,
                        "Automat löschen?",
                        "Automat löschen",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    automaton.clear();
                    canvas.repaint();
                }
            }
            @Override
            public void menuDeselected(javax.swing.event.MenuEvent e) {}
            @Override
            public void menuCanceled(javax.swing.event.MenuEvent e) {}
        });
        menuBar.add(clearMenu);

        // Help menu
        JMenu helpMenu = new JMenu("Hilfe");

        JMenuItem aboutItem = new JMenuItem("Über");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        JMenuItem tutorialItem = new JMenuItem("Kurzanleitung");
        tutorialItem.addActionListener(e -> showTutorial());
        helpMenu.add(tutorialItem);

        JMenuItem conceptDEA = new JMenuItem("DEA");
        conceptDEA.addActionListener(e -> showDEA());
        helpMenu.add(conceptDEA);

        JMenuItem conceptNEA = new JMenuItem("NEA");
        conceptNEA.addActionListener(e -> showNEA());
        helpMenu.add(conceptNEA);

        JMenuItem conceptDifference = new JMenuItem("Unterschied zwischen DEA und NEA");
        conceptDifference.addActionListener(e -> showDifference());
        helpMenu.add(conceptDifference);

        menuBar.add(helpMenu);

        return menuBar;
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(
                this,
                "AutomatenTutor\n\n" +
                        "Ein visuelles Werkzeug zum Erstellen und Testen\n" +
                        "deterministischer endlicher Automaten (DEA) und nicht-deterministischer endlicher Automaten (NEA)",
                "Über AutomatenTutor",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showTutorial() {
        String tutorial =
                "Kurzanleitung:\n\n" +
                        "1. Zustände hinzufügen:\n" +
                        "   - 'Zustand hinzufügen' Modus wählen\n" +
                        "   - Auf Canvas klicken, um Zustände zu erstellen\n\n" +
                        "2. Anfangszustand setzen:\n" +
                        "   - 'Anfangszustand setzen' Modus wählen\n" +
                        "   - Auf einen Zustand klicken\n\n" +
                        "3. Endzustände setzen:\n" +
                        "   - 'Endzustand setzen' Modus wählen\n" +
                        "   - Zustände anklicken zum Umschalten (Doppelkreis)\n\n" +
                        "4. Übergänge hinzufügen:\n" +
                        "   - 'Übergang hinzufügen' Modus wählen\n" +
                        "   - Quellzustand klicken, dann Zielzustand\n" +
                        "   - Symbol eingeben, wenn aufgefordert\n\n" +
                        "5. Zeichenfolgen testen:\n" +
                        "   - Zeichenfolge im unteren Panel eingeben\n" +
                        "   - 'Testen' klicken oder Enter drücken\n" +
                        "   - Sehen, ob akzeptiert/abgelehnt\n\n" +
                        "6. Zustände verschieben:\n" +
                        "   - 'Auswählen/Bewegen' Modus wählen\n" +
                        "   - Zustände ziehen, um neu zu positionieren\n\n" +
                        "Beispiel: Erstellen Sie einen DEA, der Zeichenfolgen\n" +
                        "mit einer geraden Anzahl von 1en über Alphabet {0,1} akzeptiert";

        JTextArea textArea = new JTextArea(tutorial);
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 12));
        textArea.setBackground(getBackground());

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 450));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Kurzanleitung",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showDEA() {
        // Main panel with vertical layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Text section
        String infoText =
                "<html>" +
                        "<b>Deterministische Endliche Automaten (DEA):</b><br>" +
                        "Ein DEA ist ein mathematisches Modell zur Erkennung formaler Sprachen.<br>" +
                        "\"Deterministisch\" bedeutet, dass es für jeden Zustand und jedes Eingabesymbol genau einen Folgezustand gibt.<br><br>" +
                        "Formale Definition:<br>" +
                        "Ein DEA besteht aus:<br><br>" +
                        "Q: endliche Menge von Zuständen<br>" +
                        "Σ: Eingabealphabet<br>" +
                        "δ: Übergangsfunktion (Q × Σ → Q)<br>" +
                        "q₀: Startzustand<br>" +
                        "F: Menge der Endzustände (akzeptierende Zustände)<br><br>" +
                        "Beispiel DEA:<br>" +
                        "Automat, der alle Wörter über {0,1} akzeptiert, die mit \"01\" enden:<br><br>" +
                        "Beispielwörter:<br>" +
                        "\"101\" ✓ akzeptiert (endet in q₂)<br>" +
                        "\"001\" ✓ akzeptiert<br>" +
                        "\"110\" ✗ abgelehnt (endet nicht in q₂)" +
                        "</html>";


        JLabel textLabel = new JLabel(infoText);
        textLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        textLabel.setBackground(Color.WHITE);
        textLabel.setOpaque(true);

        JScrollPane scrollPane = new JScrollPane(textLabel);
        scrollPane.setPreferredSize(new Dimension(700, 300));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(20));

        // Image section
        ImageIcon imageIcon;
        try {
            imageIcon = new ImageIcon(getClass().getResource("/DEA_B_1.png"));
            Image image = imageIcon.getImage().getScaledInstance(500, 310, Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(image);

            JLabel imageLabel = new JLabel(imageIcon);
            imageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            imageLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            mainPanel.add(imageLabel);
        } catch (Exception e) {
            JLabel missing = new JLabel("(Kein Bild gefunden)");
            missing.setAlignmentX(Component.LEFT_ALIGNMENT);
            mainPanel.add(missing);
        }

        JScrollPane mainScrollPane = new JScrollPane(mainPanel);
        mainScrollPane.setPreferredSize(new Dimension(750, 700));

        JOptionPane.showMessageDialog(
                this,
                mainScrollPane,
                "DEA",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showNEA() {
        // Main panel with vertical layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Text section
        String infoText =
                "<html>" +
                        "<b>NEA (Nichtdeterministischer Endlicher Automat)</b><br>" +
                        "Ein NEA erlaubt mehrere Möglichkeiten:<br><br>" +
                        "Mehrere Übergänge für dasselbe Symbol<br>" +
                        "ε-Übergänge (Zustandswechsel ohne Eingabe)<br><br>" +
                        "<b>Formale Definition:</b><br><br>" +
                        "δ: Übergangsfunktion (Q × (Σ ∪ {ε}) → P(Q))<br><br>" +
                        "P(Q) = Potenzmenge (Menge aller Teilmengen von Q)<br><br>" +
                        "<b>WICHTIG: NEA verfolgt ALLE möglichen Pfade parallel!</b><br><br>" +
                        "<b>Beispiel NEA:</b><br>" +
                        "Automat, der Wörter akzeptiert, die \"01\" enthalten.<br>" +
                        "Bei Eingabe '0' in q₀ kann der Automat nach q₀ oder q₁ gehen (nichtdeterministisch).<br>" +
                        "Für das Wort \"01\":<br><br>" +
                        "Pfad 1: q₀ --0--> q₀ --1--> q₀ (endet in q₀, kein Endzustand)<br>" +
                        "Pfad 2: q₀ --0--> q₁ --1--> q₂ (endet in q₂, Endzustand!) ✓<br><br>" +
                        "<b>Da mindestens ein Pfad (Pfad 2) den Endzustand q₂ erreicht, wird \"01\" akzeptiert.<br>" +
                        "Der NEA verfolgt beide Pfade parallel. Beim ersten Symbol '0' spaltet<br>" +
                        "sich die Berechnung in zwei Möglichkeiten auf.<br>" +
                        "Nur einer dieser parallelen Pfade (über q₁) erreicht am Ende den Endzustand q₂,<br>" +
                        "was ausreicht, um das Wort zu akzeptieren.</b>" +
                        "</html>";

        JLabel textLabel = new JLabel(infoText);
        textLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        textLabel.setBackground(Color.WHITE);
        textLabel.setOpaque(true);

        JScrollPane scrollPane = new JScrollPane(textLabel);
        scrollPane.setPreferredSize(new Dimension(700, 300));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(20));

        // Image section
        ImageIcon imageIcon;
        try {
            imageIcon = new ImageIcon(getClass().getResource("/NEA_B_1.png"));
            Image image = imageIcon.getImage().getScaledInstance(500, 310, Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(image);

            JLabel imageLabel = new JLabel(imageIcon);
            imageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            imageLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            mainPanel.add(imageLabel);
        } catch (Exception e) {
            JLabel missing = new JLabel("(Kein Bild gefunden)");
            missing.setAlignmentX(Component.LEFT_ALIGNMENT);
            mainPanel.add(missing);
        }

        JScrollPane mainScrollPane = new JScrollPane(mainPanel);
        mainScrollPane.setPreferredSize(new Dimension(750, 700));

        JOptionPane.showMessageDialog(
                this,
                mainScrollPane,
                "DEA",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showDifference() {
        // Main panel with vertical layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Text section
        String infoText =
                "<html>" +
                        "<b>Alle Unterschiede zwischen DEA und NEA:</b><br><br>" +

                        "<b>1. Mehrfache Übergänge</b><br>" +
                        "NEA: Von einem Zustand können mehrere Pfeile mit demselben Symbol ausgehen<br>" +
                        "DEA: Nur ein Pfeil pro Symbol<br><br>" +

                        "<b>2. ε-Übergänge (Epsilon-Übergänge)</b><br>" +
                        "Das ist ein großer Unterschied!<br>" +
                        "NEA kann Zustandswechsel OHNE Eingabe machen:<br>" +
                        "&nbsp;&nbsp;&nbsp;&nbsp;ε<br>" +
                        "Z₀ -----> Z₁<br>" +
                        "Der Automat kann \"spontan\" von Z₀ nach Z₁ springen, ohne ein Symbol zu lesen!<br>" +
                        "DEA: Keine ε-Übergänge erlaubt. Jeder Übergang braucht ein Eingabesymbol.<br><br>" +

                        "<b>3. Fehlende Übergänge</b><br>" +
                        "NEA: Muss nicht für jedes Symbol einen Übergang haben<br>" +
                        "Beispiel NEA:<br>" +
                        "Z₀ --a--> Z₁<br>" +
                        "(kein Übergang für 'b' definiert = Wort wird abgelehnt)<br>" +
                        "DEA: Muss für jedes Symbol aus jedem Zustand einen Übergang haben (totale Funktion)<br>" +
                        "Beispiel DEA:<br>" +
                        "Z₀ --a--> Z₁<br>" +
                        "Z₀ --b--> Z_fehler (oft ein \"Müllzustand\")<br><br>" +

                        "<b>4. Mehrere Startzustände</b><br>" +
                        "NEA: Kann theoretisch mehrere Startzustände haben (erweiterte Definition)<br>" +
                        "DEA: Immer genau ein Startzustand<br><br>" +

                        "<b>Zusammenfassung aller Unterschiede:</b><br><br>" +
                        "<table border='1' cellspacing='0' cellpadding='5'>" +
                        "<tr><th>Eigenschaft</th><th>DEA</th><th>NEA</th></tr>" +
                        "<tr><td>Mehrfache Übergänge</td><td>✗ Nein</td><td>✓ Ja</td></tr>" +
                        "<tr><td>ε-Übergänge</td><td>✗ Nein</td><td>✓ Ja</td></tr>" +
                        "<tr><td>Vollständige Übergänge</td><td>✓ Pflicht</td><td>✗ Optional</td></tr>" +
                        "<tr><td>Anzahl Startzustände</td><td>Genau 1</td><td>1 (oder mehrere)</td></tr>" +
                        "<tr><td>Ausführung</td><td>Eindeutig</td><td>\"Raten\" / Parallel</td></tr>" +
                        "</table><br><br>" +

                        "<b>Beispiel mit ε-Übergang (NEA):</b><br>" +
                        "&nbsp;&nbsp;&nbsp;&nbsp;a&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ε&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;b<br>" +
                        "Start -----> Z₁ -----> Z₂ -----> Ende<br><br>" +
                        "Dieser NEA akzeptiert \"ab\", weil:<br><br>" +
                        "Lese 'a' → gehe zu Z₁<br>" +
                        "Spontan (ohne Input) → gehe zu Z₂<br>" +
                        "Lese 'b' → gehe zu Ende" +
                        "</html>";


        JLabel textLabel = new JLabel(infoText);
        textLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        textLabel.setBackground(Color.WHITE);
        textLabel.setOpaque(true);

        JScrollPane scrollPane = new JScrollPane(textLabel);
        scrollPane.setPreferredSize(new Dimension(700, 300));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(20));

        JScrollPane mainScrollPane = new JScrollPane(mainPanel);
        mainScrollPane.setPreferredSize(new Dimension(750, 700));

        JOptionPane.showMessageDialog(
                this,
                mainScrollPane,
                "DEA",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}