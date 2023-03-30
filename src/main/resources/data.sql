MERGE INTO Genre KEY (id)
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');

MERGE INTO Rating KEY (id)
    VALUES (1, 'G'),
           (2, 'PG'),
           (3, 'PG13'),
           (4, 'R'),
           (5, 'NC17');

MERGE INTO FriendshipStatus KEY (id)
    VALUES (0, 'Запрошено'),
           (1, 'Принято');


