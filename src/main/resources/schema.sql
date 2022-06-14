-- удаление всех таблиц в БД
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS likes;
DROP TABLE IF EXISTS genre_film;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS mpaa;
DROP TABLE IF EXISTS genre;


create table IF NOT EXISTS films
(
    film_id      BIGINT generated by default as identity primary key,
    name         VARCHAR(255),
    descrittion  VARCHAR(255),
    release_date Date,
    duration     interval minute(18),
    mpaa_id      int
);

create table IF NOT EXISTS likes
(
    like_id BIGINT generated by default as identity PRIMARY KEY,
    user_id BIGINT,
    film_id BIGINT
);

create table IF NOT EXISTS users
(
    user_id  BIGINT generated by default as identity PRIMARY KEY,
    email    VARCHAR(30),
    login    VARCHAR(30),
    name     VARCHAR(30),
    birthday Date
);

create table IF NOT EXISTS genre
(
    genre_id BIGINT generated by default as identity PRIMARY KEY,
    name     VARCHAR(50)
);


create table IF NOT EXISTS friends
(
    friend_id  BIGINT generated by default as identity PRIMARY KEY,
    user_id    BIGINT,
    isfriend    BOOLEAN
);
create table IF NOT EXISTS mpaa
(
    mpaa_id  BIGINT generated by default as identity PRIMARY KEY,
    name    VARCHAR
);

INSERT INTO films (name, descrittion, release_date, duration, mpaa_id)
VALUES ('a', 'aa', '2022-10-10', 100, 1);
INSERT INTO films (name, descrittion, release_date, duration, mpaa_id)
VALUES ('b', 'bb', '2022-10-11', 60, 2);
INSERT INTO films (name, descrittion, release_date, duration, mpaa_id)
VALUES ('c', 'cc', '2022-10-13', 60, 3);