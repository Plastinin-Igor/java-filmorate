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
    public User addUser(User user) {
        isUserUnique(user.getLogin(), user.getEmail(), user.getId());
        user.setName(checkName(user.getName(), user.getLogin()));
        return userStorage.addUser(user);
    }

    //Список всех пользователей
    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    //Получение пользователя по Id
    public User getUserById(Long userId) {
        userExist(userId);
        return userStorage.getUserById(userId).get();
    }

    //Исправление пользователя
    public User updateUser(User newUser) {
        userExist(newUser.getId());
        return userStorage.updateUser(newUser);
    }

    //Удаление пользователя
    public void deleteUser(Long userId) {
        userExist(userId);
        userStorage.deleteUser(userId);
    }

    //Добавление в друзья.
    public void addFriends(Long userId, Long friendId) {
        userExist(userId);
        userExist(friendId);
        userStorage.addFriends(userId, friendId);
    }

    //Удаление из друзей.
    public void deleteFriend(Long userId, Long friendId) {
        userExist(userId);
        userExist(friendId);
        if (userStorage.isFriendExist(userId, friendId)) {
            userStorage.deleteFriend(userId, friendId);
        } else {
            log.error("В списке друзей пользователя c Id: {} отсутствует друг с Id: {}", userId, friendId);
        }
    }

    //Возвращаем список пользователей, являющихся его друзьями.
    public Collection<User> getFriends(Long userId) {
        userExist(userId);
        return userStorage.getFriends(userId);
    }

    //Список друзей, общих с другим пользователем.
    public Collection<User> getCommonFriends(Long userId, Long otherUserId) {
        userExist(userId);
        userExist(otherUserId);
        return userStorage.getCommonFriends(userId, otherUserId);
    }

    //Проверить наличие пользователя в хранилище
    private void userExist(Long userId) {
        if (!userStorage.isUserExists(userId)) {
            log.error("Пользователь с Id: {} не найден в системе.", userId);
            throw new NotFoundException("Пользователь с Id: " + userId + " не найден в системе.");
        }
    }

    //Проверить пользователя на уникальность
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
