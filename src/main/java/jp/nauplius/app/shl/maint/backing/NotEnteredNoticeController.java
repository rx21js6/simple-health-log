package jp.nauplius.app.shl.maint.backing;

import java.io.Serializable;
import java.util.ResourceBundle;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.maint.service.NotEnteredNoticeService;

@Named
@ViewScoped
public class NotEnteredNoticeController implements Serializable {
    @Inject
    private transient FacesContext facesContext;

    @Inject
    private NotEnteredNoticeService notEnteredNoticeService;

    @Inject
    private transient ResourceBundle messageBundle;

    public void init() {
        this.notEnteredNoticeService.init();
    }

    public String update() {

        this.facesContext.getExternalContext().getFlash().setKeepMessages(true);
        try {
            this.notEnteredNoticeService.update();

            this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    this.messageBundle.getString("contents.maint.settings.customSetting.msg.notEnteredNoticeChanged"),
                    null));

        } catch (SimpleHealthLogException e) {
            this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), null));
        }

        return null;

    }
}
