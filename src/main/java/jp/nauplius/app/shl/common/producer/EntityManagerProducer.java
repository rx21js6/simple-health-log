package jp.nauplius.app.shl.common.producer;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

@ApplicationScoped
public class EntityManagerProducer implements Serializable {
    private EntityManagerFactory factory;

    private EntityManager em;

    @Produces
    public EntityManager getEntityManager() {
        if (this.factory == null || !this.factory.isOpen()) {
            synchronized (this) {
                this.factory = Persistence.createEntityManagerFactory("simple-health-log");
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
