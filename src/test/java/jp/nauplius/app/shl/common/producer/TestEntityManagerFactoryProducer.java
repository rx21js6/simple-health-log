package jp.nauplius.app.shl.common.producer;

import java.io.Serializable;

import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceUnit;

import org.slf4j.Logger;

import jp.nauplius.app.shl.common.constants.ShlConstants;

@Named
@Alternative
public class TestEntityManagerFactoryProducer implements Serializable {
    @Inject
    private Logger logger;

    @PersistenceUnit
    private EntityManagerFactory factory;

    @Produces
    public EntityManagerFactory getEntityManagerFactory() {
        this.logger.debug("#getEntityManagerFactory() begin");
        this.logger.debug(String.format("getEntityManager: %s", ShlConstants.PERSISTENCE_UNIT_NAME_TEST));

        if (factory == null) {
            factory = Persistence.createEntityManagerFactory(ShlConstants.PERSISTENCE_UNIT_NAME_TEST);
        }

        this.logger.debug(String.format("factory: %s", factory));
        this.logger.debug("#getEntityManagerFactory() complete");
        return factory;
    }

    @Produces
    public EntityManager getEntityManager() {
        EntityManager em = getEntityManagerFactory().createEntityManager();
        return em;
    }

    @Produces
    @InitializationQualifier
    public EntityManager getInitialQualifier() {
        this.logger.debug("#getInitialQualifier() begin");

        if (factory == null) {
            factory = Persistence.createEntityManagerFactory(ShlConstants.PERSISTENCE_UNIT_NAME_TEST);
        }

        EntityManager entityManager = this.factory.createEntityManager();

        this.logger.debug(String.format("entityManager: %s", entityManager));
        this.logger.debug("#getInitialQualifier() complete");
        return entityManager;
    }
}
