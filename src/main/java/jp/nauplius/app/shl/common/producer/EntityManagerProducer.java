package jp.nauplius.app.shl.common.producer;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;

import jp.nauplius.app.shl.common.constants.ShlConstants;

@Named
public class EntityManagerProducer implements Serializable {
    @Inject
    private Logger logger;

    @Inject
    private EntityManagerFactory factory;

    @Produces
    @Default
    @RequestScoped
    public EntityManager getEntityManager() {
        this.logger.debug("#getEntityManager()");
        this.logger.debug(String.format("unitName: %s", ShlConstants.PERSISTENCE_UNIT_NAME));

        EntityManager entityManager = this.factory.createEntityManager();
        this.logger.debug(String.format("entityManager: %s", entityManager));

        return entityManager;
    }

    @Produces
    @InitializationQualifier
    public EntityManager getInitialQualifier() {
        this.logger.debug("#getInitialQualifier()");

        EntityManager entityManager = this.factory.createEntityManager();
        this.logger.debug(String.format("entityManager: %s", entityManager));

        return entityManager;
    }


    protected void closeEntityManager(@Disposes EntityManager entityManager) {
        this.logger.debug(String.format("EntityManager closeing: %s", entityManager));
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
