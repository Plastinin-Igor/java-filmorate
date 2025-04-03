package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;

@Repository
@Slf4j
public class DbRatingStorage extends BaseDBStorage<Rating> {
    private static final String FIND_BY_ID_QUERY = "select rating_id, rating from rating r where rating_id = ?";
    private static final String FIND_ALL_QUERY = "select rating_id, rating from rating order by rating_id";

    public DbRatingStorage(JdbcTemplate jdbc, RowMapper<Rating> mapper) {
        super(jdbc, mapper);
    }

    public Rating getRatingById(Long ratingId) {
        try {
            return findOne(FIND_BY_ID_QUERY, ratingId)
                    .orElseThrow(() -> new NotFoundException("Рейтинг mpa с id: " + ratingId + " не найден в системе"));
        } catch (NotFoundException e) {
            log.error("Рейтинг mpa с id: {} не найден в системе", ratingId);
            throw e;
        }
    }

    public Collection<Rating> getRatings() {
        return findMany(FIND_ALL_QUERY);
    }

}
