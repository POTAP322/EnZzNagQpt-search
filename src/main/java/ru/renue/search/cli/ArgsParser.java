package ru.renue.search.cli;

import java.nio.file.Path;

public class ArgsParser {
    public AppConfig parse(String[] args) {
        Path data = null;
        Path input = null;
        Path output = null;

        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 >= args.length) {
                throw new IllegalArgumentException("Missing value for: " + args[i]);
            }
            switch (args[i]) {
                case "--data":
                    data = Path.of(args[i + 1]);
                    break;
                case "--input-file":
                    input = Path.of(args[i + 1]);
                    break;
                case "--output-file":
                    output = Path.of(args[i + 1]);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown argument: " + args[i]);
            }
        }
        if (data == null) throw new IllegalArgumentException("Missing --data");
        if (input == null) throw new IllegalArgumentException("Missing --input-file");
        if (output == null) throw new IllegalArgumentException("Missing --output-file");

        return new AppConfig(data, input, output);
    }
}
