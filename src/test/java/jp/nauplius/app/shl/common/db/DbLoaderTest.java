package jp.nauplius.app.shl.common.db;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.cdiunit.ActivatedAlternatives;
import io.github.cdiunit.CdiRunner;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jp.nauplius.app.shl.common.db.loader.DbLoader;
import jp.nauplius.app.shl.common.db.model.KeyIv;
import jp.nauplius.app.shl.common.producer.TestEntityManagerFactoryProducer;
import jp.nauplius.app.shl.common.producer.TestLoggerProducer;

@RunWith(CdiRunner.class)
@ActivatedAlternatives({TestLoggerProducer.class, TestEntityManagerFactoryProducer.class})
public class DbLoaderTest {
    @Inject
    private DbLoader dbLoader;

    @Inject
    private EntityManager em;

    @Test
    public void testCreateTables() {
        try {
            KeyIv keyIv = this.em.find(KeyIv.class, 1);
            if (keyIv != null) {
                System.out.println("KeyIv found.");
                return;
            }
        } catch (DatabaseException e) {
            System.err.println("Table Not found? createing...");
            this.dbLoader.createTables();
        }
    }
}
