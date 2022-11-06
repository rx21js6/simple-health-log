package jp.nauplius.app.shl.common.producer;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

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
                HttpSession httpSession = (HttpSession) this.facesContextProducer.getFacesContext().getExternalContext().getSession(true);
                locale = (Locale) httpSession.getAttribute(LocaleService.SESSION_KEY);
                if (Objects.isNull(locale)) {
                    locale = Locale.getDefault();
                }
            }
        }

        return ResourceBundle.getBundle("i18n.messages", locale);
    }
}
