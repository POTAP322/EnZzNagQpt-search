package ru.renue.search.cli;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ArgsParserTest {

    @Test
    void shouldParseAllArgumentsInOrder() {
        String[] args = {
                "--data", "data.csv",
                "--input-file", "input.txt",
                "--output-file", "out.json"
        };

        AppConfig config = new ArgsParser().parse(args);

        assertEquals(Path.of("data.csv"), config.getDataPath());
        assertEquals(Path.of("input.txt"), config.getInputPath());
        assertEquals(Path.of("out.json"), config.getOutputPath());
    }

    @Test
    void shouldParseArgumentsInDifferentOrder() {
        String[] args = {
                "--input-file", "input.txt",
                "--output-file", "out.json",
                "--data", "data.csv"
        };

        AppConfig config = new ArgsParser().parse(args);

        assertEquals(Path.of("data.csv"), config.getDataPath());
        assertEquals(Path.of("input.txt"), config.getInputPath());
        assertEquals(Path.of("out.json"), config.getOutputPath());
    }

    @Test
    void shouldThrowWhenDataMissing() {
        String[] args = {
                "--input-file", "input.txt",
                "--output-file", "out.json"
        };

        assertThrows(IllegalArgumentException.class,
                () -> new ArgsParser().parse(args));
    }

    @Test
    void shouldThrowWhenInputMissing() {
        String[] args = {
                "--data", "data.csv",
                "--output-file", "out.json"
        };

        assertThrows(IllegalArgumentException.class,
                () -> new ArgsParser().parse(args));
    }

    @Test
    void shouldThrowWhenOutputMissing() {
        String[] args = {
                "--data", "data.csv",
                "--input-file", "input.txt"
        };

        assertThrows(IllegalArgumentException.class,
                () -> new ArgsParser().parse(args));
    }

    @Test
    void shouldThrowWhenValueMissing() {
        String[] args = {"--data"};

        assertThrows(IllegalArgumentException.class,
                () -> new ArgsParser().parse(args));
    }

    @Test
    void shouldThrowWhenUnknownArgument() {
        String[] args = {
                "--foo", "bar",
                "--data", "data.csv",
                "--input-file", "input.txt",
                "--output-file", "out.json"
        };

        assertThrows(IllegalArgumentException.class,
                () -> new ArgsParser().parse(args));
    }

    @Test
    void shouldThrowWhenArgsEmpty() {
        String[] args = {};

        assertThrows(IllegalArgumentException.class,
                () -> new ArgsParser().parse(args));
    }
}