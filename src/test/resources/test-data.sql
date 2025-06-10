
INSERT INTO player(id, username, password) VALUES
(1, 'testuser1', '$2a$10$eP3FFlxW0RYWE9lO2Lb/ZeD8aswR.g07IBxTmAabZJYP9vOi/6a.e'), -- password1
(2, 'testuser2', '$2a$10$4RPXI3HNETyZ04X4ko.KcuaoG6vSbifrTgWSzg7Yf7vFf9kipwn..'), -- password2
(3, 'testuser3', '$2a$10$Hv0G6KNRtt7EO.pnPNxdr.kpxoaDEH4JJxeeH1P9pSKlylI4HhN1e') -- password3
;

INSERT INTO game(id, name, creation_date, host, guest) VALUES
(1, 'testuser1s game', '2020-05-01T00:00:00Z', 1, NULL),
(2, 'testuser2s game', '2020-05-01T01:00:00Z', 2, NULL),
(3, 'testuser3s game', '2020-05-01T02:00:00Z', 3, NULL),
(4, 'testuser1s game', '2020-05-01T03:00:00Z', 1, 2),
(5, 'testuser2s game', '2020-05-01T04:00:00Z', 2, 3)
;
