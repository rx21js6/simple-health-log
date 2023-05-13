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

import jp.nauplius.app.shl.common.constants.SecurityLevel;
import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.model.KeyIv;
import jp.nauplius.app.shl.common.model.UserInfo;
import jp.nauplius.app.shl.common.service.AbstractService;
import jp.nauplius.app.shl.common.service.KeyIvHolderService;
import jp.nauplius.app.shl.common.service.TimeZoneHolderService;
import jp.nauplius.app.shl.common.util.CipherUtil;
import jp.nauplius.app.shl.maint.backing.CustomSettingKeyIvModel;
import jp.nauplius.app.shl.maint.backing.CustomSettingMailAddressModel;
import jp.nauplius.app.shl.maint.backing.CustomSettingPasswordModel;
import jp.nauplius.app.shl.maint.bean.TimeZoneInputModel;
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
    private TimeZoneHolderService timeZoneHolderService;

    @Inject
    @Getter
    private CustomSettingPasswordModel customSettingPasswordModel;

    @Inject
    private CustomSettingMailAddressModel customSettingMailAddressModel;

    @Inject
    private CustomSettingMailSender customSettingMailSender;

    @Inject
    private CustomSettingKeyIvModel customSettingKeyIvModel;

    @Inject
    private TimeZoneInputModel timeZoneInputModel;

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

        if (userInfo.getSecurityLevel() == SecurityLevel.LEVEL1.getInt()) {
            byte[] keyBytes = this.keyIvHolderService.getKeyBytes();
            byte[] ivBytes = this.keyIvHolderService.getIvBytes();
            String salt = this.keyIvHolderService.getSalt();
            String currentMailAddress = this.cipherUtil.decrypt(userInfo, userInfo.getEncryptedMailAddress(), keyBytes,
                    ivBytes, salt);
            this.customSettingMailAddressModel.setCurrentMailAddress(currentMailAddress);
        } else {
            this.customSettingMailAddressModel.setCurrentMailAddress(userInfo.getMailAddress());
        }

        this.customSettingMailAddressModel.setMailAddress(StringUtils.EMPTY);

        this.customSettingPasswordModel.setPassword(StringUtils.EMPTY);
        this.customSettingPasswordModel.setPasswordReenter(StringUtils.EMPTY);

        // タイムゾーン設定
        this.loadTimeZone();

        // メール送信できない場合警告を表示
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
        String salt = this.keyIvHolderService.getSalt();

        // 更新
        String encryptedPassword = null;
        encryptedPassword = this.cipherUtil.encrypt(userInfo, this.customSettingPasswordModel.getPassword(), keyBytes,
                ivBytes, salt);
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

        if (userInfo.getSecurityLevel() == SecurityLevel.LEVEL1.getInt()) {
            byte[] keyBytes = this.keyIvHolderService.getKeyBytes();
            byte[] ivBytes = this.keyIvHolderService.getIvBytes();
            String salt = this.keyIvHolderService.getSalt();
            String encryptedMailAddress = this.cipherUtil.encrypt(userInfo, mailAddress, keyBytes, ivBytes, salt);
            userInfo.setEncryptedMailAddress(encryptedMailAddress);
        } else {
            userInfo.setMailAddress(mailAddress);
        }

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

    @Transactional
    public void changeTimeZone() {
        // ユーザ存在チェック
        int id = this.loginInfo.getUserInfo().getId();
        UserInfo userInfo = this.entityManager.find(UserInfo.class, id);
        if (Objects.isNull(userInfo)) {
            String message = MessageFormat
                    .format(this.messageBundle.getString("contents.maint.user.userEditing.msg.userNotFound"), id);
            throw new SimpleHealthLogException(message);
        }

        // 更新
        userInfo.setZoneId(this.timeZoneInputModel.getSelectedZoneId());
        userInfo.setModifiedBy(this.loginInfo.getUserInfo().getId());
        userInfo.setModifiedDate(Timestamp.valueOf(LocalDateTime.now()));
        this.entityManager.merge(userInfo);
        this.entityManager.flush();
        this.entityManager.clear();

        this.loginInfo.getUserInfo().setZoneId(this.timeZoneInputModel.getSelectedZoneId());

    }

    private void loadTimeZone() {
        this.timeZoneInputModel.setTimeZoneInfos(this.timeZoneHolderService.getTimeZoneInfos());
        this.timeZoneInputModel.setSelectedZoneId(this.loginInfo.getUserInfo().getZoneId());
    }
}
