package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    //TODO проверки и исключения

    //Список пользователей
    private final Map<Long, User> users;
    //Список друзей
    private final Map<Long, Set<Long>> friends;

    public InMemoryUserStorage() {
        users = new HashMap<>();
        friends = new HashMap<>();
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public User addUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User newUser) {
        User oldUser = users.get(newUser.getId());
        oldUser.setName(newUser.getName());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setEmail(newUser.getEmail());
        oldUser.setBirthday(newUser.getBirthday());
        return oldUser;
    }

    public void deleteUser(User user) {
        users.remove(user.getId());
    }

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User getUserById(User user) {
        return users.get(user.getId());
    }

    @Override
    public User addFriends(Long userId, Long friendId) {
        if (!friends.containsKey(userId)) {
            friends.put(userId, new HashSet<>());
            friends.get(userId).add(friendId);
            return users.get(friendId);
        } else {
            friends.get(userId).add(friendId);
            return users.get(friendId);
        }
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        friends.get(userId).remove(friendId);
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        if (friends.containsKey(userId)) {
            Set<Long> friendIds = friends.get(userId); // Список id-шников друзей
            Collection<User> friendsList = new ArrayList<>();
            for (Long friendId : friendIds) {
                friendsList.add(users.get(friendId));
            }
            return friendsList; // Возвращаем объекты друзей-пользователей по их id
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long otherUserId) {
        if (users.containsKey(userId) && users.containsKey(otherUserId)) {
            Set<Long> userFriendsIds = friends.get(userId);
            Set<Long> otherUserFriendsId = friends.get(otherUserId);
            Collection<User> commonFriends = new ArrayList<>();
            for (Long friendId : userFriendsIds) {
                if (otherUserFriendsId.contains(friendId)) {
                    commonFriends.add(users.get(friendId));
                }
            }
            return commonFriends;
        } else {
            return Collections.emptyList();
        }
    }
}
