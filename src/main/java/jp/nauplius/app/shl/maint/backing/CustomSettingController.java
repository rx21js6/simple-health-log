package jp.nauplius.app.shl.maint.backing;

import java.io.Serializable;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.maint.service.CustomSettingService;

@Named
@ViewScoped
public class CustomSettingController implements Serializable {
    @Inject
    private FacesContext facesContext;

    @Inject
    private CustomSettingService customSettingService;

    @Inject
    private CustomSettingPasswordModel customSettingPasswordModel;

    @Inject
    private transient ResourceBundle messageBundle;

    /**
     * 画面初期化
     */
    public void init() {
        this.facesContext.getExternalContext().getFlash().setKeepMessages(true);

        try {
            this.customSettingService.load();

        } catch (SimpleHealthLogException e) {
            this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), null));
        }
    }

    /**
     * 入力ページに戻る
     *
     * @return 日次入力ページ
     */
    public String cancel() {
        return "/contents/record/recordInput.xhtml?faces-redirect=true";
    }

    /**
     * パスワード変更
     *
     * @return null
     */
    public String changePassword() {
        this.facesContext.getExternalContext().getFlash().setKeepMessages(true);
        try {
            // パスワード比較
            if (!Objects.equals(customSettingPasswordModel.getPassword(),
                    customSettingPasswordModel.getPasswordReenter())) {

                this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        this.messageBundle.getString("common.msg.passwordUnmatch"), null));
                return null;
            }

            this.customSettingService.changePassword();

            this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    this.messageBundle.getString("contents.maint.settings.cutomSetting.msg.passwordChanged"), null));

        } catch (SimpleHealthLogException e) {
            this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), null));
        }

        return null;
    }

    /**
     * テストメール送信
     *
     * @return null
     */
    public String sendTestMail() {
        this.facesContext.getExternalContext().getFlash().setKeepMessages(true);
        try {
            this.customSettingService.sendTestMail();

            this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    this.messageBundle.getString("contents.maint.settings.cutomSetting.msg.testMailSent"), null));

        } catch (SimpleHealthLogException e) {

            this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), null));
        }

        return null;
    }

    /**
     * メールアドレス変更
     *
     * @return null
     */
    public String changeMailAddress() {
        this.facesContext.getExternalContext().getFlash().setKeepMessages(true);
        try {
            this.customSettingService.changeMailAddress();

            this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    this.messageBundle.getString("contents.maint.settings.cutomSetting.msg.mailAddressChanged"), null));

        } catch (SimpleHealthLogException e) {

            this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), null));
        }

        return null;
    }
}