package org.example.core;

import java.util.*;

public class Automaton {
    private Set<State> states;
    private Set<Transition> transitions;
    private Set<String> alphabet;
    private State initialState;
    private boolean isNFA;

    public Automaton() {
        this.states = new HashSet<>();
        this.transitions = new HashSet<>();
        this.alphabet = new HashSet<>();
        this.initialState = null;
        this.isNFA = false; // Default to DEA
    }

    public boolean isNFA() {
        return isNFA;
    }

    public void setNFA(boolean nfa) {
        isNFA = nfa;
    }

    public void addState(State state) {
        states.add(state);
        if (state.isInitial()) {
            setInitialState(state);
        }
    }

    public void removeState(State state) {
        states.remove(state);
        // Remove all transitions involving this state
        transitions.removeIf(t -> t.getFromState().equals(state) ||
                t.getToState().equals(state));
        if (state.equals(initialState)) {
            initialState = null;
        }
    }

    public void addTransition(Transition transition) {
        transitions.add(transition);
        // Add all individual symbols to alphabet
        for (String symbol : transition.getIndividualSymbols()) {
            alphabet.add(symbol);
        }
    }

    public void removeTransition(Transition transition) {
        transitions.remove(transition);
    }

    public Set<State> getStates() {
        return states;
    }

    public Set<Transition> getTransitions() {
        return transitions;
    }

    public Set<String> getAlphabet() {
        return alphabet;
    }

    public State getInitialState() {
        return initialState;
    }

    public void setInitialState(State state) {
        if (initialState != null) {
            initialState.setInitial(false);
        }
        this.initialState = state;
        if (state != null) {
            state.setInitial(true);
        }
    }

    public State getStateAt(int x, int y) {
        for (State state : states) {
            if (state.contains(x, y)) {
                return state;
            }
        }
        return null;
    }

    public Transition getTransition(State from, String symbol) {
        for (Transition t : transitions) {
            if (t.getFromState().equals(from) && t.acceptsSymbol(symbol)) {
                return t;
            }
        }
        return null;
    }

    public List<Transition> getTransitionsFrom(State state) {
        List<Transition> result = new ArrayList<>();
        for (Transition t : transitions) {
            if (t.getFromState().equals(state)) {
                result.add(t);
            }
        }
        return result;
    }

    // DFA Simulation
    public boolean simulate(String input) {
        if (initialState == null) {
            return false;
        }

        State currentState = initialState;

        for (char c : input.toCharArray()) {
            String symbol = String.valueOf(c);
            Transition transition = getTransition(currentState, symbol);

            if (transition == null) {
                return false; // No valid transition
            }

            currentState = transition.getToState();
        }

        return currentState.isFinal();
    }

    // Step-by-step simulation
    public List<State> simulateSteps(String input) {
        List<State> steps = new ArrayList<>();

        if (initialState == null) {
            return steps;
        }

        State currentState = initialState;
        steps.add(currentState);

        for (char c : input.toCharArray()) {
            String symbol = String.valueOf(c);
            Transition transition = getTransition(currentState, symbol);

            if (transition == null) {
                break; // No valid transition
            }

            currentState = transition.getToState();
            steps.add(currentState);
        }

        return steps;
    }

    public void clear() {
        states.clear();
        transitions.clear();
        alphabet.clear();
        initialState = null;
    }

    public int getStateCount() {
        return states.size();
    }
}