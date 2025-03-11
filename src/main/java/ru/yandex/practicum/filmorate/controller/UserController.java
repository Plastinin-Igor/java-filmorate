package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@Service
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public void create(@Valid @RequestBody User user) {
        userService.addUser(user);
        log.info("Пользователь {} успешно добавлен в систему.", user.getLogin());
    }

    @PutMapping
    public void update(@Valid @RequestBody User newUser) {
        userService.updateUser(newUser);
        log.info("Пользователь {} с Id: {} успешно обновлен в системе", newUser.getLogin(), newUser.getId());
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Выполнен запрос к списку пользователей. В системе зарегистрировано пользователей: {}.",
                userService.getUsers().size());
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable long userId) {
        return userService.getUserById(userId);
    }

    //TODO: PUT /users/{id}/friends/{friendId} — добавление в друзья.
    //TODO: DELETE /users/{id}/friends/{friendId} — удаление из друзей.
    //TODO: GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
    //TODO: GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.


}
