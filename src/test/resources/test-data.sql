
INSERT INTO player(id, username, password) VALUES
(1, 'testuser1', 'password1'),
(2, 'testuser2', 'password2'),
(3, 'testuser3', 'password3')
;

INSERT INTO game(id, name, creation_date, host, guest) VALUES
(1, 'testuser1s game', '2020-05-01T00:00:00Z', 1, NULL),
(2, 'testuser2s game', '2020-05-01T01:00:00Z', 2, NULL),
(3, 'testuser3s game', '2020-05-01T02:00:00Z', 3, NULL),
(4, 'testuser1s game', '2020-05-01T03:00:00Z', 1, 2),
(5, 'testuser2s game', '2020-05-01T04:00:00Z', 2, 3)
;
