package interpreter;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;

import interpreter.LanguageParser.AddSubContext;
import interpreter.LanguageParser.AssignmentContext;
import interpreter.LanguageParser.DeclarationContext;
import interpreter.LanguageParser.FloatContext;
import interpreter.LanguageParser.IdContext;
import interpreter.LanguageParser.IntContext;
import interpreter.LanguageParser.MulDivContext;
import interpreter.LanguageParser.ParensContext;
import interpreter.LanguageParser.PrintExprContext;

public class CodeGeneratorVisitor extends LanguageBaseVisitor<Type> {

    private final SymbolTable symbolTable;
    private final List<Instruction> instructions = new ArrayList<>();

    public CodeGeneratorVisitor(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    @Override
    public Type visitDeclaration(DeclarationContext ctx) {
        String typeText = ctx.primitiveType().getText();
        Type type = typeText.equals("int") ? Type.INT : Type.FLOAT;

        for (TerminalNode id : ctx.IDENTIFIER()) {
            String name = id.getText();
            symbolTable.define(name, type);

            // init with 0
            if (type == Type.INT) {
                instructions.add(new Instruction(Instruction.OpCode.PUSH_I, "0"));
                instructions.add(new Instruction(Instruction.OpCode.SAVE_I, name));
            } else {
                instructions.add(new Instruction(Instruction.OpCode.PUSH_F, "0"));
                instructions.add(new Instruction(Instruction.OpCode.SAVE_F, name));
            }
        }

        return null;
    }

    @Override
    public Type visitPrintExpr(PrintExprContext ctx) {
        Type exprType = visit(ctx.expr());

        // then print
        if (exprType == Type.FLOAT) {
            instructions.add(new Instruction(Instruction.OpCode.PRINT_F));
        } else {
            instructions.add(new Instruction(Instruction.OpCode.PRINT_I));
        }
        return exprType;
    }


    @Override
    public Type visitAssignment(AssignmentContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        Type varType = symbolTable.lookup(name);

        Type exprType = visit(ctx.expr());

        // save result and leave it on stack (for chained assigns)
        if (varType == Type.FLOAT || exprType == Type.FLOAT) {
            instructions.add(new Instruction(Instruction.OpCode.SAVE_F, name));
            instructions.add(new Instruction(Instruction.OpCode.LOAD, name)); // важно: вернуть значение на стек
        } else {
            instructions.add(new Instruction(Instruction.OpCode.SAVE_I, name));
            instructions.add(new Instruction(Instruction.OpCode.LOAD, name));
        }

        return varType;
    }


    @Override
    public Type visitId(IdContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        Type varType = symbolTable.lookup(name);
        instructions.add(new Instruction(Instruction.OpCode.LOAD, name));
        return varType;
    }

    @Override
    public Type visitInt(IntContext ctx) {
        instructions.add(new Instruction(Instruction.OpCode.PUSH_I, ctx.INT().getText()));
        return Type.INT;
    }

    @Override
    public Type visitFloat(FloatContext ctx) {
        instructions.add(new Instruction(Instruction.OpCode.PUSH_F, ctx.FLOAT().getText()));
        return Type.FLOAT;
    }

    @Override
    public Type visitParens(ParensContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Type visitAddSub(AddSubContext ctx) {
        Type leftType = visit(ctx.expr(0));
        Type rightType = visit(ctx.expr(1));
        Type resultType = (leftType == Type.FLOAT || rightType == Type.FLOAT) ? Type.FLOAT : Type.INT;

        switch (ctx.op.getText()) {
            case "+" -> instructions.add(new Instruction(Instruction.OpCode.ADD));
            case "-" -> instructions.add(new Instruction(Instruction.OpCode.SUB));
        }

        return resultType;
    }

    @Override
    public Type visitMulDiv(MulDivContext ctx) {
        Type leftType = visit(ctx.expr(0));
        Type rightType = visit(ctx.expr(1));
        Type resultType = (leftType == Type.FLOAT || rightType == Type.FLOAT) ? Type.FLOAT : Type.INT;

        switch (ctx.op.getText()) {
            case "*" -> instructions.add(new Instruction(Instruction.OpCode.MUL));
            case "/" -> instructions.add(new Instruction(Instruction.OpCode.DIV));
            case "%" -> instructions.add(new Instruction(Instruction.OpCode.MOD));
        }

        return resultType;
    }
}
