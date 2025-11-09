package org.example.core;

import java.util.*;

public class Transition {
    private State fromState;
    private State toState;
    private String symbol;

    public Transition(State fromState, State toState, String symbol) {
        this.fromState = fromState;
        this.toState = toState;
        this.symbol = symbol;
    }

    public State getFromState() {
        return fromState;
    }

    public State getToState() {
        return toState;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public boolean acceptsSymbol(String inputSymbol) {
        if (symbol.contains(",")) {
            // Split by comma and check if any matches
            String[] symbols = symbol.split(",");
            for (String s : symbols) {
                if (s.trim().equals(inputSymbol)) {
                    return true;
                }
            }
            return false;
        } else {
            return symbol.equals(inputSymbol);
        }
    }

    public List<String> getIndividualSymbols() {
        List<String> result = new ArrayList<>();
        if (symbol.contains(",")) {
            String[] symbols = symbol.split(",");
            for (String s : symbols) {
                result.add(s.trim());
            }
        } else {
            result.add(symbol);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transition that = (Transition) o;
        return Objects.equals(fromState, that.fromState) &&
                Objects.equals(toState, that.toState) &&
                Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromState, toState, symbol);
    }

    @Override
    public String toString() {
        return fromState.getId() + " --" + symbol + "--> " + toState.getId();
    }
}