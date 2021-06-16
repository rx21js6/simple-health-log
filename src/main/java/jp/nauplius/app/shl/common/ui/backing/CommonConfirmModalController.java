package jp.nauplius.app.shl.common.ui.backing;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import jp.nauplius.app.shl.common.ui.bean.ModalBean;

/**
 * 共通モーダルコントローラ
 */
@Named
@ViewScoped
public class CommonConfirmModalController extends ModalController {
    @Inject
    @Named("commonConfirmModalBean")
    protected ModalBean modalBean;

    public String fireAction(String buttonName) {
        return this.modalControllerListener.fireAction(this, this.modalBean.getCommandTypeName(), buttonName);
    }
}
