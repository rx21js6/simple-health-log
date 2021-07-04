package jp.nauplius.app.shl.common.producer;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@Alternative
@ApplicationScoped
public class TestLoggerProducer implements Serializable {
    @Produces
    public Logger produceLogger(InjectionPoint injectionPoint) {
        Logger logger = LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
        // ((ch.qos.logback.classic.Logger)logger).setLevel(Level.DEBUG);
        return logger;
    }
}
