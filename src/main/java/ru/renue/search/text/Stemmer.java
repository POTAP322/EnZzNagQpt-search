package ru.renue.search.text;

public class Stemmer {
    private static final String[] ENDINGS = {
            "ами", "ями", "ов", "ев", "ый", "ая", "ое", "ые",
            "ам", "ям", "ах", "ях", "ой", "ей", "ую", "юю",
            "их", "ых", "ому", "ему", "ого", "его",
            "ать", "ять", "ить", "еть", "уть",
            "ешь", "ишь",
            "а", "я", "о", "е", "у", "ю", "ы", "и", "й", "ь",
            "ыми", "ими", "ым", "ом", "ем"
    };

    private static final int MIN_STEM_LENGTH = 4;

    public String stem(String word) {
        if (word == null || word.length() < MIN_STEM_LENGTH) {
            return word;
        }
        for (String ending : ENDINGS) {
            if (word.endsWith(ending) && word.length() - ending.length() >= MIN_STEM_LENGTH) {
                return word.substring(0, word.length() - ending.length());
            }
        }
        return word;
    }

}
