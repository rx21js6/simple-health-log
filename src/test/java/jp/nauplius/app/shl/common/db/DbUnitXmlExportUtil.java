package jp.nauplius.app.shl.common.db;

import java.io.FileOutputStream;

import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;

public class DbUnitXmlExportUtil {

    public static void main(String[] args) throws Exception {

        String dbHost = args[0];
        String dbName = args[1];
        String dbUser = args[2];
        String dbPassword = args[3];
        String dbUrl = String.format("jdbc:postgresql://%s:5432/%s", dbHost, dbName);

        JdbcDatabaseTester tester = new JdbcDatabaseTester(
                "org.postgresql.Driver", dbUrl, dbUser, dbPassword);
        IDatabaseConnection connection = tester.getConnection();

        String[] tableNamesToDump = new String[] { "not_entered_notice" };
        IDataSet target = connection.createDataSet(tableNamesToDump);
        FileOutputStream stream = new FileOutputStream("/tmp/simple_health_log_export.xml");
        XmlDataSet.write(target, stream);

    }

}
