package ua.hackhud.simplesyntaxanalyzer;

import java.util.*;

public class Parser {
    private final List<Token> tokens = new ArrayList<>();
    private int p = 0;

    public Parser(Lexer lex) {
        Token t;
        do {
            t = lex.nextToken();
            tokens.add(t);
        } while (t.type != Token.Type.EOF);
    }

    private Token peek() { return tokens.get(p); }
    private Token previous() { return tokens.get(p-1); }
    private Token advance() { if (!isAtEnd()) p++; return previous(); }
    private boolean isAtEnd() { return peek().type == Token.Type.EOF; }
    private boolean check(Token.Type t) { return peek().type == t; }
    private boolean match(Token.Type... types) {
        for (Token.Type t: types) if (check(t)) { advance(); return true; }
        return false;
    }
    private Token consume(Token.Type type, String msg) {
        if (check(type)) return advance();
        throw new RuntimeException("Parse error at " + peek().pos + ": " + msg + ". Found: " + peek());
    }

    public Block parse() {
        List<Stmt> stmts = new ArrayList<>();
        while (!isAtEnd()) {
            stmts.add(declaration());
        }
        return new Block(stmts);
    }

    private Stmt declaration() {
        if (match(Token.Type.INT)) {
            Token name = consume(Token.Type.IDENT, "Expect variable name after 'int'");
            consume(Token.Type.SEMI, "Expect ';' after declaration");
            return new VarDecl(name.text);
        }
        return statement();
    }

    private Stmt statement() {
        if (match(Token.Type.LBRACE)) {
            List<Stmt> list = new ArrayList<>();
            while (!check(Token.Type.RBRACE) && !isAtEnd()) list.add(declaration());
            consume(Token.Type.RBRACE, "Expect '}'");
            return new Block(list);
        }
        if (match(Token.Type.IF)) {
            consume(Token.Type.LPAREN, "Expect '(' after if");
            Expr cond = expression();
            consume(Token.Type.RPAREN, "Expect ')'");
            Stmt thenBranch = statement();
            Stmt elseBranch = null;
            if (match(Token.Type.ELSE)) elseBranch = statement();
            return new If(cond, thenBranch, elseBranch);
        }
        if (match(Token.Type.WHILE)) {
            consume(Token.Type.LPAREN, "Expect '(' after while");
            Expr cond = expression();
            consume(Token.Type.RPAREN, "Expect ')'");
            Stmt body = statement();
            return new While(cond, body);
        }
        if (match(Token.Type.PRINT)) {
            consume(Token.Type.LPAREN, "Expect '(' after print");
            Expr e = expression();
            consume(Token.Type.RPAREN, "Expect ')'");
            consume(Token.Type.SEMI, "Expect ';' after print");
            return new Print(e);
        }

        Expr e = expression();
        if (e instanceof Var && match(Token.Type.ASSIGN)) {
            Expr value = expression();
            consume(Token.Type.SEMI, "Expect ';' after assignment");
            return new Assign(((Var)e).name, value);
        } else {
            consume(Token.Type.SEMI, "Expect ';' after expression");
            return new ExprStmt(e);
        }
    }

    private Expr expression() { return or(); }

    private Expr or() {
        Expr e = and();
        while (match(Token.Type.OR)) {
            Token op = previous();
            Expr r = and();
            e = new Binary(e, op.text, r);
        }
        return e;
    }
    private Expr and() {
        Expr e = equality();
        while (match(Token.Type.AND)) {
            Token op = previous();
            Expr r = equality();
            e = new Binary(e, op.text, r);
        }
        return e;
    }
    private Expr equality() {
        Expr e = comparison();
        while (match(Token.Type.EQEQ, Token.Type.NEQ)) {
            Token op = previous();
            Expr r = comparison();
            e = new Binary(e, op.text, r);
        }
        return e;
    }
    private Expr comparison() {
        Expr e = term();
        while (match(Token.Type.LT, Token.Type.GT, Token.Type.LE, Token.Type.GE)) {
            Token op = previous();
            Expr r = term();
            e = new Binary(e, op.text, r);
        }
        return e;
    }
    private Expr term() {
        Expr e = factor();
        while (match(Token.Type.PLUS, Token.Type.MINUS)) {
            Token op = previous();
            Expr r = factor();
            e = new Binary(e, op.text, r);
        }
        return e;
    }
    private Expr factor() {
        Expr e = unary();
        while (match(Token.Type.STAR, Token.Type.SLASH)) {
            Token op = previous();
            Expr r = unary();
            e = new Binary(e, op.text, r);
        }
        return e;
    }
    private Expr unary() {
        if (match(Token.Type.MINUS)) {
            Token op = previous();
            Expr right = unary();
            return new Unary(op.text, right);
        }
        return primary();
    }
    private Expr primary() {
        if (match(Token.Type.NUMBER)) {
            return new Literal(Integer.parseInt(previous().text));
        }
        if (match(Token.Type.IDENT)) {
            return new Var(previous().text);
        }
        if (match(Token.Type.LPAREN)) {
            Expr e = expression();
            consume(Token.Type.RPAREN, "Expect ')'");
            return e;
        }
        throw new RuntimeException("Unexpected token in expression: " + peek());
    }
}

