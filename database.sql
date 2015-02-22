DROP DATABASE IF EXISTS `test`;
CREATE DATABASE `test`;
USE `test`;

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
	`user_id` INT AUTO_INCREMENT,
	`username` VARCHAR(40) NOT NULL,
	`password` VARCHAR(255) NOT NULL,
	`email` VARCHAR(255) NOT NULL,
	`location_id` INT,
	`refresh_time` BIGINT(14),
	`blurb` VARCHAR(255),
	PRIMARY KEY(user_id),
	UNIQUE (username),
	UNIQUE (email)
);

DROP TABLE IF EXISTS `events`;
CREATE TABLE `events` (
	`event_id` INT AUTO_INCREMENT,
	`host_id` INT NOT NULL,
	`title` VARCHAR(255) NOT NULL,
	`location_id` INT NOT NULL,
	`start_time` BIGINT(14) NOT NULL,
	`end_time` BIGINT(14), # Start is mandatory and end is optional?
	`description` VARCHAR(255),
	`public` BOOLEAN NOT NULL,
	PRIMARY KEY(event_id),
	UNIQUE (host_id)
);


# Yes, users and events will share the same table.
DROP TABLE IF EXISTS `locations`;
CREATE TABLE `locations` (
	`location_id` INT AUTO_INCREMENT,
	`latitude` DOUBLE(10, 7) NOT NULL,
	`longitude` DOUBLE(10, 7) NOT NULL,
	PRIMARY KEY(location_id)
);

DROP TABLE IF EXISTS `event_users`;
CREATE TABLE `event_users` (
	`event_id` INT NOT NULL,
	`user_id` INT NOT NULL
);

DROP TABLE IF EXISTS `user_games`;
CREATE TABLE `user_games` (	
	`user_id` INT NOT NULL,
	`game_id` INT NOT NULL
);

DROP TABLE IF EXISTS `event_games`;
CREATE TABLE `event_games` (
	`event_id` INT NOT NULL,
	`game_id` INT NOT NULL
);

DROP TABLE IF EXISTS `notifications`;
CREATE TABLE `notifications` (
	`notification_id` INT AUTO_INCREMENT,
	`sender_id` INT NOT NULL,
	`receiver_id` INT NOT NULL,
	`message` VARCHAR(255) NOT NULL,
	`send_time` BIGINT(14) NOT NULL,
	`type` INT NOT NULL,
	PRIMARY KEY(`notification_id`)
);

# Added UPDATE cascade relationships for users and events (for safety), and UPDATE
# and DELETE cascade relationships for all the other tables.
ALTER TABLE `users`
ADD FOREIGN KEY (`location_id`) REFERENCES locations(`location_id`) ON UPDATE CASCADE;

ALTER TABLE `events`
ADD FOREIGN KEY (`host_id`) REFERENCES users(`user_id`) ON UPDATE CASCADE,
ADD FOREIGN KEY (`location_id`) REFERENCES locations(`location_id`) ON UPDATE CASCADE;

ALTER TABLE `user_games`
ADD FOREIGN KEY (`user_id`) REFERENCES users(`user_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE `event_games`
ADD FOREIGN KEY (`event_id`) REFERENCES events(`event_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE `event_users`
ADD FOREIGN KEY (`event_id`) REFERENCES events(`event_id`) ON UPDATE CASCADE ON DELETE CASCADE,
ADD FOREIGN KEY (`user_id`) REFERENCES users(`user_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE `notifications`
ADD FOREIGN KEY (`sender_id`) REFERENCES users(`user_id`) ON UPDATE CASCADE ON DELETE CASCADE,
ADD FOREIGN KEY (`receiver_id`) REFERENCES users(`user_id`) ON UPDATE CASCADE ON DELETE CASCADE;
