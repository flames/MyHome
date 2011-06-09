
package me.taylorkelly.myhome;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionManager {
    private static Connection conn;
        
    public static Connection initialize() {
        try {
        	if(HomeSettings.usemySQL) {
        		Class.forName("com.mysql.jdbc.Driver");
        		conn = DriverManager.getConnection(HomeSettings.mySQLconn, HomeSettings.mySQLuname, HomeSettings.mySQLpass);
        		conn.setAutoCommit(false);
        		return conn;
        	} else {
        		Class.forName("org.sqlite.JDBC");
        		conn = DriverManager.getConnection("jdbc:sqlite:" + HomeSettings.dataDir.getAbsolutePath() + "/homes.db");
        		conn.setAutoCommit(false);
        		return conn;
        	}
        } catch (SQLException ex) {
            HomeLogger.severe("SQL exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            HomeLogger.severe("You need the SQLite/MySQL library.", ex);
        }
        return conn;
    }

    public static Connection getConnection() {
        if(conn == null) conn = initialize();
        if(HomeSettings.usemySQL) {
	        try {
	        	if(!conn.isValid(10)) conn = initialize();
	        } catch (SQLException ex) {
	        	HomeLogger.severe("Failed to check SQL status", ex);
	        }
        }
        return conn;
    }

    public static void closeConnection() {
        if(conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException ex) {
                HomeLogger.severe("Error on Connection close", ex);
            }
        }
    }


}
