package jp.nauplius.app.shl.common.producer;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

import org.slf4j.Logger;

import jp.nauplius.app.shl.common.constants.ShlConstants;

@Named
@Alternative
@ApplicationScoped
public class TestEntityManagerFactoryProducer implements Serializable {
	@Inject
	private Logger logger;
	
    @PersistenceUnit
    private EntityManagerFactory factory;

    @Produces
    public EntityManagerFactory getEntityManagerFactory() {
        this.logger.debug(String.format("getEntityManager: %s", ShlConstants.PERSISTENCE_UNIT_NAME_TEST));
        
        if (factory == null) {
            factory = Persistence.createEntityManagerFactory(ShlConstants.PERSISTENCE_UNIT_NAME_TEST);
        }
        return factory;
    }

    @Produces
    public EntityManager getEntityManager() {
        EntityManager em = getEntityManagerFactory().createEntityManager();
        return em;
    }

}
