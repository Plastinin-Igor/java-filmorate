package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    // Добавление пользователя
    public void addUser(User user) {
        isUserUnique(user.getLogin(), user.getEmail(), user.getId());
        user.setName(checkName(user.getName(), user.getLogin()));
        userStorage.addUser(user);
    }

    //Список всех пользователей
    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    //Получение пользователя по Id
    public User getUserById(Long userId) {
        final User user = userStorage.getUserById(userId);
        if (user == null) {
            log.error("При получении пользователя по Id: {} данные не нйдены.", userId);
            throw new NotFoundException("Пользователь с Id: " + userId + " не найден.");
        }
        return userStorage.getUserById(userId);
    }

    //Исправление пользователя
    public void updateUser(User newUser) {
        final User oldUser = userStorage.getUserById(newUser.getId());
        if (oldUser == null) {
            log.error("Пользователь с Id: {} не найден.", newUser.getId());
            throw new NotFoundException("Пользователь с Id: " + newUser.getId() + " не найден.");
        }
        isUserUnique(newUser.getLogin(), newUser.getEmail(), newUser.getId());
        oldUser.setName(checkName(newUser.getName(), newUser.getLogin()));
        oldUser.setLogin(newUser.getLogin());
        oldUser.setEmail(newUser.getEmail());
        oldUser.setBirthday(newUser.getBirthday());
    }


    //TODO: PUT /users/{id}/friends/{friendId} — добавление в друзья.
    //TODO: DELETE /users/{id}/friends/{friendId} — удаление из друзей.
    //TODO: GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
    //TODO: GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.


    //Проверить, существует ли пользователь
    private void isUserUnique(String login, String email, Long id) {
        List<User> users = new ArrayList<>(userStorage.getUsers());
        for (User userList : users) {
            if (userList.getId() != id) {
                if (userList.getLogin().equals(login)) {
                    log.error("Пользователь с логином {} уже зарегистрирован в системе.", login);
                    throw new DuplicatedDataException("Пользователь с логином " + login
                            + " уже зарегистрирован в системе.");
                }
                if (userList.getEmail().equals(email)) {
                    log.error("Пользователь с email {} уже зарегистрирован в системе.", email);
                    throw new DuplicatedDataException("Пользователь с email " + email
                            + " уже зарегистрирован в системе.");
                }
            }
        }
    }

    // Если имя пользователя не задано - используем логин
    private String checkName(String name, String login) {
        return name != null ? name : login;
    }


}
