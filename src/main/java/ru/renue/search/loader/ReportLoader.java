package ru.renue.search.loader;

import ru.renue.search.model.Report;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface ReportLoader {
    //метод для загрузки отчета
    List<Report> load(Path path) throws IOException;
}
