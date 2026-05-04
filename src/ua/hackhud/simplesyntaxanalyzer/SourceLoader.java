package ua.hackhud.simplesyntaxanalyzer;

import java.io.IOException;

public interface SourceLoader {
    String loadDemoProgram();

    String loadFromFile(String path) throws IOException;
}
