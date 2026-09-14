package ru.renue.search.loader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface InputLoader {
    //метод для загрузки запроса пользователя
    List<String> load(Path path) throws IOException;
}
