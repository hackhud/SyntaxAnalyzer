package ua.hackhud.simplesyntaxanalyzer;

// Token.java
public class Token {
    public enum Type {
        PLUS, MINUS, STAR, SLASH, LPAREN, RPAREN, LBRACE, RBRACE, SEMI, COMMA,
        LT, GT, LE, GE, EQEQ, NEQ, ASSIGN,

        NUMBER, IDENT,

        INT, IF, ELSE, WHILE, PRINT,

        AND, OR,

        EOF
    }

    public final Type type;
    public final String text;
    public final int pos;

    public Token(Type type, String text, int pos) {
        this.type = type;
        this.text = text;
        this.pos = pos;
    }

    @Override
    public String toString() {
        return type + (text != null ? "(" + text + ")" : "");
    }
}
