# Smash Locator (SSBL)
Super Smash Bros. Locator is an application designed to help people coordinate smash sessions. It's authors Eric Lee and Ashwin Madavan enjoy to smash other people in their free time. While they usually smash large groups of friends, they are not opposed to smashing strangers. In 2015, Ashwin Madavan won the Best Smasher of the Year when he rekt Eric's backend!

## Server Changelog
### 0.1.0 (3-20-2014) [ashwin]
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
