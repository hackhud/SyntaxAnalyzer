package ua.hackhud.simplesyntaxanalyzer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LexerTest {
    private List<Token.Type> tokenTypes;

    @BeforeEach
    void setUp() {
        tokenTypes = new ArrayList<>();
    }

    @ParameterizedTest(name = "lexes reserved word ''{0}'' as {1}")
    @CsvSource({
            "int, INT",
            "if, IF",
            "else, ELSE",
            "while, WHILE",
            "print, PRINT",
            "and, AND",
            "or, OR"
    })
    void lexes_reserved_words(String source, Token.Type expectedType) {
        Lexer lexer = new Lexer(source);

        Token token = lexer.nextToken();

        assertAll(
                () -> assertEquals(expectedType, token.type),
                () -> assertEquals(source, token.text),
                () -> assertEquals(0, token.pos)
        );
    }

    @Test
    void skips_whitespace_and_comments_and_preserves_token_order() {
        Lexer lexer = new Lexer("""
                int x;
                // ignore this line
                x = 10 + 20;
                print(x);
                """);

        Token token;
        do {
            token = lexer.nextToken();
            tokenTypes.add(token.type);
        } while (token.type != Token.Type.EOF);

        assertIterableEquals(
                List.of(
                        Token.Type.INT,
                        Token.Type.IDENT,
                        Token.Type.SEMI,
                        Token.Type.IDENT,
                        Token.Type.ASSIGN,
                        Token.Type.NUMBER,
                        Token.Type.PLUS,
                        Token.Type.NUMBER,
                        Token.Type.SEMI,
                        Token.Type.PRINT,
                        Token.Type.LPAREN,
                        Token.Type.IDENT,
                        Token.Type.RPAREN,
                        Token.Type.SEMI,
                        Token.Type.EOF
                ),
                tokenTypes
        );
    }

    @Test
    void throws_for_lonely_exclamation_mark() {
        Lexer lexer = new Lexer("!");

        RuntimeException exception = assertThrows(RuntimeException.class, lexer::nextToken);

        assertTrue(exception.getMessage().contains("Unexpected !"));
    }

    @Test
    void token_to_string_contains_type_and_text() {
        Token token = new Token(Token.Type.NUMBER, "42", 3);

        assertEquals("NUMBER(42)", token.toString());
    }

    @Test
    void collects_identifier_and_number_tokens() {
        Lexer lexer = new Lexer("answer = 123;");

        List<Token> tokens = new ArrayList<>();
        Token token;
        do {
            token = lexer.nextToken();
            tokens.add(token);
        } while (token.type != Token.Type.EOF);

        List<String> renderedTokens = tokens.stream()
                .map(Token::toString)
                .collect(Collectors.toList());

        assertIterableEquals(
                List.of("IDENT(answer)", "ASSIGN(=)", "NUMBER(123)", "SEMI(;)", "EOF(<EOF>)"),
                renderedTokens
        );
    }
}
