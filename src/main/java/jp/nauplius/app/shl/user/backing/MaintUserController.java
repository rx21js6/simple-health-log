package jp.nauplius.app.shl.user.backing;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.model.UserInfo;
import jp.nauplius.app.shl.common.ui.backing.CommonConfirmModalController;
import jp.nauplius.app.shl.common.ui.backing.ModalController;
import jp.nauplius.app.shl.common.ui.backing.ModalControllerListener;
import jp.nauplius.app.shl.common.ui.bean.CommonConfirmModalBean;
import jp.nauplius.app.shl.user.bean.MaintUserInfo;
import jp.nauplius.app.shl.user.service.UserService;
import lombok.Getter;
import lombok.Setter;

/**
 * ユーザ管理画面コントローラ
 */
@Named
@SessionScoped
public class MaintUserController implements Serializable, ModalControllerListener {
    @Inject
    private Logger logger;

    @Inject
    private UserService userService;

    @Inject
    private CommonConfirmModalController commonConfirmModalController;

    @Inject
    private CommonConfirmModalBean commonConfirmModalBean;

    @Getter
    @Setter
    private String dummyText;

    @Getter
    @Setter
    private List<UserInfo> userInfos;

    @Getter
    @Setter
    private int selectedId;

    @Getter
    @Setter
    private MaintUserInfo selectedMaintUserInfo;

    private Method dispatchMethod;

    @PostConstruct
    public void init() {
        this.logger.debug("init: " + selectedMaintUserInfo);
    }

    public void loadUserInfos() {
        this.userInfos = this.userService.loadMaintUserInfos();
        this.dummyText = "ダミー";
    }

    public String showList() {
        this.logger.info("MaintUserController#showList");
        return "/contents/maint/user/userList.xhtml?faces-redirect=true";
    }

    public String newData() {
        this.selectedMaintUserInfo = this.userService.createNewDate();
        return "/contents/maint/user/userEditing.xhtml?faces-redirect=true";
    }

    public String editData() {
        this.selectedMaintUserInfo = this.userService.getMaintUsernfo(this.selectedId);
        return "/contents/maint/user/userEditing.xhtml?faces-redirect=true";
    }

    public String register() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.getExternalContext().getFlash().setKeepMessages(true);

        try {
            if (this.selectedMaintUserInfo.isNewData()) {
                this.userService.register(this.selectedMaintUserInfo);

                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "ユーザを作成しました。", null));
            } else {
                this.userService.update(this.selectedMaintUserInfo);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "ユーザを更新しました。", null));
            }

        } catch (SimpleHealthLogException e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), null));
            return null;
        }
        return "/contents/maint/user/userList.xhtml?faces-redirect=true";
    }

    public String showDeletionModal() {
        try {
            this.selectedMaintUserInfo = this.userService.getMaintUsernfo(this.selectedId);

            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("利用者削除");
            messageBuilder.append(this.selectedMaintUserInfo.getLoginId());
            messageBuilder.append("を削除します。削除後は戻せません。");

            List<String> messages = Arrays.asList(new String[] { messageBuilder.toString(), "よろしいですか？" });
            this.commonConfirmModalBean.setTitle("利用者削除");
            this.commonConfirmModalBean.setMessages(messages);
            this.commonConfirmModalBean.setVisible(true);
            this.commonConfirmModalBean.setCommandTypeName("delete");
            this.commonConfirmModalBean.setOkButtonValue("実行");
            this.commonConfirmModalBean.setCancelButtonValue("中止");
            this.dispatchMethod = getActionMethod("delete");
        } catch (SimpleHealthLogException e) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), null));
        }

        return null;
    }

    /**
     * 削除実行
     * @return
     */
    public String delete() {
        this.commonConfirmModalBean.setVisible(false);

        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.getExternalContext().getFlash().setKeepMessages(true);


        try {
            this.userService.delete(selectedMaintUserInfo);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "ユーザを削除しました。", null));
        } catch (SimpleHealthLogException e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), null));
        }

        return null;
    }

    public String cancel() {
        return "/contents/maint/user/userList.xhtml?faces-redirect=true";
    }

    public void onPasswordCheckboxChanged() {
        return;
    }

    @Override
    public void initModal() {
        this.logger.debug("initModal: " + selectedMaintUserInfo);
        this.commonConfirmModalBean.setMessage("押してね！");
        this.commonConfirmModalBean.setVisible(false);
        this.commonConfirmModalController.setModalControllerListener(this);
    }

    @Override
    public String fireAction(ModalController modalAction, String commandTypeName, String buttonName) {
        String res;
        try {
            res = (String) this.dispatchMethod.invoke(this);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new SimpleHealthLogException(e);
        }
        return res;
    }
}
