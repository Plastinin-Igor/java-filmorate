package ru.yandex.practicum.filmorate.controller;

import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;


@SpringBootTest
@Validated
class FilmControllerTest {

    private final Film film = Film.builder()
            .id(1L)
            .name("Интерстеллар")
            .description("Фильм, вдохновленный идеями физика Кипа Торна, исследует темы выживания человечества," +
                         " родительской любви и парадоксов времени через призму релятивистской физики")
            .releaseDate(LocalDate.of(2014, 10, 26))
            .duration(169)
            .rating(new Rating(3L, "PG-13"))
            .genres(new LinkedHashSet<Genre>(Set.of(new Genre(2L, "Драма"),
                    new Genre(4L, "Триллер"))))
            .build();

}