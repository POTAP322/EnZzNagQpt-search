package ru.renue.search.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.renue.search.model.SearchResponse;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;

public class JsonWriter {
    private final ObjectMapper mapper = new ObjectMapper();

    public void write(SearchResponse response, Path outputPath) throws IOException {
        if (response == null) {
            throw new IllegalArgumentException("Response cannot be null");
        }
        if (outputPath == null) {
            throw new IllegalArgumentException("Output path cannot be null");
        }

        Path parent = outputPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(outputPath.toFile(), response);
    }
}
