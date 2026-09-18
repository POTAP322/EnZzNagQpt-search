package ru.renue.search.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StemmerTest {
    @Test
    void shouldStemDifferentFormsToSame() {
        Stemmer stemmer = new Stemmer();
        assertEquals(stemmer.stem("отчётности"), stemmer.stem("отчётность"));
        assertEquals(stemmer.stem("налоговым"), stemmer.stem("налоговые"));
        assertEquals(stemmer.stem("вкладам"), stemmer.stem("вклады"));
        assertEquals(stemmer.stem("банкротства"), stemmer.stem("банкротство"));
    }

    @Test
    void shouldNotStemShortWords() {
        Stemmer stemmer = new Stemmer();
        assertEquals("дом", stemmer.stem("дом"));
        assertEquals("кот", stemmer.stem("кот"));
        assertEquals("ндс", stemmer.stem("ндс"));
    }

}
