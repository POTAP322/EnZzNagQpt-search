package ru.renue.search.model;

import java.util.List;

public class SearchResult {
    private final String search;
    private final List<String> result;
    private final long time;

    public SearchResult(String search, List<String> result, long time) {
        this.search = search;
        this.result = result;
        this.time = time;
    }

    public String getSearch() {
        return search;
    }

    public List<String> getResult() {
        return result;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "search='" + search + '\'' +
                ", result=" + result +
                ", time=" + time +
                '}';
    }
}
