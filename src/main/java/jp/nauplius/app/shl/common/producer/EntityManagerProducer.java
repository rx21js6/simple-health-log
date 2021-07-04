package jp.nauplius.app.shl.common.producer;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;

import jp.nauplius.app.shl.common.constants.ShlConstants;

@Named
@ApplicationScoped
public class EntityManagerProducer implements Serializable {
    @Inject
    private Logger logger;

    private EntityManagerFactory factory;

    @PersistenceContext(unitName = ShlConstants.PERSISTENCE_UNIT_NAME)
    private EntityManager em;

    @Produces
    public EntityManager getEntityManager() {
        this.logger.debug(String.format("getEntityManager: %s", ShlConstants.PERSISTENCE_UNIT_NAME));

        if (this.factory == null || !this.factory.isOpen()) {
            synchronized (this) {
                this.factory = Persistence.createEntityManagerFactory(ShlConstants.PERSISTENCE_UNIT_NAME);
            }
        }

        if (this.em == null) {
            synchronized (this) {
                this.em = this.factory.createEntityManager();
            }
        }

        if (!this.em.isOpen()) {
            System.err.println("em is closed!: " + this.em);
            synchronized (this) {
                this.em = this.factory.createEntityManager();
            }
        }

        System.out.println("em: " + this.em);
        return this.em;
    }

    protected void closeEntityManager(@Disposes EntityManager entityManager) {
        System.out.println("EntityManager closeing: " + entityManager);
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
