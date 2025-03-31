package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {

    private Long id;

    @NotNull(message = "Адрес электронной почты должен быть задан")
    @Email(message = "Не является электронной почтой")
    private String email;

    @NotBlank(message = "Логин не может быть пустым и содержать пробелы")
    private String login;

    private String name;

    @NotNull(message = "Дата рождения должна быть задана")
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    public boolean hasEmail() {
        return !(email == null || email.isBlank());
    }

    public boolean hasLogin() {
        return !(login == null || login.isBlank());
    }

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasBirthday() {
        return !(name == null || name.isBlank());
    }

}
