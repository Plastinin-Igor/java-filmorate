package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.yandex.practicum.filmorate.model.Film;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
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


    @Test
    void createTest() {
        Film thisFilm = new Film(1L, "Интерстеллар",
                "Коллектив исследователей и учёных отправляется сквозь червоточину в путешествие, " +
                        "чтобы найти планету с подходящими для человечества условиями.",
                LocalDate.of(2014, 10, 26), 169);

        controller.create(thisFilm);
        Assertions.assertEquals(film, thisFilm);
        Assertions.assertEquals(1, controller.findAll().size(), "Должен быть добавлен 1 фильм.");
    }

    @Test
    void createFailNameTest() throws JsonProcessingException {
        Film thisFilm = Film.builder()
                .description("Фильм, вдохновленный идеями физика Кипа Торна")
                .releaseDate(LocalDate.of(2014, 10, 26))
                .duration(169)
                .build();


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
            Assertions.assertEquals(f.getDescription(), thisFilm.getDescription(), "Описание не обновлилось");
        });
    }

    @Test
    void findAllTest() {
        controller.create(film);
        controller.create(film);
        controller.create(film);

        Assertions.assertEquals(3, controller.findAll().size(), "Должно быть добавлено 3 фильма.");
    }


}