package jp.nauplius.app.shl.common.ui.backing;

import java.io.Serializable;

/**
 * モーダルコントローラ
 */
public abstract class ModalController implements Serializable {

    protected ModalControllerListener modalControllerListener;

    public void setModalControllerListener(ModalControllerListener modalControllerListener) {
        this.modalControllerListener = modalControllerListener;
    }

    public abstract String fireAction(String buttonName);
}
