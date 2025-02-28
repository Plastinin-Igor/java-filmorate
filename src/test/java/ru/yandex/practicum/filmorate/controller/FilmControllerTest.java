package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.*;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.Film;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Validated
class FilmControllerTest {
    private final Film film = Film.builder()
            .id(1L)
            .name("Интерстеллар")
            .description("Коллектив исследователей и учёных отправляется сквозь червоточину " +
                    "в путешествие, чтобы найти планету с подходящими для человечества условиями.")
            .releaseDate(LocalDate.of(2014, 10, 26))
            .duration(169)
            .build();

    private final FilmController controller = new FilmController();


    private void validate(Film film) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }


    @Test
    void createTest() {
        Film thisFilm = new Film(1L, "Интерстеллар",
                "Коллектив исследователей и учёных отправляется сквозь червоточину в путешествие, " +
                        "чтобы найти планету с подходящими для человечества условиями.",
                LocalDate.of(2014, 10, 26), 169);

        controller.create(thisFilm);
        assertEquals(film, thisFilm);
        assertEquals(1, controller.findAll().size(), "Должен быть добавлен 1 фильм.");
    }

    @Test
    void createFailNameTest() {
        // Проверка, что имя фильма не может быть пустым
        Film film = Film.builder()
                .name("")
                .description("Фильм, вдохновленный идеями физика Кипа Торна, исследует темы выживания человечества, " +
                        "родительской любви и парадоксов времени через призму релятивистской физики")
                .releaseDate(LocalDate.of(2014, 10, 26))
                .duration(169)
                .build();

        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class,
                () -> validate(film));

        assertEquals("Название фильма не может быть пустым",
                exception.getConstraintViolations().iterator().next().getMessage());
    }


    @Test
    void createFailDescriptionTest() {
        // Проверка, что описание не может превышать 200 символов
        Film film = Film.builder()
                .name("Интерстеллар")
                .description("Когда засуха, пыльные бури и вымирание растений приводят человечество к " +
                        "продовольственному кризису, коллектив исследователей и учёных отправляется сквозь червоточину " +
                        "(которая предположительно соединяет области пространства-времени через большое расстояние) " +
                        "в путешествие, чтобы превзойти прежние ограничения для космических путешествий человека и найти " +
                        "планету с подходящими для человечества условиями.")
                .releaseDate(LocalDate.of(2014, 10, 26))
                .duration(169)
                .build();

        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class,
                () -> validate(film));

        assertEquals("Описание не должно превышать 200 символов",
                exception.getConstraintViolations().iterator().next().getMessage());
    }

    @Test
    void createFailReleaseDateTest() {
        // Проверка, что дата релиза не может быть раньше 28 декабря 1895 года
        Film film = Film.builder()
                .name("Интерстеллар")
                .description("Фильм, вдохновленный идеями физика Кипа Торна, исследует темы выживания человечества, " +
                        "родительской любви и парадоксов времени через призму релятивистской физики")
                .releaseDate(LocalDate.of(1814, 10, 26))
                .duration(169)
                .build();

        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class,
                () -> validate(film));

        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года",
                exception.getConstraintViolations().iterator().next().getMessage());
    }

    @Test
    void createFailDurationTest() {
        // Проверка, что продолжительность фильма не может быть меньше нуля
        Film film = Film.builder()
                .name("Интерстеллар")
                .description("Фильм, вдохновленный идеями физика Кипа Торна, исследует темы выживания человечества, " +
                        "родительской любви и парадоксов времени через призму релятивистской физики")
                .releaseDate(LocalDate.of(2014, 10, 26))
                .duration(-169)
                .build();

        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class,
                () -> validate(film));

        assertEquals("Продолжительность фильма не может быть меньше нуля",
                exception.getConstraintViolations().iterator().next().getMessage());
    }

    @Test
    void updateTest() {
        Film thisFilm = new Film(1L, "Интерстеллар",
                "Фильм, вдохновленный идеями физика Кипа Торна, исследует темы выживания человечества, " +
                        "родительской любви и парадоксов времени через призму релятивистской физики",
                LocalDate.of(2014, 10, 26), 169);

        controller.create(film);
        controller.update(thisFilm);

        controller.findAll().forEach(f -> {
            assertEquals(f.getDescription(), thisFilm.getDescription(), "Описание не обновлилось");
        });
    }

    @Test
    void findAllTest() {
        controller.create(film);
        controller.create(film);
        controller.create(film);

        assertEquals(3, controller.findAll().size(), "Должно быть добавлено 3 фильма.");
    }

}