package jp.nauplius.app.shl.maint.backing;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import jp.nauplius.app.shl.common.ui.backing.ModalController;
import jp.nauplius.app.shl.common.ui.backing.ModalControllerListener;

@Named
@ViewScoped
public class CustomSettingController implements ModalControllerListener {

    @Override
    public void initModal() {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public String fireAction(ModalController modalAction, String commandTypeName, String buttonName) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @PostConstruct
    public void init() {

    }

    public String cancel() {
        return null;
    }

    public String changePassword() {
        return null;
    }

    public String changeMailAddress() {
        return null;
    }
}
