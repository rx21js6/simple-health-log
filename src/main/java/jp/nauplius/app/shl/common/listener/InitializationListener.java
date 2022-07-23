package jp.nauplius.app.shl.common.listener;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.slf4j.Logger;

import jp.nauplius.app.shl.common.db.DbLoader;
import jp.nauplius.app.shl.common.model.KeyIv;
import jp.nauplius.app.shl.common.producer.InitializationQualifier;

@Named
@WebListener
public class InitializationListener implements ServletContextListener {
    @Inject
    private Logger logger;

    @Inject
    @InitializationQualifier
    private EntityManager em;

    @Inject
    private DbLoader dbLoader;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        this.logger.info("InitializationListener#contextInitialized() em: " + this.em);

        this.checkDbInitialized();
        this.dbLoader.updateDb();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("InitializationListener#contextDestroyed()");
    }

    private void checkDbInitialized() {
        KeyIv keyIv = null;

        try {
            keyIv = this.em.find(KeyIv.class, 1);
            if (keyIv != null) {
                System.out.println("KeyIv found.");
                return;
            }
        } catch (DatabaseException e) {
            System.err.println("Table Not found? createing...");
            this.dbLoader.createTables();
        }

        System.out.println("KeyIv not found.");
        // this.dbLoader.loadKeyIvData();
    }

}
