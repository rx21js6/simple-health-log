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

import jp.nauplius.app.shl.common.constants.ShlConstants;
import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.page.initial.service.InitialSettingService;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class InitialSettingController implements Serializable {
    @Inject
    private Logger logger;

    @Inject
    private FacesContext facesContext;

    @Inject
    @Getter
    @Setter
    private InitialSettingForm initialSettingForm;

    @Inject
    private InitialSettingService initialSettingService;

    @Inject
    private transient ResourceBundle messageBundle;

    public void init() {
        this.logger.info("init()");
        this.initialSettingForm.setLoginId(ShlConstants.LOGIN_ID_ADMIN);
        this.initialSettingForm.setName(this.messageBundle.getString("initial.initialSetting.label.admin"));
        this.initialSettingForm.setPassword(StringUtils.EMPTY);
        this.initialSettingForm.setPasswordReenter(StringUtils.EMPTY);
        this.initialSettingForm.setMailAddress(StringUtils.EMPTY);
        this.initialSettingForm.setMailAddressReEnter(StringUtils.EMPTY);
    }

    public String register() {
        this.logger.info("initialSettingForm: " + initialSettingForm);

        String password = this.initialSettingForm.getPassword();
        String passwordReenter = this.initialSettingForm.getPasswordReenter();
        if (!password.contentEquals(passwordReenter)) {
            this.facesContext.getExternalContext().getFlash().setKeepMessages(true);

            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
                    this.messageBundle.getString("initial.initialSetting.msg.passwordUnmatch"));
            this.facesContext.addMessage("initialSettingValues", message);
            return null;
        }

        this.facesContext.getExternalContext().getFlash().setKeepMessages(true);

        try {
            this.initialSettingService.register(this.facesContext.getExternalContext().getApplicationContextPath(),
                    this.initialSettingForm);
        } catch (SimpleHealthLogException e) {

            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
            return null;
        }

        return "/contents/initial/initialSettingComplete.xhtml?faces-redirect=true";
    }

    public String toLogin() {
        return "/contents/login/login.xhtml?faces-redirect=true";
    }

    public String toInput() {
        return "/contents/record/recordInput.xhtml?faces-redirect=true";
    }
}
