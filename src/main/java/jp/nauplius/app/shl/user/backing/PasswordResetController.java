package jp.nauplius.app.shl.user.backing;

import java.io.Serializable;
import java.util.ResourceBundle;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.page.login.service.LoginService;
import jp.nauplius.app.shl.user.bean.PasswordResetForm;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class PasswordResetController implements Serializable {
    @Inject
    private transient ResourceBundle messageBundle;

    @Inject
    private transient FacesContext context;

    @Inject
    private LoginService loginService;

    @Inject
    @Getter
    @Setter
    private PasswordResetForm passwordResetForm;

    public String showPage() {
        return "/contents/password/passwordReset.xhtml?faces-redirect=true";
    }

    public String reset() {
        this.context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            this.loginService.resetPassword(this.passwordResetForm);
            this.context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    this.messageBundle.getString("resetPassword.msg.emailSent"), null));
        } catch (SimpleHealthLogException e) {
            this.context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    this.messageBundle.getString("resetPassword.msg.failed") + e.getMessage(), null));
        }

        return null;
    }
}
