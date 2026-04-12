package ua.hackhud.simplesyntaxanalyzer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MainTest {
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
    void runs_demo_program_when_no_arguments_are_provided() throws IOException {
        Main.main(new String[0]);

        String stdout = outContent.toString(StandardCharsets.UTF_8);

        assertAll(
                () -> assertTrue(stdout.contains("Usage: java Main <source-file>")),
                () -> assertTrue(stdout.contains("Running demo program...")),
                () -> assertTrue(stdout.contains("999")),
                () -> assertTrue(errContent.toString(StandardCharsets.UTF_8).isBlank())
        );
    }

    @Test
    void runs_program_from_file(@TempDir Path tempDir) throws IOException {
        Path sourceFile = tempDir.resolve("program.sa");
        Files.writeString(sourceFile, """
                int x;
                x = 42;
                print(x);
                """);

        Main.main(new String[]{sourceFile.toString()});

        assertTrue(outContent.toString(StandardCharsets.UTF_8).contains("42"));
    }
}
