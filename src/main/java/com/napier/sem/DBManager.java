package com.napier.sem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages a connection to a MySQL world database.
 *
 * <p>The manager attempts to establish a database connection when created. It
 * implements {@link AutoCloseable}, so will automatically close the connection when destroyed.</p>
 */
public class DBManager implements AutoCloseable
{
    /** Connection to the database */
    private Connection connection = null;

    /**
     * @return the active database connection
     */
    public Connection getConnection() { return connection; }

    /**
     * Constructor loads the MySQL driver and connects to the world database.
     *
     * @param location database host and port
     * @param delay delay in milliseconds between each connection attempt
     */
    public DBManager(String location, int delay)
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load Database driver
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        // Connection to the database
        int retries = 10;
        for (int i = 0; i < retries; ++i)
        {
            System.out.println("Connecting to database...");
            try
            {
                // Wait a bit for db to start
                Thread.sleep(delay);
                this.connection = DriverManager.getConnection("jdbc:mysql://" + location
                                + "/world?allowPublicKeyRetrieval=true&useSSL=false",
                            "root", "example");
                System.out.println("Successfully connected");
                break;
            }
            catch (SQLException sqle)
            {
                System.out.println("Failed to connect to database attempt " + i);
                System.out.println(sqle.getMessage());
            }
            catch (InterruptedException ie)
            {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Closes the managed database connection if it exists.
     * Automatically called when the object is garbage collected.
     */
    @Override
    public void close()
    {
        if (connection != null)
        {
            try { connection.close(); }
            catch (Exception e) { System.out.println("Error closing connection to database"); }
        }
    }

}
