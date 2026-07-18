package interpreter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class Main {

    private static final String EXTENSION = "lang";
    private static final String DIRBASE = "src/test/resources/";

    public static void main(String[] args) throws IOException {
        String[] files = (args.length == 0) ? new String[]{"test." + EXTENSION} : args;
        System.out.println("Dirbase: " + DIRBASE);

        for (String file : files) {
            System.out.println("START: " + file);

            CharStream in = CharStreams.fromFileName(DIRBASE + file);
            LanguageLexer lexer = new LanguageLexer(in);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            LanguageParser parser = new LanguageParser(tokens);

            parser.removeErrorListeners();
            parser.addErrorListener(new VerboseListener());

            LanguageParser.ProgramContext tree = parser.program();

            if (parser.getNumberOfSyntaxErrors() > 0) {
                System.err.println("Aborted due to syntax errors.");
                return;
            }

            SymbolTable symbolTable = new SymbolTable();

            TypeCheckerVisitor typeChecker = new TypeCheckerVisitor(symbolTable);
            typeChecker.visit(tree);

            if (typeChecker.hasErrors()) {
                typeChecker.printErrors();
                System.err.println("Aborted due to type errors.");
                return;
            }

            CodeGeneratorVisitor generator = new CodeGeneratorVisitor(symbolTable);
            generator.visit(tree);

            List<Instruction> instructions = generator.getInstructions();
            List<String> instructionStrings = new ArrayList<>();
            for (Instruction inst : instructions) {
                System.out.println(inst);
            }

            for (Instruction inst : instructions) {
                instructionStrings.add(inst.toString());
            }

            // to run the stack machine on the reference listing instead:
            // instructionStrings = Files.readAllLines(Paths.get("input.txt"));

            StackMachine machine = new StackMachine();
            machine.execute(instructionStrings);

            System.out.println("FINISH: " + file);
        }
    }
}
