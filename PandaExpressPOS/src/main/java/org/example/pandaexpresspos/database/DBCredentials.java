package org.example.pandaexpresspos.database;

/**
 * The DBCredentials class holds the database connection credentials
 * for the Panda Express POS system.
 *
 * <p>Note: Hardcoding credentials in source code is generally considered
 * bad practice due to security concerns. This implementation is for simplicity
 * and can be modified later as needed.</p>
 */
class DBCredentials {
    /** The username for database access */
    public static final String username = "team_8p";

    /** The password for database access */
    public static final String passwd = "Electabuzz8!";

    /** The name of the database associated with the username */
    public static final String dbName = username + "_db";

    /** The database connection string for PostgreSQL */
    public static final String dbConnectionString
            = "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + dbName;
}
