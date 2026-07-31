# USE CASE: 4 Report on population distribution of geographic entity
## CHARACTERISTIC INFORMATION
### Goal in context
Produce a report on the population distribution of: the world as a whole, each country in the world, a continent,
a region, a country, a district or a city. This is chosen by the user. The report should have columns: Name of 
geographic entity, (Its) Total population, Total population living in cities, Percentage of population living in cities,
Total population living in rural areas and Percentage of population living in rural areas.
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
