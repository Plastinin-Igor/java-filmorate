package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    // Добавление пользователя
    public UserDto addUser(NewUserRequest request) {
        User user = UserMapper.mapToUser(request);
        userStorage.isUserUnique(user.getLogin(), user.getEmail(), user.getId());
        user.setName(checkName(user.getName(), user.getLogin()));
        user = userStorage.addUser(user);
        return UserMapper.mapToUserDto(user);
    }

    //Список всех пользователей
    public Collection<UserDto> getUsers() {
        return userStorage.getUsers()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    //Получение пользователя по Id
    public UserDto getUserById(Long userId) {
        userExist(userId);
        return UserMapper.mapToUserDto(userStorage.getUserById(userId).get());
    }

    //Исправление пользователя
    public UserDto updateUser(UpdateUserRequest request) {
        userExist(request.getId());
        User user = userStorage.getUserById(request.getId()).get();
        user = UserMapper.updateUserFields(user, request);
        user = userStorage.updateUser(user);

        return UserMapper.mapToUserDto(user);
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
    public Collection<UserDto> getFriends(Long userId) {
        userExist(userId);
        return userStorage.getFriends(userId)
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    //Список друзей, общих с другим пользователем.
    public Collection<UserDto> getCommonFriends(Long userId, Long otherUserId) {
        userExist(userId);
        userExist(otherUserId);
        return userStorage.getCommonFriends(userId, otherUserId)
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    //Проверить наличие пользователя в хранилище
    private void userExist(Long userId) {
        if (!userStorage.isUserExists(userId)) {
            log.error("Пользователь с Id: {} не найден в системе.", userId);
            throw new NotFoundException("Пользователь с Id: " + userId + " не найден в системе.");
        }
    }

    // Если имя пользователя не задано - используем логин
    private String checkName(String name, String login) {
        return name != null ? name : login;
    }

}
