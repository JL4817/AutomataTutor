package org.example.core;

import java.awt.Point;
import java.util.Objects;

public class State {
    private String id;
    private boolean isInitial;
    private boolean isFinal;
    private Point position;

    public State(String id, int x, int y) {
        this.id = id;
        this.position = new Point(x, y);
        this.isInitial = false;
        this.isFinal = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isInitial() {
        return isInitial;
    }

    public void setInitial(boolean initial) {
        isInitial = initial;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean finalState) {
        isFinal = finalState;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(int x, int y) {
        this.position.setLocation(x, y);
    }

    public boolean contains(int x, int y) {
        // Check if point is within state circle (radius = 30)
        int dx = x - position.x;
        int dy = y - position.y;
        return Math.sqrt(dx * dx + dy * dy) <= 30;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return Objects.equals(id, state.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }
}