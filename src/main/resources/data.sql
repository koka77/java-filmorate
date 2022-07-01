
-- заполняем таблицу рейтингов

INSERT INTO PUBLIC.MPAA (NAME)
VALUES ('G');

INSERT INTO PUBLIC.MPAA (NAME)
VALUES ('PG');

INSERT INTO PUBLIC.MPAA (NAME)
VALUES ('PG-13');

INSERT INTO PUBLIC.MPAA (NAME)
VALUES ('R');

INSERT INTO PUBLIC.MPAA (NAME)
VALUES ('NC-17');

-- заполняем таблицу жанров
INSERT INTO PUBLIC.GENRES (NAME) VALUES ('Комедия');
INSERT INTO PUBLIC.GENRES (NAME) VALUES ('Драма');
INSERT INTO PUBLIC.GENRES (NAME) VALUES ('Мультфильм');
INSERT INTO PUBLIC.GENRES (NAME) VALUES ('Триллер');
INSERT INTO PUBLIC.GENRES (NAME) VALUES ('Документальный');
INSERT INTO PUBLIC.GENRES (NAME) VALUES ('Боевик');


-- заполняем таблицу фильмов
INSERT INTO films (name, description, release_date, duration, mpaa_id) VALUES ('Побег из Шоушенка', 'Молодого финансиста Энди Дюфрейна подозревают в убийстве, которого он не совершал. Несмотря на это его приговаривают к пожизненному заключению в тюрьме, из которой ещё никому не удавалось сбежать.', '2022-10-10', 100, 1);
INSERT INTO films (name, description, release_date, duration, mpaa_id) VALUES ('Крёстный отец', 'Первая часть криминальной саги о сицилийской мафиозной семье Корлеоне, которая обладает огромным авторитетом в Нью-Йорке.', '2022-10-11', 60, 2);
INSERT INTO films (name, description, release_date, duration, mpaa_id) VALUES ('Тёмный рыцарь', 'Городу в очередной раз требуется герой, и им традиционно становится Бэтмен, задача которого — ликвидировать преступника по кличке Джокер.', '2022-10-13', 60, 3);



-- заполняем таблицу FILMS_GENRES
insert into FILMS_GENRES values ( 1,1 );
insert into FILMS_GENRES values ( 1,2 );
insert into FILMS_GENRES values ( 3,3 );
insert into FILMS_GENRES values ( 3,5 );

-- заполняем таблицу юзеров
INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) values ('1@ya.ru', 'login1', 'Федорова Алина', '2021-10-20');
INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) values ('2@ya.ru', 'login2', 'Чернова Екатерина', '2021-11-21');
INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) values ('3@ya.ru', 'login3', 'Столяров Даниил', '2021-12-22');

-- заполняем таблицу FRIENDS
INSERT INTO FRIENDS (FRIEND_ID, USER_ID) VALUES ( 2,1);
INSERT INTO FRIENDS (FRIEND_ID, USER_ID) VALUES ( 1,2);
INSERT INTO FRIENDS (FRIEND_ID, USER_ID) VALUES ( 3,1);

-- заполняем таблицу отзывов
INSERT INTO REVIEWS (FILM_ID, USER_ID, CONTENT, IS_POSITIVE, USEFUL)
VALUES (1, 2, 'Обалденный фильм', true, DEFAULT);

INSERT INTO REVIEWS (FILM_ID, USER_ID, CONTENT, IS_POSITIVE, USEFUL)
VALUES (2, 1, 'Фильм, который вызывает уважение', true, DEFAULT);

INSERT INTO REVIEWS (FILM_ID, USER_ID, CONTENT, IS_POSITIVE, USEFUL)
VALUES (1, 3, 'Всем рекомендую', true, DEFAULT);


