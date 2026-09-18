package ru.renue.search;

import ru.renue.search.cli.AppConfig;
import ru.renue.search.cli.ArgsParser;
import ru.renue.search.io.JsonWriter;
import ru.renue.search.loader.CsvReportLoader;
import ru.renue.search.loader.FileInputLoader;
import ru.renue.search.loader.InputLoader;
import ru.renue.search.loader.ReportLoader;
import ru.renue.search.model.Report;
import ru.renue.search.model.SearchResponse;
import ru.renue.search.model.SearchResult;
import ru.renue.search.search.KeywordSearchEngine;
import ru.renue.search.search.SearchEngine;

import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        try {
            //время старта
            long startNanos = System.nanoTime();

            //аргументы
            AppConfig config = new ArgsParser().parse(args);

            //загрузка
            ReportLoader reportLoader = new CsvReportLoader();
            InputLoader inputLoader = new FileInputLoader();
            List<Report> reports = reportLoader.load(config.getDataPath());
            List<String> queries = inputLoader.load(config.getInputPath());

            // initTime
            long initTime = (System.nanoTime() - startNanos) / 1_000_000;

            // поиск
            SearchEngine engine = new KeywordSearchEngine(reports);
            List<SearchResult> results = new ArrayList<>();
            for (String query : queries) {
                long searchStart = System.nanoTime();
                List<String> guids = engine.search(query);
                long time = (System.nanoTime() - searchStart) / 1_000_000;
                results.add(new SearchResult(query, guids, time));
            }

            //собрать и записать
            SearchResponse response = new SearchResponse(initTime, results);
            new JsonWriter().write(response, config.getOutputPath());

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}