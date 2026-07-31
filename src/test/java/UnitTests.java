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

    @BeforeEach
    void setUp() throws SQLException
    {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);

        reporter = new PopulationReporter(connection);
    }

     @Test
    void countryReportTest() throws SQLException
    {
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("Code")).thenReturn("USA");
        when(resultSet.getString("Name")).thenReturn("United States");
        when(resultSet.getString("Continent")).thenReturn("North America");
        when(resultSet.getString("Region")).thenReturn("North America");
        when(resultSet.getInt("Population")).thenReturn(324000000);
        when(resultSet.getString("Capital")).thenReturn("Washington");

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        reporter.countryReport();

        assertTrue(output.toString().contains("USA"));
        assertTrue(output.toString().contains("United States"));
        assertTrue(output.toString().contains("North America"));
        assertTrue(output.toString().contains("324000000"));
        assertTrue(output.toString().contains("Washington"));
    }


}