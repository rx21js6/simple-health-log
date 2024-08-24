package jp.nauplius.app.shl.common.producer;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Named;

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
