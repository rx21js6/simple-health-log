package jp.nauplius.app.shl.common.producer;

import java.io.Serializable;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import org.slf4j.Logger;

import jp.nauplius.app.shl.common.constants.ShlConstants;

@Named
@ApplicationScoped
public class EntityManagerFactoryProducer implements Serializable {
    @Inject
    private Logger logger;

    private EntityManagerFactory factory;

    @Produces
    public EntityManagerFactory getEntityManagerFactory() {
        this.logger.debug("#getEntityManagerFactory()");
        this.logger.debug(String.format("unitName: %s", ShlConstants.PERSISTENCE_UNIT_NAME));

        if (this.factory == null || !this.factory.isOpen()) {
            synchronized (this) {
                this.factory = Persistence.createEntityManagerFactory(ShlConstants.PERSISTENCE_UNIT_NAME);
            }
        }

        return this.factory;
    }
}
