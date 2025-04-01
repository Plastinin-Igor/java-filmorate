package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.DBGenreStorage;
import ru.yandex.practicum.filmorate.storage.DbRatingStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final DBGenreStorage genreStorage;
    private final DbRatingStorage ratingStorage;

    //Добавление фильм
    public FilmDto addFilm(NewFilmRequest newFilmRequest) {
        Film film = FilmMapper.mapToFilm(newFilmRequest);
        film = filmStorage.addFilm(film);
        return FilmMapper.mapToFilmDto(film);
    }

    //Изменение фильма
    public FilmDto updateFilm(UpdateFilmRequest filmRequest) {
        filmExists(filmRequest.getId());
        Film film = filmStorage.getFilmById(filmRequest.getId()).get();
        film = FilmMapper.updateFilmFields(film, filmRequest);
        film = filmStorage.updateFilm(film);
        return FilmMapper.mapToFilmDto(film);
    }

    //Удаление фильма
    public void deleteFilm(Long filmId) {
        filmExists(filmId);
        filmStorage.deleteFilm(filmId);
    }

    //Список всех фильмов
    public Collection<FilmDto> getFilms() {
        return filmStorage.getFilms()
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
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
        //likeExists(filmId, userId);
        filmStorage.deleteLike(filmId, userId);
    }

    //Возвращает список из первых count фильмов по количеству лайков.
    public Collection<Film> getTopPopularFilms(int count) {
        return filmStorage.getTopPopularFilms(count);
    }

    // Список жанров
    public Collection<Genre> getAllGenres() {
        return genreStorage.getAllGenre();
    }

    // Жанр по id
    public Genre getGenreById(long genreId) {
        return genreStorage.getGenreById(genreId);
    }

    // Список рейтингов mpa
    public Collection<Rating> getAllRatings() {
        return ratingStorage.getRatings();
    }

    // Рейтинг mpa по id
    public Rating getRatingById(long ratingId) {
        return ratingStorage.getRatingById(ratingId);
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
