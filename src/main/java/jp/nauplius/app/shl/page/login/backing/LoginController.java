package jp.nauplius.app.shl.page.login.backing;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.nauplius.app.shl.page.login.service.LoginService;
import lombok.Getter;
import lombok.Setter;

@Named
@RequestScoped
public class LoginController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Inject
    @Getter
    @Setter
    private LoginForm loginForm;

    @Inject
    private LoginService loginService;

    public void init() {
        LOGGER.info("init()");
    }

    public String dummy() {
        LOGGER.info("dummy()");
        return null;
    }

    public String error() {
        throw new RuntimeException("throwing new error");
    }

    public String toInitial() {
        return "/contents/initial/initialSetting.xhtml?faces-redirect=true";
    }

    public String login() {

        if (!this.loginService.login(loginForm)) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.getExternalContext().getFlash().setKeepMessages(true);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "ログインIDまたはパスワードが不正です。");
            facesContext.addMessage("initialSettingValues", message);
            return null;
        }

        return "/contents/main.xhtml?faces-redirect=true";
    }

    public String logout() {
        return "/contents/login/login.xhtml?faces-redirect=true";
    }
}
