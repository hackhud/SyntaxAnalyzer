package ua.hackhud.simplesyntaxanalyzer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ApplicationRunnerTest {
    @Mock
    private SourceLoader sourceLoader;

    @Mock
    private ProgramExecutor programExecutor;

    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;
    private ApplicationRunner runner;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        runner = new ApplicationRunner(
                sourceLoader,
                programExecutor,
                new PrintStream(outContent, true, StandardCharsets.UTF_8),
                new PrintStream(errContent, true, StandardCharsets.UTF_8)
        );
    }

    @Test
    void run_without_args_loads_demo_and_executes_in_order() throws IOException {
        when(sourceLoader.loadDemoProgram()).thenReturn("print(999);");

        runner.run(new String[0]);

        InOrder inOrder = inOrder(sourceLoader, programExecutor);
        inOrder.verify(sourceLoader).loadDemoProgram();
        inOrder.verify(programExecutor).execute("print(999);");

        assertAll(
                () -> verify(sourceLoader, times(1)).loadDemoProgram(),
                () -> verify(sourceLoader, never()).loadFromFile(anyString()),
                () -> verify(programExecutor, times(1)).execute("print(999);"),
                () -> assertEquals(
                        List.of("Usage: java Main <source-file>", "Running demo program..."),
                        normalizedLines(outContent)
                ),
                () -> assertTrue(errContent.toString(StandardCharsets.UTF_8).isBlank())
        );
        verifyNoMoreInteractions(sourceLoader, programExecutor);
    }

    @Test
    void run_with_file_argument_uses_argument_matching_for_dynamic_behavior() throws IOException {
        when(sourceLoader.loadFromFile(argThat(path -> path.endsWith(".sa") && path.contains("program"))))
                .thenAnswer(invocation -> {
                    String path = invocation.getArgument(0, String.class);
                    return "print(%d);".formatted(path.length());
                });

        runner.run(new String[]{"program.sa"});

        assertAll(
                () -> verify(sourceLoader, times(1)).loadFromFile("program.sa"),
                () -> verify(programExecutor, times(1)).execute("print(10);"),
                () -> assertTrue(outContent.toString(StandardCharsets.UTF_8).isBlank()),
                () -> assertTrue(errContent.toString(StandardCharsets.UTF_8).isBlank())
        );
        verifyNoMoreInteractions(sourceLoader, programExecutor);
    }

    @Test
    void run_reports_exception_when_mock_loader_fails() throws IOException {
        when(sourceLoader.loadFromFile(anyString())).thenThrow(new IOException("Cannot read source file"));

        runner.run(new String[]{"broken.sa"});

        String stderr = errContent.toString(StandardCharsets.UTF_8);

        assertAll(
                () -> verify(sourceLoader, times(1)).loadFromFile("broken.sa"),
                () -> verify(programExecutor, never()).execute(anyString()),
                () -> assertTrue(stderr.contains("Error: Cannot read source file")),
                () -> assertTrue(stderr.contains("java.io.IOException: Cannot read source file"))
        );
        verifyNoMoreInteractions(sourceLoader, programExecutor);
    }

    @Test
    void run_uses_different_mock_responses_on_subsequent_calls() throws IOException {
        when(sourceLoader.loadDemoProgram()).thenReturn("print(1);", "print(2);");

        runner.run(new String[0]);
        runner.run(new String[0]);

        InOrder inOrder = inOrder(sourceLoader, programExecutor);
        inOrder.verify(sourceLoader).loadDemoProgram();
        inOrder.verify(programExecutor).execute("print(1);");
        inOrder.verify(sourceLoader).loadDemoProgram();
        inOrder.verify(programExecutor).execute("print(2);");

        assertAll(
                () -> verify(sourceLoader, times(2)).loadDemoProgram(),
                () -> verify(programExecutor, times(1)).execute("print(1);"),
                () -> verify(programExecutor, times(1)).execute("print(2);"),
                () -> assertEquals(
                        List.of(
                                "Usage: java Main <source-file>",
                                "Running demo program...",
                                "Usage: java Main <source-file>",
                                "Running demo program..."
                        ),
                        normalizedLines(outContent)
                )
        );
        verifyNoMoreInteractions(sourceLoader, programExecutor);
    }

    private List<String> normalizedLines(ByteArrayOutputStream stream) {
        return stream.toString(StandardCharsets.UTF_8)
                .lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();
    }
}
