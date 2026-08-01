import com.napier.sem.*;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTests
{
    private static DBManager database;
    private static Connection connection;
    private static PopulationReporter reporter;

    @BeforeAll
    static void init()
    {
        database = new DBManager("localhost:33060", 2000);
        connection = database.getConnection();
        reporter = new PopulationReporter(connection);
    }

    @AfterAll
    static void closeDatabase()
    {
        if (database != null)
        {
            database.close();
        }
    }

    @Test
    void connectsToDatabase() throws SQLException
    {
        assertNotNull(connection);

        try (Statement statement = connection.createStatement();
             ResultSet results = statement.executeQuery("SELECT COUNT(*) FROM country"))
        {
            assertTrue(results.next());
            assertTrue(results.getInt(1) > 0);
        }
    }

    @Test
    void countryReportTest()
    {
        PrintStream systemOut = System.out;
        var testOut = new ByteArrayOutputStream();

        try
        {
            System.setOut(new PrintStream(testOut));
            reporter.countryReport();
        }
        finally
        {
            System.setOut(systemOut);
        }

        String outString = testOut.toString();

        assertTrue(outString.toLowerCase().contains("china"));
        assertTrue(outString.toLowerCase().contains("population"));
        assertTrue(outString.matches("(?s).*\\d.*")); //regex for contains number
    }

    @Test
    void cityReportTest()
    {
        PrintStream systemOut = System.out;
        var testOut = new ByteArrayOutputStream();

        try
        {
            System.setOut(new PrintStream(testOut));
            reporter.cityReport("Cities", 5);
        }
        finally
        {
            System.setOut(systemOut);
        }

        String outString = testOut.toString();

        assertTrue(outString.toLowerCase().contains("country"));
        assertTrue(outString.toLowerCase().contains("district"));
        assertTrue(outString.toLowerCase().contains("population"));
        assertTrue(outString.matches("(?s).*\\d.*")); //regex for contains number
    }

    @Test
    void capitalReportTest()
    {
        PrintStream systemOut = System.out;
        var testOut = new ByteArrayOutputStream();

        try
        {
            System.setOut(new PrintStream(testOut));
            reporter.cityReport("Captials");
        }
        finally
        {
            System.setOut(systemOut);
        }

        String outString = testOut.toString();

        assertTrue(outString.toLowerCase().contains("london"));
        assertTrue(outString.toLowerCase().contains("country"));
        assertTrue(outString.toLowerCase().contains("population"));
        assertTrue(outString.matches("(?s).*\\d.*")); //regex for contains number
    }

    @Test
    void populationReportAllCountryTest()
    {
        PrintStream systemOut = System.out;
        var testOut = new ByteArrayOutputStream();

        try
        {
            System.setOut(new PrintStream(testOut));
            reporter.populationReport("Country");
        }
        finally
        {
            System.setOut(systemOut);
        }

        String outString = testOut.toString();

        assertTrue(outString.toLowerCase().contains("china"));
        assertTrue(outString.toLowerCase().contains("%"));
        assertTrue(outString.matches("(?s).*\\d.*")); //regex for contains number
    }

    @Test
    void populationReportWorldTest()
    {
        PrintStream systemOut = System.out;
        var testOut = new ByteArrayOutputStream();

        try
        {
            System.setOut(new PrintStream(testOut));
            reporter.populationReport("World");
        }
        finally
        {
            System.setOut(systemOut);
        }

        String outString = testOut.toString();

        assertTrue(outString.toLowerCase().contains("population"));
        assertTrue(outString.toLowerCase().contains("%"));
        assertTrue(outString.matches("(?s).*\\d.*")); //regex for contains number
    }

    @Test
    void populationReportRegionTest()
    {
        PrintStream systemOut = System.out;
        var testOut = new ByteArrayOutputStream();

        try
        {
            System.setOut(new PrintStream(testOut));
            reporter.populationReport("Region", "Polynesia");
        }
        finally
        {
            System.setOut(systemOut);
        }

        String outString = testOut.toString();

        assertTrue(outString.toLowerCase().contains("tonga"));
        assertTrue(outString.toLowerCase().contains("population"));
        assertTrue(outString.toLowerCase().contains("%"));
        assertTrue(outString.matches("(?s).*\\d.*")); //regex for contains number
    }
}