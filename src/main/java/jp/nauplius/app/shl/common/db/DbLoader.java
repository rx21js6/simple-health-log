package jp.nauplius.app.shl.common.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.transaction.Transactional;

import org.slf4j.Logger;

import jp.nauplius.app.shl.common.model.KeyIv;
import jp.nauplius.app.shl.common.util.CipherUtil;

@Named
public class DbLoader {
    @Inject
    private Logger logger;

    @Inject
    private EntityManager em;

    @Inject
    private CipherUtil cipherUtil;

    /**
     * テーブル作成
     */
    public void createTables() {
        EntityTransaction transaction = this.em.getTransaction();
        try {
            transaction.begin();
            String sqlString = this.loadSqlString();
            String[] sqlLines = sqlString.split(";");
            for (String sqlLine : sqlLines) {
                this.logger.info(String.format("execute query: %s", sqlLine));
                this.em.createNativeQuery(sqlLine).executeUpdate();
            }
            transaction.commit();

        } catch (Throwable e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            System.err.println("craete table failed.");
            throw new RuntimeException(e);
        }
    }

    /**
     * KeyIvデータ登録。
     */
    @Transactional
    public void loadKeyIvData() {
        System.out.println("Transaction begin.");
        KeyIv keyIv = new KeyIv();
        try {
            byte[] keyBytes = this.cipherUtil.createKey();
            byte[] ivBytes = this.cipherUtil.createInitialVector();
            keyIv.setEncryptionKey(this.cipherUtil.byteToBase64String(keyBytes));
            keyIv.setEncryptionIv(this.cipherUtil.byteToBase64String(ivBytes));

            Timestamp timeStamp = Timestamp.valueOf(LocalDateTime.now());

            keyIv.setCreatedDate(timeStamp);
            keyIv.setModifiedDate(timeStamp);
            this.em.persist(keyIv);
            this.em.merge(keyIv);
            this.em.flush();
            System.out.println("key iv registered.");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.err.println("key iv insert failed.");
            throw new RuntimeException(e);
        }
    }

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
            throw new RuntimeException(e);
        }
    }
}
