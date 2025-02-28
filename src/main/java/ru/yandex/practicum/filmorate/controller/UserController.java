package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Validated
@RestController
@RequestMapping("users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    // Генерация уникального идетнификатора
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    //Проверить, существует ли пользователь
    private boolean isUserUnique(String login, String email) {
        for (User userList : users.values()) {
            if (userList.getLogin().equals(login)) {
                log.error("Пользователь с логином {} уже зарегистрирован в системе.", login);
                return false;
            }
            if (userList.getEmail().equals(email)) {
                log.error("Пользователь с email {} уже зарегистрирован в системе.", email);
                return false;
            }
        }
        return true;
    }

    // Если имя пользователя не задано - используем логин
    private String checkName(String name, String login) {
        return name != null ? name : login;
    }


    /**
     * Добавление пользователя
     *
     * @param user User
     * @return ResponseEntity User
     */
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        if (isUserUnique(user.getLogin(), user.getEmail())) {
            user.setId(getNextId());
            user.setName(checkName(user.getName(), user.getLogin()));
            users.put(user.getId(), user);
            log.info("Пользователь {} (id: {}) добавлен в систему.", user.getLogin(), user.getId());
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Обновление данных пользователя
     *
     * @param newUser User
     * @return ResponseEntity User
     */
    @PutMapping
    public ResponseEntity<User> update(@Valid @RequestBody User newUser) {
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (isUserUnique(newUser.getLogin(), newUser.getEmail())) {
                oldUser.setName(checkName(newUser.getName(), newUser.getLogin()));
                oldUser.setLogin(newUser.getLogin());
                oldUser.setEmail(newUser.getEmail());
                oldUser.setBirthday(newUser.getBirthday());
                log.info("Пользоватеь {} (id: {}) успешно обновлен в системе.", oldUser.getLogin(), oldUser.getId());
                return ResponseEntity.ok(oldUser);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } else {
            log.error("Пользователь с id {} не найден в системе.", newUser.getId());
            return ResponseEntity.internalServerError().body(newUser);
        }
    }

    /**
     * Получение списка пользователей
     *
     * @return Collection User
     */
    @GetMapping
    public Collection<User> findAll() {
        log.info("Выполнен запрос к списку пользователей. В системе зарегистрировано пользователей: {}.", users.size());
        return users.values();
    }
}
