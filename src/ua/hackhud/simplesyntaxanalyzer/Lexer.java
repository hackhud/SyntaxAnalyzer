package ua.hackhud.simplesyntaxanalyzer;

import java.util.*;

public class Lexer {
    private final String input;
    private int p = 0;
    private final int n;

    public Lexer(String input) {
        this.input = input;
        this.n = input.length();
    }

    private char peek() {
        if (p >= n) return '\0';
        return input.charAt(p);
    }

    private char next() {
        char c = peek();
        p++;
        return c;
    }

    private void skipWhitespaceAndComments() {
        while (true) {
            char c = peek();
            if (Character.isWhitespace(c)) { next(); continue; }
            if (c == '/' && p+1 < n && input.charAt(p+1) == '/') {
                next(); next();
                while (peek() != '\n' && peek() != '\0') next();
                continue;
            }
            break;
        }
    }

    public Token nextToken() {
        skipWhitespaceAndComments();
        int pos = p;
        char c = peek();

        if (c == '\0') return new Token(Token.Type.EOF, "<EOF>", p);

        switch (c) {
            case '+': next(); return new Token(Token.Type.PLUS, "+", pos);
            case '-': next(); return new Token(Token.Type.MINUS, "-", pos);
            case '*': next(); return new Token(Token.Type.STAR, "*", pos);
            case '/': next(); return new Token(Token.Type.SLASH, "/", pos);
            case '(': next(); return new Token(Token.Type.LPAREN, "(", pos);
            case ')': next(); return new Token(Token.Type.RPAREN, ")", pos);
            case '{': next(); return new Token(Token.Type.LBRACE, "{", pos);
            case '}': next(); return new Token(Token.Type.RBRACE, "}", pos);
            case ';': next(); return new Token(Token.Type.SEMI, ";", pos);
            case ',': next(); return new Token(Token.Type.COMMA, ",", pos);
            case '<':
                next();
                if (peek()=='='){ next(); return new Token(Token.Type.LE, "<=", pos);}
                return new Token(Token.Type.LT, "<", pos);
            case '>':
                next();
                if (peek()=='='){ next(); return new Token(Token.Type.GE, ">=", pos);}
                return new Token(Token.Type.GT, ">", pos);
            case '=':
                next();
                if (peek()=='='){ next(); return new Token(Token.Type.EQEQ, "==", pos);}
                return new Token(Token.Type.ASSIGN, "=", pos);
            case '!':
                next();
                if (peek()=='='){ next(); return new Token(Token.Type.NEQ, "!=", pos);}
                throw new RuntimeException("Unexpected ! at " + pos);
        }

        if (Character.isDigit(c)) {
            StringBuilder sb = new StringBuilder();
            while (Character.isDigit(peek())) sb.append(next());
            return new Token(Token.Type.NUMBER, sb.toString(), pos);
        }

        if (Character.isLetter(c) || c == '_') {
            StringBuilder sb = new StringBuilder();
            while (Character.isLetterOrDigit(peek()) || peek()=='_') sb.append(next());
            String word = sb.toString();
            switch (word) {
                case "int": return new Token(Token.Type.INT, word, pos);
                case "if": return new Token(Token.Type.IF, word, pos);
                case "else": return new Token(Token.Type.ELSE, word, pos);
                case "while": return new Token(Token.Type.WHILE, word, pos);
                case "print": return new Token(Token.Type.PRINT, word, pos);
                case "and": return new Token(Token.Type.AND, word, pos);
                case "or": return new Token(Token.Type.OR, word, pos);
                default: return new Token(Token.Type.IDENT, word, pos);
            }
        }

        throw new RuntimeException("Unknown char '" + c + "' at " + pos);
    }
}

