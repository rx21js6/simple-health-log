package jp.nauplius.app.shl.common.producer;

import java.io.Serializable;

import org.slf4j.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jp.nauplius.app.shl.common.constants.ShlConstants;

@Named
@ApplicationScoped
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
