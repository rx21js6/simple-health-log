package jp.nauplius.app.shl.common.service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.jboss.weld.context.bound.BoundSessionContext;

import jp.nauplius.app.shl.common.db.model.KeyIv;
import jp.nauplius.app.shl.common.ui.bean.KeyIvHolder;
import jp.nauplius.app.shl.common.util.CipherUtil;

public class AbstractServiceTest {
    @Inject
    protected BoundSessionContext sessionContext;

    @Inject
    protected KeyIvHolder keyIvHolder;

    @Inject
    protected CipherUtil cipherUtil;

    /**
     * Insert test data.
     *
     * @param xmlFilePath
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws DatabaseUnitException
     * @throws FileNotFoundException
     */
    protected void insertTestDataXml(EntityManager entityManager, String xmlFilePath) {
        // コネクション取得
        Connection jdbcConnection = entityManager.unwrap(Connection.class);
        DatabaseConnection connection = null;

        try {
            // DBUnitのコネクション生成
            connection = new DatabaseConnection(jdbcConnection);

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            IDataSet dataSet = new XmlDataSet(inputStream);

            // 空フィールドを許可
            DatabaseConfig config = connection.getConfig();
            config.setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, Boolean.TRUE);

            // クリーンインサート処理を実行
            DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
            initKeyIv(entityManager);
        } catch (DatabaseUnitException | SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Insert Key, Initial Vector.
     *
     * @param entityManager
     */
    private void initKeyIv(EntityManager entityManager) {
        KeyIv keyIv = entityManager.find(KeyIv.class, 1);
        if (Objects.nonNull(keyIv)) {
            this.keyIvHolder.setKeyBytes(this.cipherUtil.base64StringToBytes(keyIv.getEncryptionKey()));
            this.keyIvHolder.setIvBytes(this.cipherUtil.base64StringToBytes(keyIv.getEncryptionIv()));
            this.keyIvHolder.setSalt("abc123xyz");
        }
    }
}
