INSERT INTO locations (latitude, longitude) VALUES (33.046680, -96.820804);
INSERT INTO locations (latitude, longitude) VALUES (33.049144, -96.819559);
INSERT INTO locations (latitude, longitude) VALUES (33.050529, -96.814796);
INSERT INTO locations (latitude, longitude) VALUES (33.051500, -96.817488); # private
# for events
INSERT INTO locations (latitude, longitude) VALUES (33.044719, -96.814066);
INSERT INTO locations (latitude, longitude) VALUES (33.051509, -96.817027); # private

INSERT INTO users (username, password, email, location_id, last_login_time, last_location_time, last_message_time, blurb, private) VALUES ("stateFromJakeFarm", PASSWORD('pwd'), "sfjf@test.com", 1, 1426398620000, 1426398620000, 1426398620000, "I'm wearing khakis", false);
INSERT INTO users (username, password, email, location_id, last_login_time, last_location_time, last_message_time, blurb, private) VALUES ("buddy", PASSWORD('pwd'), "buddy@test.com", 2, 1426398630000, 1426398630000, 1426398630000, "woof woof", false);
INSERT INTO users (username, password, email, location_id, last_login_time, last_location_time, last_message_time, blurb, private) VALUES ("papaya", PASSWORD('pwd'), "papaya@test.com", 3, 1426398640000, 1426398640000, 1426398640000, "ayy lmao", false);
INSERT INTO users (username, password, email, location_id, last_login_time, last_location_time, last_message_time, blurb, private) VALUES ("privateUser", PASSWORD('pwd'), "private@test.com", 4, 1426398620000, 1426398620000, 1426398620000, "you shouldn't being seeing this user", true);

INSERT INTO events (host_id, title, location_id, start_time, end_time, description, public) VALUES (2, "Smashfest 2015", 5, 142689862000, 142699862000, "Everyone except socially awkward people are welcome", true);
INSERT INTO events (host_id, title, location_id, start_time, end_time, description, public) VALUES (2, "private event", 6, 142689862000, 142699862000, "you shouldnt be seeing this", true);

INSERT INTO event_users VALUES (1, 1);

INSERT INTO user_games VALUES (2, 1);

INSERT INTO event_games VALUES (1, 2);
INSERT INTO event_games VALUES (1, 4);

INSERT INTO friends VALUES (2, 1);
INSERT INTO friends VALUES (1, 3);

INSERT INTO notifications (sender_id, receiver_id, message, send_time, type) VALUES (3, 2, "Welcome to Smash Bros. Locator", 1426398621000, 1);

INSERT INTO conversations VALUES (1);

INSERT INTO messages (conversation_id, sender_id, sent_time, body) VALUES (1, 1, 1426399999999, "are you down to smash ;)");
INSERT INTO messages (conversation_id, sender_id, sent_time, body) VALUES (1, 2, 1426400000000, "my mom told me not to talk to strangers");
INSERT INTO messages (conversation_id, sender_id, sent_time, body) VALUES (1, 1, 1426400001000, "i'm so fundamentally lonely");

INSERT INTO conversation_users VALUES (1, 1);
INSERT INTO conversation_users VALUES (2, 1);