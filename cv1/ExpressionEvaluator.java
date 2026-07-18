package cv1;
import java.util.List;
import java.util.Stack;

// evaluation of an expression in postfix notation
public class ExpressionEvaluator {
    public static int evaluatePostfix(List<String> postfix) throws Exception {
        Stack<Integer> stack = new Stack<>();
        
        for (String token : postfix) {
            if (OperatorUtil.isNumeric(token)) {
                stack.push(Integer.parseInt(token));
            } else {
                if (stack.size() < 2) throw new Exception("ERROR");

                int b = stack.pop();
                int a = stack.pop();

                switch (token.charAt(0)) {
                    case '+': stack.push(a + b); break;
                    case '-': stack.push(a - b); break;
                    case '*': stack.push(a * b); break;
                    case '/': 
                        if (b == 0) throw new Exception("ERROR");
                        stack.push(a / b); 
                        break;
                    default: throw new Exception("ERROR");
                }
            }
        }
        
        if (stack.size() != 1) throw new Exception("ERROR");
        return stack.pop();
    }
}
