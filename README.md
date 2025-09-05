# Приложение для оценки просмотренных фильмов и рекомендаций
java, spring-boot, jdbc, sql, rest-api, junit, postgresql, h2-database

##  О проекте
Проект представляет собой разработку бэкенда веб-приложения предназначенного для подбора рекомендаций фильмов пользователям. Сервис будет собирать оценки зрителей и формировать список из пяти лучших фильмов для просмотра, облегчая выбор зрителям.

![image_project.png](src/main/resources/image_project.png)

## Функциональность приложения

- Регистрация и получение пользователей
- Добавление пользователей в список друзей
- Добавление, обновление и удаление фильмов
- Поиск фильмов по названию и режиссёру
- Добавление пользовательских отзывов на фильмы

## Эндпоинты
- PUT /users/{id}/friends/{friendId} — добавление в друзья.
- DELETE /users/{id}/friends/{friendId} — удаление из друзей.
- GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
- GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
- PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.
- DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.
- GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков. Если значение параметра count не задано, верните первые 10.
- GET /genres — возвращает список объектов содержащих жанр
- GET /genres/{id} — возвращает объект содержащий жанр с идентификатором id
- GET /mpa — возвращает список объектов содержащих рейтинг
- GET /mpa/{id} — возвращает объект содержащий рейтинг с идентификатором id

## База данных

### ER-диаграмма

![er_filmorate_db.png](src/main/resources/er_filmorate_db.png)

### Текстовое описание базы данных

| Таблица    | Наименование колонки | Тип данных   | Описание                              |
|------------|----------------------|--------------|---------------------------------------|
| users      | user_id              | integer      | Уникальный идентификатор пользователя |
| users      | email                | varchar(50)  | Адрес электронной почты               |
| users      | login                | varchar(50)  | Логин пользователя                    |
| users      | name                 | varchar(150) | Имя пользователя                      |
| users      | birthday             | date         | Дата рождения                         |
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

## ЗАПРОСЫ

### Примеры запросов для сущностей "Пользователи" и "Друзья"

- Список пользователй

```
select * from users;
```

- Найти пользователя по id

```
select * from users where user_id = :id;
```

- Найти пользователя по email

```
select * from users where email = :email; 
```

- Найти пользователя по логину

```
select * from users where login = :login;
```

- Удаление пользователя

```
delete from users where id = :id;
```

- Добавление нового пользователя

```
insert into users (email, login, name, birthday) 
values (:email, :login, :name, :birthday);
```

- Исправление пользователя

```
update users
   set email = :email,
       login = :login,
       name = :name,
       birthday = :birthday
 where user_id = :id;
```

- Добавление пользователя в друзья

```
insert into friends (user_id, friend_id) values(:user_id, :friend_id);
```

- Удаление пользователя из друзей

```
delete friends where user_id = :user_id and friend_id = :friend_id;
```

- Список друзей пользователя

```
select * 
  from users u
 inner join friends f on (u.user_id = f.friend_id)
 where f.user_id = :id;
```

- Поиск пользователя в друзьях

```
select * 
 from users u
inner join friends f on (u.user_id = f.friend_id)
where f.user_id = :user_id
  and f.friend_id = :user_id;
```

- Поиск общих друзей

```
select u.*
  from users u
 inner join friends f on (u.user_id = f.friend_id)
 inner join friends f2 on (u.user_id = f2.friend_id and f.friend_id = f2.friend_id)
 where f.user_id = :user_id_1 
   and f2.user_id = :user_id_2;
```

### Примеры запросов для сущностей "Фильмы"

- Список фильмов с рейтингом

```
select f.film_id,
       f.name,
       f.description,
       f.releasedate,
       f.duration,
       r.rating_id,
       r.rating,
       r.description rating_description
  from film f
  left join rating r on (f.rating = r.rating_id);
  ```

- Добавление фильма

```
insert into film
(name, description, releasedate, duration, rating)
values(:name, :description, :releasedate, :duration, :rating)
```

- Изменение фильма

```
update film
   set name = :name,
       description = :description,
	   releasedate = :releasedate,
	   duration = :duration,
	   rating = :rating
 where film_id = :film_id; 
```

- Список топ n фильмов

```
 select film_id,
        name,
        description,
        releasedate,
        duration,
        rating_id,
        rating,
        rating_description
   from (select (select count(1)
                   from likes l
                  where l.film_id = f.film_id) likes_qnt,
                f.film_id,
                f.name,
                f.description,
                f.releasedate,
                f.duration,
                r.rating_id,
                r.rating,
                r.description rating_description
           from film f
           left join rating r
             on (f.rating = r.rating_id)
          order by likes_qnt desc) limit :n;
```

- Количество лайков у фильма

```
select count(*) from likes where film_id = :id
```