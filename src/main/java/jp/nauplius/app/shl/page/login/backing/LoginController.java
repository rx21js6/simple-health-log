package jp.nauplius.app.shl.page.login.backing;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.page.login.bean.LoginForm;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
import jp.nauplius.app.shl.page.login.bean.LoginResponse;
import jp.nauplius.app.shl.page.login.service.CookieService;
import jp.nauplius.app.shl.page.login.service.LoginService;
import lombok.Getter;
import lombok.Setter;

@Named
@SessionScoped
public class LoginController implements Serializable {
    @Inject
    private Logger logger;

    @Inject
    @Getter
    @Setter
    private LoginForm loginForm;

    @Inject
    private LoginService loginService;

    @Inject
    private CookieService cookieService;

    @Inject
    private FacesContext facesContext;

    @Inject
    @Getter
    @Setter
    private LoginInfo loginInfo;

    public void init() {
        this.logger.info("init()");
    }

    public String dummy() {
        this.logger.info("dummy()");
        return null;
    }

    public String error() {
        throw new RuntimeException("throwing new error");
    }

    public String toInitial() {
        return "/contents/initial/initialSetting.xhtml?faces-redirect=true";
    }

    public String login() {
        this.logger.info("login");
        if (Objects.isNull(this.loginInfo.getUserInfo())) {
            try {
                LoginResponse loginResponse = this.loginService.login(this.loginForm);
                if (this.loginForm.isKeepLogin()) {
                    this.cookieService.registerToken(this.facesContext, loginResponse.getUserToken().getToken());
                }

            } catch (SimpleHealthLogException e) {
                facesContext.getExternalContext().getFlash().setKeepMessages(true);
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "認証に失敗しました。: " + e.getMessage(), null));
            }
        }
        return null;
    }

    public String logout() {
        this.logger.info("logout");
        try {
            if (!Objects.isNull(this.loginInfo.getUserInfo())) {
                this.loginService.logout();
                this.cookieService.removeToken(this.facesContext);
            }
        } catch (SimpleHealthLogException e) {
            facesContext.getExternalContext().getFlash().setKeepMessages(true);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "ログアウトしました。", null));
        }

        return null;
    }

    public void timeout() throws IOException {
        ExternalContext externalContext = this.facesContext.getExternalContext();
        this.facesContext.getExternalContext().getFlash().setKeepMessages(true);
        this.facesContext.addMessage(null, new FacesMessage("セッションが切れました。ログインしてください。"));

        externalContext.redirect(externalContext.getRequestContextPath() + "/login.xhtml");
    }
}
