
--
drop table IF EXISTS FILM;

drop table IF EXISTS FILM_GENRE;

drop table IF EXISTS FILM_LIKE;

drop table IF EXISTS FRIENDSHIP;

drop table IF EXISTS GENRE;

drop table IF EXISTS RATING;

drop table IF EXISTS USERS;

 CREATE TABLE IF NOT EXISTS public.film (
    film_id integer AUTO_INCREMENT NOT NULL,
    name character varying NOT NULL,
    description character varying,
    release_date date,
    duration integer,
    rating_id integer
) ;


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

-----



