# USE CASE: 5 Report on number speakers of world languages
## CHARACTERISTIC INFORMATION
### Goal in context
Produce a report on the number of speakers of a list of languages specified by the user, ordered from most to least
commonly spoken. The report should have columns: Name of language, Total number of speakers and 
Percentage of world population that speaks the language.
### Scope
Population data reporting system
### Level
Primary Task
### Preconditions
- The application is running and connected to the database.
- The console output is available to user.
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
2. User selects the corresponding report.
3. System queries the database for the necessary data and formats the results.
4. System displays the ordered list of countries.
5. System terminates and disconnects from the database.
## EXTENSION 
**Database connection fails**: System retries connection or displays “Unable to connect to database.”<br> 
**SQL query fails**: System logs the error.<br>
## SCHEDULE
31/07/26
