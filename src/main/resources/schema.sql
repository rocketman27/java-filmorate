create table if not exists MPA
(
    MPA_ID BIGINT auto_increment,
    NAME   CHARACTER VARYING(20) not null,
    constraint PK_RATINGS
        primary key (MPA_ID)
);

create table if not exists FILMS
(
    FILM_ID       BIGINT auto_increment,
    NAME          CHARACTER VARYING(100) not null,
    DESCRIPTION   CHARACTER VARYING(200),
    RELEASE_DATE  DATE,
    DURATION      INTEGER,
    MPA_ID        BIGINT,
    AVERAGE_SCORE DECIMAL(2, 1),
        constraint PK_FILMS
            primary key (FILM_ID),
    constraint FK_FILMS_MPA
        foreign key (MPA_ID) references MPA
);

create table if not exists GENRES
(
    GENRE_ID BIGINT auto_increment,
    NAME     CHARACTER VARYING(40) not null,
    constraint PK_GENRES
        primary key (GENRE_ID)
);

create table if not exists FILMS_GENRES
(
    FILM_ID  BIGINT not null,
    GENRE_ID BIGINT not null,
    constraint PK_FILM_GENRES
        primary key (FILM_ID, GENRE_ID),
    constraint FK_FILMS_GENRES_FILMS
        foreign key (FILM_ID) references FILMS
            ON DELETE CASCADE,
    constraint FK_FILMS_GENRES_GENRES
        foreign key (GENRE_ID) references GENRES
            ON DELETE CASCADE
);

create unique index if not exists GENRES_GENRE_NAME_UNQ
    on GENRES (NAME);

create table if not exists DIRECTORS
(
    DIRECTOR_ID BIGINT auto_increment,
    NAME        CHARACTER VARYING(200) not null,
    constraint PK_DIRECTORS
        primary key (DIRECTOR_ID)
);

create table if not exists FILMS_DIRECTORS
(
    FILM_ID     BIGINT not null,
    DIRECTOR_ID BIGINT not null,
    constraint PK_FILMS_DIRECTORS
        primary key (FILM_ID, DIRECTOR_ID),
    constraint FK_FILMS_DIRECTORS_FILMS
        foreign key (FILM_ID) references FILMS
            ON DELETE CASCADE,
    constraint FK_FILMS_DIRECTORS_DIRECTORS
        foreign key (DIRECTOR_ID) references DIRECTORS
            ON DELETE CASCADE
);

create table if not exists USERS
(
    USER_ID       BIGINT auto_increment,
    EMAIL         CHARACTER VARYING(100) not null,
    LOGIN         CHARACTER VARYING(50)  not null,
    NAME          CHARACTER VARYING(100),
    BIRTHDAY_DATE DATE,
    constraint USER_ID
        primary key (USER_ID)
);

create table if not exists FRIENDS
(
    USER_ID   BIGINT not null,
    FRIEND_ID BIGINT not null,
    STATUS    BOOLEAN,
    constraint PK_FRIENDS
        primary key (USER_ID, FRIEND_ID),
    constraint FK_FRIENDS_1
        foreign key (USER_ID) references USERS
            ON DELETE CASCADE,
    constraint FK_FRIENDS_2
        foreign key (FRIEND_ID) references USERS
            ON DELETE CASCADE
);

create unique index if not exists USERS_EMAIL_UNQ
    on USERS (EMAIL);

create unique index if not exists USERS_LOGIN_UNQ
    on USERS (LOGIN);

create table if not exists LIKES
(
    USER_ID BIGINT not null,
    FILM_ID BIGINT not null,
    SCORE   INTEGER,
        constraint PK_USERS_LIKES
            primary key (USER_ID, FILM_ID),
    constraint FK_USERS_LIKES_FILMS
        foreign key (FILM_ID) references FILMS
            ON DELETE CASCADE,
    constraint FK_USERS_LIKES_USERS
        foreign key (USER_ID) references USERS
            ON DELETE CASCADE
);

create table if not exists EVENTS
(
    EVENT_ID       BIGINT auto_increment,
    USER_ID        BIGINT                not null,
    ENTITY_ID      BIGINT                not null,
    EVENT_TYPE     CHARACTER VARYING(20) not null,
    OPERATION_TYPE CHARACTER VARYING(20) not null,
    CREATED_AT     TIMESTAMP             not null,
    constraint PK_EVENTS
        primary key (EVENT_ID),
    constraint FK_EVENTS_USERS
        foreign key (USER_ID) references USERS
            ON DELETE CASCADE
);

create table if not exists REVIEWS
(
    REVIEW_ID   BIGINT auto_increment,
    CONTENT     VARCHAR(250),
    IS_POSITIVE BOOLEAN           not null,
    USEFUL      INTEGER DEFAULT 0 NOT NULL,
    FILM_ID     BIGINT            not null,
    AUTHOR_ID   BIGINT            not null,
    constraint REVIEW_ID primary key (REVIEW_ID),
    foreign key (FILM_ID) references films (film_id)
        ON DELETE CASCADE,
    foreign key (AUTHOR_ID) references USERS (USER_ID)
        ON DELETE CASCADE
);
create table if not exists REVIEWS_LIKES
(
    REVIEW_ID   BIGINT  not null,
    USER_ID     BIGINT  not null,
    IS_POSITIVE BOOLEAN not null,
    foreign key (REVIEW_ID) references REVIEWS (REVIEW_ID)
        ON DELETE CASCADE,
    foreign key (USER_ID) references USERS (USER_ID)
        ON DELETE CASCADE
);
