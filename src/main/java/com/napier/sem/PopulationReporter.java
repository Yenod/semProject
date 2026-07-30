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
        String query = "SELECT * FROM country";

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

    }

    public void cityReport(String capitals, int limit)
    {

    }
}
