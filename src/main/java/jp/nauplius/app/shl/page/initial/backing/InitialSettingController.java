package jp.nauplius.app.shl.page.initial.backing;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.nauplius.app.shl.page.initial.service.InitialsettingService;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class InitialSettingController implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(InitialSettingController.class);

    @Inject
    @Getter
    @Setter
    private InitialSettingForm initialSettingForm;

    @Inject
    private InitialsettingService initialsettingService;

    public void init() {
        LOGGER.info("init()");
        this.initialSettingForm.setLoginId("admin");
        this.initialSettingForm.setName("管理者");
        this.initialSettingForm.setPassword("");
        this.initialSettingForm.setPasswordReEnter("");
        this.initialSettingForm.setMailAddress("");
        this.initialSettingForm.setMailAddressReEnter("");
    }

    public String register() {
        System.out.println("initialSettingForm: " + initialSettingForm);

        String password = this.initialSettingForm.getPassword();
        String passwordReEnter = this.initialSettingForm.getPasswordReEnter();
        if (!password.contentEquals(passwordReEnter)) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.getExternalContext().getFlash().setKeepMessages(true);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "パスワード不一致");
            facesContext.addMessage("initialSettingValues", message);
            return null;
        }

        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.getExternalContext().getFlash().setKeepMessages(true);

        this.initialsettingService.register(this.initialSettingForm);
        return "/contents/initial/initialSettingComplete.xhtml?faces-redirect=true";
    }

    public String toLogin() {
        return "/contents/login/login.xhtml?faces-redirect=true";
    }
}
