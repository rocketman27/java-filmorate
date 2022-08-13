create table if not exists MPA
(
    MPA_ID      INTEGER auto_increment,
    MPA_NAME    CHARACTER VARYING not null,
    constraint PK_RATINGS
        primary key (MPA_ID)
);

create table if not exists FILMS
(
    FILM_ID      INTEGER auto_increment,
    NAME         CHARACTER VARYING(100) not null,
    DESCRIPTION  CHARACTER VARYING(200),
    RELEASE_DATE DATE,
    DURATION     INTEGER,
    MPA_ID       INTEGER,
    constraint PK_FILMS
        primary key (FILM_ID),
    constraint FK_FILMS_MPA
        foreign key (MPA_ID) references MPA
);

create unique index if not exists FILMS_NAME_UNQ
    on FILMS (NAME);

create table if not exists GENRES
(
    GENRE_ID   INTEGER auto_increment,
    GENRE_NAME CHARACTER VARYING(20) not null,
    constraint PK_GENRES
        primary key (GENRE_ID)
);

create table if not exists FILMS_GENRES
(
    FILM_ID  INTEGER not null,
    GENRE_ID INTEGER not null,
    constraint PK_FILM_GENRES
        primary key (FILM_ID, GENRE_ID),
    constraint FK_FILMS_GENRES_FILMS
        foreign key (FILM_ID) references FILMS,
    constraint FK_FILMS_GENRES_GENRES
        foreign key (GENRE_ID) references GENRES
);

create unique index if not exists GENRES_GENRE_NAME_UNQ
    on GENRES (GENRE_NAME);

/*create table if not exists FILMS_MPA
(
    FILM_ID INTEGER not null,
    MPA_ID  INTEGER not null,
    constraint PK_FILMS_RATINGS
        primary key (FILM_ID, MPA_ID),
    constraint FK_FILMS_RATINGS_FILMS
        foreign key (FILM_ID) references FILMS,
    constraint FK_FILMS_RATINGS_RATINGS
        foreign key (MPA_ID) references MPA
);*/

create table if not exists USERS
(
    USER_ID       INTEGER auto_increment,
    EMAIL         CHARACTER VARYING(50) not null,
    LOGIN         CHARACTER VARYING(50) not null,
    NAME          CHARACTER VARYING(50),
    BIRTHDAY_DATE DATE,
    constraint USER_ID
        primary key (USER_ID)
);

create table if not exists FRIENDS
(
    USER_ID   INTEGER not null,
    FRIEND_ID INTEGER not null,
    STATUS    BOOLEAN,
    constraint PK_FRIENDS
        primary key (USER_ID, FRIEND_ID),
    constraint FK_FRIENDS_1
        foreign key (USER_ID) references USERS,
    constraint FK_FRIENDS_2
        foreign key (FRIEND_ID) references USERS
);

create unique index if not exists USERS_EMAIL_UNQ
    on USERS (EMAIL);

create unique index if not exists USERS_LOGIN_UNQ
    on USERS (LOGIN);

create table if not exists USERS_LIKES
(
    USER_ID INTEGER not null,
    FILM_ID INTEGER not null,
    constraint PK_USERS_LIKES
        primary key (USER_ID, FILM_ID),
    constraint FK_USERS_LIKES_FILMS
        foreign key (FILM_ID) references FILMS,
    constraint FK_USERS_LIKES_USERS
        foreign key (USER_ID) references USERS
);