-- Для тестов каждый раз удалять придётся, руками день

DROP TABLE IF EXISTS Genre;
DROP TABLE IF EXISTS Rating;
DROP TABLE IF EXISTS FilmGenres;
DROP TABLE IF EXISTS Films;
DROP TABLE IF EXISTS Likes;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Friends;
DROP TABLE IF EXISTS FriendshipStatus;

CREATE TABLE IF NOT EXISTS Genre
(
    id   integer NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS Rating
(
    id   integer NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS FilmGenres
(
    filmId  integer NOT NULL,
    genreId integer NOT NULL
);

CREATE TABLE IF NOT EXISTS Films
(
    id          integer NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name        varchar NOT NULL,
    description varchar,
    releaseDate date,
    duration    integer,
    rating      integer
);

CREATE TABLE IF NOT EXISTS Likes
(
    filmId integer NOT NULL,
    userId integer NOT NULL
);

CREATE TABLE IF NOT EXISTS Users
(
    id       integer NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    varchar NOT NULL,
    login    varchar NOT NULL,
    name     varchar,
    birthday date
);

CREATE TABLE IF NOT EXISTS Friends
(
    userId           integer NOT NULL,
    friendId         integer NOT NULL,
    friendshipStatus integer NOT NULL
);

CREATE TABLE IF NOT EXISTS FriendshipStatus
(
    id   integer NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar NOT NULL
);