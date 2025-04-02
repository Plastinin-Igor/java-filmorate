package ru.yandex.practicum.filmorate.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

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

}