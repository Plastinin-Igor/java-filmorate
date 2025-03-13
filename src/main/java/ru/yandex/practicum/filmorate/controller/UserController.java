package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("users")
    public User create(@Valid @RequestBody User user) {
        User userLocal = userService.addUser(user);
        log.info("Пользователь с Id: {} успешно добавлен в систему.", user.getId());
        return userLocal;
    }

    @PutMapping("users")
    public User update(@Valid @RequestBody User newUser) {
        User user = userService.updateUser(newUser);
        log.info("Пользователь {} с Id: {} успешно обновлен в системе.", newUser.getLogin(), newUser.getId());
        return user;
    }

    @GetMapping("users")
    public Collection<User> findAll() {
        log.info("Выполнен запрос к списку пользователей. В системе зарегистрировано пользователей: {}.",
                userService.getUsers().size());
        return userService.getUsers();
    }

    @GetMapping("users/{userId}")
    public User getUserById(@PathVariable long userId) {
        return userService.getUserById(userId);
    }


    //Добавление в друзья.
    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriends(@PathVariable long id,
                           @PathVariable long friendId) {
        userService.addFriends(id, friendId);
        log.info("Пользователь c Id: {} добавил в друзья пользователя с Id: {}.", id, friendId);
    }

    //Удаление из друзей.
    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable long id,
                             @PathVariable long friendId) {
        userService.deleteFriend(id, friendId);
        log.info("Пользователь c Id: {} удалил из друзей пользователя с Id: {}.", id, friendId);
    }

    //Возвращаем список пользователей, являющихся его друзьями.
    @GetMapping("/users/{id}/friends")
    public Collection<User> getFriends(@PathVariable long id) {
        log.info("Запросили список друзей для пользователя c Id: {}.", id);
        return userService.getFriends(id);
    }

    //Список друзей, общих с другим пользователем.
    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable long id,
                                             @PathVariable long otherId) {
        log.info("Запросили список общих друзей для пользователя с Id: {} и пользователя с Id: {}.",
                id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

}
