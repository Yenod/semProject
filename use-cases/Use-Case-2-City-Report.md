# USE CASE: 2 Report on all cities ordered by population descending up to N supplied by user
## CHARACTERISTIC INFORMATION
### Goal in context
Produce a report on all the cities in the world ordered by population from largest to smallest. The user can optionally 
supply a limit on the number displayed. The report should have columns: City name, Country, District and Population.
### Scope
Population data reporting system
### Level
Primary Task
### Preconditions
- The application is running and connected to the database.
- The console output is available to user.
- The user supplies a valid number as a limit.
### Success Condition
- The system successfully produces the report.
### Failed Condition
- The system is unable to connect to the database.
- The system retrieves incorrect data.
- The application terminates before displaying output.
### Primary Actor
- Data Analyst
### Trigger
The application is launched with the report hard-coded or the user selects the report from a menu.
## MAIN SUCCESS SCENARIO
1. Application launches and connects to the database.
2. User selects the corresponding report and supplies a limit N.
3. System queries the database for the necessary data and formats the results.
4. System displays the ordered list of cities.
5. System terminates and disconnects from the database.
## EXTENSION 
**Database connection fails**: System retries connection or displays “Unable to connect to database.”<br> 
**SQL query fails**: System logs the error.<br>
**User enters invalid value for N (e.g., negative or non-numeric)**: System displays an error message and 
requests valid input.<br>
## SCHEDULE
31/07/26
