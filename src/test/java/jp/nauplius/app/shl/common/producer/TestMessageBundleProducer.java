package jp.nauplius.app.shl.common.producer;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

@Named
@Alternative
@ApplicationScoped
public class TestMessageBundleProducer {
    @Produces
    public ResourceBundle getMessageBundle() {
        return ResourceBundle.getBundle("i18n.messages", Locale.getDefault());
    }
}
