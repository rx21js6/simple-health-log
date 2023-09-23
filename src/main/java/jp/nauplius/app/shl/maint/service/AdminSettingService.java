package jp.nauplius.app.shl.maint.service;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import jp.nauplius.app.shl.common.db.model.KeyIv;
import jp.nauplius.app.shl.common.db.model.UserInfo;
import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.service.AbstractService;
import jp.nauplius.app.shl.maint.bean.CustomSettingKeyIvModel;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;

/**
 * 管理者設定サービス
 */
@Named
@SessionScoped
public class AdminSettingService extends AbstractService {
    @Inject
    private LoginInfo loginInfo;

    @Inject
    private transient ResourceBundle messageBundle;

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

        // Key/Iv表示
        KeyIv keyIv = this.entityManager.find(KeyIv.class, 1);
        this.customSettingKeyIvModel.setKey(keyIv.getEncryptionKey());
        this.customSettingKeyIvModel.setIv(keyIv.getEncryptionIv());

        // メール送信できない場合警告を表示
        if (!this.customSettingMailSender.isActive()) {
            String message = MessageFormat.format(this.messageBundle.getString("common.msg.mailSendingDisabled"), id);
            throw new SimpleHealthLogException(message);
        }
    }
}
