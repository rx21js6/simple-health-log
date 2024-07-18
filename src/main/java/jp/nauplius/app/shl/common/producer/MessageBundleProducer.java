package jp.nauplius.app.shl.common.producer;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;

import jp.nauplius.app.shl.common.service.LocaleService;

@Named
@Dependent
public class MessageBundleProducer {

    @Inject
    private transient FacesContextProducer facesContextProducer;

    @Produces
    public ResourceBundle getMessageBundle() {
        FacesContext facesContext = this.facesContextProducer.getFacesContext();
        Locale locale = null;

        if (Objects.isNull(facesContext)) {
            locale = Locale.getDefault();
        } else {
            ExternalContext externalContext = facesContext.getExternalContext();
            if (Objects.isNull(externalContext)) {
                locale = Locale.getDefault();
            } else {
                HttpSession httpSession = (HttpSession) this.facesContextProducer.getFacesContext().getExternalContext()
                        .getSession(true);
                locale = (Locale) httpSession.getAttribute(LocaleService.SESSION_KEY);
                if (Objects.isNull(locale)) {
                    locale = Locale.getDefault();
                }
            }
        }

        return ResourceBundle.getBundle("i18n.messages", locale);
    }
}
