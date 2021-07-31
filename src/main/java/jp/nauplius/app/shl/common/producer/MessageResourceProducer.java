package jp.nauplius.app.shl.common.producer;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

@Named
@Dependent
public class MessageResourceProducer {
    @Produces
    public ResourceBundle getMessageBundle() {
        return ResourceBundle.getBundle("i18n.messages", Locale.getDefault());
    }
}
