package com.napier.sem;

import java.sql.*;
import java.util.ArrayList;

public class PopulationReporter
{
    private Connection connection;

    public PopulationReporter (Connection connection) { this.connection = connection; }

    public void languageReport(String... args)
    {

    }

    public void populationReport(String type)
    {

    }

    public void populationReport(String type, String name)
    {

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
        String query;

        if (capitals.equalsIgnoreCase("capitals") || capitals.equalsIgnoreCase("capital"))
        {
            query = "SELECT city.Name, country.Name AS Country, city.District, city.Population "
                    + "FROM city JOIN country ON city.ID = country.Capital "
                    + "ORDER BY city.Population DESC";
        }
        else
        {
            query = "SELECT * FROM city ORDER BY Population DESC";
        }

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
                    System.out.println(city);
                }
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }
}
