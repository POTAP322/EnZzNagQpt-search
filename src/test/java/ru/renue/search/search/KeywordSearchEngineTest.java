package ru.renue.search.search;

import org.junit.jupiter.api.Test;
import ru.renue.search.model.Report;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KeywordSearchEngineTest {

    private final KeywordSearchEngine engine = new KeywordSearchEngine();

    @Test
    void shouldFindReportByExactWords() {
        Report report = new Report("guid-1", "code1", "Список клиентов");
        List<String> result = engine.search("список клиентов", List.of(report));

        assertEquals(List.of("guid-1"), result);
    }

    @Test
    void shouldReturnEmptyListWhenNothingMatches() {
        Report report = new Report("guid-1", "code1", "Список клиентов");
        List<String> result = engine.search("налоги", List.of(report));

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldSortByRelevance() {
        Report weak = new Report("weak", "c1", "Отчет по клиентам");
        Report strong = new Report("strong", "c2", "Отчет по налоговым вычетам для клиентов");

        List<String> result = engine.search("отчет налоговым вычетам клиентов", List.of(weak, strong));

        assertEquals("strong", result.get(0));
    }

    @Test
    void shouldReturnEmptyListForBlankQuery() {
        Report report = new Report("guid-1", "code1", "Список клиентов");
        List<String> result = engine.search("   ", List.of(report));

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyListForNullQuery() {
        Report report = new Report("guid-1", "code1", "Список клиентов");
        List<String> result = engine.search(null, List.of(report));

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyListForEmptyReports() {
        List<String> result = engine.search("что-то", List.of());

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldBeCaseInsensitive() {
        Report report = new Report("guid-1", "code1", "Список КЛИЕНТОВ");
        List<String> result = engine.search("список клиентов", List.of(report));

        assertEquals(List.of("guid-1"), result);
    }
}