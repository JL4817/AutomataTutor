package org.example;

import org.example.core.Automaton;
import org.example.core.State;
import org.example.core.Transition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class CanvasPanel extends JPanel {
    private Automaton automaton;
    private State selectedState;
    private State draggedState;
    private Point dragOffset;
    private EditorMode mode;

    public enum EditorMode {
        SELECT, ADD_STATE, ADD_TRANSITION, SET_INITIAL, SET_FINAL, DELETE
    }

    public CanvasPanel(Automaton automaton) {
        this.automaton = automaton;
        this.mode = EditorMode.SELECT;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));

        setupMouseListeners();
    }

    private void setupMouseListeners() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            private State transitionStart = null;

            @Override
            public void mousePressed(MouseEvent e) {
                State clickedState = automaton.getStateAt(e.getX(), e.getY());

                switch (mode) {
                    case SELECT:
                        if (clickedState != null) {
                            draggedState = clickedState;
                            dragOffset = new Point(
                                    e.getX() - clickedState.getPosition().x,
                                    e.getY() - clickedState.getPosition().y
                            );
                        }
                        selectedState = clickedState;
                        break;

                    case ADD_STATE:
                        if (clickedState == null) {
                            int stateNum = automaton.getStateCount();
                            State newState = new State("q" + stateNum, e.getX(), e.getY());
                            automaton.addState(newState);
                        }
                        break;

                    case ADD_TRANSITION:
                        if (clickedState != null) {
                            if (transitionStart == null) {
                                transitionStart = clickedState;
                                selectedState = clickedState;
                            } else {
                                // Ask for symbol
                                String symbol = JOptionPane.showInputDialog(
                                        CanvasPanel.this,
                                        "Enter transition symbol:",
                                        "Add Transition",
                                        JOptionPane.PLAIN_MESSAGE
                                );

                                if (symbol != null && !symbol.trim().isEmpty()) {
                                    Transition t = new Transition(transitionStart, clickedState, symbol.trim());
                                    automaton.addTransition(t);
                                }

                                transitionStart = null;
                                selectedState = null;
                            }
                        }
                        break;

                    case SET_INITIAL:
                        if (clickedState != null) {
                            automaton.setInitialState(clickedState);
                        }
                        break;

                    case SET_FINAL:
                        if (clickedState != null) {
                            clickedState.setFinal(!clickedState.isFinal());
                        }
                        break;

                    case DELETE:
                        if (clickedState != null) {
                            automaton.removeState(clickedState);
                        }
                        break;
                }

                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (mode == EditorMode.SELECT && draggedState != null) {
                    draggedState.setPosition(
                            e.getX() - dragOffset.x,
                            e.getY() - dragOffset.y
                    );
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                draggedState = null;
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    public void setMode(EditorMode mode) {
        this.mode = mode;
        selectedState = null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw transitions
        for (Transition t : automaton.getTransitions()) {
            drawTransition(g2, t);
        }

        // Draw states
        for (State state : automaton.getStates()) {
            drawState(g2, state);
        }
    }

    private void drawState(Graphics2D g2, State state) {
        Point pos = state.getPosition();
        int radius = 30;

        // Draw state circle
        if (state.equals(selectedState)) {
            g2.setColor(new Color(173, 216, 230));
        } else {
            g2.setColor(Color.WHITE);
        }
        g2.fillOval(pos.x - radius, pos.y - radius, radius * 2, radius * 2);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(pos.x - radius, pos.y - radius, radius * 2, radius * 2);

        // Draw double circle for final states
        if (state.isFinal()) {
            g2.drawOval(pos.x - radius + 5, pos.y - radius + 5,
                    (radius - 5) * 2, (radius - 5) * 2);
        }

        // Draw arrow for initial state
        if (state.isInitial()) {
            int arrowX = pos.x - radius - 30;
            int arrowY = pos.y;
            g2.drawLine(arrowX, arrowY, pos.x - radius, pos.y);
            // Arrow head
            g2.drawLine(pos.x - radius, pos.y, pos.x - radius - 8, pos.y - 5);
            g2.drawLine(pos.x - radius, pos.y, pos.x - radius - 8, pos.y + 5);
        }

        // Draw state label
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(state.getId());
        g2.drawString(state.getId(), pos.x - textWidth / 2, pos.y + 5);
    }

    private void drawTransition(Graphics2D g2, Transition t) {
        Point from = t.getFromState().getPosition();
        Point to = t.getToState().getPosition();

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));

        // Self-loop
        if (t.getFromState().equals(t.getToState())) {
            int loopSize = 40;
            g2.drawArc(from.x - loopSize/2, from.y - 50, loopSize, loopSize, 0, 270);

            // Arrow head
            int arrowX = from.x + 15;
            int arrowY = from.y - 30;
            g2.drawLine(arrowX, arrowY, arrowX - 5, arrowY - 5);
            g2.drawLine(arrowX, arrowY, arrowX + 3, arrowY - 5);

            // Label
            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.drawString(t.getSymbol(), from.x + 5, from.y - 55);
        } else {
            // Check if there's a reverse transition
            boolean hasReverse = hasReverseTransition(t);

            if (hasReverse) {
                // Draw curved arrow
                drawCurvedTransition(g2, t);
            } else {
                // Draw straight arrow
                drawStraightTransition(g2, t);
            }
        }
    }

    private boolean hasReverseTransition(Transition t) {
        for (Transition other : automaton.getTransitions()) {
            if (other.getFromState().equals(t.getToState()) &&
                    other.getToState().equals(t.getFromState())) {
                return true;
            }
        }
        return false;
    }

    private void drawStraightTransition(Graphics2D g2, Transition t) {
        Point from = t.getFromState().getPosition();
        Point to = t.getToState().getPosition();

        // Calculate arrow position (on edge of circle)
        double angle = Math.atan2(to.y - from.y, to.x - from.x);
        int startX = from.x + (int)(30 * Math.cos(angle));
        int startY = from.y + (int)(30 * Math.sin(angle));
        int endX = to.x - (int)(30 * Math.cos(angle));
        int endY = to.y - (int)(30 * Math.sin(angle));

        // Draw line
        g2.drawLine(startX, startY, endX, endY);

        // Draw arrow head
        drawArrowHead(g2, endX, endY, angle);

        // Draw label in middle
        int midX = (startX + endX) / 2;
        int midY = (startY + endY) / 2;
        drawTransitionLabel(g2, t.getSymbol(), midX, midY);
    }

    private void drawCurvedTransition(Graphics2D g2, Transition t) {
        Point from = t.getFromState().getPosition();
        Point to = t.getToState().getPosition();

        // Calculate midpoint
        double midX = (from.x + to.x) / 2.0;
        double midY = (from.y + to.y) / 2.0;

        // Calculate perpendicular offset
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Normalize perpendicular vector
        double perpX = -dy / distance;
        double perpY = dx / distance;

        // Offset the curve
        double curveOffset = 20; // How much to curve
        double controlX = midX + perpX * curveOffset;
        double controlY = midY + perpY * curveOffset;

        // Calculate start and end points on circle edges
        double angleStart = Math.atan2(controlY - from.y, controlX - from.x);
        int startX = from.x + (int)(30 * Math.cos(angleStart));
        int startY = from.y + (int)(30 * Math.sin(angleStart));

        double angleEnd = Math.atan2(controlY - to.y, controlX - to.x);
        int endX = to.x + (int)(30 * Math.cos(angleEnd));
        int endY = to.y + (int)(30 * Math.sin(angleEnd));

        // Draw quadratic curve
        QuadCurve2D curve = new QuadCurve2D.Double(
                startX, startY,
                controlX, controlY,
                endX, endY
        );
        g2.draw(curve);

        // Calculate angle for arrow head (tangent at end point)
        double arrowAngle = Math.atan2(endY - controlY, endX - controlX);
        drawArrowHead(g2, endX, endY, arrowAngle);

        // Draw label at control point
        drawTransitionLabel(g2, t.getSymbol(), (int)controlX, (int)controlY);
    }

    private void drawArrowHead(Graphics2D g2, int x, int y, double angle) {
        int arrowSize = 10;
        int arrowX1 = x - (int)(arrowSize * Math.cos(angle - Math.PI / 6));
        int arrowY1 = y - (int)(arrowSize * Math.sin(angle - Math.PI / 6));
        int arrowX2 = x - (int)(arrowSize * Math.cos(angle + Math.PI / 6));
        int arrowY2 = y - (int)(arrowSize * Math.sin(angle + Math.PI / 6));

        g2.drawLine(x, y, arrowX1, arrowY1);
        g2.drawLine(x, y, arrowX2, arrowY2);
    }

    private void drawTransitionLabel(Graphics2D g2, String symbol, int x, int y) {
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(symbol);

        // Background for label
        g2.setColor(Color.WHITE);
        g2.fillRect(x - textWidth/2 - 2, y - 10, textWidth + 4, 16);
        g2.setColor(Color.BLACK);
        g2.drawString(symbol, x - textWidth/2, y + 3);
    }
}