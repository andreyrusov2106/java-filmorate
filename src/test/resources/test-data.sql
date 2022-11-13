insert into FILM (NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID)
values ('film1', 'description1', '2001-01-01', 90, 1),
       ('film2', 'description2', '2001-02-02', 100, 2),
       ('film3', 'description3', '2001-03-03', 80, 3);

insert into USERS (EMAIL, LOGIN, NAME, BIRTHDAY)
values ('user1@ya.ru', 'user1', 'vovka', '2001-01-01'),
       ('user2@ya.ru', 'user2', 'rusik', '2002-01-02'),
       ('user3@ya.ru', 'user3', 'julia', '2003-01-03');

insert into FILM_GENRE (FILM_ID, GENRE_ID)
values (1, 1), (1, 2),
       (2, 1), (2, 4),
       (3, 1);

insert into FILM_LIKE (FILM_ID, USER_ID)
values (1, 1), (1, 2), (1, 3),
       (2, 1), (2, 2),
       (3, 3);

insert into FRIENDSHIP (USER_ID, FRIEND_ID) values (1, 2), (1, 3), (3, 2);

insert into REVIEW (CONTENT, IS_POSITIVE, USER_ID, FILM_ID, USEFUL)
values ('This film is soo bad.', true, 1, 1, 5);