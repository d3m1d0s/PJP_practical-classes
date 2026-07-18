package interpreter;

import org.antlr.v4.runtime.Token;

public class TypeCheckerVisitor extends LanguageBaseVisitor<Type> {

    private final SymbolTable symbolTable;
    private final ErrorCollector errors = new ErrorCollector();

    public TypeCheckerVisitor(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public boolean hasErrors() {
        return errors.hasErrors();
    }

    public void printErrors() {
        errors.printErrors();
    }

    @Override
    public Type visitDeclaration(LanguageParser.DeclarationContext ctx) {
        Type type = visit(ctx.primitiveType());
        for (var idToken : ctx.IDENTIFIER()) {
            String name = idToken.getText();
            if (!symbolTable.declare(name, type)) {
                Token t = idToken.getSymbol();
                errors.addError(t.getLine(), t.getCharPositionInLine(), "Variable '" + name + "' is already declared.");
            }
        }
        return null;
    }

    @Override
    public Type visitPrimitiveType(LanguageParser.PrimitiveTypeContext ctx) {
        if (ctx.type.getText().equals("int")) return Type.INT;
        if (ctx.type.getText().equals("float")) return Type.FLOAT;
        return Type.ERROR;
    }

    @Override
    public Type visitAssignment(LanguageParser.AssignmentContext ctx) {
        String varName = ctx.IDENTIFIER().getText();
        Token token = ctx.IDENTIFIER().getSymbol();

        if (!symbolTable.isDeclared(varName)) {
            errors.addError(token.getLine(), token.getCharPositionInLine(), "Variable '" + varName + "' is not declared.");
            return Type.ERROR;
        }

        Type leftType = symbolTable.getType(varName);
        Type rightType = visit(ctx.expr());

        if (rightType == Type.ERROR) return Type.ERROR;

        if (leftType == Type.INT && rightType == Type.FLOAT) {
            errors.addError(token.getLine(), token.getCharPositionInLine(), "Variable '" + varName + "' type is int, but the assigned value is float.");
            return Type.ERROR;
        }

        // assignment is an expression. returning the type enables chained assignments
        return leftType;
    }

    @Override
    public Type visitPrintExpr(LanguageParser.PrintExprContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Type visitAddSub(LanguageParser.AddSubContext ctx) {
        Type left = visit(ctx.expr(0));
        Type right = visit(ctx.expr(1));
        return computeBinaryType(ctx.op, left, right);
    }

    @Override
    public Type visitMulDiv(LanguageParser.MulDivContext ctx) {
        Type left = visit(ctx.expr(0));
        Type right = visit(ctx.expr(1));
        return computeBinaryType(ctx.op, left, right);
    }

    @Override
    public Type visitParens(LanguageParser.ParensContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Type visitFloat(LanguageParser.FloatContext ctx) {
        return Type.FLOAT;
    }

    @Override
    public Type visitInt(LanguageParser.IntContext ctx) {
        return Type.INT;
    }

    @Override
    public Type visitId(LanguageParser.IdContext ctx) {
        String varName = ctx.IDENTIFIER().getText();
        if (!symbolTable.isDeclared(varName)) {
            Token t = ctx.IDENTIFIER().getSymbol();
            errors.addError(t.getLine(), t.getCharPositionInLine(), "Variable '" + varName + "' is not declared.");
            return Type.ERROR;
        }
        return symbolTable.getType(varName);
    }

    private Type computeBinaryType(Token op, Type left, Type right) {
        if (left == Type.ERROR || right == Type.ERROR) return Type.ERROR;

        // % operator - only for int
        if (op.getText().equals("%")) {
            if (left != Type.INT || right != Type.INT) {
                errors.addError(op.getLine(), op.getCharPositionInLine(), "Modulo can be used only with integers.");
                return Type.ERROR;
            }
            return Type.INT;
        }

        if (left == Type.FLOAT || right == Type.FLOAT) return Type.FLOAT;
        return Type.INT;
    }
}
