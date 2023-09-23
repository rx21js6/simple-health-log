package jp.nauplius.app.shl.maint.backing;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.interceptor.PermissionInterceptor;
import jp.nauplius.app.shl.maint.service.AdminSettingService;
import jp.nauplius.app.shl.user.service.UserService;

@Named
@ViewScoped
public class AdminSettingController implements Serializable {
    @Inject
    private FacesContext facesContext;

    @Inject
    private AdminSettingService adminSettingService;

    @Inject
    private UserService userService;

    @Inject
    private transient ResourceBundle messageBundle;

    /**
     * 画面初期化
     */
    @PermissionInterceptor
    public void init() {
        this.facesContext.getExternalContext().getFlash().setKeepMessages(true);

        try {
            this.adminSettingService.load();

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
     * セキュリティ強化実行
     *
     * @return null
     */
    public String performSecurityEnhancement() {
        try {
            this.userService.performSecurityEnhancement();

            this.facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            this.messageBundle.getString(
                                    "contents.maint.settings.customSetting.msg.performSecurityEnhancementCompleted"),
                            null));

        } catch (SimpleHealthLogException e) {

            this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), null));
        }

        return null;
    }
}
