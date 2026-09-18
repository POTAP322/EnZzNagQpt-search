package ru.renue.search.search;

import ru.renue.search.model.Report;
import ru.renue.search.text.Stemmer;
import ru.renue.search.text.StopWords;

import java.util.*;
import java.util.regex.Pattern;

public class KeywordSearchEngine implements SearchEngine {
    private static final int TOP_N = 2;
    private static final Pattern NON_WORD = Pattern.compile("[^а-яa-z0-9\\s]");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private final StopWords stopWords = new StopWords();

    private record ScoredReport(Report report, double score) {
    }

    private record PreparedReport(
            Report report,
            String[] descriptionWords,
            String[] codeNameWords
    ) {
    }

    private final Stemmer stemmer = new Stemmer();

    private final List<PreparedReport> prepared;
    private final Map<String, Double> idf;

    //конструктор
    public KeywordSearchEngine(List<Report> reports) {
        if (reports == null || reports.isEmpty()) {
            this.prepared = List.of();
            this.idf = Map.of();
            return;
        }
        this.prepared = prepare(reports);
        this.idf = computeIdf(prepared);
    }

    @Override
    public List<String> search(String query) {

        //Защита от пустого входа
        if (query == null || query.isBlank()) {
            return List.of();
        }
        if (prepared.isEmpty()) {
            return List.of();
        }

        // нормализация
        String normalizedQuery = normalize(query);
        String[] rawWords = normalizedQuery.split(" ");

        // фильтр стоп-слов до стемминга
        String[] queryWords = Arrays.stream(rawWords)
                .filter(word -> !word.isEmpty())
                .filter(word -> !stopWords.contains(word))
                .toArray(String[]::new);

        // стемминг после фильтра
        queryWords = stemAll(queryWords);


        // Если после фильтрации не осталось слов, то возвращаем пустой список
        if (queryWords.length == 0) {
            return List.of();
        }

        //Считаем score для каждого отчёта и складываем в список пар
        List<ScoredReport> scored = new ArrayList<>();
        for (PreparedReport pr : prepared) {
            double score = countMatchingScore(queryWords, pr);
            scored.add(new ScoredReport(pr.report(), score));
        }

        //Сортируем по убыванию score
        scored.sort((a, b) -> Double.compare(b.score(), a.score()));

        //вычисляем порог для включения в список
        double maxScore = scored.get(0).score();
        if (maxScore == 0) {
            return List.of();
        }
        double threshold = maxScore * 0.8;  // 50% от максимума

        //Берём топ N с score > 0
        List<String> guids = new ArrayList<>();
        for (ScoredReport sr : scored) {
            if (sr.score() < threshold) {
                break;
            }
            guids.add(sr.report().getGuid());
            if (guids.size() >= TOP_N) {
                break;
            }
        }

        return guids;
    }

    private double countMatchingScore(String[] queryWords, PreparedReport prepared) {
        if (queryWords == null || queryWords.length == 0 || prepared == null) {
            return 0;
        }

        String[] descriptionWords = prepared.descriptionWords();
        String[] codeNameWords = prepared.codeNameWords();

        double score = 0;

        for (String queryWord : queryWords) {
            if (queryWord.isEmpty()) {
                continue;
            }

            double wordIdf = idf.getOrDefault(queryWord, 0.0);

            //сначала ищем в description это основной источник
            boolean foundInDescription = false;
            for (String descriptionWord : descriptionWords) {
                if (queryWord.equals(descriptionWord)) {
                    score += wordIdf;
                    foundInDescription = true;
                    break;
                }
            }
            //если не нашли в description ,то проверяем codeName
            if (!foundInDescription) {
                for (String codeNameWord : codeNameWords) {
                    if (queryWord.equals(codeNameWord)) {
                        score += wordIdf * 2;
                        break;
                    }
                }
            }
        }

        return score;
    }

    private Map<String, Double> computeIdf(List<PreparedReport> prepared) {
        Map<String, Integer> documentFrequency = new HashMap<>();

        for (PreparedReport pr : prepared) {
            Set<String> uniqueWords = new HashSet<>(Arrays.asList(pr.descriptionWords()));
            for (String word : uniqueWords) {
                documentFrequency.merge(word, 1, Integer::sum);
            }
        }

        Map<String, Double> idf = new HashMap<>();
        int totalDocuments = prepared.size();
        for (Map.Entry<String, Integer> entry : documentFrequency.entrySet()) {
            // IDF = log(N / df), где N - общее количество документов (+1 для сглаживания, дает минимальный idf, даже если в каждом документе есть слово ( для прохождения тестов))
            double idfValue = Math.log(1 + (double) totalDocuments / entry.getValue());
            idf.put(entry.getKey(), idfValue);
        }
        return idf;
    }

    private String normalize(String text) {
        String cleaned = NON_WORD.matcher(text.toLowerCase().replace('ё', 'е')).replaceAll("");
        return WHITESPACE.matcher(cleaned).replaceAll(" ").trim();
    }

    private List<PreparedReport> prepare(List<Report> reports) {
        List<PreparedReport> prepared = new ArrayList<>(reports.size());
        for (Report report : reports) {
            String[] descriptionWords = stemAll(normalize(report.getDescription()).split(" "));
            String[] codeNameWords = normalize(report.getCodeName()).split(" ");
            prepared.add(new PreparedReport(report, descriptionWords, codeNameWords));
        }
        return prepared;
    }

    private String[] stemAll(String[] words) {
        String[] result = new String[words.length];
        for (int i = 0; i < words.length; i++) {
            result[i] = stemmer.stem(words[i]);
        }
        return result;
    }


}
