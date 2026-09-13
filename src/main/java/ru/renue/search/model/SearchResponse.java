package ru.renue.search.model;

import java.util.List;

public class SearchResponse {
    private final long initTime;
    private final List<SearchResult> result;

    public SearchResponse(long initTime, List<SearchResult> result) {
        this.initTime = initTime;
        this.result = result;
    }

    public long getInitTime() {
        return initTime;
    }

    public List<SearchResult> getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "SearchResponse{" +
                "initTime=" + initTime +
                ", result=" + result +
                '}';
    }
}
