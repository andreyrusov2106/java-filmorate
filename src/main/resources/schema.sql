
--
drop table IF EXISTS DIRECTOR_FILMS;

drop table IF EXISTS DIRECTORS;

drop table IF EXISTS FILM;

drop table IF EXISTS FILM_GENRE;

drop table IF EXISTS FILM_LIKE;

drop table IF EXISTS FRIENDSHIP;

drop table IF EXISTS GENRE;

drop table IF EXISTS RATING;

drop table IF EXISTS USERS;
drop table IF EXISTS REVIEW;
drop table IF EXISTS REVIEW_LIKE;


CREATE TABLE IF NOT EXISTS public.film (
    film_id integer AUTO_INCREMENT NOT NULL,
    name character varying NOT NULL,
    description character varying,
    release_date date,
    duration integer,
    rating_id integer
);


CREATE TABLE IF NOT EXISTS  public.film_genre (
    film_genre_id  integer AUTO_INCREMENT NOT NULL,
    film_id integer NOT NULL,
    genre_id integer NOT NULL
);



CREATE TABLE IF NOT EXISTS  public.film_like (
    like_id integer AUTO_INCREMENT NOT NULL,
    film_id integer NOT NULL,
    user_id integer NOT NULL
);


CREATE TABLE IF NOT EXISTS  public.friendship (
    friendship_id integer AUTO_INCREMENT NOT NULL,
    user_id integer NOT NULL,
    friend_id integer NOT NULL,
    is_confirmed boolean
);



CREATE TABLE IF NOT EXISTS  public.genre (
    genre_id integer AUTO_INCREMENT NOT NULL,
    name character varying
);


CREATE TABLE IF NOT EXISTS  public.rating (
    rating_id integer AUTO_INCREMENT NOT NULL,
    name character varying
);


CREATE TABLE IF NOT EXISTS  public.USERS (
    user_id integer AUTO_INCREMENT NOT NULL,
    email character varying,
    login character varying,
    name character varying,
    birthday date
);
CREATE TABLE IF NOT EXISTS  public.REVIEW (
    review_id integer AUTO_INCREMENT NOT NULL,
    content character varying NOT NULL,
    is_positive boolean NOT NULL,
    user_id integer NOT NULL,
    film_id integer NOT NULL,
    useful integer
);

CREATE TABLE IF NOT EXISTS  public.review_like (
    review_id integer NOT NULL,
    user_id integer NOT NULL,
    is_like boolean
);

ALTER TABLE  public.review_like
    ADD CONSTRAINT IF NOT EXISTS review_like_pkey PRIMARY KEY (review_id,user_id);


CREATE TABLE IF NOT EXISTS DIRECTORS
(
    DIRECTOR_ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    NAME        VARCHAR(256) NOT NULL
    );


ALTER TABLE  public.film_genre
    ADD CONSTRAINT IF NOT EXISTS film_genre_pkey PRIMARY KEY (film_genre_id);


ALTER TABLE  public.film
    ADD CONSTRAINT IF NOT EXISTS film_pkey PRIMARY KEY (film_id);


ALTER TABLE  public.friendship
    ADD CONSTRAINT IF NOT EXISTS friendship_pkey PRIMARY KEY (friendship_id);


ALTER TABLE  public.genre
    ADD CONSTRAINT IF NOT EXISTS genre_pkey PRIMARY KEY (genre_id);


ALTER TABLE  public.film_like
    ADD CONSTRAINT IF NOT EXISTS like_pkey PRIMARY KEY (like_id);


ALTER TABLE public.rating
    ADD CONSTRAINT IF NOT EXISTS rating_pkey PRIMARY KEY (rating_id);


ALTER TABLE  public.USERS
    ADD CONSTRAINT IF NOT EXISTS user_pkey PRIMARY KEY (user_id);

ALTER TABLE public.film
    ADD CONSTRAINT IF NOT EXISTS fk1 FOREIGN KEY (rating_id) REFERENCES public.rating(rating_id);



ALTER TABLE public.film_genre
    ADD CONSTRAINT IF NOT EXISTS fk1 FOREIGN KEY (film_id) REFERENCES public.film(film_id);


ALTER TABLE public.friendship
    ADD CONSTRAINT IF NOT EXISTS fk1 FOREIGN KEY (user_id) REFERENCES public.USERS(user_id);



ALTER TABLE public.film_like
    ADD CONSTRAINT IF NOT EXISTS fk1 FOREIGN KEY (film_id) REFERENCES public.film(film_id);



ALTER TABLE public.film_genre
    ADD CONSTRAINT IF NOT EXISTS fk2 FOREIGN KEY (genre_id) REFERENCES public.genre(genre_id);



ALTER TABLE public.friendship
    ADD CONSTRAINT IF NOT EXISTS fk2 FOREIGN KEY (friend_id) REFERENCES public.USERS(user_id);



ALTER TABLE public.film_like
    ADD CONSTRAINT IF NOT EXISTS fk2 FOREIGN KEY (user_id) REFERENCES public.USERS(user_id);


ALTER TABLE  public.REVIEW
    ADD CONSTRAINT IF NOT EXISTS review_pkey PRIMARY KEY (review_id);

ALTER TABLE public.REVIEW
    ADD CONSTRAINT IF NOT EXISTS fk1 FOREIGN KEY (film_id) REFERENCES public.film(film_id);

ALTER TABLE public.REVIEW
    ADD CONSTRAINT IF NOT EXISTS fk2 FOREIGN KEY (user_id) REFERENCES public.users(user_id);

ALTER TABLE public.review_like
    ADD CONSTRAINT IF NOT EXISTS fk1 FOREIGN KEY (review_id) REFERENCES public.review(review_id);

ALTER TABLE public.review_like
    ADD CONSTRAINT IF NOT EXISTS fk2 FOREIGN KEY (user_id) REFERENCES public.users(user_id);

CREATE TABLE IF NOT EXISTS DIRECTOR_FILMS
(
    DIRECTOR_ID BIGINT NOT NULL,
    FILM_ID     BIGINT NOT NULL,
    CONSTRAINT FK_DIRECTOR_FILMS1 FOREIGN KEY (DIRECTOR_ID) REFERENCES DIRECTORS (DIRECTOR_ID) ON DELETE CASCADE,
    CONSTRAINT FK_DIRECTOR_FILMS2 FOREIGN KEY (FILM_ID) REFERENCES FILM (FILM_ID) ON DELETE CASCADE
);

