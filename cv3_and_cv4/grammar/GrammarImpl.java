package cv3_and_cv4.grammar;

import java.util.*;
import java.io.*;

public class GrammarImpl implements Grammar {

    @Override
    public Collection<Terminal> getTerminals() {
        return terminals.values();
    }

    public Terminal addTerminal(String name) {
        Terminal t = (Terminal) terminals.get(name);
        if (t == null) {
            t = new Terminal(name);
            terminals.put(name, t);
        }
        return t;
    }

    @Override
    public Collection<Nonterminal> getNonterminals() {
        return nonterminals.values();
    }

    public Nonterminal addNonterminal(String name) {
        Nonterminal nt = (Nonterminal) nonterminals.get(name);
        if (nt == null) {
            nt = new Nonterminal(name);
            nonterminals.put(name, nt);
        }
        return nt;
    }

    @Override
    public List<Rule> getRules() {
        ArrayList<Rule> rules = new ArrayList<Rule>();

        for (Nonterminal nt : this.getNonterminals()) {
            for (Rule r : nt.getRules()) {
                rules.add(r);
            }
        }
        return rules;
    }

    public void addRule(Rule rule) {
        rule.getLHS().addRule(rule);
    }

    @Override
    public Nonterminal getStartNonterminal() {
        return startNonterminal;
    }

    public void setStartNonterminal(Nonterminal start) {
        startNonterminal = start;
    }

    public void dump(PrintStream out) {
        out.print("Terminals:");
        for (Terminal t : getTerminals()) {
            out.print(" " + t.getName());
        }
        out.println();

        out.print("Nonterminals:");
        for (Nonterminal nt : getNonterminals()) {
            out.print(" " + nt.getName());
        }
        out.println();

        out.println("Starting nonterminal: " + getStartNonterminal().getName());

        out.println("Rules:");
        int i = 0;
        for (Rule rule : getRules()) {
            i++;
            out.print("[" + i + "] " + rule.getLHS().getName() + " -> ");

            for (Symbol s : rule.getRHS()) {
                out.print(s.getName() + " ");
            }
            out.println();
        }
    }

    private final Map<String, Terminal> terminals = new TreeMap<String, Terminal>();

    private final Map<String, Nonterminal> nonterminals = new TreeMap<String, Nonterminal>();

    private Nonterminal startNonterminal;
}

