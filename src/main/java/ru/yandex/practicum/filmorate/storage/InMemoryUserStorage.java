package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    //Список пользователей
    private final Map<Long, User> users;
    //Список друзей
    private final Map<Long, Set<Long>> friends;

    public InMemoryUserStorage() {
        users = new HashMap<>();
        friends = new HashMap<>();
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
        return newUser;
    }

    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public void addFriends(Long userId, Long friendId) {
        //Если Лена стала другом Саши ...
        if (friends.containsKey(userId)) {
            friends.get(userId).add(friendId);
        } else {
            friends.put(userId, new HashSet<>());
            friends.get(userId).add(friendId);
        }
        //...то это значит, что Саша теперь друг Лены
        if (friends.containsKey(friendId)) {
            friends.get(friendId).add(userId);
        } else {
            friends.put(friendId, new HashSet<>());
            friends.get(friendId).add(userId);
        }
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        friends.get(userId).remove(friendId); //Если Лена перестала быть другом Саши ...
        friends.get(friendId).remove(userId); //...то это значит, что Саша теперь тоже не дружит с Леной
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
        if (friends.containsKey(userId) && friends.containsKey(otherUserId)) {
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

    @Override
    public boolean isUserExists(Long userId) {
        return users.containsKey(userId);
    }

    @Override
    public boolean isFriendExist(Long userId, Long friendId) {
        return friends.containsKey(userId) && friends.get(userId).contains(friendId);
    }

    @Override
    public void isUserUnique(String login, String email, Long id) {
        for (User userList : users.values()) {
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

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
