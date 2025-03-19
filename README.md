# java-filmorate
### Бэкенд сервиса, который работает с фильмами и оценками пользователей

## База данных
### ER-диаграмма
![er_filmorate_db.png](src/main/resources/er_filmorate_db.png)

### Текстовое описание базы данных
| Таблица    | Наименование колонки | Тип данных   | Описание                              |
|------------|----------------------|--------------|---------------------------------------|
| user       | user_id              | integer      | Уникальный идентификатор пользователя |
| user       | email                | varchar(50)  | Адрес электронной почты               |
| user       | login                | varchar(50)  | Логин пользователя                    |
| user       | name                 | varchar(150) | Имя пользователя                      |
| user       | birthday             | date         | Дата рождения                         |
| film       | film_id              | integer      | Уникальный идентификатор фильма       |
| film       | name                 | varchar(150) | Название фильма                       |
| film       | description          | varchar(200) | Описание фильма                       |
| film       | releasedate          | date         | Дата релиза                           |
| film       | duration             | integer      | Продолжительность фильма              |
| film       | rating               | integer      | Рейтинг                               |
| likes      | likes_id             | integer      | Уникальный идентификатор              |
| likes      | film_id              | integer      | Идентификатор фильма                  |
| likes      | user_id              | integer      | Идентификатор пользователя            |
| friends    | id                   | integer      | Уникальный идентификатор              |
| friends    | user_id              | integer      | Пользователь                          |
| friends    | friend_id            | integer      | Друг                                  |
| friends    | sign_friendship      | boolean      | Признак подтверждения дружбы          |
| genre      | genre_id             | integer      | Идентификатор жанра                   |
| genre      | genre_name           | varchar(50)  | Жанр                                  |
| film_genre | id                   | integer      | Уникальный идентификатор              |
| film_genre | film_id              | integer      | Фильм                                 |
| film_genre | genre_id             | integer      | Жанр                                  |
| rating     | rating_id            | integer      | Идентификатор рейтинга                |
| rating     | rating               | varchar(15)  | Рейтинг                               |
| rating     | description          | varchar(50)  | Описание                              |

