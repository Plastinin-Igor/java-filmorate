package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.mapper.RatingMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final DBGenreStorage genreStorage;
    private final DbRatingStorage ratingStorage;
    private final DbFilmGenreStorage filmGenreStorage;

    //Добавление фильма
    public FilmDto addFilm(NewFilmRequest newFilmRequest) {
        Film film = FilmMapper.mapToFilm(newFilmRequest);
        // Считаем из базы рейтинг mpa по его id
        if (film.getRating() != null) {
            film.setRating(ratingStorage.getRatingById(film.getRating().getId()));
        }

        film = filmStorage.addFilm(film);

        //Добавление жанра(ов)
        if (!film.getGenres().isEmpty()) {
            long filmId = film.getId();
            film.getGenres()
                    .stream()
                    .forEach(g -> genreStorage.addFilmGenre(filmId, g.getId()));
        }

        return FilmMapper.mapToFilmDto(film);
    }

    //Изменение фильма
    public FilmDto updateFilm(UpdateFilmRequest filmRequest) {
        filmExists(filmRequest.getId());
        Film film = filmStorage.getFilmById(filmRequest.getId());
        // Считаем из базы рейтинг mpa по его id
        if (film.getRating() != null) {
            film.setRating(ratingStorage.getRatingById(filmRequest.getMpa().getId()));
        }
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
        //Получаем все фильмы
        List<Film> films = new ArrayList<>(filmStorage.getFilms());

        //Получаем все связи фильм -> жанры
        List<FilmGenre> filmsGenres = filmGenreStorage.getAllFilmGenre();

        // Создаем и заполняем карту: filmId -> Set<Genre>
        Map<Long, LinkedHashSet<Genre>> filmsGenresMap = new HashMap<>();
        for (FilmGenre filmGenre : filmsGenres) {
            Long filmId = filmGenre.getFilmId();
            Genre genre = filmGenre.getGenre();
            filmsGenresMap.computeIfAbsent(filmId, key -> new LinkedHashSet<>()).add(genre);
        }

        //Добавляем жанры к фильмам
        for (Film film : films) {
            LinkedHashSet<Genre> genres = filmsGenresMap.get(film.getId());
            if (genres != null) {
                film.setGenres(genres);
            }
        }

        // Возвращаем результат
        return films
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    //Получение фильма по id
    public FilmDto getFilmById(Long filmId) {
        Film film = filmStorage.getFilmById(filmId);
        //Добавляем к фильму жанры
        LinkedHashSet<Genre> genres = new LinkedHashSet<>(genreStorage.getFilmGenres(filmId));
        if (!genres.isEmpty()) {
            film.setGenres(genres);
        }
        return FilmMapper.mapToFilmDto(film);
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
        filmStorage.deleteLike(filmId, userId);
    }

    //Возвращает список из первых count фильмов по количеству лайков.
    public Collection<FilmDto> getTopPopularFilms(int count) {
        //Получаем топ n фильмов
        List<Film> films = new ArrayList<>(filmStorage.getTopPopularFilms(count));

        //Получаем все связи фильм -> жанры
        List<FilmGenre> filmsGenres = filmGenreStorage.getAllFilmGenre();

        // Создаем и заполняем карту: filmId -> Set<Genre>
        Map<Long, LinkedHashSet<Genre>> filmsGenresMap = new HashMap<>();
        for (FilmGenre filmGenre : filmsGenres) {
            Long filmId = filmGenre.getFilmId();
            Genre genre = filmGenre.getGenre();
            filmsGenresMap.computeIfAbsent(filmId, key -> new LinkedHashSet<>()).add(genre);
        }

        //Добавляем жанры к фильмам
        for (Film film : films) {
            LinkedHashSet<Genre> genres = filmsGenresMap.get(film.getId());
            if (genres != null) {
                film.setGenres(genres);
            }
        }

        // Возвращаем результат
        return films
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    // Список жанров
    public Collection<GenreDto> getAllGenres() {
        return genreStorage.getAllGenre()
                .stream()
                .map(GenreMapper::mapToGenreDto)
                .collect(Collectors.toList());
    }

    // Жанр по id
    public GenreDto getGenreById(long genreId) {
        return GenreMapper.mapToGenreDto(genreStorage.getGenreById(genreId));
    }

    // Список рейтингов mpa
    public Collection<RatingDto> getAllRatings() {
        return ratingStorage.getRatings()
                .stream()
                .map(RatingMapper::mapToRatingDto)
                .collect(Collectors.toList());
    }

    // Рейтинг mpa по id
    public RatingDto getRatingById(long ratingId) {
        return RatingMapper.mapToRatingDto(ratingStorage.getRatingById(ratingId));
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
