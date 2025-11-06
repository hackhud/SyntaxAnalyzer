package ua.hackhud.simplesyntaxanalyzer;

import java.util.*;

abstract class Stmt {
    public interface Visitor<R> { R visitBlock(Block s); R visitVarDecl(VarDecl s); R visitAssign(Assign s);
        R visitIf(If s); R visitWhile(While s); R visitPrint(Print s); R visitExprStmt(ExprStmt s); }
    public abstract <R> R accept(Visitor<R> v);
}

abstract class Expr {
    public interface Visitor<R> { R visitBinary(Binary e); R visitLiteral(Literal e); R visitVar(Var e); R visitUnary(Unary e); }
    public abstract <R> R accept(Visitor<R> v);
}


class Block extends Stmt {
    public final List<Stmt> stmts;
    public Block(List<Stmt> stmts){ this.stmts = stmts; }
    public <R> R accept(Visitor<R> v){ return v.visitBlock(this); }
}
class VarDecl extends Stmt {
    public final String name;
    public VarDecl(String name){ this.name = name; }
    public <R> R accept(Visitor<R> v){ return v.visitVarDecl(this); }
}
class Assign extends Stmt {
    public final String name;
    public final Expr expr;
    public Assign(String name, Expr expr){ this.name = name; this.expr = expr; }
    public <R> R accept(Visitor<R> v){ return v.visitAssign(this); }
}
class If extends Stmt {
    public final Expr cond; public final Stmt thenBranch; public final Stmt elseBranch;
    public If(Expr cond, Stmt thenBranch, Stmt elseBranch){ this.cond = cond; this.thenBranch = thenBranch; this.elseBranch = elseBranch; }
    public <R> R accept(Visitor<R> v){ return v.visitIf(this); }
}
class While extends Stmt {
    public final Expr cond; public final Stmt body;
    public While(Expr cond, Stmt body){ this.cond = cond; this.body = body; }
    public <R> R accept(Visitor<R> v){ return v.visitWhile(this); }
}
class Print extends Stmt {
    public final Expr expr;
    public Print(Expr expr){ this.expr = expr; }
    public <R> R accept(Visitor<R> v){ return v.visitPrint(this); }
}
class ExprStmt extends Stmt {
    public final Expr expr; public ExprStmt(Expr expr){ this.expr = expr; }
    public <R> R accept(Visitor<R> v){ return v.visitExprStmt(this); }
}


class Binary extends Expr {
    public final Expr left; public final String op; public final Expr right;
    public Binary(Expr left, String op, Expr right){ this.left = left; this.op = op; this.right = right; }
    public <R> R accept(Visitor<R> v){ return v.visitBinary(this); }
}
class Unary extends Expr {
    public final String op; public final Expr right;
    public Unary(String op, Expr right){ this.op = op; this.right = right; }
    public <R> R accept(Visitor<R> v){ return v.visitUnary(this); }
}
class Literal extends Expr {
    public final Object value;
    public Literal(Object value){ this.value = value; }
    public <R> R accept(Visitor<R> v){ return v.visitLiteral(this); }
}
class Var extends Expr {
    public final String name;
    public Var(String name){ this.name = name; }
    public <R> R accept(Visitor<R> v){ return v.visitVar(this); }
}
