package ru.renue.search.cli;

import java.nio.file.Path;

public class AppConfig {
    private final Path dataPath;
    private final Path inputPath;
    private final Path outputPath;

    public AppConfig(Path dataPath, Path inputPath, Path outputPath) {
        this.dataPath = dataPath;
        this.inputPath = inputPath;
        this.outputPath = outputPath;
    }

    public Path getDataPath() {
        return dataPath;
    }

    public Path getInputPath() {
        return inputPath;
    }

    public Path getOutputPath() {
        return outputPath;
    }
}
