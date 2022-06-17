
-- заполняем таблицу рейтингов

insert into MPAA values ( 0,'G' );
insert into MPAA values ( 1,'PG' );
insert into MPAA values ( 2,'PG13' );
insert into MPAA values ( 3,'R' );
insert into MPAA values ( 4,'NC17' );
-- заполняем таблицу фильмов
INSERT INTO films (name, description, release_date, duration, mpaa_id) VALUES ('Побег из Шоушенка', 'Молодого финансиста Энди Дюфрейна подозревают в убийстве, которого он не совершал. Несмотря на это его приговаривают к пожизненному заключению в тюрьме, из которой ещё никому не удавалось сбежать.', '2022-10-10', 100, 1);
INSERT INTO films (name, description, release_date, duration, mpaa_id) VALUES ('Крёстный отец', 'Первая часть криминальной саги о сицилийской мафиозной семье Корлеоне, которая обладает огромным авторитетом в Нью-Йорке.', '2022-10-11', 60, 2);
INSERT INTO films (name, description, release_date, duration, mpaa_id) VALUES ('Тёмный рыцарь', 'Городу в очередной раз требуется герой, и им традиционно становится Бэтмен, задача которого — ликвидировать преступника по кличке Джокер.', '2022-10-13', 60, 3);

-- заполняем таблицу жанров
insert into GENRES ( NAME)  VALUES( 'COMEDY');
insert into GENRES ( NAME)  VALUES( 'DRAMA');
insert into GENRES ( NAME)  VALUES( 'CARTOON');
insert into GENRES ( NAME)  VALUES( 'THRILLER');
insert into GENRES ( NAME)  VALUES( 'DOCUMENTARY');
insert into GENRES ( NAME)  VALUES( 'ACTION');

-- заполняем таблицу жанров
insert into FILMS_GENRES values ( 1,1 );
insert into FILMS_GENRES values ( 1,2 );

insert into FILMS_GENRES values ( 3,3 );
insert into FILMS_GENRES values ( 3,5 );
