package ru.renue.search.search;

import org.junit.jupiter.api.Test;
import ru.renue.search.model.Report;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KeywordSearchEngineTest {

    @Test
    void shouldFindReportByExactWords() {
        Report report = new Report("guid-1", "code1", "Список клиентов");
        KeywordSearchEngine engine = new KeywordSearchEngine(List.of(report));
        List<String> result = engine.search("список клиентов");

        assertEquals(List.of("guid-1"), result);
    }

    @Test
    void shouldReturnEmptyListWhenNothingMatches() {
        Report report = new Report("guid-1", "code1", "Список клиентов");
        KeywordSearchEngine engine = new KeywordSearchEngine(List.of(report));
        List<String> result = engine.search("налоги");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldSortByRelevance() {
        Report weak = new Report("weak", "c1", "Отчет по клиентам");
        Report strong = new Report("strong", "c2", "Отчет по налоговым вычетам для клиентов");
        KeywordSearchEngine engine = new KeywordSearchEngine(List.of(weak, strong));

        List<String> result = engine.search("отчет налоговым вычетам клиентов");

        assertEquals("strong", result.get(0));
    }

    @Test
    void shouldReturnEmptyListForBlankQuery() {
        Report report = new Report("guid-1", "code1", "Список клиентов");
        KeywordSearchEngine engine = new KeywordSearchEngine(List.of(report));
        List<String> result = engine.search("   ");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyListForNullQuery() {
        Report report = new Report("guid-1", "code1", "Список клиентов");
        KeywordSearchEngine engine = new KeywordSearchEngine(List.of(report));
        List<String> result = engine.search(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyListForEmptyReports() {
        KeywordSearchEngine engine = new KeywordSearchEngine(List.of());
        List<String> result = engine.search("что-то");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldBeCaseInsensitive() {
        Report report = new Report("guid-1", "code1", "Список КЛИЕНТОВ");
        KeywordSearchEngine engine = new KeywordSearchEngine(List.of(report));
        List<String> result = engine.search("список клиентов");

        assertEquals(List.of("guid-1"), result);
    }
}