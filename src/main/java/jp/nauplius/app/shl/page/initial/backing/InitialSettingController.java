package jp.nauplius.app.shl.page.initial.backing;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.nauplius.app.shl.common.constants.ShlConstants;
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

    @Inject
    private transient ResourceBundle messageBundle;

    public void init() {
        LOGGER.info("init()");
        this.initialSettingForm.setLoginId(ShlConstants.LOGIN_ID_ADMIN);
        this.initialSettingForm.setName(this.messageBundle.getString("initial.initialSetting.label.admin"));
        this.initialSettingForm.setPassword(StringUtils.EMPTY);
        this.initialSettingForm.setPasswordReenter(StringUtils.EMPTY);
        this.initialSettingForm.setMailAddress(StringUtils.EMPTY);
        this.initialSettingForm.setMailAddressReEnter(StringUtils.EMPTY);
    }

    public String register() {
        System.out.println("initialSettingForm: " + initialSettingForm);

        String password = this.initialSettingForm.getPassword();
        String passwordReenter = this.initialSettingForm.getPasswordReenter();
        if (!password.contentEquals(passwordReenter)) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.getExternalContext().getFlash().setKeepMessages(true);

            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
                    this.messageBundle.getString("initial.initialSetting.msg.passwordUnmatch"));
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

    public String toInput() {
        return "/contents/record/recordInput.xhtml?faces-redirect=true";
    }
}
