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
        transitions.removeIf(t -> t.getFromState().equals(state) ||
                t.getToState().equals(state));
        if (state.equals(initialState)) {
            initialState = null;
        }
    }

    public void addTransition(Transition transition) {
        transitions.add(transition);
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

    // Get single transition (for DEA)
    public Transition getTransition(State from, String symbol) {
        for (Transition t : transitions) {
            if (t.getFromState().equals(from) && t.acceptsSymbol(symbol)) {
                return t;
            }
        }
        return null;
    }

    // Get all possible transitions for a symbol (for NEA)
    public List<Transition> getAllTransitions(State from, String symbol) {
        List<Transition> result = new ArrayList<>();
        for (Transition t : transitions) {
            if (t.getFromState().equals(from) && t.acceptsSymbol(symbol)) {
                result.add(t);
            }
        }
        return result;
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

    // Main simulation method - handles both DEA and NEA
    public boolean simulate(String input) {
        if (initialState == null) {
            return false;
        }

        if (isNFA) {
            return simulateNFA(input);
        } else {
            return simulateDFA(input);
        }
    }

    // DEA simulation
    private boolean simulateDFA(String input) {
        State currentState = initialState;

        for (char c : input.toCharArray()) {
            String symbol = String.valueOf(c);
            Transition transition = getTransition(currentState, symbol);

            if (transition == null) {
                return false;
            }
            currentState = transition.getToState();
        }

        return currentState.isFinal();
    }

    // NEA simulation
    private boolean simulateNFA(String input) {
        // Set of current states (parallel execution)
        Set<State> currentStates = new HashSet<>();
        currentStates.add(initialState);

        for (char c : input.toCharArray()) {
            String symbol = String.valueOf(c);
            Set<State> nextStates = new HashSet<>();

            for (State state : currentStates) {
                List<Transition> transitions = getAllTransitions(state, symbol);
                for (Transition t : transitions) {
                    nextStates.add(t.getToState());
                }
            }

            // If no next states, all paths are blocked
            if (nextStates.isEmpty()) {
                return false;
            }

            currentStates = nextStates;
        }

        // Accept if any final state is reachable
        for (State state : currentStates) {
            if (state.isFinal()) {
                return true;
            }
        }

        return false;
    }

    public List<State> simulateSteps(String input) {
        if (initialState == null) {
            return new ArrayList<>();
        }

        if (isNFA) {
            return simulateStepsNFA(input);
        } else {
            return simulateStepsDFA(input);
        }
    }

    // DEA step-by-step
    private List<State> simulateStepsDFA(String input) {
        List<State> steps = new ArrayList<>();
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

    // NEA step-by-step
    private List<State> simulateStepsNFA(String input) {
        List<State> steps = new ArrayList<>();

        // Use BFS to find a path
        Queue<List<State>> queue = new LinkedList<>();
        List<State> initialPath = new ArrayList<>();
        initialPath.add(initialState);
        queue.offer(initialPath);

        String[] symbols = input.isEmpty() ? new String[0] : input.split("");

        for (String symbol : symbols) {
            Queue<List<State>> nextQueue = new LinkedList<>();

            while (!queue.isEmpty()) {
                List<State> currentPath = queue.poll();
                State lastState = currentPath.get(currentPath.size() - 1);

                List<Transition> transitions = getAllTransitions(lastState, symbol);

                for (Transition t : transitions) {
                    List<State> newPath = new ArrayList<>(currentPath);
                    newPath.add(t.getToState());
                    nextQueue.offer(newPath);
                }
            }

            if (nextQueue.isEmpty()) {
                // No path continues, return what we have
                return steps.isEmpty() ? initialPath : steps;
            }

            queue = nextQueue;
        }

        // Return a successful path (preferably one ending in final state)
        while (!queue.isEmpty()) {
            List<State> path = queue.poll();
            State lastState = path.get(path.size() - 1);
            if (lastState.isFinal()) {
                return path;
            }
            // Save first path as fallback
            if (steps.isEmpty()) {
                steps = path;
            }
        }

        return steps.isEmpty() ? initialPath : steps;
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