package cv3_and_cv4.grammarOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import cv3_and_cv4.grammar.Grammar;
import cv3_and_cv4.grammar.Nonterminal;
import cv3_and_cv4.grammar.Rule;
import cv3_and_cv4.grammar.Symbol;
import cv3_and_cv4.grammar.Terminal;

public class GrammarOps {
    Grammar g;
    Set<Nonterminal> emptyNonterminals;
    Map<Symbol, Set<String>> first = new HashMap<>();
    Map<Nonterminal, Set<String>> follow = new HashMap<>();

    public GrammarOps(Grammar g) {
        this.g = g;
        compute_empty();
        compute_first();
        compute_follow();
    }

    // 1: compute nonterminals that derive epsilon
    private void compute_empty() {
        emptyNonterminals = new TreeSet<>();
        boolean changed;
        do {
            changed = false;
            for (Rule rule : g.getRules()) {
                Nonterminal lhs = rule.getLHS();
                if (!emptyNonterminals.contains(lhs)) {
                    boolean emptyRule = true;
                    for (Symbol s : rule.getRHS()) {
                        // "{e}" is the epsilon terminal produced by GrammarReader
                        boolean nullable = s.getName().equals("{e}")
                                || (s instanceof Nonterminal && emptyNonterminals.contains(s));
                        if (!nullable) {
                            emptyRule = false;
                            break;
                        }
                    }
                    if (emptyRule) {
                        emptyNonterminals.add(lhs);
                        changed = true;
                    }
                }
            }
        } while (changed);
    }

    // 2: compute FIRST sets
    private void compute_first() {
        // initialize FIRST sets for terminals
        for (Terminal t : g.getTerminals()) {
            first.put(t, new HashSet<>(Set.of(t.getName())));
        }

        // initialize FIRST sets for nonterminals
        for (Nonterminal nt : g.getNonterminals()) {
            first.put(nt, new HashSet<>());
        }

        boolean changed;
        do {
            changed = false;
            for (Rule rule : g.getRules()) {
                Set<String> currentSet = first.get(rule.getLHS());
                int prevSize = currentSet.size();

                List<Symbol> rhs = rule.getRHS();
                boolean allNullable = true;

                for (Symbol s : rhs) {
                    Set<String> fs = first.get(s);
                    currentSet.addAll(fs);
                    if (!fs.contains("{e}")) {
                        allNullable = false;
                        break;
                    }
                }

                // if all symbols can be nullable -> add {e}
                if (allNullable) currentSet.add("{e}");


                if (currentSet.size() != prevSize) {
                    changed = true;
                }
            }
        } while (changed);
    }

    // 3: compute FOLLOW sets
    private void compute_follow() {
        for (Nonterminal nt : g.getNonterminals()) {
            follow.put(nt, new HashSet<>());
        }
        follow.get(g.getStartNonterminal()).add("$");

        boolean changed;
        do {
            changed = false;
            for (Rule rule : g.getRules()) {
                List<Symbol> rhs = rule.getRHS();
                for (int i = 0; i < rhs.size(); i++) {
                    if (rhs.get(i) instanceof Nonterminal) {
                        Nonterminal current = (Nonterminal) rhs.get(i);
                        Set<String> followCurrent = follow.get(current);
                        int prevSize = followCurrent.size();

                        boolean allNullable = true;
                        for (int j = i + 1; j < rhs.size(); j++) {
                            Symbol next = rhs.get(j);
                            Set<String> firstNext = new HashSet<>(first.get(next));
                            followCurrent.addAll(firstNext);
                            followCurrent.remove("{e}");
                            if (!(next instanceof Nonterminal && emptyNonterminals.contains(next))) {
                                allNullable = false;
                                break;
                            }
                        }

                        if (allNullable || i == rhs.size() - 1) {
                            followCurrent.addAll(follow.get(rule.getLHS()));
                        }

                        if (followCurrent.size() != prevSize) {
                            changed = true;
                        }
                    }
                }
            }
        } while (changed);
    }

    // methods to access FIRST and FOLLOW
    public Set<String> getFirst(Symbol s) {
        return first.getOrDefault(s, Set.of());
    }

    public Set<String> getFollow(Nonterminal nt) {
        return follow.getOrDefault(nt, Set.of());
    }

    public Set<Nonterminal> getEmptyNonterminals() {
        return emptyNonterminals;
    }

    public boolean isLL1() {
        for (Nonterminal nt : g.getNonterminals()) {
            List<Rule> rules = nt.getRules();
    
            // store FIRST sets for RHSs
            List<Set<String>> firstSets = new ArrayList<>();
    
            for (Rule rule : rules) {
                Set<String> result = new HashSet<>();
                boolean nullable = true;
    
                for (Symbol s : rule.getRHS()) {
                    Set<String> fs = getFirst(s);
                    result.addAll(fs);
                    if (!fs.contains("{e}")) {
                        nullable = false;
                        break;
                    }
                }
                if (nullable) result.add("{e}");
                firstSets.add(result);
            }
    
            // compare FIRST sets pairwise
            for (int i = 0; i < firstSets.size(); i++) {
                for (int j = i + 1; j < firstSets.size(); j++) {
                    Set<String> intersection = new HashSet<>(firstSets.get(i));
                    intersection.retainAll(firstSets.get(j));
                    if (!intersection.isEmpty() && !intersection.equals(Set.of("{e}"))) {
                        return false;
                    }
    
                    // if {e} is in i-th, FOLLOW(nt) && FIRST(j) must be empty
                    if (firstSets.get(i).contains("{e}")) {
                        Set<String> overlap = new HashSet<>(firstSets.get(j));
                        overlap.retainAll(getFollow(nt));
                        if (!overlap.isEmpty()) return false;
                    }
    
                    // if {e} is in j-th, FOLLOW(nt) && FIRST(i) must be empty
                    if (firstSets.get(j).contains("{e}")) {
                        Set<String> overlap = new HashSet<>(firstSets.get(i));
                        overlap.retainAll(getFollow(nt));
                        if (!overlap.isEmpty()) return false;
                    }
                }
            }
        }
        return true;
    }
    
    
}
