package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    //Добавление фильм
    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    //Изменение фильма
    public Film updateFilm(Film newFilm) {
        filmExists(newFilm.getId());
        return filmStorage.updateFilm(newFilm);
    }

    //Удаление фильма
    public void deleteFilm(Long filmId) {
        filmExists(filmId);
        filmStorage.deleteFilm(filmId);
    }

    //Список всех фильмов
    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    //Получение фильма по id
    public Optional<Film> getFilmById(Long filmId) {
        filmExists(filmId);
        return filmStorage.getFilmById(filmId);
    }

    //Пользователь ставит лайк фильму.
    public void addLike(Long filmId, Long userId) {

        if (!userStorage.isUserExists(userId)) {
            log.error("Пользователь с Id: {} не найден в системе.", userId);
            throw new NotFoundException("Пользователь с Id: " + userId + " не найден в системе.");
        }
        filmExists(filmId);
        filmStorage.addLike(filmId, userId);
    }

    // Пользователь удаляет лайк.
    public void deleteLike(Long filmId, Long userId) {
        likeExists(filmId, userId);
        filmStorage.deleteLike(filmId, userId);
    }

    //Возвращает список из первых count фильмов по количеству лайков.
    public Collection<Film> getTopPopularFilms(int count) {
        return filmStorage.getTopPopularFilms(count);
    }


    //Проверить наличие фильма в хранилище
    private void filmExists(Long id) {
        if (!filmStorage.isFilmExists(id)) {
            log.error("Фильм с Id: {} не найден в системе.", id);
            throw new NotFoundException("Фильм с Id: " + id + " не найден в системе.");
        }
    }

    //Проверить наличие лайка в хранилище
    private void likeExists(Long filmId, Long userId) {
        if (!filmStorage.isLikeExists(filmId, userId)) {
            log.error("Лайк пользователя {} для фильма {} не найден.", filmId, userId);
            throw new NotFoundException("Лайк пользователя " + filmId + " для фильма " + userId + " не найден.");
        }
    }

}
