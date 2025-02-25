package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@Validated
@RestController
@RequestMapping("users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    /**
     * Генерация уникального идетнификатора
     *
     * @return long
     */
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    /**
     * Добавление пользователя
     *
     * @param user User
     * @return ResponseEntity User
     */
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        //Логин и маил не должны повторяться у разных пользователей
        for (User userList : users.values()) {
            if (userList.getLogin().equals(user.getLogin())
                    || userList.getEmail().equals(user.getEmail())) {
                return ResponseEntity.badRequest().build();
            }
        }
        //Имя для отображения может быть пустым — в таком случае будет использован логин
        user.setId(getNextId());
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return ResponseEntity.ok(user);
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
            //Логин и маил не должны повторяться у разных пользователей
            for (User userList : users.values()) {
                if (!userList.getId().equals(newUser.getId())
                        && (userList.getLogin().equals(newUser.getLogin())
                        || userList.getEmail().equals(newUser.getEmail()))) {
                    return ResponseEntity.badRequest().build();
                }
            }

            User oldUser = users.get(newUser.getId());

            //Имя для отображения может быть пустым — в таком случае будет использован логин
            if (newUser.getName() == null) {
                newUser.setName(newUser.getLogin());
            }

            oldUser.setName(newUser.getName());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setEmail(newUser.getEmail());
            oldUser.setBirthday(newUser.getBirthday());
            return ResponseEntity.ok(oldUser);
        } else {
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
        return users.values();
    }
}
