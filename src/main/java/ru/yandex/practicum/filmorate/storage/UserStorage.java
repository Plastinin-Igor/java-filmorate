package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    User addUser(User user);

    User updateUser(User user);

    void deleteUser(Long userId);

    Collection<User> getUsers();

    Optional<User> getUserById(Long userId);

    void addFriends(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    Collection<User> getFriends(Long userId);

    Collection<User> getCommonFriends(Long userId, Long otherUserId);

    boolean isUserExists(Long userId);

    boolean isFriendExist(Long userId, Long friendId);

    void isUserUnique(String login, String email, Long id);
}
