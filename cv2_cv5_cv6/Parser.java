package cv2_cv5_cv6;

import java.util.*;

/*
  Grammar for the recursive descent:
  E  :  T E1;        (1)
  E1 :  '+' T E1     (2)
     |  '-' T E1     (3)
     |  {e};         (4)
  T  :  F T1;        (5)
  T1 :  '*' F T1     (6)
     |  '/' F T1     (7)
     |  {e}          (8)
  F  :  '(' E ')'    (9)
     |  num          (10)
 */

 public class Parser {
    private final List<Token> tokens;
    private int index = 0;
    private final List<Integer> rulesUsed = new ArrayList<>();
    private final boolean evaluate;

    public Parser(List<Token> tokens) {
        this(tokens, true);
    }

    public Parser(List<Token> tokens, boolean evaluate) {
        this.tokens = tokens;
        this.evaluate = evaluate;
    }

    public int parse() {
        int result = E();
        if (index < tokens.size()) throw new RuntimeException("Unexpected token after expression");
        return result;
    }

    public List<Integer> getRuleNumbers() {
        return rulesUsed;
    }

    private int E() {
        rulesUsed.add(1);
        int t = T();
        return E1(t);
    }

    private int E1(int acc) {
        if (match("+")) {
            rulesUsed.add(2);
            int t = T();
            return E1(acc + t);
        } else if (match("-")) {
            rulesUsed.add(3);
            int t = T();
            return E1(acc - t);
        } else {
            rulesUsed.add(4);
            return acc; // {e}
        }
    }

    private int T() {
        rulesUsed.add(5);
        int f = F();
        return T1(f);
    }

    private int T1(int acc) {
        if (match("*")) {
            rulesUsed.add(6);
            int f = F();
            return T1(acc * f);
        } else if (match("/")) {
            rulesUsed.add(7);
            int f = F();
            if (!evaluate) return T1(0); // only the rule trace matters here
            if (f == 0) throw new ArithmeticException("Division by zero");
            return T1(acc / f);
        } else {
            rulesUsed.add(8);
            return acc; // {e}
        }
    }

    private int F() {
        if (match("(")) {
            rulesUsed.add(9);
            int e = E();
            if (!match(")")) throw new RuntimeException("Expected )");
            return e;
        } else if (peek().type == TokenType.NUMBER) {
            rulesUsed.add(10);
            int value = Integer.parseInt(peek().value);
            advance();
            return value;
        } else {
            throw new RuntimeException("Unexpected token in F()");
        }
    }

    private boolean match(String symbol) {
        if (index < tokens.size() && tokens.get(index).value.equals(symbol)) {
            index++;
            return true;
        }
        return false;
    }

    private Token peek() {
        if (index >= tokens.size()) throw new RuntimeException("Unexpected end of input");
        return tokens.get(index);
    }

    private void advance() {
        index++;
    }
}

