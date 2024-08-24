package jp.nauplius.app.shl.common.ui.backing;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

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
