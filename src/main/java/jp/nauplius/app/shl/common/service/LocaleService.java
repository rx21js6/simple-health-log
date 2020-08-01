package jp.nauplius.app.shl.common.service;

import java.io.Serializable;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import lombok.Getter;
import lombok.Setter;

@Named
@SessionScoped
public class LocaleService implements Serializable {
    public static final String SESSION_KEY = "locale";
    public static final String DEFAULT_LANG = "en";

    @Getter
    private Locale locale;

    @Getter
    @Setter
    private String selectedLocale;

    public LocaleService() {
        this.selectedLocale = DEFAULT_LANG;
    }

    @PostConstruct
    public void init() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpSession session = (HttpSession) externalContext.getSession(true);
        if (session.getAttribute(SESSION_KEY) == null) {
            this.selectedLocale = DEFAULT_LANG;
            this.locale = new Locale(this.selectedLocale);
            session.setAttribute("locale", this.locale);
        } else {
            this.locale = (Locale) session.getAttribute(SESSION_KEY);
            this.selectedLocale = this.locale.getLanguage();
        }
    }

    public String changeLocale() {
        this.setLocale();
        return null;
    }

    public void setLocale() {
        this.locale = new Locale(this.selectedLocale);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
        session.setAttribute(SESSION_KEY, locale);
    }
}
