package com.napier.sem;
import java.sql.*;
import java.util.Locale;
import java.util.Collections;

import static java.lang.Math.*;

/**
 * Generates and prints reports on population statistics using data from a database.
 */
public class PopulationReporter
{
    /** Database connection used to retrieve report data. */
    private final Connection connection;

    /**
     * Creates a {@link PopulationReporter} with the supplied database connection.
     *
     * @param connection connection to the world database
     */
    public PopulationReporter (Connection connection) { this.connection = connection; }

    /**
     * Prints speaker counts and percentages for each requested language.
     *
     * @param languages one or more languages to include in the report
     */
    public void languageReport(String... languages)
    {
        String placeholders = String.join(", ", Collections.nCopies(languages.length, "?"));
        String query = "SELECT Language, SUM(Population * Percentage / 100) AS Speakers, "
                + "(SELECT SUM(Population) FROM country) AS TotalPopulation "
                + "FROM countrylanguage JOIN country ON Code = CountryCode "
                + "WHERE Language IN (" + placeholders + ") GROUP BY Language ORDER BY Speakers DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query))
        {
            for (int i = 0; i < languages.length; i++)
            {
                stmt.setString(i + 1, languages[i]); // Placeholders start at index 1
            }

            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next())
                {
                    String language = rs.getString("Language");
                    long speakers = rs.getLong("Speakers");
                    long totalPopulation = rs.getLong("TotalPopulation");
                    if (totalPopulation == 0) return;
                    double percentage = (double) speakers / totalPopulation * 100;
                    System.out.printf(
                            "%nLanguage = %s, Speakers = %d, Percentage = %.2f%%",
                            language, speakers, percentage);
                }
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Prints a population report for all geopolitical entities of the specified type.
     *
     * <p>Overload for report types that do not require a name; {@code world} or {@code country}.</p>
     *
     * @param type the report type: {@code world}, {@code continent},
     *             {@code region}, {@code country}, {@code district}, or
     *             {@code city}
     */
    public void populationReport(String type)
    {
        populationReport(type, null);
    }

    /**
     * Prints population data for the requested geopolitical entity type of the given name.
     *
     * <p>{@code type} of {@code world} does not accept a {@code name}. {@code district}
     * and {@code city} cannot calculate urban/rural population distribution.</p>
     *
     * @param type the type of entity: {@code world}, {@code continent}, {@code region},
     * {@code country}, {@code district}, or {@code city}
     *
     * @param name the name of the entity
     */
    public void populationReport(String type, String name)
    {
        String typeLower = type.toLowerCase(Locale.ROOT);
        String query;
        int placeholders = 0; //number of placeholders in the query

        switch (typeLower) {
            case "world":
                query = "SELECT 'World' AS Name, "
                        + "(SELECT SUM(Population) FROM country) AS TotalPopulation, "
                        + "(SELECT SUM(Population) FROM city) AS UrbanPopulation";
                break;
            case "continent":
                query = "SELECT ? AS Name, "
                        + "(SELECT SUM(Population) FROM country WHERE Continent = ?) AS TotalPopulation, "
                        + "(SELECT SUM(city.Population) FROM city "
                        + "JOIN country ON city.CountryCode = country.Code "
                        + "WHERE country.Continent = ?) AS UrbanPopulation";
                placeholders = 3;
                break;
            case "region":
                query = "SELECT ? AS Name, "
                        + "(SELECT SUM(Population) FROM country WHERE Region = ?) AS TotalPopulation, "
                        + "(SELECT SUM(city.Population) FROM city "
                        + "JOIN country ON city.CountryCode = country.Code "
                        + "WHERE country.Region = ?) AS UrbanPopulation";
                placeholders = 3;
                break;
            case "country":
                query = "SELECT country.Name AS Name, "
                        + "country.Population AS TotalPopulation, "
                        + "SUM(COALESCE(city.Population, 0)) AS UrbanPopulation "
                        + "FROM country LEFT JOIN city ON city.CountryCode = country.Code ";

                if (name != null) {
                    query += "WHERE country.Name = ? ";
                    placeholders = 1;
                }
                query += "GROUP BY country.Code, country.Name, country.Population ORDER BY TotalPopulation DESC";
                break;
            case "district":
                query = "SELECT ? AS Name, SUM(Population) AS TotalPopulation FROM city WHERE District = ?";
                placeholders = 2;
                break;
            case "city":
                query = "SELECT ID, Name, Population AS TotalPopulation FROM city WHERE Name = ? ORDER BY Population DESC";
                placeholders = 1;
                break;
            default:
                return;
        }

        try (PreparedStatement stmt = connection.prepareStatement(query))
        {
            for (int i = 1; i <= placeholders; i++) stmt.setString(i, name);

            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next())
                {
                    String entityName = rs.getString("Name");
                    long totalPopulation = rs.getLong("TotalPopulation");

                    System.out.printf("%nName: %s | Total Population: %,d", entityName, totalPopulation);

                    if (!( typeLower.equals("district") || typeLower.equals("city") ))
                    {
                        long urbanPopulation = rs.getLong("UrbanPopulation");
                        long ruralPopulation = max(totalPopulation - urbanPopulation, 0);

                        double urbanPercent = totalPopulation > 0 ?
                                ((double) urbanPopulation / totalPopulation) * 100 : 0.0;
                        double ruralPercent = totalPopulation > 0 ?
                                ((double) ruralPopulation / totalPopulation) * 100 : 0.0;

                        System.out.printf(" | Urban Population: %,d (%.2f%%) | Rural Population: %,d (%.2f%%)",
                                urbanPopulation, urbanPercent, ruralPopulation, ruralPercent);
                    }
                }
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Prints all countries in descending order of population.
     */
    public void countryReport()
    {
        String query = "SELECT country.Code, country.Name, country.Continent, country.Region, "
                     + "country.Population, city.Name AS Capital FROM country LEFT JOIN city ON country.Code = city.CountryCode "
                     + "AND city.ID = country.Capital ORDER BY country.Population DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query))
        {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Country country = new Country(
                            rs.getString("Code"),
                            rs.getString("Name"),
                            rs.getString("Continent"),
                            rs.getString("Region"),
                            rs.getLong("Population"),
                            rs.getString("Capital")
                    );
                    System.out.println(country);
                }
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Prints cities in descending order of population.
     *
     * @param capitals whether to report on {@code cities} or {@code capitals}
     */
    public void cityReport(String capitals)
    {
        cityReport(capitals, null);
    }

    /**
     * Prints cities in descending order of population up to a given limit.
     *
     * @param capitals whether to report on {@code cities} or {@code capitals}
     *
     * @param limit the maximum number of cities to print
     */
    public void cityReport(String capitals, Integer limit)
    {
        String query = "SELECT city.Name, country.Name AS Country, city.District, city.Population "
                     + "FROM city JOIN country ON city.CountryCode = country.Code ";

        if (capitals.equalsIgnoreCase("capitals") || capitals.equalsIgnoreCase("capital"))
        {
            query += "WHERE city.ID = country.Capital ";

        }

        query += "ORDER BY city.Population DESC";
        if (limit != null) query += " LIMIT " + limit;

        try (PreparedStatement stmt = connection.prepareStatement(query))
        {
            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next())
                {
                    City city = new City(
                            rs.getString("Name"),
                            rs.getString("Country"),
                            rs.getString("District"),
                            rs.getInt("Population")
                    );
                    if (capitals.equalsIgnoreCase("capitals") || capitals.equalsIgnoreCase("capital"))
                    {
                        System.out.println("Name = " + city.name()
                                + ", Country = " + city.country()
                                + ", Population = " + city.population());
                    }
                    else
                    {
                        System.out.println(city);
                    }

                }
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }
}
