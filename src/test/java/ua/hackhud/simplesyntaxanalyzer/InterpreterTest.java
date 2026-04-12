package ua.hackhud.simplesyntaxanalyzer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class InterpreterTest {
    private PrintStream originalOut;
    private PrintStream originalErr;
    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        originalErr = System.err;
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(errContent, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void executes_loop_and_if_else_program() {
        runProgram("""
                int x;
                int y;
                x = 1;
                y = 4;
                while (x < y) {
                    print(x);
                    x = x + 1;
                }
                if (x == y) { print(999); } else { print(0); }
                """);

        assertLinesMatch(
                List.of("1", "2", "3", "999"),
                normalizedLines(outContent)
        );
    }

    @ParameterizedTest(name = "evaluates arithmetic expression ''{0}'' to {1}")
    @CsvSource({
            "'1 + 2', 3",
            "'7 - 4', 3",
            "'3 * 5', 15",
            "'8 / 2', 4"
    })
    void evaluates_arithmetic_expressions(String expression, int expectedResult) {
        runProgram("""
                int x;
                x = %s;
                print(x);
                """.formatted(expression));

        assertEquals(List.of(String.valueOf(expectedResult)), normalizedLines(outContent));
    }

    @Test
    void reports_runtime_error_for_undeclared_variable_assignment() {
        runProgram("x = 1;");

        assertTrue(errContent.toString(StandardCharsets.UTF_8).contains("Runtime error: Undeclared variable: x"));
    }

    @Test
    void reports_runtime_error_for_duplicate_declaration() {
        runProgram("""
                int x;
                int x;
                """);

        assertTrue(errContent.toString(StandardCharsets.UTF_8).contains("Runtime error: Variable already declared: x"));
    }

    @Test
    void handles_boolean_logic_and_unary_minus() {
        runProgram("""
                int x;
                x = -5;
                if ((1 < 2) and (2 < 3)) { print(x); } else { print(0); }
                if ((1 > 2) or (3 > 2)) { print(1); } else { print(0); }
                """);

        assertAll(
                () -> assertLinesMatch(List.of("-5", "1"), normalizedLines(outContent)),
                () -> assertEquals("", errContent.toString(StandardCharsets.UTF_8).trim())
        );
    }

    private void runProgram(String source) {
        Lexer lexer = new Lexer(source);
        Parser parser = new Parser(lexer);
        Block program = parser.parse();
        new Interpreter().interpret(program);
    }

    private List<String> normalizedLines(ByteArrayOutputStream stream) {
        return stream.toString(StandardCharsets.UTF_8)
                .lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();
    }
}
