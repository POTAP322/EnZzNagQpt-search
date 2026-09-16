package ru.renue.search.loader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.renue.search.model.Report;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvReportLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldLoadValidCsv() throws IOException {
        Path csv = tempDir.resolve("test.csv");
        Files.writeString(csv, """
                guid-1| code1| Описание 1
                guid-2| code2| Описание 2
                guid-3| code3| Описание 3
                """, StandardCharsets.UTF_8);

        List<Report> reports = new CsvReportLoader().load(csv);

        assertEquals(3, reports.size());
        assertEquals("guid-1", reports.get(0).getGuid());
        assertEquals("code1", reports.get(0).getCodeName());
        assertEquals("Описание 1", reports.get(0).getDescription());
    }

    @Test
    void shouldReturnEmptyListForEmptyFile() throws IOException {
        Path csv = tempDir.resolve("empty.csv");
        Files.writeString(csv, "", StandardCharsets.UTF_8);

        List<Report> reports = new CsvReportLoader().load(csv);

        assertTrue(reports.isEmpty());
    }

    @Test
    void shouldSkipBlankLines() throws IOException {
        Path csv = tempDir.resolve("blank.csv");
        Files.writeString(csv, """
                guid-1| code1| Описание 1

                guid-2| code2| Описание 2
                """, StandardCharsets.UTF_8);

        List<Report> reports = new CsvReportLoader().load(csv);

        assertEquals(2, reports.size());
    }

    @Test
    void shouldSkipMalformedLines() throws IOException {
        Path csv = tempDir.resolve("malformed.csv");
        Files.writeString(csv, """
                guid-1| code1| Описание 1
                broken line without separators
                guid-2| code2| Описание 2
                """, StandardCharsets.UTF_8);

        List<Report> reports = new CsvReportLoader().load(csv);

        assertEquals(2, reports.size());
        assertEquals("guid-1", reports.get(0).getGuid());
        assertEquals("guid-2", reports.get(1).getGuid());
    }

    @Test
    void shouldThrowWhenFileDoesNotExist() {
        Path csv = tempDir.resolve("nonexistent.csv");

        assertThrows(IllegalArgumentException.class,
                () -> new CsvReportLoader().load(csv));
    }

    @Test
    void shouldThrowWhenPathIsDirectory() {
        assertThrows(IllegalArgumentException.class,
                () -> new CsvReportLoader().load(tempDir));
    }

    @Test
    void shouldPreservePipeInsideDescription() throws IOException {
        Path csv = tempDir.resolve("with-pipe.csv");
        Files.writeString(csv, "guid-1| code1| Описание | с пайпом\n",
                StandardCharsets.UTF_8);

        List<Report> reports = new CsvReportLoader().load(csv);

        assertEquals(1, reports.size());
        assertEquals("Описание | с пайпом", reports.get(0).getDescription());
    }
}