package jp.nauplius.app.shl.user.backing;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.page.login.service.LoginService;
import jp.nauplius.app.shl.user.bean.PasswordResetForm;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class PasswordResetController implements Serializable {
    @Inject
    private FacesContext context;

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
            this.context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "メールを送信しました。", null));
        } catch (SimpleHealthLogException e) {
            this.context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "処理中にエラーが発生しました。管理者に問い合わせてください。" + e.getMessage(), null));
        }

        return null;
    }
}
