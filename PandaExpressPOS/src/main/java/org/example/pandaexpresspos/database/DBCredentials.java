package org.example.pandaexpresspos.database;

/*
Note: it's definitely bad practice to be hardcoding our credentials
in the source code like this, but I'm doing so for simplicity for now.
We can always change this later if it's a big deal
 */
public class DBCredentials {
    public static final String username = "team_8p";
    public static final String passwd = "Electabuzz8!";

    public static final String dbName = username + "_db";
    public static final String dbConnectionString
            = "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + dbName;
}
