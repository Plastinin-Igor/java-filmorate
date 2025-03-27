MERGE INTO PUBLIC."user" (USER_ID, email,login,NAME,birthday) VALUES
	 (1, 'igor-plastinin@yandex.ru','Plastinin','Plastinin Igor','1984-08-12'),
	 (2, 'ivanov.i@mail.ru','Ivanov','Ivanov Ivan','1991-12-02');

MERGE INTO rating (rating_id,rating,description) VALUES
	 (1,'G','У фильма нет возрастных ограничений'),
	 (2,'PG','Детям рекомендуется смотреть фильм с родителями'),
	 (3,'PG-13','Детям до 13 лет просмотр не желателен'),
	 (4,'R','Лицам до 17 лет только в присутствии взрослого'),
	 (5,'NC-17','Лицам до 18 лет просмотр запрещён');

MERGE INTO film (film_id,name,description,releasedate,duration,rating) VALUES
	 (1,'Интерстеллар','Фильм, вдохновленный идеями физика Кипа Торна, исследует темы выживания человечества, родительской любви и парадоксов времени через призму релятивистской физики','2014-10-26',169,3),
	 (2,'Гладиатор','Римская империя. Бесстрашного и благородного генерала Максимуса боготворят солдаты, а старый император Марк Аврелий безгранично доверяет ему и относится как к сыну.','2000-05-18',155,4),
	 (3,'Солярис','На космическую станцию, сотрудники которой давно и тщетно пытаются сладить с загадкой планеты Солярис, полностью покрытой Океаном, прибывает новый учёный, психолог Крис Кельвин.','1972-05-05',169,2);

MERGE INTO genre (genre_id,genre_name) VALUES
	 (1,'Комедия'),
	 (2,'Драма'),
	 (3,'Мультфильм'),
	 (4,'Триллер'),
	 (5,'Документальный'),
	 (6,'Боевик');

MERGE INTO film_genre (id,film_id,genre_id) VALUES
	 (1,1,2),
	 (2,1,4),
	 (3,2,6),
	 (4,2,2),
	 (5,3,2);

MERGE INTO likes (likes_id,film_id,user_id) VALUES
	 (1,1,1),
	 (2,1,2),
	 (3,2,2),
	 (4,3,1);

MERGE INTO friends (id,user_id,friend_id,sign_friendship) VALUES
	 (1,1,2,true),
	 (2,2,1,true);