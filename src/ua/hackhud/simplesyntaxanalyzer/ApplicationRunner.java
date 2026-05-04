package ua.hackhud.simplesyntaxanalyzer;

import java.io.PrintStream;
import java.util.Objects;

public class ApplicationRunner {
    private final SourceLoader sourceLoader;
    private final ProgramExecutor programExecutor;
    private final PrintStream out;
    private final PrintStream err;

    public ApplicationRunner(SourceLoader sourceLoader, ProgramExecutor programExecutor, PrintStream out, PrintStream err) {
        this.sourceLoader = Objects.requireNonNull(sourceLoader, "sourceLoader");
        this.programExecutor = Objects.requireNonNull(programExecutor, "programExecutor");
        this.out = Objects.requireNonNull(out, "out");
        this.err = Objects.requireNonNull(err, "err");
    }

    public void run(String[] args) {
        String[] actualArgs = args == null ? new String[0] : args;

        try {
            String source;
            if (actualArgs.length == 0) {
                out.println("Usage: java Main <source-file>");
                out.println("Running demo program...");
                source = sourceLoader.loadDemoProgram();
            } else {
                source = sourceLoader.loadFromFile(actualArgs[0]);
            }

            programExecutor.execute(source);
        } catch (Exception ex) {
            err.println("Error: " + ex.getMessage());
            ex.printStackTrace(err);
        }
    }
}
