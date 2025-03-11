package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.*;
import org.springframework.validation.annotation.Validated;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Validated
class UserControllerTest {
    private final User user = User.builder()
            .id(1L)
            .email("plastinin@yandex.ru")
            .login("Plastinin")
            .name("Igor Plastinin")
            .birthday(LocalDate.of(1984, 8, 12))
            .build();

    private final UserController controller = new UserController();

    private void validate(User user) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }


    @Test
    void userCreateTest() {
        User thisUser = new User(1L, "plastinin@yandex.ru", "Plastinin", "Igor Plastinin",
                LocalDate.of(1984, 8, 12));

        controller.create(thisUser);
        assertEquals(thisUser, user);
        assertEquals(1, controller.findAll().size(), "Должен быть создан один пользователь.");

    }

    @Test
    void createFailLoginTest() {
        // Проверка, что логин не может быть пустым и содержать пробелы
        User user = User.builder()
                .id(1L)
                .email("plastinin@yandex.ru")
                .login(" ")
                .name("Igor Plastinin")
                .birthday(LocalDate.of(1984, 8, 12))
                .build();

        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class,
                () -> validate(user));

        assertEquals("Логин не может быть пустым и содержать пробелы",
                exception.getConstraintViolations().iterator().next().getMessage());
    }

    @Test
    void userCreateWithEmptyNameTest() {
        //Если имя не задано — в таком случае будет использован логин
        User thisUser = new User(1L, "plastinin@yandex.ru", "Plastinin", null,
                LocalDate.of(1984, 8, 12));

        controller.create(thisUser);

        controller.findAll().forEach(f -> {
            assertEquals(f.getLogin(), f.getName(), "Описание не обновилось");
        });


    }

    @Test
    void createFailEmailTest() {
        // Проверка, что введенное значение является электронной почтой
        User user = User.builder()
                .id(1L)
                .email("@plastinin@yandex.ru")
                .login("Plastinin")
                .name("Igor Plastinin")
                .birthday(LocalDate.of(1984, 8, 12))
                .build();

        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class,
                () -> validate(user));

        assertEquals("Не является электронной почтой",
                exception.getConstraintViolations().iterator().next().getMessage());
    }

    @Test
    void createFailBirthdayTest() {
        // Проверка, что дата рождения не может быть в будущем
        User user = User.builder()
                .id(1L)
                .email("plastinin@yandex.ru")
                .login("Plastinin")
                .name("Igor Plastinin")
                .birthday(LocalDate.of(2084, 8, 12))
                .build();

        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class,
                () -> validate(user));

        assertEquals("Дата рождения не может быть в будущем",
                exception.getConstraintViolations().iterator().next().getMessage());
    }

    @Test
    void userUpdateTest() {
        User thisUser = new User(1L, "plastinin84@yandex.ru", "PlastininIgor84", "Plastinin Igor",
                LocalDate.of(1984, 8, 13));

        controller.create(user);
        controller.update(thisUser);

        controller.findAll().forEach(u -> {
            assertEquals(u.getLogin(), thisUser.getLogin(), "Логин не обновился.");
            assertEquals(u.getName(), thisUser.getName(), "Имя пользователя не обновилось.");
            assertEquals(u.getEmail(), thisUser.getEmail(), "Электронная почта не обновилась");
            assertEquals(u.getBirthday(), thisUser.getBirthday(), "Дата рождения не обновилась");
        });
    }


    @Test
    void userFindAllTest() {
        User thisUser1 = new User(1L, "plastinin@yandex.ru", "Plastinin", null,
                LocalDate.of(1984, 8, 12));
        User thisUser2 = new User(1L, "plastinin84@yandex.ru", "Plastinin84", null,
                LocalDate.of(1984, 8, 12));
        User thisUser3 = new User(1L, "plastinin-igor@yandex.ru", "Plastinin123", null,
                LocalDate.of(1984, 8, 12));

        controller.create(thisUser1);
        controller.create(thisUser2);
        controller.create(thisUser3);

        assertEquals(3, controller.findAll().size());
    }

}