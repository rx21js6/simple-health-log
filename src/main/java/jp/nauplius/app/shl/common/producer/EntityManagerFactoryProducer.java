package jp.nauplius.app.shl.common.producer;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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
        this.logger.debug(String.format("getEntityManagerFactory: %s", ShlConstants.PERSISTENCE_UNIT_NAME));

        if (this.factory == null || !this.factory.isOpen()) {
            synchronized (this) {
                this.factory = Persistence.createEntityManagerFactory(ShlConstants.PERSISTENCE_UNIT_NAME);
            }
        }

        return this.factory;
    }
}
