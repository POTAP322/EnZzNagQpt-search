package ru.renue.search.search;

import ru.renue.search.model.Report;

import java.util.ArrayList;
import java.util.List;

public class KeywordSearchEngine implements SearchEngine {
    private static final int TOP_N = 5;

    private record ScoredReport(Report report, int score) {
    }

    @Override
    public List<String> search(String query, List<Report> reports) {
        //Защита от пустого входа
        if (query == null || query.isBlank() || reports == null || reports.isEmpty()) {
            return List.of();
        }

        //нормализуем запрос: нижний регистр, разбиение по пробелам
        String[] queryWords = query.toLowerCase().split("\\s+");

        //Считаем score для каждого отчёта и складываем в список пар
        List<ScoredReport> scored = new ArrayList<>();
        for (Report report : reports) {
            int score = countMatchingScore(queryWords, report);
            scored.add(new ScoredReport(report, score));
        }

        //Сортируем по убыванию score
        scored.sort((a, b) -> Integer.compare(b.score(), a.score()));

        //Берём топ-N с score > 0
        List<String> guids = new ArrayList<>();
        for (ScoredReport sr : scored) {
            if (sr.score() == 0) {
                break;
            }
            guids.add(sr.report().getGuid());
            if (guids.size() >= TOP_N) {
                break;
            }
        }

        return guids;
    }

    //TODO сейчас "налогам" != "налог", надо например по проценту совпадняи букв в слове считать. Еще слова из 1-2 символов можно не считать( они как шум баллы дают)
    public int countMatchingScore(String[] queryWords, Report report) {
        if (queryWords == null || queryWords.length == 0 || report == null) {
            return 0;
        }
        String[] descriptionWords = report.getDescription().toLowerCase().split("\\s+");
        String[] codeNameWords = report.getCodeName().toLowerCase().split("\\s+");

        int score = 0;

        for (String queryWord : queryWords) {
            if (queryWord.isEmpty()) {
                continue;
            }
            //сначала ищем в description это основной источник
            boolean foundInDescription = false;
            for (String descriptionWord : descriptionWords) {
                if (queryWord.equals(descriptionWord)) {
                    score += 1;
                    foundInDescription = true;
                    break;
                }
            }
            //если не нашли в description ,то проверяем codeName
            if (!foundInDescription) {
                for (String codeNameWord : codeNameWords) {
                    if (queryWord.equals(codeNameWord)) {
                        score += 2;
                        break;
                    }
                }
            }
        }

        return score;
    }


}
