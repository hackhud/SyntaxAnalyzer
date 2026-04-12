package ua.hackhud.simplesyntaxanalyzer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class ParserTest {
    private Parser parser;

    @BeforeEach
    void setUp() {
        parser = null;
    }

    @Test
    void parses_declaration_assignment_and_print_statement() {
        parser = new Parser(new Lexer("""
                int x;
                x = 5;
                print(x);
                """));

        Block program = parser.parse();

        assertEquals(3, program.stmts.size());
        assertInstanceOf(VarDecl.class, program.stmts.get(0));
        assertInstanceOf(Assign.class, program.stmts.get(1));
        assertInstanceOf(Print.class, program.stmts.get(2));
    }

    @Test
    void respects_operator_precedence_inside_assignment() {
        parser = new Parser(new Lexer("""
                int x;
                x = 1 + 2 * 3;
                """));

        Block program = parser.parse();
        Assign assignment = (Assign) program.stmts.get(1);
        Binary sum = assertInstanceOf(Binary.class, assignment.expr);
        Literal left = assertInstanceOf(Literal.class, sum.left);
        Binary product = assertInstanceOf(Binary.class, sum.right);

        assertAll(
                () -> assertEquals("+", sum.op),
                () -> assertEquals(1, left.value),
                () -> assertEquals("*", product.op),
                () -> assertEquals(2, ((Literal) product.left).value),
                () -> assertEquals(3, ((Literal) product.right).value)
        );
    }

    @Test
    void parses_if_else_with_block_bodies() {
        parser = new Parser(new Lexer("""
                int x;
                if (1 < 2) { print(1); } else { print(0); }
                """));

        Block program = parser.parse();
        If ifStatement = assertInstanceOf(If.class, program.stmts.get(1));
        Block thenBlock = assertInstanceOf(Block.class, ifStatement.thenBranch);
        Block elseBlock = assertInstanceOf(Block.class, ifStatement.elseBranch);

        assertAll(
                () -> assertInstanceOf(Binary.class, ifStatement.cond),
                () -> assertEquals(1, thenBlock.stmts.size()),
                () -> assertEquals(1, elseBlock.stmts.size())
        );
    }

    @ParameterizedTest(name = "rejects malformed program: {0}")
    @ValueSource(strings = {
            "int ;",
            "print(1)",
            "if (1 < 2 { print(1); }",
            "x = ;"
    })
    void rejects_invalid_programs(String source) {
        parser = new Parser(new Lexer(source));

        RuntimeException exception = assertThrows(RuntimeException.class, parser::parse);

        assertTrue(exception.getMessage().contains("Parse error") || exception.getMessage().contains("Unexpected token"));
    }

    @ParameterizedTest(name = "parses single statement block for program: {0}")
    @MethodSource("singleStatementPrograms")
    void parses_single_statement_programs(String source, Class<? extends Stmt> expectedStatementType) {
        parser = new Parser(new Lexer(source));

        Block program = parser.parse();

        assertAll(
                () -> assertEquals(1, program.stmts.size()),
                () -> assertInstanceOf(expectedStatementType, program.stmts.get(0))
        );
    }

    private static Stream<Arguments> singleStatementPrograms() {
        return Stream.of(
                Arguments.of("print(10);", Print.class),
                Arguments.of("while (1 < 2) print(1);", While.class),
                Arguments.of("{ print(1); }", Block.class)
        );
    }
}
