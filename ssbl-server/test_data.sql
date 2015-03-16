INSERT INTO locations (latitude, longitude) VALUES (33.049126, -96.819387);
INSERT INTO locations (latitude, longitude) VALUES (33.052426, -96.815074);

INSERT INTO users (username, password, email, location_id, last_login_time, last_location_time, blurb) VALUES ("timeline62x", PASSWORD('pwd'), "hunnymustardapps@gmail.com", 1, 1426398620000, 1426398620000, "I am the creator");
INSERT INTO users (username, password, email, location_id, last_login_time, last_location_time, blurb) VALUES ("buddy", PASSWORD('pwd'), "woof@woof.com", 2, 1426398620000, 1426398620000, "woof woof");

INSERT INTO conversations VALUES (1);

INSERT INTO messages (conversation_id, sender_id, sent_time, body) VALUES (1, 2, 1426398620000, "i am a dog");
INSERT INTO messages (conversation_id, sender_id, sent_time, body) VALUES (1, 1, 1426398620010, "have dinner with me");

INSERT INTO conversation_users VALUES (1, 1);
INSERT INTO conversation_users VALUES (2, 1);