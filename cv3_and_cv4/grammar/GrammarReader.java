package cv3_and_cv4.grammar;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

public final class GrammarReader {

    public GrammarReader(Reader input) {
        inp = new LineNumberReader(input);
        inp.setLineNumber(1);
    }

    public Grammar read() throws GrammarException, IOException {
        GrammarImpl grammar = new GrammarImpl();

        ch = inp.read();
        int sym = getSym();

        while (sym != -1) {
            if (sym != SYM_NT) {
                error("On the left side of the rule, we are expecting a nonterminal.");
            }
            Nonterminal lhs = grammar.addNonterminal(attr);
            if (grammar.getStartNonterminal() == null) {
                grammar.setStartNonterminal(lhs);
            }
            sym = getSym();
            if (sym != ':') {
                error("':' expected.");
            }
            do {
                sym = getSym();
                Rule rule = new Rule(lhs);
            
                // an empty alternative (nothing before ';' or '|'). epsilon is encoded as the synthetic terminal "{e}"
                if (sym == ';' || sym == '|') {
                    Terminal epsilon = grammar.addTerminal("{e}");
                    rule.addSymbol(epsilon);
                    lhs.addRule(rule);
                    continue;
                }
            
                while (sym == SYM_NT || sym == SYM_T) {
                    if (sym == SYM_NT) {
                        Nonterminal nt = grammar.addNonterminal(attr);
                        rule.addSymbol(nt);
                        sym = getSym();
                    } else if (sym == SYM_T) {
                        Terminal t = grammar.addTerminal(attr);
                        rule.addSymbol(t);
                        sym = getSym();
                    }
                }
            
                lhs.addRule(rule);
            } while (sym == '|');
            
            if (sym != ';') {
                error("';' expected.");
            }
            sym = getSym();
        }
        return grammar;
    }

    private void error(String msg) throws GrammarException {
        throw new GrammarException(msg, inp.getLineNumber());
    }

    private static final int SYM_NT = 'N';
    private static final int SYM_T = 'T';
    private static final int SYM_EOF = -1;

    private int getSym() throws IOException {
        for (;;) {
            if (ch < 0) {
                return SYM_EOF;
            }
            if (Character.isWhitespace((char) ch)) {
                ch = inp.read();
            } else if (ch == '{') {
                do {
                    ch = inp.read();
                } while (ch >= 0 && ch != '}');
                if (ch >= 0) {
                    ch = inp.read();
                }
            } else {
                break;
            }
        }

        if (Character.isLetter((char) ch)) {
            StringBuilder buf = new StringBuilder();
            do {
                buf.append((char) ch);
                ch = inp.read();
            } while (ch > 0 && Character.isLetterOrDigit((char) ch));
            attr = buf.toString();
            return Character.isLowerCase(attr.charAt(0)) ? SYM_T : SYM_NT;
        }

        int sym = ch;
        ch = inp.read();
        return sym;
    }
    private final LineNumberReader inp;

    private int ch;

    private String attr;
}

