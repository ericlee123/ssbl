# Smash Locator (SSBL)
Super Smash Bros. Locator is an application designed to help people coordinate smash sessions. It's authors Eric Lee and Ashwin Madavan enjoy to smash other people in their free time. While they usually smash large groups of friends, they are not opposed to smashing strangers. In 2015, Ashwin Madavan won the Best Smasher of the Year when he rekt Eric's backend!

## To Do Server
Users Hibernate Initialization
1) You
	- Everything
2) Friend
	- Same as stranger for name?	
3) Stranger (public)
	- Friends
	- Games
	- Events
	- Username
	- Blurb
	- Last Login Time
	- Last Location Time

Users Model Update
- Private (Whether or not they appear on the map)

## Changelog
### 0.0.2 (2-22-2015) [ashwin]
- Minor bug fixes to repository layer
- Enabled ProGuard to shrink and obfuscate Android code at runtime

### 0.0.1 (2-21-2015) [ashwin]
- Created maven project and wrote base .pom files
- Refactored database.sql
- Created and tested POJOs (models)
- Configured Spring, Hibernate, Hikari CP, MySQL, Jackson, etc.
- Validated schema using Hibernate
- Created and tested basic repository layer
- Created working template Android project
- Created database.test.sql to inject test data into database
- Updated README.md

[ashwin]:http://github.com/ashwin153
[eric]:https://github.com/ericlee123
