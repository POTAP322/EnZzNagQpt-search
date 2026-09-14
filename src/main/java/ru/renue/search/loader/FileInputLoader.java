package ru.renue.search.loader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class FileInputLoader implements InputLoader {
    @Override
    public List<String> load(Path path) throws IOException {
        return List.of();
    }
}
