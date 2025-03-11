package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    void addUser(User user);

    void updateUser(User user);

    void deleteUser(Long userId);

    Collection<User> getUsers();

    Optional<User> getUserById(Long userId);

    void addFriends(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    boolean isFriendExist(Long userId, Long friendId);

    Collection<User> getFriends(Long userId);

    Collection<User> getCommonFriends(Long userId, Long otherUserId);

}
