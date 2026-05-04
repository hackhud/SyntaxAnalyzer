package ua.hackhud.simplesyntaxanalyzer;

public class SyntaxAnalyzerExecutor implements ProgramExecutor {
    @Override
    public void execute(String source) {
        Lexer lexer = new Lexer(source);
        Parser parser = new Parser(lexer);
        Block program = parser.parse();
        Interpreter interpreter = new Interpreter();
        interpreter.interpret(program);
    }
}
