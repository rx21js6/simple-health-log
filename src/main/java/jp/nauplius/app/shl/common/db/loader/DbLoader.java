package jp.nauplius.app.shl.common.db.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.transaction.Transactional;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;

import jp.nauplius.app.shl.common.producer.InitializationQualifier;

/**
 * データベース登録・更新（Flyway）機能
 */
@Named
public class DbLoader {
    @Inject
    private Logger logger;

    @Inject
    @InitializationQualifier
    private EntityManager entityManager;

    /**
     * テーブル作成SQL実行
     */
    public void createTables() {
        this.logger.info("#createTables() begin");
        EntityTransaction transaction = this.entityManager.getTransaction();
        try {
            transaction.begin();
            String sqlString = this.loadSqlString();
            String[] sqlLines = sqlString.split(";");
            for (String sqlLine : sqlLines) {
                this.logger.info(String.format("execute query: %s", sqlLine));
                this.entityManager.createNativeQuery(sqlLine).executeUpdate();
            }
            transaction.commit();
            this.logger.info("#createTables() complete");
        } catch (Throwable e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            this.logger.error("#craeteTables() failed. : " + e.getMessage());
            throw e;
        }
    }

    /**
     * 登録用SQL読み込み
     *
     * @return sql
     */
    private String loadSqlString() {
        StringBuilder sqlBuilder = new StringBuilder();
        String sqlPath = "/sql/create_tables.sql";

        try {
            InputStream in = getClass().getResourceAsStream(sqlPath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line = null;

            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("--")) {
                    // System.out.println(line);
                    sqlBuilder.append(line);
                }

            }

            String sqlString = sqlBuilder.toString();
            sqlString = sqlString.replace(" +$", "").replace(";$", ";");
            return sqlString;
        } catch (IOException e) {
            e.printStackTrace();
            this.logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * DB更新（Flyway）
     */
    @Transactional
    public void updateDb() {
        Map<String, Object> properties = this.entityManager.getEntityManagerFactory().getProperties();
        String dbUrl = String.valueOf(properties.get("javax.persistence.jdbc.url"));
        String dbUser = String.valueOf(properties.get("javax.persistence.jdbc.user"));
        String dbPassword = String.valueOf(properties.get("javax.persistence.jdbc.password"));
        Flyway flyway = Flyway.configure().dataSource(dbUrl, dbUser, dbPassword).load();
        flyway.baseline();
        flyway.migrate();
    }
}
