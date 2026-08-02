package com.napier.sem;

import java.sql.Connection;

public class App
{
    public static void main(String[] args)
    {
        String dbLocation = "localhost:33060";
        int delay = 5000;

        if (args.length > 0) dbLocation = args[0];
        if (args.length > 1) delay = Integer.parseInt(args[1]);

        //DBManager implements auto-closeable
        try (DBManager db = new DBManager(dbLocation, delay))
        {
            Connection connection = db.getConnection();
            PopulationReporter populationReporter = new PopulationReporter(connection);

        // Desired reports go here:
            populationReporter.countryReport();
            //populationReporter.cityReport("Cities");
            //populationReporter.cityReport("Capitals");
            //populationReporter.cityReport("Cities", 5);
            //populationReporter.populationReport("Country");
            //populationReporter.populationReport("World");
            //populationReporter.populationReport("Continent", "Africa");
            //populationReporter.populationReport("Region", "Caribbean");
            //populationReporter.populationReport("Country", "Malaysia");
            //populationReporter.populationReport("District", "Henan");
            //populationReporter.populationReport("City", "Paris");
            //populationReporter.languageReport("Chinese", "English", "Spanish");
        }
    }
}