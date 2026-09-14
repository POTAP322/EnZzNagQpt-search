package ru.renue.search.loader;

import ru.renue.search.model.Report;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class CsvReportLoader implements ReportLoader{
    @Override
    public List<Report> load(Path path) throws IOException {
        return List.of();
    }
}
