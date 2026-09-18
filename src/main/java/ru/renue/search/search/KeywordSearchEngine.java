package ru.renue.search.search;

import ru.renue.search.model.Report;

import java.util.*;
import java.util.regex.Pattern;

public class KeywordSearchEngine implements SearchEngine {
    private static final int TOP_N = 5;
    private static final Pattern NON_WORD = Pattern.compile("[^а-яa-z0-9\\s]");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private static final Set<String> STOP_WORDS = Set.of(
            "и", "в", "во", "не", "что", "он", "на", "я", "с", "со", "как", "а",
            "то", "все", "она", "так", "его", "но", "да", "ты", "к", "у", "же",
            "вы", "за", "бы", "по", "только", "ее", "мне", "было", "вот", "от",
            "меня", "еще", "нет", "о", "из", "ему", "теперь", "когда", "даже",
            "ну", "вдруг", "ли", "если", "уже", "или", "ни", "быть", "был",
            "него", "до", "вас", "нибудь", "опять", "уж", "вам", "ведь", "там",
            "потом", "себя", "ничего", "ей", "может", "они", "тут", "где",
            "есть", "надо", "ней", "для", "мы", "тебя", "их", "чем", "была",
            "сам", "чтоб", "без", "будто", "чего", "раз", "тоже", "себе",
            "под", "будет", "ж", "тогда", "кто", "этот", "того",
            "потому", "этого", "какой", "совсем", "ним", "здесь", "этом",
            "один", "почти", "мой", "тем", "чтобы", "нее", "сейчас", "были",
            "куда", "зачем", "всех", "никогда", "можно", "при", "наконец",
            "два", "об", "другой", "хоть", "после", "над", "больше", "тот",
            "через", "эти", "нас", "про", "всего", "них", "какая", "много",
            "разве", "три", "эту", "моя", "впрочем", "хорошо", "свою",
            "этой", "перед", "иногда", "лучше", "чуть", "том", "нельзя",
            "такой", "им", "более", "всегда", "конечно", "всю", "между"
    );

    private record ScoredReport(Report report, double score) { }

    private record PreparedReport(
            Report report,
            String[] descriptionWords,
            String[] codeNameWords
    ) {}

    @Override
    public List<String> search(String query, List<Report> reports) {
        //Защита от пустого входа
        if (query == null || query.isBlank() || reports == null || reports.isEmpty()) {
            return List.of();
        }

        //нормализуем запрос: нижний регистр, замена ё на е, удаление пунктуации, разбиение по пробелам
        List<PreparedReport> prepared = prepare(reports);

        String normalizedQuery = normalize(query);
        String[] queryWords = normalizedQuery.split(" ");

        // фильтруем стоп слова и пустые строки
        String[] filtredQueryWords = Arrays.stream(queryWords)
                .filter(word -> !word.isEmpty())
                .filter(word -> !STOP_WORDS.contains(word))
                .toArray(String[]::new);

        // Если после фильтрации не осталось слов, то возвращаем пустой список
        if (filtredQueryWords.length == 0) {
            return List.of();
        }

        Map<String, Double> idf = computeIdf(prepared);

        //Считаем score для каждого отчёта и складываем в список пар
        List<ScoredReport> scored = new ArrayList<>();
        for (PreparedReport pr : prepared) {
            double score = countMatchingScore(filtredQueryWords, pr, idf);
            scored.add(new ScoredReport(pr.report(), score));
        }

        //Сортируем по убыванию score
        scored.sort((a, b) -> Double.compare(b.score(), a.score()));

        //вычисляем порог для включения в список
        double maxScore = scored.get(0).score();
        if (maxScore == 0) {
            return List.of();
        }
        double threshold = maxScore * 0.6;  // 50% от максимума

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

    //TODO сейчас "налогам" != "налог", надо например по проценту совпадняи букв в слове считать. Еще слова из 1-2 символов можно не считать( они как шум баллы дают)
    public double countMatchingScore(String[] queryWords, PreparedReport prepared, Map<String,Double> idf) {
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
            String[] descriptionWords = normalize(report.getDescription()).split(" ");
            String[] codeNameWords = normalize(report.getCodeName()).split(" ");
            prepared.add(new PreparedReport(report, descriptionWords, codeNameWords));
        }
        return prepared;
    }


}
