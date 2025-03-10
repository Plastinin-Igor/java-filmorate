package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    //TODO: добавление и удаление лайка, вывод 10 наиболее популярных фильмов по количеству лайков

    //TODO: сервисы зависят от интерфейсов классов-хранилищ, а не их реализаций
}
