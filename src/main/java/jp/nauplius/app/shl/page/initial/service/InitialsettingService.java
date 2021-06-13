package jp.nauplius.app.shl.page.initial.service;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.transaction.RollbackException;

import org.apache.deltaspike.jpa.api.transaction.Transactional;

import jp.nauplius.app.shl.common.exception.DatabaseException;
import jp.nauplius.app.shl.common.model.UserInfo;
import jp.nauplius.app.shl.common.service.KeyIvHolderService;
import jp.nauplius.app.shl.common.service.MailSenderService;
import jp.nauplius.app.shl.common.util.CipherUtil;
import jp.nauplius.app.shl.page.initial.backing.InitialSettingForm;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;

@Named
public class InitialsettingService implements Serializable {
    @Inject
    private EntityManager em;

    @Inject
    private KeyIvHolderService KeyIvHolderService;

    @Inject
    private CipherUtil cipherUtil;

    @Inject
    private MailSenderService mailSenderService;

    @Inject
    private LoginInfo loginInfo;

    @Transactional
    public void register(InitialSettingForm initialSettingForm) {
        if (this.KeyIvHolderService.isRegistered()) {
            throw new DatabaseException(new RollbackException("already registered"));
        }
        this.KeyIvHolderService.registerKeyIv();

        if (this.em.find(UserInfo.class, 1) != null) {
            throw new DatabaseException(new RollbackException("already registered"));
        }
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        UserInfo loginUser = new UserInfo();
        // loginUser.setId(1); // serial型は不要
        loginUser.setLoginId(initialSettingForm.getLoginId());
        loginUser.setName(initialSettingForm.getName());
        loginUser.setMailAddress(initialSettingForm.getMailAddress());
        loginUser.setRoleId(0);
        loginUser.setDeleted(false);
        loginUser.setCreatedDate(timestamp);
        loginUser.setModifiedDate(timestamp);

        // パスワード暗号化
        String encryptedPassword = this.cipherUtil.encrypt(initialSettingForm.getPassword(),
                this.KeyIvHolderService.getKeyBytes(), this.KeyIvHolderService.getIvBytes());
        loginUser.setEncryptedPassword(encryptedPassword);
        this.em.persist(loginUser);
        this.em.merge(loginUser);
        this.em.flush();

        // メール送信
        // TODO: 後で戻す
        // this.mailSenderService.sendInitialSettingMail(initialSettingForm);

        this.loginInfo.setUserInfo(loginUser);
    }
}
