package jp.nauplius.app.shl.common.db;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

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
