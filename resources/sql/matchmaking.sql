SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `player`;
CREATE TABLE `player`(
	id INTEGER NOT NULL AUTO_INCREMENT,
	username VARCHAR(128) NOT NULL UNIQUE,
	password VARCHAR(255) NOT NULL,
    CONSTRAINT player_pk PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `game`;
CREATE TABLE `game`(
	id INTEGER NOT NULL AUTO_INCREMENT,
	name VARCHAR(128) NOT NULL,
	creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	join_code VARCHAR(128) NOT NULL,
	
	host INTEGER NOT NULL,
	guest INTEGER DEFAULT NULL,
	
    CONSTRAINT game_pk PRIMARY KEY(`id`),
	CONSTRAINT game_host_fk FOREIGN KEY(`host`) REFERENCES `player`(`id`),
	CONSTRAINT game_guest_fk FOREIGN KEY(`guest`) REFERENCES `player`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;


INSERT INTO player(id, username, password) VALUES
(1, 'user1', 'password1'),
(2, 'user2', 'password2'),
(3, 'user3', 'password3')
;

INSERT INTO game(id, name, creation_date, host, guest) VALUES
(1, 'user1s game', now(), 1, NULL),
(2, 'user2s game', now(), 2, NULL),
(3, 'user3s game', now(), 3, NULL),
(4, 'user1s game', now(), 1, 2),
(5, 'user2s game', now(), 2, 3)
;
