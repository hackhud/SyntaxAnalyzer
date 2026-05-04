package ua.hackhud.simplesyntaxanalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSystemSourceLoader implements SourceLoader {
    @Override
    public String loadDemoProgram() {
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

    @Override
    public String loadFromFile(String path) throws IOException {
        return Files.readString(Path.of(path));
    }
}
