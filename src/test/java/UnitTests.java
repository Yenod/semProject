import com.napier.sem.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class UnitTests
{
    private ResultSet resultSet;
    private PopulationReporter reporter;
    private PreparedStatement statement;
    private final PrintStream systemOut = System.out;
    private PrintStream testOut;

    @BeforeEach
    void setUp() throws SQLException
    {
        Connection connection = mock(Connection.class);
        statement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);
        testOut = new PrintStream(new ByteArrayOutputStream());

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);

        reporter = new PopulationReporter(connection);
    }

    @AfterEach
    void cleanUp()
    {
        System.setOut(systemOut);
    }

    @Test
    void countryReportTest() throws SQLException
    {
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("Code")).thenReturn("USA");
        when(resultSet.getString("Name")).thenReturn("United States");
        when(resultSet.getString("Continent")).thenReturn("North America");
        when(resultSet.getString("Region")).thenReturn("North America");
        when(resultSet.getInt("Population")).thenReturn(278357000);
        when(resultSet.getString("Capital")).thenReturn("Washington");

        System.setOut(testOut);

        reporter.countryReport();

        assertTrue(testOut.toString().contains("USA"));
        assertTrue(testOut.toString().contains("United States"));
        assertTrue(testOut.toString().contains("North America"));
        assertTrue(testOut.toString().contains("278357000"));
        assertTrue(testOut.toString().contains("Washington"));
    }

    @Test
    void cityReportTest() throws SQLException
    {
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("Name")).thenReturn("Edinburgh");
        when(resultSet.getString("Country")).thenReturn("United Kingdom");
        when(resultSet.getString("District")).thenReturn("Scotland");
        when(resultSet.getInt("Population")).thenReturn(450180);

        System.setOut(testOut);

        reporter.cityReport("cities");

        String report = testOut.toString();
        assertTrue(report.contains("Edinburgh"));
        assertTrue(report.contains("United Kingdom"));
        assertTrue(report.contains("Scotland"));
        assertTrue(report.contains("450180"));
    }

    @Test
    void capitalReportTest() throws SQLException
    {
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("Name")).thenReturn("London");
        when(resultSet.getString("Country")).thenReturn("United Kingdom");
        when(resultSet.getString("District")).thenReturn("England");
        when(resultSet.getInt("Population")).thenReturn(7285000);

        System.setOut(testOut);

        reporter.cityReport("capitals");

        String report = testOut.toString();
        assertTrue(report.contains("London"));
        assertTrue(report.contains("United Kingdom"));
        assertTrue(report.contains("7285000"));
    }

    @Test
    void populationReportWorldTest() throws SQLException
    {
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("Name")).thenReturn("World");
        when(resultSet.getLong("TotalPopulation")).thenReturn(1000L);
        when(resultSet.getLong("UrbanPopulation")).thenReturn(200L);

        System.setOut(testOut);

        reporter.populationReport("World");

        String report = testOut.toString();
        assertTrue(report.contains("World"));
        assertTrue(report.contains("1,000"));
        assertTrue(report.contains("60)"));
        assertTrue(report.contains("40"));
        assertTrue(report.contains("%"));
    }

    @Test
    void populationReportContinentTest() throws SQLException
    {
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("Name")).thenReturn("Europe");
        when(resultSet.getLong("TotalPopulation")).thenReturn(1000L);
        when(resultSet.getLong("UrbanPopulation")).thenReturn(200L);

        System.setOut(testOut);

        reporter.populationReport("continent", "Europe");

        String report = testOut.toString();
        verify(statement).setString(3, "Europe");
        assertTrue(report.contains("Europe"));
        assertTrue(report.contains("1,000"));
        assertTrue(report.contains("60)"));
        assertTrue(report.contains("40"));
        assertTrue(report.contains("%"));
    }

    @Test
    void languageReportTest() throws SQLException
    {
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("Language")).thenReturn("English");
        when(resultSet.getLong("Speakers")).thenReturn(200L);
        when(resultSet.getLong("TotalPopulation")).thenReturn(1000L);

        System.setOut(testOut);

        reporter.languageReport("English");

        String report = testOut.toString();
        verify(statement).setString(anyInt(), "English");
        assertTrue(report.contains("English"));
        assertTrue(report.contains("200"));
        assertTrue(report.contains("1,000"));
        assertTrue(report.contains("20"));
        assertTrue(report.contains("%"));
    }
}