package jp.nauplius.app.shl.common.producer;

import java.util.Locale;
import java.util.ResourceBundle;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

@Named
@Alternative
@ApplicationScoped
public class TestMessageBundleProducer {
    @Produces
    public ResourceBundle getMessageBundle() {
        return ResourceBundle.getBundle("i18n.messages", Locale.getDefault());
    }
}
