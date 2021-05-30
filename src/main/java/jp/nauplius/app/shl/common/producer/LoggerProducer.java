package jp.nauplius.app.shl.common.producer;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@Dependent
public class LoggerProducer {
    @Produces
    public Logger produceLogger(InjectionPoint injectionPoint) {
        Logger logger = LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
        return logger;
    }
}
