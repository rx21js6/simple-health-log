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

import org.apache.deltaspike.jpa.api.transaction.Transactional;

import jp.nauplius.app.shl.common.model.KeyIv;
import jp.nauplius.app.shl.common.util.CipherUtil;

@Named
public class DbLoader {
    @Inject
    private EntityManager em;

    @Inject
    private CipherUtil cipherUtil;

    /**
     * テーブル作成
     */
    @Transactional
    public void createTables() {
        try {
            // key_iv
            System.out.println("create table key_iv.");
            String createKeyIvSql = this.loadSqlString("key_iv");
            this.em.createNativeQuery(createKeyIvSql).executeUpdate();

            // login_user
            System.out.println("create table login_user.");
            String createLoginUserSql = this.loadSqlString("login_user");
            this.em.createNativeQuery(createLoginUserSql).executeUpdate();

            // user_role
            System.out.println("create table user_role.");
            String createUserRoleSql = this.loadSqlString("user_role");
            this.em.createNativeQuery(createUserRoleSql).executeUpdate();

            System.out.println("Table created.");
        } catch (Throwable e) {
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

    private String loadSqlString(String tableName) {
        StringBuilder sqlBuilder = new StringBuilder();
        String sqlPath = String.format("/sql/derby_create_%s.sql", tableName);

        try {
            InputStream in = getClass().getResourceAsStream(sqlPath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line = null;

            while ((line = reader.readLine()) != null) {
                // System.out.println(line);
                sqlBuilder.append(line);
            }

            String sqlString = sqlBuilder.toString();
            sqlString = sqlString.replace(" +$", "").replace(";$", "");
            return sqlString;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
