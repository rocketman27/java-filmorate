merge into PUBLIC.MPA (MPA_ID, MPA_NAME) values (1, 'G');
merge into PUBLIC.MPA (MPA_ID, MPA_NAME) values (2, 'PG');
merge into PUBLIC.MPA (MPA_ID, MPA_NAME) values (3, 'PG-13');
merge into PUBLIC.MPA (MPA_ID, MPA_NAME) values (4, 'R');
merge into PUBLIC.MPA (MPA_ID, MPA_NAME) values (5, 'NC-17');

merge into PUBLIC.GENRES (GENRE_ID, GENRE_NAME) values (1, 'Комедия');
merge into PUBLIC.GENRES (GENRE_ID, GENRE_NAME) values (2, 'Драма');
merge into PUBLIC.GENRES (GENRE_ID, GENRE_NAME) values (3, 'Мультфильм');
merge into PUBLIC.GENRES (GENRE_ID, GENRE_NAME) values (4, 'Триллер');
merge into PUBLIC.GENRES (GENRE_ID, GENRE_NAME) values (5, 'Документальный');
merge into PUBLIC.GENRES (GENRE_ID, GENRE_NAME) values (6, 'Боевик');

COMMIT;