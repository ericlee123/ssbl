INSERT INTO locations (latitude, longitude) VALUES (37.324004, -122.058499);
INSERT INTO locations (latitude, longitude) VALUES (30.286144, -97.7368801);

INSERT INTO users (username, password, email, location_id, refresh_time, blurb) VALUES ("ashwin", "p0", "ashwin@test.com", 1, 0000000002, "test account blurb #1");
INSERT INTO user_games (user_id, game_id) VALUES (LAST_INSERT_ID(), 0);
INSERT INTO user_games (user_id, game_id) VALUES (LAST_INSERT_ID(), 3);

INSERT INTO users (username, password, email, location_id, refresh_time, blurb) VALUES ("eric", "p1", "eric@test.com", 2, 0000000001, "test account blurb #2");
INSERT INTO user_games (user_id, game_id) VALUES (LAST_INSERT_ID(), 0);
INSERT INTO user_games (user_id, game_id) VALUES (LAST_INSERT_ID(), 2);

INSERT INTO events (host_id, title, location_id, start_time, end_time, description, public) VALUES (1, "smash fest", 1, 0000000003, 0000000004, "smash long and hard", true);
INSERT INTO event_games (event_id, game_id) VALUES (LAST_INSERT_ID(), 0);
INSERT INTO event_games (event_id, game_id) VALUES (LAST_INSERT_ID(), 3);

