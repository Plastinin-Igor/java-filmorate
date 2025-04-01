package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;

@Repository
@Slf4j
@Primary
public class DbUserStorage extends BaseDBStorage<User> implements UserStorage {

    private static final String FIND_ALL_QUERY = "select * from users";
    private static final String FIND_BY_ID_QUERY = "select * from users where user_id = ?";
    private static final String FIND_BY_EMAIL = "select * from users where email = ?";
    private static final String FIND_BY_LOGIN = "select * from users where login = ?";
    private static final String DELETE_BY_ID = "delete from users where id = ?";
    private static final String INSERT_FRIEND = "insert into friends (user_id, friend_id) values(?, ?)";
    private static final String DELETE_FRIEND = "delete friends where user_id = ? and friend_id = ?";
    private static final String INSERT_QUERY = """
             insert into users\s
             (email, login, name, birthday)\s
             values(?, ?, ?, ?)
            \s""";
    private static final String UPDATE_QUERY = """
            update users
               set email = ?,
                   login = ?,
                   name = ?,
                   birthday = ?
             where user_id = ?
            """;
    private static final String FIND_FRIENDS = """
             select *\s
              from users u
             inner join friends f on (u.user_id = f.friend_id)
             where f.user_id = ?
            \s""";
    private static final String FIND_FRIENDS_ID = """
             select *\s
              from users u
             inner join friends f on (u.user_id = f.friend_id)
             where f.user_id = ?
               and f.friend_id = ?
            \s""";
    private static final String FIND_COMMON_FRIENDS = """
            select u.*
              from users u
             inner join friends f on (u.user_id = f.friend_id)
             inner join friends f2 on (u.user_id = f2.friend_id and f.friend_id = f2.friend_id)
             where f.user_id = ?
               and f2.user_id = ?
            """;

    public DbUserStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public User addUser(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        update(DELETE_BY_ID, userId);
    }

    @Override
    public Collection<User> getUsers() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }

    //TODO И последнее небольшое изменение: дружба должна стать односторонней.
    // Теперь, если пользователь отправляет заявку в друзья, он добавляет другого человека
    // в свой список друзей, но сам в его список не попадает.

    @Override
    public void addFriends(Long userId, Long friendId) {
        update(INSERT_FRIEND, userId, friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        update(DELETE_FRIEND, userId, friendId);
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        return findMany(FIND_FRIENDS, userId);
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long otherUserId) {
        return findMany(FIND_COMMON_FRIENDS, userId, otherUserId);
    }

    @Override
    public boolean isUserExists(Long userId) {
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();

        return (findOne(FIND_BY_ID_QUERY, userId).isPresent());
    }

    @Override
    public boolean isFriendExist(Long userId, Long friendId) {
        return (findOne(FIND_FRIENDS_ID, userId, friendId).isPresent());
    }

    @Override
    public void isUserUnique(String login, String email, Long id) {
        Optional<User> userByLogin = findOne(FIND_BY_LOGIN, login);
        if (userByLogin.isPresent() && userByLogin.get().getId() != id) {
            log.error("Пользователь с логином {} уже зарегистрирован в системе.", login);
            throw new DuplicatedDataException("Пользователь с логином " + login + " уже зарегистрирован в системе.");
        }
        Optional<User> userByEmail = findOne(FIND_BY_EMAIL, email);
        if (userByEmail.isPresent() && userByEmail.get().getId() != id) {
            log.error("Пользователь с email {} уже зарегистрирован в системе.", email);
            throw new DuplicatedDataException("Пользователь с email " + email + " уже зарегистрирован в системе.");
        }
    }
}
