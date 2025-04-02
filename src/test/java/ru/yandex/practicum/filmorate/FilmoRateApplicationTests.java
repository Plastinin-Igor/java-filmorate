package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
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

    private Long filmId1;
    private Long filmId2;
    private Long userId1;
    private Long userId2;

    @BeforeEach
    void addUsersForTests() {
        User user1 = User.builder()
                .email("plastinin@yandex.ru")
                .login("Plastinin")
                .name("Igor Plastinin")
                .birthday(LocalDate.of(1984, 8, 12))
                .build();

        User user2 = User.builder()
                .email("ivanov@gmail.com")
                .login("IvanovII")
                .name("Ivanov Ivan")
                .birthday(LocalDate.of(1989, 10, 1))
                .build();

        user1 = userStorage.addUser(user1);
        user2 = userStorage.addUser(user2);

        userId1 = user1.getId();
        userId2 = user2.getId();
    }

    @BeforeEach
    void addFilmForTests() {
        Film film1 = Film.builder()
                .name("Сталкер")
                .description("Мистическое путешествие через Зону к комнате, где исполняются желания. " +
                             "Философский шедевр Андрея Тарковского.")
                .releaseDate(LocalDate.of(1980, 5, 19))
                .duration(163)
                .rating(new Rating(2L, "PG"))
                .genres(new LinkedHashSet<Genre>(Set.of(new Genre(2L, "Драма"),
                        new Genre(4L, "Триллер"))))
                .build();

        Film film2 = Film.builder()
                .name("Интерстеллар")
                .description("Фильм, вдохновленный идеями физика Кипа Торна, исследует темы выживания человечества, " +
                             "родительской любви и парадоксов времени через призму релятивистской физики")
                .releaseDate(LocalDate.of(2014, 10, 26))
                .duration(169)
                .rating(new Rating(2L, "PG-13"))
                .genres(new LinkedHashSet<Genre>(Set.of(new Genre(2L, "Драма"),
                        new Genre(4L, "Триллер"))))
                .build();

        // Создать фильмы
        film1 = filmStorage.addFilm(film1);
        film2 = filmStorage.addFilm(film2);

        filmId1 = film1.getId();
        filmId2 = film2.getId();
    }

    // U S E R S

    //Добавление пользователя
    @Test
    public void testAddUser() {
        System.out.println("Добавление пользователя");
        User user = User.builder()
                .email("kip-thorne@emc2.io")
                .login("kip-thorne")
                .name("Kip Thorne")
                .birthday(LocalDate.of(1940, 6, 1))
                .build();

        //Добавление пользователя
        User userAdded = userStorage.addUser(user);
        //Получение добавленного пользователя из базы
        Optional<User> userNew = userStorage.getUserById(userAdded.getId());
        assertThat(userNew)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("login", "kip-thorne")
                )
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("name", "Kip Thorne")
                )
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("email", "kip-thorne@emc2.io")
                );
    }

    //Запрос пользователя по id
    @Test
    public void testGetUserById() {
        System.out.println("Запрос пользователя по id");
        //Запрашиваем из базы пользователя с id = 1
        Optional<User> userOptional = userStorage.getUserById(userId1);
        //Проверяем, что пользователь с id = 1 получен из базы
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("id", userId1)
                );
    }

    //Запрос всех пользователей
    @Test
    public void testGetUsers() {
        System.out.println("Запрос из базы всех пользователей");
        Collection<User> users = userStorage.getUsers();
        assertThat(users.size()).isGreaterThanOrEqualTo(2);
    }


    //Исправление пользователя
    @Test
    public void testUpdateUser() {
        System.out.println("Исправление пользователя");
        //Получаем из базы пользователя с id = 1
        Optional<User> user = userStorage.getUserById(userId1);
        //Устанавливаем новое значение в поле Email
        user.get().setEmail("new-mail@ya.ru");
        user.get().setLogin("new-login");
        user.get().setName("new-name");
        //Обновляем пользователя в базе данных
        userStorage.updateUser(user.get());
        //Получаем из базы данных обновленного пользователя с id = 1
        Optional<User> userUpdated = userStorage.getUserById(userId1);
        //Проверяем, что значение в полях изменилось
        assertThat(userUpdated)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("email", "new-mail@ya.ru")
                )
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("login", "new-login")
                )
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("name", "new-name")
                );
    }

    // Добавление/удаление/получение друзей
    @Test
    public void testAddFriends() {
        System.out.println("Добавление/удаление/получение друзей");

        //Проверим, что у пользователя нет друзей
        Collection<User> friends = userStorage.getFriends(userId1);
        assertThat(friends.size()).isGreaterThanOrEqualTo(0);

        //Добавим пользователя в друзья
        userStorage.addFriends(userId1, userId2);

        //Проверим, что друг появился в системе
        friends = userStorage.getFriends(userId1);
        assertThat(friends.size()).isGreaterThanOrEqualTo(1);

        //Удалим пользователя из друзей
        userStorage.deleteFriend(userId1, userId2);

        //Проверим, что друга нет в системе
        friends = userStorage.getFriends(userId1);
        assertThat(friends.size()).isGreaterThanOrEqualTo(0);
    }

    // F I L M S

    //Добавление фильма
    @Test
    public void testAddFilm() {
        System.out.println("Добавление фильма");
        Film film = Film.builder()
                .name("Солярис")
                .description("На космическую станцию, сотрудники которой давно и тщетно пытаются сладить с загадкой планеты Солярис, " +
                             "полностью покрытой Океаном, прибывает новый учёный, психолог Крис Кельвин.")
                .releaseDate(LocalDate.of(1972, 5, 5))
                .duration(169)
                .rating(new Rating(2L, "PG"))
                .genres(new LinkedHashSet<Genre>(Set.of(new Genre(2L, "Драма"), new Genre(4L, "Триллер"))))
                .build();

        //Добавляем фильм
        Film filmAdded = filmStorage.addFilm(film);
        //Получаем из базы добавленный фильм
        Optional<Film> filmOptional = filmStorage.getFilmById(filmAdded.getId());
        //Проверяем, что добавился корректно
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("name", film.getName())
                )
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("description", film.getDescription())
                )
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("rating", film.getRating())
                );
    }

    //Получить фильм по id
    @Test
    public void testFilmById() {
        System.out.println("Получить фильм по id");
        Optional<Film> filmOptional = filmStorage.getFilmById(filmId1);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("id", filmId1)
                );
    }

    //Получить список всех фильмов
    @Test
    public void testGetFilms() {
        System.out.println("Получить список всех фильмов");
        Collection<Film> films = filmStorage.getFilms();
        assertThat(films.size()).isGreaterThanOrEqualTo(2);
    }

    //Изменение фильма
    @Test
    public void testUpdateFilm() {
        System.out.println("Изменение фильма");
        //Получаем из базы фильм с id = 1
        Optional<Film> film = filmStorage.getFilmById(filmId1);
        //Устанавливаем новое значение в поле name
        film.get().setName("new name film");
        //Обновляем фильм в базе данных
        filmStorage.updateFilm(film.get());
        //Получаем из базы данных обновленный фильм с id = 1
        Optional<Film> filmUpdated = filmStorage.getFilmById(filmId1);
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
        System.out.println("Удаление-добавление лайков");
        //Получим количество лайков из базы по фильму с id = 1
        assertThat(filmStorage.getCountOfLikeByFilmId(1L)).isGreaterThanOrEqualTo(0);
        //Удалим лайк к фильму с id = 1
        filmStorage.deleteLike(1L, 1L);
        //Проверим, что лайка больше нет
        assertThat(filmStorage.getCountOfLikeByFilmId(1L)).isGreaterThanOrEqualTo(0);
    }

    //Получить топ фильмов
    @Test
    public void testGetTopPopularFilms() {
        System.out.println("Получить топ фильмов");

        Collection<Film> films = filmStorage.getTopPopularFilms(2);
        assertThat(films.size()).isGreaterThanOrEqualTo(2);

        films = filmStorage.getTopPopularFilms(1);
        assertThat(films.size()).isGreaterThanOrEqualTo(1);
    }

    //Получить список жанров
    @Test
    public void testGetGenres() {
        System.out.println("Получить список жанров");
        Collection<Genre> genres = genreStorage.getAllGenre();
        assertThat(genres.size()).isGreaterThanOrEqualTo(6);
    }

    //Получить жанр по id
    @Test
    public void testGetGenreById() {
        System.out.println("Получить жанр по id");
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
        System.out.println("Получить список рейтингов mpa");
        Collection<Genre> genres = genreStorage.getAllGenre();
        assertThat(genres.size()).isGreaterThanOrEqualTo(5);
    }

    //Получить рейтинг mpa по id
    @Test
    public void testGetRatingById() {
        System.out.println("Получить рейтинг mpa по id");
        Rating rating = ratingStorage.getRatingById(1L);
        assertThat(Optional.of(rating))
                .isPresent()
                .hasValueSatisfying(g ->
                        assertThat(g).hasFieldOrPropertyWithValue("name", "G")
                );
    }
}