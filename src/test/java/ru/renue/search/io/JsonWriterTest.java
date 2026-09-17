package ru.renue.search.io;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.renue.search.model.SearchResponse;
import ru.renue.search.model.SearchResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonWriterTest {

    @TempDir
    Path tempDir;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldWriteValidJson() throws IOException {
        SearchResult result = new SearchResult(
                "тестовый запрос",
                List.of("guid-1", "guid-2"),
                42L
        );
        SearchResponse response = new SearchResponse(100L, List.of(result));

        Path output = tempDir.resolve("result.json");
        new JsonWriter().write(response, output);

        assertTrue(Files.exists(output));

        JsonNode root = mapper.readTree(output.toFile());
        assertEquals(100L, root.get("initTime").asLong());
        assertTrue(root.get("result").isArray());
        assertEquals(1, root.get("result").size());

        JsonNode first = root.get("result").get(0);
        assertEquals("тестовый запрос", first.get("search").asText());
        assertEquals(2, first.get("result").size());
        assertEquals("guid-1", first.get("result").get(0).asText());
        assertEquals("guid-2", first.get("result").get(1).asText());
        assertEquals(42L, first.get("time").asLong());
    }

    @Test
    void shouldOverwriteExistingFile() throws IOException {
        Path output = tempDir.resolve("result.json");
        Files.writeString(output, "старое содержимое");

        SearchResponse response = new SearchResponse(
                10L,
                List.of(new SearchResult("q", List.of("g"), 1L))
        );

        new JsonWriter().write(response, output);

        String content = Files.readString(output);
        assertTrue(content.contains("initTime"));
        assertFalse(content.contains("старое содержимое"));
    }

    @Test
    void shouldCreateParentDirectories() throws IOException {
        Path output = tempDir.resolve("nested/deep/result.json");

        SearchResponse response = new SearchResponse(1L, List.of());
        new JsonWriter().write(response, output);

        assertTrue(Files.exists(output));
    }

    @Test
    void shouldWriteEmptyResultArray() throws IOException {
        SearchResponse response = new SearchResponse(5L, List.of());

        Path output = tempDir.resolve("empty.json");
        new JsonWriter().write(response, output);

        JsonNode root = mapper.readTree(output.toFile());
        assertEquals(5L, root.get("initTime").asLong());
        assertTrue(root.get("result").isArray());
        assertEquals(0, root.get("result").size());
    }

    @Test
    void shouldThrowWhenResponseIsNull() {
        Path output = tempDir.resolve("out.json");

        assertThrows(IllegalArgumentException.class,
                () -> new JsonWriter().write(null, output));
    }

    @Test
    void shouldThrowWhenPathIsNull() {
        SearchResponse response = new SearchResponse(1L, List.of());

        assertThrows(IllegalArgumentException.class,
                () -> new JsonWriter().write(response, null));
    }
}