MERGE INTO MPA (MPA_ID, NAME)
VALUES (1, 'PG-13');

MERGE INTO FILMS (FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)
VALUES (1, 'TEST FILM', 'Описание', '2000-01-01', 120, 1);

