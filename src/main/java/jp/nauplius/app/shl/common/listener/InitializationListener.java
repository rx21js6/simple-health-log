package jp.nauplius.app.shl.common.listener;

import java.io.IOException;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.slf4j.Logger;

import jp.nauplius.app.shl.common.db.loader.DbLoader;
import jp.nauplius.app.shl.common.db.model.KeyIv;
import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.producer.InitializationQualifier;
import jp.nauplius.app.shl.common.service.ConfigFileService;

@Named
@WebListener
public class InitializationListener implements ServletContextListener {
    @Inject
    private Logger logger;

    @Inject
    @InitializationQualifier
    private EntityManager entityManager;

    @Inject
    private DbLoader dbLoader;

    @Inject
    private ConfigFileService configFileService;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        this.logger.info("#contextInitialized() begin");
        this.logger.debug("entityManager: " + this.entityManager);

        this.checkDbInitialized();
        this.dbLoader.updateDb();

        try {
            this.configFileService.createFile();
        } catch (IOException e) {
            throw new SimpleHealthLogException(e);
        }

        this.logger.info("#contextInitialized() complete");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        this.logger.info("#contextDestroyed()");
    }

    private void checkDbInitialized() {
        KeyIv keyIv = null;

        try {
            keyIv = this.entityManager.find(KeyIv.class, 1);
            if (Objects.nonNull(keyIv)) {
                this.logger.info("KeyIv found.");
                return;
            }
        } catch (DatabaseException e) {
            this.logger.warn("Table Not found? createing...");
            this.dbLoader.createTables();
        }

        this.logger.warn("KeyIv not found.");
    }

}
