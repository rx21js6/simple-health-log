package jp.nauplius.app.shl.maint.service;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.jpa.api.transaction.Transactional;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.model.UserInfo;
import jp.nauplius.app.shl.common.service.AbstractService;
import jp.nauplius.app.shl.common.service.KeyIvHolderService;
import jp.nauplius.app.shl.common.util.CipherUtil;
import jp.nauplius.app.shl.maint.backing.CustomSettingMailAddressModel;
import jp.nauplius.app.shl.maint.backing.CustomSettingPasswordModel;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;

@Named
@SessionScoped
public class CustomSettingService extends AbstractService {
    @Inject
    private LoginInfo loginInfo;

    @Inject
    private transient ResourceBundle messageBundle;

    @Inject
    private CipherUtil cipherUtil;

    @Inject
    private KeyIvHolderService keyIvHolderService;

    @Inject
    private CustomSettingPasswordModel customSettingPasswordModel;

    @Inject
    private CustomSettingMailAddressModel customSettingMailAddressModel;

    @Inject
    private CustomSettingMailSender customSettingMailSender;

    /**
     * 情報ロード
     */
    public void load() {
        // ユーザ存在チェック
        int id = this.loginInfo.getUserInfo().getId();
        UserInfo userInfo = this.entityManager.find(UserInfo.class, id);
        if (Objects.isNull(userInfo)) {
            String message = MessageFormat
                    .format(this.messageBundle.getString("contents.maint.user.userEditing.msg.userNotFound"), id);
            throw new SimpleHealthLogException(message);
        }

        this.customSettingMailAddressModel.setCurrentMailAddress(userInfo.getMailAddress());
    }

    /**
     * パスワード変更
     */
    public void changePassword() {
        // ユーザ存在チェック
        int id = this.loginInfo.getUserInfo().getId();
        UserInfo userInfo = this.entityManager.find(UserInfo.class, id);
        if (Objects.isNull(userInfo)) {
            String message = MessageFormat
                    .format(this.messageBundle.getString("contents.maint.user.userEditing.msg.userNotFound"), id);
            throw new SimpleHealthLogException(message);
        }

        byte[] keyBytes = this.keyIvHolderService.getKeyBytes();
        byte[] ivBytes = this.keyIvHolderService.getIvBytes();

        // 更新
        String encryptedPassword = this.cipherUtil.encrypt(this.customSettingPasswordModel.getPassword(), keyBytes,
                ivBytes);
        userInfo.setEncryptedPassword(encryptedPassword);
        userInfo.setModifiedBy(this.loginInfo.getUserInfo().getId());
        userInfo.setModifiedDate(Timestamp.valueOf(LocalDateTime.now()));
        this.entityManager.merge(userInfo);
        this.entityManager.flush();
        this.entityManager.clear();

        // ログインセッション更新
        this.loginInfo.setUserInfo(userInfo);

        // TODO: メールで送信

    }

    /**
     * テストメール送信
     */
    public void sendTestMail() {
        this.customSettingMailSender.sendTestMail(this.customSettingMailAddressModel.getMailAddress());
    }

    /**
     * メールアドレス変更
     */
    @Transactional
    public void changeMailAddress() {
        // ユーザ存在チェック
        int id = this.loginInfo.getUserInfo().getId();
        UserInfo userInfo = this.entityManager.find(UserInfo.class, id);
        if (Objects.isNull(userInfo)) {
            String message = MessageFormat
                    .format(this.messageBundle.getString("contents.maint.user.userEditing.msg.userNotFound"), id);
            throw new SimpleHealthLogException(message);
        }

        // 更新
        userInfo.setMailAddress(this.customSettingMailAddressModel.getMailAddress());
        userInfo.setModifiedBy(this.loginInfo.getUserInfo().getId());
        userInfo.setModifiedDate(Timestamp.valueOf(LocalDateTime.now()));
        this.entityManager.merge(userInfo);
        this.entityManager.flush();
        this.entityManager.clear();

        // ログインセッション更新
        this.loginInfo.setUserInfo(userInfo);

        this.customSettingMailSender.sendChangedMail(this.customSettingMailAddressModel.getMailAddress());
    }
}
