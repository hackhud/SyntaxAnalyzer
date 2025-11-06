package ua.hackhud.simplesyntaxanalyzer;

import java.util.*;

public class Interpreter implements Stmt.Visitor<Void>, Expr.Visitor<Object> {
    private final Map<String, Object> env = new HashMap<>();
    private final Set<String> declared = new HashSet<>();

    public void interpret(Block program) {
        try {
            program.accept(this);
        } catch (RuntimeException ex) {
            System.err.println("Runtime error: " + ex.getMessage());
        }
    }

    public Void visitBlock(Block s) {
        for (Stmt st: s.stmts) st.accept(this);
        return null;
    }
    public Void visitVarDecl(VarDecl s) {
        if (declared.contains(s.name)) throw new RuntimeException("Variable already declared: " + s.name);
        declared.add(s.name);
        env.put(s.name, 0);
        return null;
    }
    public Void visitAssign(Assign s) {
        if (!declared.contains(s.name)) throw new RuntimeException("Undeclared variable: " + s.name);
        Object val = s.expr.accept(this);
        env.put(s.name, val);
        return null;
    }
    public Void visitIf(If s) {
        Object cond = s.cond.accept(this);
        if (!(cond instanceof Boolean)) throw new RuntimeException("Condition is not boolean");
        if ((Boolean)cond) s.thenBranch.accept(this);
        else if (s.elseBranch != null) s.elseBranch.accept(this);
        return null;
    }
    public Void visitWhile(While s) {
        Object cond = s.cond.accept(this);
        if (!(cond instanceof Boolean)) throw new RuntimeException("Condition is not boolean");
        while ((Boolean)cond) {
            s.body.accept(this);
            cond = s.cond.accept(this);
            if (!(cond instanceof Boolean)) throw new RuntimeException("Condition is not boolean");
        }
        return null;
    }
    public Void visitPrint(Print s) {
        Object val = s.expr.accept(this);
        System.out.println(val);
        return null;
    }
    public Void visitExprStmt(ExprStmt s) { s.expr.accept(this); return null; }


    public Object visitBinary(Binary e) {
        Object a = e.left.accept(this);
        Object b = e.right.accept(this);
        String op = e.op;

        if (a instanceof Integer && b instanceof Integer) {
            int ai = (Integer)a, bi = (Integer)b;
            switch (op) {
                case "+": return ai + bi;
                case "-": return ai - bi;
                case "*": return ai * bi;
                case "/": return ai / bi;
                case "<": return ai < bi;
                case ">": return ai > bi;
                case "<=": return ai <= bi;
                case ">=": return ai >= bi;
                case "==": return ai == bi;
                case "!=": return ai != bi;
            }
        }

        if (op.equals("and") || op.equals("or")) {
            if (!(a instanceof Boolean) || !(b instanceof Boolean)) throw new RuntimeException("Logical operands must be boolean");
            boolean ba = (Boolean)a, bb = (Boolean)b;
            if (op.equals("and")) return ba && bb;
            return ba || bb;
        }

        throw new RuntimeException("Unsupported binary op or operand types for op " + op);
    }
    public Object visitUnary(Unary e) {
        Object r = e.right.accept(this);
        if (e.op.equals("-")) {
            if (!(r instanceof Integer)) throw new RuntimeException("Unary - applied to non-int");
            return - (Integer) r;
        }
        throw new RuntimeException("Unsupported unary operator " + e.op);
    }
    public Object visitLiteral(Literal e) { return e.value; }
    public Object visitVar(Var e) {
        if (!declared.contains(e.name)) throw new RuntimeException("Undeclared variable " + e.name);
        return env.get(e.name);
    }
}

