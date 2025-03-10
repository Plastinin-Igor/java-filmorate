package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryUserStorage implements UserStorage {

    //Список пользователей
    private final Map<Long, User> users = new HashMap<>();
    //Список друзей
    private final Map<Long, Set<Long>> friends = new HashMap<>();

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
     * @return User
     */
    public User addUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    /**
     * Исправление пользователя
     *
     * @param newUser User
     * @return User
     */
    public User updateUser(User newUser) {
        User oldUser = users.get(newUser.getId());
        oldUser.setName(newUser.getName());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setEmail(newUser.getEmail());
        oldUser.setBirthday(newUser.getBirthday());
        return oldUser;
    }

    /**
     * Удаление пользователя
     *
     * @param user User
     */
    public void deleteUser(User user) {
        users.remove(user.getId());
    }

    /**
     * Список всех пользователей
     *
     * @return Collection User
     */
    public Collection<User> findAll() {
        return users.values();
    }
}
