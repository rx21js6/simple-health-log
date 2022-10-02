package jp.nauplius.app.shl.maint.backing;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.maint.service.NotEnteredNoticeService;

@Named
@ViewScoped
public class NotEnteredNoticeController implements Serializable {
    @Inject
    private FacesContext facesContext;

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
                    this.messageBundle.getString("contents.maint.settings.cutomSetting.msg.notEnteredNoticeChanged"), null));

        } catch (SimpleHealthLogException e) {
            this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), null));
        }

        return null;

    }
}

