package jp.nauplius.app.shl.common.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbChecker {

    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection conn = null;
        ResultSet rs = null;

        if (args.length < 3) {
            System.err.println("Argument(dbName, user, password) required.");
            System.exit(-1);
        }

        String dbName = args[0];
        String dbUser = args[1];
        String dbPassword = args[2];
        String dbUrl = String.format("jdbc:postgresql://localhost:5432/%s", dbName);

        try {
            conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            DatabaseMetaData metaData = conn.getMetaData();
            rs = metaData.getTables(null, null, "%", null);
            while (rs.next()) {
              System.out.println(rs.getString(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

}
