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

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public int parse() {
        int result = E();
        if (index < tokens.size()) throw new RuntimeException("Unexpected token after expression");
        return result;
    }

    private int E() {
        int t = T();
        return E1(t);
    }

    private int E1(int acc) {
        if (match("+")) {
            int t = T();
            return E1(acc + t);
        } else if (match("-")) {
            int t = T();
            return E1(acc - t);
        } else {
            return acc; // {e}
        }
    }

    private int T() {
        int f = F();
        return T1(f);
    }

    private int T1(int acc) {
        if (match("*")) {
            int f = F();
            return T1(acc * f);
        } else if (match("/")) {
            int f = F();
            if (f == 0) throw new ArithmeticException("Division by zero");
            return T1(acc / f);
        } else {
            return acc; // {e}
        }
    }

    private int F() {
        if (match("(")) {
            int e = E();
            if (!match(")")) throw new RuntimeException("Expected )");
            return e;
        } else if (peek().type == TokenType.NUMBER) {
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

