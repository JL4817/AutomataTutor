package org.example;

import org.example.core.Automaton;
import org.example.core.State;
import org.example.core.Transition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

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
            // Count transitions between these two states
            List<Transition> parallelTransitions = getParallelTransitions(t.getFromState(), t.getToState());
            int index = parallelTransitions.indexOf(t);
            int totalCount = parallelTransitions.size();

            // Also check reverse direction
            List<Transition> reverseTransitions = getParallelTransitions(t.getToState(), t.getFromState());
            boolean hasReverse = !reverseTransitions.isEmpty();

            // Check if line would pass through other states
            boolean wouldIntersectStates = checkLineIntersectsStates(t.getFromState(), t.getToState());

            if (totalCount == 1 && !hasReverse && !wouldIntersectStates) {
                // Single transition, no reverse, no obstruction - draw straight
                drawStraightTransition(g2, t);
            } else {
                // Multiple transitions, bidirectional, or obstructed - draw curved
                drawCurvedTransitionWithIndex(g2, t, index, totalCount, hasReverse || wouldIntersectStates);
            }
        }
    }

    private List<Transition> getParallelTransitions(State from, State to) {
        List<Transition> result = new ArrayList<>();
        for (Transition t : automaton.getTransitions()) {
            if (t.getFromState().equals(from) && t.getToState().equals(to)) {
                result.add(t);
            }
        }
        return result;
    }

    private boolean checkLineIntersectsStates(State from, State to) {
        // Check if a straight line the already existing would pass too close to any other state
        int checkRadius = 45; // Distance threshold

        for (State state : automaton.getStates()) {
            if (state.equals(from) || state.equals(to)) {
                continue;
            }

            // Calculate distance from state to line segment
            Point statePos = state.getPosition();
            Point fromPos = from.getPosition();
            Point toPos = to.getPosition();

            double distance = pointToLineDistance(
                    statePos.x, statePos.y,
                    fromPos.x, fromPos.y,
                    toPos.x, toPos.y
            );

            if (distance < checkRadius) {
                // Also check if the state is actually between the two endpoints
                // (not behind or beyond them)
                if (isPointBetweenEndpoints(statePos, fromPos, toPos)) {
                    return true;
                }
            }
        }

        return false;
    }

    private double pointToLineDistance(double px, double py, double x1, double y1, double x2, double y2) {
        // Calculate distance from point
        double lineLength = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));

        if (lineLength == 0) {
            // Line segment is actually a point
            return Math.sqrt((px - x1) * (px - x1) + (py - y1) * (py - y1));
        }

        // Calculate projection of point onto line
        double t = ((px - x1) * (x2 - x1) + (py - y1) * (y2 - y1)) / (lineLength * lineLength);
        t = Math.max(0, Math.min(1, t)); // Clamp to [0, 1] to stay on segment

        // Find closest point on line segment
        double closestX = x1 + t * (x2 - x1);
        double closestY = y1 + t * (y2 - y1);

        // Return distance to closest point
        return Math.sqrt((px - closestX) * (px - closestX) + (py - closestY) * (py - closestY));
    }

    private boolean isPointBetweenEndpoints(Point p, Point start, Point end) {
        // Check if point p is roughly between start and end (not behind or beyond)
        double dotProduct = (p.x - start.x) * (end.x - start.x) + (p.y - start.y) * (end.y - start.y);
        double squaredLength = (end.x - start.x) * (end.x - start.x) + (end.y - start.y) * (end.y - start.y);

        if (squaredLength == 0) return false;

        double t = dotProduct / squaredLength;
        return t > 0.1 && t < 0.9; // Between endpoints with some margin
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

    private void drawCurvedTransitionWithIndex(Graphics2D g2, Transition t, int index, int totalCount, boolean forceCurve) {
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

        double baseOffset = 70; // How far the curve should be or how "curved"
        double curveOffset;

        if (forceCurve && totalCount == 1) {
            // Single transition but needs to curve (e.g., to avoid other states)
            curveOffset = baseOffset;
        } else if (totalCount == 1) {
            // Single transition, no force curve
            curveOffset = baseOffset;
        } else {
            // Multiple parallel transitions - spread them out
            // Center them around 0, with spacing between each
            double spacing = 25;
            double totalSpread = (totalCount - 1) * spacing;
            curveOffset = (index * spacing) - (totalSpread / 2.0);

            // Add base offset to move away from straight line
            if (curveOffset >= 0) {
                curveOffset += baseOffset;
            } else {
                curveOffset -= baseOffset;
            }
        }

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

        // Draw label at control point (slightly offset for better visibility)
        int labelOffsetX = (int)(perpX * 10);
        int labelOffsetY = (int)(perpY * 10);
        drawTransitionLabel(g2, t.getSymbol(), (int)controlX + labelOffsetX, (int)controlY + labelOffsetY);
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