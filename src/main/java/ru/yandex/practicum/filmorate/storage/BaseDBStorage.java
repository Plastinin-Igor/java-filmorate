package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class BaseDBStorage<T> {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    // Создание записи
    protected long insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        Integer id = keyHolder.getKeyAs(Integer.class);

        // Возвращаем id нового пользователя
        if (id != null) {
            return id;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    // Обновление строки
    protected void update(String query, Object... params) {
        int rowsUpdated = jdbc.update(query, params);
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
    }

    //Удаление строки по id
    protected boolean delete(String query, Object... params) {
        int rowDeleted = jdbc.update(query, params);
        return rowDeleted > 0;
    }

    // Вернуть из базы одну строку
    protected Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbc.queryForObject(query, mapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    // Вернуть из базы список
    protected List<T> findMany(String query, Object... params) {
        return jdbc.query(query, mapper, params);
    }

    // Вернуть из базы кол-во строк. Запрос с параметрами
    protected int getCountFromTab(String query, Object... params) {
        Integer result = jdbc.queryForObject(query, Integer.class, params);
        return result != null ? result : 0;
    }

    // Вернуть из базы кол-во строк. Запрос без параметров
    protected int getCountFromTab(String query) {
        Integer result = jdbc.queryForObject(query, Integer.class);
        return result != null ? result : 0;
    }
}
