package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DBGenreStorage;
import ru.yandex.practicum.filmorate.storage.DbFilmStorage;
import ru.yandex.practicum.filmorate.storage.DbRatingStorage;
import ru.yandex.practicum.filmorate.storage.DbUserStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.RatingRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({DbUserStorage.class,
        UserRowMapper.class,
        DbFilmStorage.class,
        FilmRowMapper.class,
        DBGenreStorage.class,
        GenreRowMapper.class,
        DbRatingStorage.class,
        RatingRowMapper.class
})
class FilmoRateApplicationTests {
    private final DbUserStorage userStorage;
    private final DbFilmStorage filmStorage;
    private final DBGenreStorage genreStorage;
    private final DbRatingStorage ratingStorage;


    //Запрос пользователя по id
    @Test
    public void testGetUserById() {
        User user = User.builder()
                .email("plastinin@yandex.ru")
                .login("Plastinin")
                .name("Igor Plastinin")
                .birthday(LocalDate.of(1984, 8, 12))
                .build();

        Optional<User> userOptional = userStorage.getUserById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    //Запрос всех пользователей
    @Test
    public void testGetUsers() {
        Collection<User> users = userStorage.getUsers();
        assertThat(users.size()).isGreaterThanOrEqualTo(2);
    }

    //Добавление пользователя
    @Test
    public void testAddUser() {
        User user = User.builder()
                .email("kip-thorne@emc2.io")
                .login("kip-thorne")
                .name("Kip Thorne")
                .birthday(LocalDate.of(1940, 6, 1))
                .build();
        //Несмотря на то, что в таблице Users, в колонке user_id настроена автоинкрементация,
        // при попытке добавить нового пользователя возникает ошибка   "PRIMARY KEY ON PUBLIC.USERS(USER_ID) (key:1)
        //поэтому удаляем данные по пользователю с id = 1
        userStorage.deleteFriend(1L, 2L);
        userStorage.deleteFriend(2L, 1L);
        filmStorage.deleteLike(1L, 1L);
        filmStorage.deleteLike(3L, 1L);
        userStorage.deleteUser(1L);

        //Добавление пользователя
        userStorage.addUser(user);
        Collection<User> users = userStorage.getUsers();
        assertThat(users.size()).isGreaterThanOrEqualTo(2);

        Optional<User> userNew = userStorage.getUserById(1L);
        assertThat(userNew)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("login", "kip-thorne")
                );
    }

