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
public class UserController {
    private final UserService userService;

    @PostMapping("users")
    public void create(@Valid @RequestBody User user) {
        userService.addUser(user);
        log.info("Пользователь {} успешно добавлен в систему.", user.getLogin());
    }

    @PutMapping("users")
    public void update(@Valid @RequestBody User newUser) {
        userService.updateUser(newUser);
        log.info("Пользователь {} с Id: {} успешно обновлен в системе", newUser.getLogin(), newUser.getId());
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


    //TODO: PUT /users/{id}/friends/{friendId} — добавление в друзья.
    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriends(@PathVariable long id,
                           @PathVariable long friendId) {
        userService.addFriends(id, friendId);
        log.info("Пользователь c Id: {} добавил в друзья пользователя с Id: {}", id, friendId);
    }

    //TODO: DELETE /users/{id}/friends/{friendId} — удаление из друзей.
    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable long id,
                             @PathVariable long friendId) {
        userService.deleteFriend(id, friendId);
        log.info("Пользователь c Id: {} удалил из друзей пользователя с Id: {}", id, friendId);
    }

    //TODO: GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
    @GetMapping("/users/{id}/friends")
    public Collection<User> getFriends(@PathVariable long id) {
        log.info("Пользователь c Id: {} запросил список друзей", id);
        return userService.getFriends(id);
    }

    //TODO: GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable long id,
                                             @PathVariable long otherId) {
        log.info("Запросили список общих друзей для пользователя с Id: {} и пользователя с Id: {}",
                id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

}
