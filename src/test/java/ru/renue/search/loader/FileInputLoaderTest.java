package ru.renue.search.loader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileInputLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldLoadValidFile() throws IOException {
        Path file = tempDir.resolve("input.txt");
        Files.writeString(file, """
                Как получить отчет?
                Список клиентов
                Число работников
                """, StandardCharsets.UTF_8);

        List<String> queries = new FileInputLoader().load(file);

        assertEquals(3, queries.size());
        assertEquals("Как получить отчет?", queries.get(0));
        assertEquals("Список клиентов", queries.get(1));
        assertEquals("Число работников", queries.get(2));
    }

    @Test
    void shouldReturnEmptyListForEmptyFile() throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.writeString(file, "", StandardCharsets.UTF_8);

        List<String> queries = new FileInputLoader().load(file);

        assertTrue(queries.isEmpty());
    }

    @Test
    void shouldSkipBlankLines() throws IOException {
        Path file = tempDir.resolve("blank.txt");
        Files.writeString(file, """
                Первый запрос

                Второй запрос
                   \s
                Третий запрос
                """, StandardCharsets.UTF_8);

        List<String> queries = new FileInputLoader().load(file);

        assertEquals(3, queries.size());
        assertEquals("Первый запрос", queries.get(0));
        assertEquals("Второй запрос", queries.get(1));
        assertEquals("Третий запрос", queries.get(2));
    }

    @Test
    void shouldTrimLines() throws IOException {
        Path file = tempDir.resolve("trim.txt");
        Files.writeString(file, "   запрос с пробелами   \n", StandardCharsets.UTF_8);

        List<String> queries = new FileInputLoader().load(file);

        assertEquals(1, queries.size());
        assertEquals("запрос с пробелами", queries.get(0));
    }

    @Test
    void shouldThrowWhenPathIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new FileInputLoader().load(null));
    }

    @Test
    void shouldThrowWhenFileDoesNotExist() {
        Path file = tempDir.resolve("nonexistent.txt");

        assertThrows(IllegalArgumentException.class,
                () -> new FileInputLoader().load(file));
    }

    @Test
    void shouldThrowWhenPathIsDirectory() {
        assertThrows(IllegalArgumentException.class,
                () -> new FileInputLoader().load(tempDir));
    }
}