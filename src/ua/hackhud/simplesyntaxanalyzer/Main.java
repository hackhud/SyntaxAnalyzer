package ua.hackhud.simplesyntaxanalyzer;

public class Main {
    public static void main(String[] args) {
        ApplicationRunner runner = new ApplicationRunner(
                new FileSystemSourceLoader(),
                new SyntaxAnalyzerExecutor(),
                System.out,
                System.err
        );
        runner.run(args);
    }
}

