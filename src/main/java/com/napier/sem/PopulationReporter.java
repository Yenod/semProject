package com.napier.sem;

import java.sql.*;
import java.util.Locale;

import static java.lang.Math.*;

public class PopulationReporter
{
    private final Connection connection;

    public PopulationReporter (Connection connection) { this.connection = connection; }

    public void languageReport(String... args)
    {

    }

    public void populationReport(String type)
    {
        populationReport(type, null);
    }

    public void populationReport(String type, String name)
    {
        String typeLower = type.toLowerCase(Locale.ROOT);
        String query;
        int placeholders = 0;

        switch (typeLower)
        {
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
                        + "FROM country "
                        + "LEFT JOIN city ON city.CountryCode = country.Code ";

                if (name != null)
                {
                    query += "WHERE country.Name = ? ";
                    placeholders = 1;
                }

                query += "GROUP BY country.Code, country.Name, country.Population";
                break;
            case "district":
                query = "SELECT ? AS Name, "
                        + "SUM(Population) AS TotalPopulation, "
                        + "SUM(Population) AS UrbanPopulation "
                        + "FROM city WHERE District = ?";
                placeholders = 2;
                break;
            case "city":
                query = "SELECT ? AS Name, "
                        + "SUM(Population) AS TotalPopulation, "
                        + "SUM(Population) AS UrbanPopulation "
                        + "FROM city WHERE Name = ?";
                placeholders = 2;
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
                    long urbanPopulation = rs.getLong("UrbanPopulation");
                    long ruralPopulation = max(totalPopulation - urbanPopulation, 0);

                    PopulationRecord record = new PopulationRecord(
                            entityName,
                            totalPopulation,
                            urbanPopulation,
                            ruralPopulation
                    );

                    double urbanPercent = totalPopulation > 0 ?
                            ((double) urbanPopulation / totalPopulation) * 100 : 0.0;
                    double ruralPercent = totalPopulation > 0 ?
                            ((double) ruralPopulation / totalPopulation) * 100 : 0.0;

                    System.out.printf("Name: %s | Total Population: %,d", record.name(), record.totalPopulation());

                    if (!typeLower.equals("district") && !typeLower.equals("city"))
                    {
                        System.out.printf(" | Urban Population: %,d (%.2f%%) | Rural Population: %,d (%.2f%%)%n",
                            record.urbanPopulation(), urbanPercent, record.ruralPopulation(), ruralPercent);
                    }
                }
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public void countryReport()
    {
        String query = "SELECT * FROM country ORDER BY Population DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query))
        {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Country country = new Country(
                            rs.getString("Code"),
                            rs.getString("Name"),
                            rs.getString("Continent"),
                            rs.getString("Region"),
                            rs.getInt("Population"),
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

    public void cityReport(String capitals)
    {
        cityReport(capitals, null);
    }

    public void cityReport(String capitals, Integer limit)
    {
        String query = "SELECT city.Name, country.Name AS Country, city.District, city.Population "
                     + "FROM city JOIN country ON city.CountryCode = country.Code ";

        if (capitals.equalsIgnoreCase("capitals") || capitals.equalsIgnoreCase("capital"))
        {
            query += "WHERE city.CountryCode = country.Code ";

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
