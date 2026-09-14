package ru.renue.search.loader;

import ru.renue.search.model.Report;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class CsvReportLoader implements ReportLoader{
    @Override
    public List<Report> load(Path path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        if (!Files.isRegularFile(path)) {
            throw new IOException("Not a regular file: " + path);
        }

        List<Report> reports = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                Report report = parseReport(line);
                reports.add(report);
            }
        }
        return reports;
    }

    private Report parseReport (String line) throws IOException{
        String[] lineParts = line.split("\\|");
        if (lineParts.length != 3) {
            throw new IOException("Wrong format: expected 3 fields, got " + lineParts.length);
        }
        String guid = lineParts[0].trim();
        String codeName = lineParts[1].trim();
        String description = lineParts[2].trim();
        return new Report(guid,codeName,description);
    }
}