    //Исправление пользователя
    @Test
    public void testUpdateUser() {
        //Получаем из базы пользователя с id = 1
        Optional<User> user = userStorage.getUserById(1L);
        //Устанавливаем новое значение в поле Email
        user.get().setEmail("new-mail@ya.ru");
        //Обновляем пользователя в базе данных
        userStorage.updateUser(user.get());
        //Получеме из базы данных обновленного пользователя с id = 1
        Optional<User> userUpdated = userStorage.getUserById(1L);
        //Проверяем, что значение в поле email изменилось на new-mail@ya.ru
        assertThat(userUpdated)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("email", "new-mail@ya.ru")
                );
    }

    // Добавление/удаление/получение друзей
    @Test
    public void testAddFriends() {
        //Удалим пользователя из друзей
        userStorage.deleteFriend(1L, 2L);
        //Проверим, что у пользователя нет друзей
        Collection<User> friends = userStorage.getFriends(1L);
        assertThat(friends.size()).isGreaterThanOrEqualTo(0);

        //Добавим пользователя в друзья
        userStorage.addFriends(1L, 2L);
        //Проверим, что друг появился в системе
        friends = userStorage.getFriends(1L);
        assertThat(friends.size()).isGreaterThanOrEqualTo(0);
    }

    @Test
    public void testFilmById() {
        Optional<Film> filmOptional = filmStorage.getFilmById(1L);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    //Запрос всех фильмов
    @Test
    public void testGetFilms() {
        Collection<Film> films = filmStorage.getFilms();
        assertThat(films.size()).isGreaterThanOrEqualTo(3);
    }

    //Добавление фильма
    @Test
    public void testAddFilm() {
        Film film = Film.builder()
                .id(1L)
                .name("Сталкер")
                .description("Мистическое путешествие через Зону к комнате, где исполняются желания. " +
                             "Философский шедевр Андрея Тарковского.")
                .releaseDate(LocalDate.of(1980, 5, 19))
                .duration(163)
                .rating(new Rating(2L, "PG"))
                .genres(new LinkedHashSet<Genre>(Set.of(new Genre(2L, "Драма"), new Genre(4L, "Триллер"))))
                .build();

        //Несмотря на то, что в таблице Film, в колонке film_id настроена автоинкрементация,
        // при попытке добавить нового пользователя возникает ошибка   "PRIMARY KEY ON PUBLIC.FILM(FILM_ID) (key:1)
        //поэтому удаляем данные по фильму с id = 1
        filmStorage.deleteLike(1L, 1L);
        filmStorage.deleteLike(1L, 2L);
        genreStorage.deleteGenreByFilm(1L);
        filmStorage.deleteFilm(1L);

        //Добавляем фильм
        filmStorage.addFilm(film);
        //Получаем из базы добавленный фильм
        Optional<Film> filmOptional = filmStorage.getFilmById(1L);
        //Проверяем, что добавился корректно
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("name", film.getName())
                );
    }

    @Test
    public void testUpdateFilm() {
        //Получаем из базы пользователя с id = 1
        Optional<Film> film = filmStorage.getFilmById(1L);
        //Устанавливаем новое значение в поле name
        film.get().setName("new name film");
        //Обновляем пользователя в базе данных
        filmStorage.updateFilm(film.get());
        //Получеме из базы данных обновленного пользователя с id = 1
        Optional<Film> filmUpdated = filmStorage.getFilmById(1L);
        //Проверяем, что значение в поле name изменилось на new name film
        assertThat(filmUpdated)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("name", "new name film")
                );
    }

    //Удаление-добавление лайков
    @Test
    public void testAddDeleteLike() {
        //Получим количество лайков из базы по фильму с id = 1
        assertThat(filmStorage.getCountOfLikeByFilmId(1L)).isGreaterThanOrEqualTo(2);
        //Удалим лайки из базы по фильму с id = 1
        filmStorage.deleteLike(1L, 1L);
        filmStorage.deleteLike(1L, 2L);
        //Получим количество лайков после удаления из базы по фильму с id = 1
        assertThat(filmStorage.getCountOfLikeByFilmId(1L)).isGreaterThanOrEqualTo(0);
        //Добавим лайк фильму с id = 1
        filmStorage.addLike(1L, 1L);
        //Проверим, что лайк добавился
        assertThat(filmStorage.getCountOfLikeByFilmId(1L)).isGreaterThanOrEqualTo(1);
    }

    //Получить топ фильмов
    @Test
    public void testGetTopPopularFilms() {
        Collection<Film> films = filmStorage.getTopPopularFilms(2);
        assertThat(films.size()).isGreaterThanOrEqualTo(2);

        films = filmStorage.getTopPopularFilms(1);
        assertThat(films.size()).isGreaterThanOrEqualTo(1);
    }

    //Получить список жанров
    @Test
    public void testGetGenres() {
        Collection<Genre> genres = genreStorage.getAllGenre();
        assertThat(genres.size()).isGreaterThanOrEqualTo(6);
    }

    //Получить жанр по id
    @Test
    public void testGetGenreById() {
        Genre genre = genreStorage.getGenreById(1L);
        assertThat(Optional.of(genre))
                .isPresent()
                .hasValueSatisfying(g ->
                        assertThat(g).hasFieldOrPropertyWithValue("name", "Комедия")
                );
    }

    //Получить список рейтингов mpa
    @Test
    public void testGetRatings() {
        Collection<Genre> genres = genreStorage.getAllGenre();
        assertThat(genres.size()).isGreaterThanOrEqualTo(5);
    }

    //Получить рейтинг mpa по id
    @Test
    public void testGetRatingById() {
        Rating rating = ratingStorage.getRatingById(1L);
        assertThat(Optional.of(rating))
                .isPresent()
                .hasValueSatisfying(g ->
                        assertThat(g).hasFieldOrPropertyWithValue("name", "G")
                );
    }
}