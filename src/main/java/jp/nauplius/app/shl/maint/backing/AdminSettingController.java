package jp.nauplius.app.shl.maint.backing;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ResourceBundle;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.interceptor.PermissionInterceptor;
import jp.nauplius.app.shl.common.service.ConfigFileService;
import jp.nauplius.app.shl.maint.service.AdminSettingService;
import jp.nauplius.app.shl.user.service.UserService;

@Named
@ViewScoped
public class AdminSettingController implements Serializable {
    @Inject
    private transient FacesContext facesContext;

    @Inject
    private AdminSettingService adminSettingService;

    @Inject
    private UserService userService;

    @Inject
    private ConfigFileService configFileService;

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

    /**
     * config.ymlダウンロード
     *
     * @return null
     */
    public void downloadConfigYml() {
        this.facesContext.getExternalContext().getFlash().setKeepMessages(true);
        ExternalContext externalContext = this.facesContext.getExternalContext();

        try {
            byte[] configYmlBytes = this.configFileService.getConfigYmlByteArray();

            externalContext.responseReset();
            externalContext.setResponseContentType("application/octet-stream");
            externalContext.setResponseContentLength(configYmlBytes.length);
            externalContext.setResponseHeader("Content-Disposition",
                    "attachment; filename=\"" + ConfigFileService.CONFIG_FILE_NAME + "\"");

            OutputStream output = externalContext.getResponseOutputStream();
            output.write(configYmlBytes);
        } catch (SimpleHealthLogException e) {
            this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), null));
        } catch (IOException e) {
            this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), null));
        } finally {
            this.facesContext.responseComplete();
        }
    }
}
