package ua.hackhud.simplesyntaxanalyzer;

import java.nio.file.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String src;
        if (args.length == 0) {
            System.out.println("Usage: java Main <source-file>");
            System.out.println("Running demo program...");
            src = demo();
        } else {
            src = new String(Files.readAllBytes(Paths.get(args[0])));
        }

        try {
            Lexer lex = new Lexer(src);
            Parser parser = new Parser(lex);
            Block program = parser.parse();
            Interpreter it = new Interpreter();
            it.interpret(program);
        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static String demo() {
        return ""
                + "int x;\n"
                + "int y;\n"
                + "x = 1;\n"
                + "y = 10;\n"
                + "while (x < y) {\n"
                + "  print(x);\n"
                + "  x = x + 1;\n"
                + "}\n"
                + "if (x == y) { print(999); } else { print(0); }\n";
    }
}

