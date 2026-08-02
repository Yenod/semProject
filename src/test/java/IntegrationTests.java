import com.napier.sem.*;
import org.junit.jupiter.api.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link App}.
 */
public class IntegrationTests
{
    private static DBManager database;
    private static Connection connection;
    private static PopulationReporter reporter;
    private static final PrintStream systemOut = System.out;
    private ByteArrayOutputStream outputBuffer;
    private PrintStream testOut;

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
        if (database != null) { database.close(); }
    }

    /**
     * Creates a buffer to capture report output.
     */
    @BeforeEach
    void setUp()
    {
        outputBuffer = new ByteArrayOutputStream();
        testOut = new PrintStream(outputBuffer);
    }

    @AfterEach
    void cleanUp()
    {
        System.setOut(systemOut);
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
        System.setOut(testOut);
        reporter.countryReport();

        String report = outputBuffer.toString();
        assertTrue(report.toLowerCase().contains("china"));
        assertTrue(report.toLowerCase().contains("population"));
        assertTrue(report.matches("(?s).*\\d.*")); //regex for contains number
    }

    @Test
    void cityReportTest()
    {
        System.setOut(testOut);
        reporter.cityReport("Cities", 5);

        String report = outputBuffer.toString();
        assertTrue(report.toLowerCase().contains("country"));
        assertTrue(report.toLowerCase().contains("district"));
        assertTrue(report.toLowerCase().contains("population"));
        assertTrue(report.matches("(?s).*\\d.*")); //regex for contains number
    }

    @Test
    void capitalReportTest()
    {
        System.setOut(testOut);
        reporter.cityReport("Capitals");

        String report = outputBuffer.toString();
        assertTrue(report.toLowerCase().contains("london"));
        assertTrue(report.toLowerCase().contains("country"));
        assertTrue(report.toLowerCase().contains("population"));
        assertTrue(report.matches("(?s).*\\d.*")); //regex for contains number
    }

    @Test
    void populationReportAllCountryTest()
    {
        System.setOut(testOut);
        reporter.populationReport("Country");

        String report = outputBuffer.toString();
        assertTrue(report.toLowerCase().contains("china"));
        assertTrue(report.toLowerCase().contains("%"));
        assertTrue(report.matches("(?s).*\\d.*")); //regex for contains number
    }

    @Test
    void populationReportWorldTest()
    {
        System.setOut(testOut);
        reporter.populationReport("World");

        String report = outputBuffer.toString();
        assertTrue(report.toLowerCase().contains("population"));
        assertTrue(report.toLowerCase().contains("%"));
        assertTrue(report.matches("(?s).*\\d.*")); //regex for contains number
    }

    @Test
    void populationReportRegionTest()
    {
        System.setOut(testOut);
        reporter.populationReport("Region", "Polynesia");

        String report = outputBuffer.toString();
        assertTrue(report.toLowerCase().contains("polynesia"));
        assertTrue(report.toLowerCase().contains("population"));
        assertTrue(report.toLowerCase().contains("%"));
        assertTrue(report.matches("(?s).*\\d.*")); //regex for contains number
    }
}