package jp.nauplius.app.shl.maint.service;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.jpa.api.transaction.Transactional;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.model.KeyIv;
import jp.nauplius.app.shl.common.model.UserInfo;
import jp.nauplius.app.shl.common.service.AbstractService;
import jp.nauplius.app.shl.common.service.KeyIvHolderService;
import jp.nauplius.app.shl.common.util.CipherUtil;
import jp.nauplius.app.shl.maint.backing.CustomSettingKeyIvModel;
import jp.nauplius.app.shl.maint.backing.CustomSettingMailAddressModel;
import jp.nauplius.app.shl.maint.backing.CustomSettingPasswordModel;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
import lombok.Getter;

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
    @Getter
    private CustomSettingPasswordModel customSettingPasswordModel;

    @Inject
    private CustomSettingMailAddressModel customSettingMailAddressModel;

    @Inject
    private CustomSettingMailSender customSettingMailSender;

    @Inject
    private CustomSettingKeyIvModel customSettingKeyIvModel;

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

        KeyIv keyIv = this.entityManager.find(KeyIv.class, 1);
        this.customSettingKeyIvModel.setKey(keyIv.getEncryptionKey());
        this.customSettingKeyIvModel.setIv(keyIv.getEncryptionIv());

        this.customSettingMailAddressModel.setCurrentMailAddress(userInfo.getMailAddress());
        this.customSettingMailAddressModel.setMailAddress(StringUtils.EMPTY);

        this.customSettingPasswordModel.setPassword(StringUtils.EMPTY);
        this.customSettingPasswordModel.setPasswordReenter(StringUtils.EMPTY);

        if (!this.customSettingMailSender.isActive()) {
            String message = MessageFormat.format(this.messageBundle.getString("common.msg.mailSendingDisabled"), id);
            throw new SimpleHealthLogException(message);
        }
    }

    /**
     * パスワード変更
     */
    @Transactional
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

        // メール送信
        this.customSettingMailSender.sendPasswordChangedMail(this.loginInfo.getUserInfo().getMailAddress(),
                this.getAdminMailAddress());

        this.customSettingPasswordModel.setPassword(StringUtils.EMPTY);
        this.customSettingPasswordModel.setPasswordReenter(StringUtils.EMPTY);

    }

    /**
     * テストメール送信
     */
    public void sendTestMail() {
        String mailAddress = this.customSettingMailAddressModel.getMailAddress();
        this.customSettingMailSender.sendTestMail(mailAddress, this.getAdminMailAddress());
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
        String mailAddress = this.customSettingMailAddressModel.getMailAddress();
        userInfo.setMailAddress(mailAddress);
        userInfo.setModifiedBy(this.loginInfo.getUserInfo().getId());
        userInfo.setModifiedDate(Timestamp.valueOf(LocalDateTime.now()));
        this.entityManager.merge(userInfo);
        this.entityManager.flush();
        this.entityManager.clear();

        // ログインセッション更新
        this.loginInfo.setUserInfo(userInfo);

        // メール送信
        this.customSettingMailSender.sendAddressChangedMail(this.customSettingMailAddressModel.getMailAddress(),
                this.getAdminMailAddress());

        this.customSettingMailAddressModel.setCurrentMailAddress(mailAddress);
        this.customSettingMailAddressModel.setMailAddress(StringUtils.EMPTY);
    }
}
