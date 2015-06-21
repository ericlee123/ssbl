# Smash Locator (SSBL)
Super Smash Bros. Locator is an application designed to help people coordinate smash sessions. It's authors Eric Lee and Ashwin Madavan enjoy to smash other people in their free time. While they usually smash large groups of friends, they are not opposed to smashing strangers.

## Server Changelog
### 1.0.0 (6-20-2015) [eric]
- Decoupled client to server connection through redirect URL
- Declared to be a stable, working version

### 0.1.1 (3-20-2015) [ashwin]
- Implemented basic exception handling
	- 401 = Unauthorized
	- 500 = Database error
	- 509 = Data conflict

### 0.1.0 (3-20-2015) [ashwin]
- Implemented lazy loading of properties
	- Required bytecode manipulation and extensive debugging
- Moved Hibernate initialization code to service layer
- Refactored pom files
- Added plugins to automatically build output files
- Added pretty printing to JSON output

### 0.0.3 (3-14-2015) [ashwin]
- Added messaging, auth, and search services
- Updated POJOs
- Created messaging controller

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
