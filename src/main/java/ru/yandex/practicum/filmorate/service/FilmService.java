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

    //TODO: PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.
    //TODO: DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.
    //TODO: GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков.
    // Если значение параметра count не задано, верните первые 10


}
