package ru.renue.search.search;

import ru.renue.search.model.Report;

import java.util.List;

public interface SearchEngine {
    List<String> search(String query, List<Report> reports);
}
