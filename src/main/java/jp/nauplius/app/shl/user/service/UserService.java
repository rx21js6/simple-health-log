package jp.nauplius.app.shl.user.service;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.TypedQuery;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.deltaspike.jpa.api.transaction.Transactional;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.model.UserInfo;
import jp.nauplius.app.shl.common.model.UserToken;
import jp.nauplius.app.shl.common.service.AbstractService;
import jp.nauplius.app.shl.common.service.KeyIvHolderService;
import jp.nauplius.app.shl.common.util.CipherUtil;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
import jp.nauplius.app.shl.user.bean.MaintUserInfo;
import jp.nauplius.app.shl.user.constants.UserRoleId;
import jp.nauplius.app.shl.user.constants.UserStatus;

/**
 * 利用者サービス
 */
@Named
public class UserService extends AbstractService {
    @Inject
    private transient ResourceBundle messageBundle;

    @Inject
    private LoginInfo loginInfo;

    @Inject
    private CipherUtil cipherUtil;

    @Inject
    private KeyIvHolderService keyIvHolderService;

    /**
     * 画面から登録
     *
     * @param userRegistration
     */
    @Transactional
    public void register(MaintUserInfo maintUserInfo) {

        // ユーザ存在チェック
        String loginId = maintUserInfo.getLoginId();
        TypedQuery<UserInfo> userInfoQuery = this.entityManager
                .createQuery("SELECT ui FROM UserInfo ui WHERE ui.loginId = :loginId", UserInfo.class);
        userInfoQuery.setParameter("loginId", loginId);
        List<UserInfo> results = userInfoQuery.getResultList();
        if (0 < results.size()) {
            String message = MessageFormat.format(
                    this.messageBundle.getString("contents.maint.user.userEditing.msg.userAlreadyRegistered"), loginId);
            throw new SimpleHealthLogException(message);
        }

        byte[] keyBytes = this.keyIvHolderService.getKeyBytes();
        byte[] ivBytes = this.keyIvHolderService.getIvBytes();

        // 登録
        UserInfo userInfo = new UserInfo();
        LocalDateTime timestamp = LocalDateTime.now();
        String encryptedPassword = this.cipherUtil.encrypt(maintUserInfo.getPassword(), keyBytes, ivBytes);
        userInfo.setEncryptedPassword(encryptedPassword);

        userInfo.setLoginId(maintUserInfo.getLoginId());
        userInfo.setName(maintUserInfo.getName());
        userInfo.setMailAddress(maintUserInfo.getMailAddress().toLowerCase());
        userInfo.setRoleId(maintUserInfo.getRoleId());
        userInfo.setStatus(UserStatus.REGISTERED.getInt()); // いきなり本登録、本来は仮登録
        userInfo.setDeleted(false);
        userInfo.setCreatedBy(this.loginInfo.getUserInfo().getId());
        userInfo.setCreatedDate(Timestamp.valueOf(timestamp));
        userInfo.setModifiedBy(this.loginInfo.getUserInfo().getId());
        userInfo.setModifiedDate(Timestamp.valueOf(timestamp));
        this.entityManager.persist(userInfo);

        // 仮トークン生成
        // token = this.createToken(userInfo, true);
        // TODO: メールで送信

        this.entityManager.flush();
        this.entityManager.clear();

    }

    /**
     * 更新
     *
     * @param maintUserInfo 利用者情報
     */
    @Transactional
    public void update(MaintUserInfo maintUserInfo) {

        // ユーザ存在チェック
        String loginId = maintUserInfo.getLoginId();
        TypedQuery<UserInfo> userInfoQuery = this.entityManager
                .createQuery("SELECT ui FROM UserInfo ui WHERE ui.loginId = :loginId", UserInfo.class);
        userInfoQuery.setParameter("loginId", loginId);
        List<UserInfo> results = userInfoQuery.getResultList();

        if (results.size() == 0) {
            String message = MessageFormat
                    .format(this.messageBundle.getString("contents.maint.user.userEditing.msg.userNotFound"), loginId);
            throw new SimpleHealthLogException(message);
        }

        byte[] keyBytes = this.keyIvHolderService.getKeyBytes();
        byte[] ivBytes = this.keyIvHolderService.getIvBytes();

        // 更新
        UserInfo userInfo = results.get(0);
        userInfo.setName(maintUserInfo.getName());
        userInfo.setMailAddress(maintUserInfo.getMailAddress().toLowerCase());
        userInfo.setRoleId(maintUserInfo.getRoleId());

        LocalDateTime timestamp = LocalDateTime.now();

        if (maintUserInfo.isPasswordChanged()) {
            String encryptedPassword = this.cipherUtil.encrypt(maintUserInfo.getPassword(), keyBytes, ivBytes);
            userInfo.setEncryptedPassword(encryptedPassword);
        }
        userInfo.setModifiedBy(this.loginInfo.getUserInfo().getId());
        userInfo.setModifiedDate(Timestamp.valueOf(timestamp));
        this.entityManager.merge(userInfo);
        this.entityManager.flush();
        this.entityManager.clear();

        // TODO: メールで送信

    }

    /**
     * 利用者削除（論理削除）
     *
     * @param maintUserInfo
     */
    @Transactional
    public void delete(MaintUserInfo maintUserInfo) {
        // ユーザ存在チェック
        String loginId = maintUserInfo.getLoginId();

        TypedQuery<UserInfo> userInfoQuery = this.entityManager
                .createQuery("SELECT ui FROM UserInfo ui WHERE ui.loginId = :loginId", UserInfo.class);
        userInfoQuery.setParameter("loginId", loginId);
        List<UserInfo> results = userInfoQuery.getResultList();
        if (results.size() == 0) {
            String message = MessageFormat.format(
                    this.messageBundle.getString("contents.maint.user.userEditing.msg.userAlreadyDeleted"), loginId);
            throw new SimpleHealthLogException(message);
        }
        UserInfo tmpUserInfo = results.get(0);

        // 自身は削除できない
        if (tmpUserInfo.getId().equals(this.loginInfo.getUserInfo().getId())) {
            throw new SimpleHealthLogException(
                    this.messageBundle.getString("contents.maint.user.userList.msg.deleteOwnAccount"));
        }

        // トークン削除
        UserToken tmpUserToken = this.entityManager.find(UserToken.class, tmpUserInfo.getId());
        if (Objects.nonNull(tmpUserToken)) {
            this.entityManager.remove(tmpUserToken);
        }

        // 削除フラグを追加
        Timestamp updatedDate = Timestamp.valueOf(LocalDateTime.now());
        tmpUserInfo.setDeleted(true);
        tmpUserInfo.setDeletedDate(updatedDate);
        tmpUserInfo.setModifiedBy(this.loginInfo.getUserInfo().getId());
        tmpUserInfo.setModifiedDate(updatedDate);
        this.entityManager.merge(tmpUserInfo);
        this.entityManager.flush();
        this.entityManager.clear();
    }

    /**
     * 利用者管理画面用の利用者一覧取得
     *
     * @return
     */
    public List<UserInfo> loadMaintUserInfos() {
        TypedQuery<UserInfo> query = this.entityManager
                .createQuery("SELECT u FROM UserInfo u WHERE u.deleted = FALSE ORDER BY u.id", UserInfo.class);

        return query.getResultList();
    }

    /**
     * 新規登録用情報生成
     *
     * @return
     */
    public MaintUserInfo createNewDate() {
        MaintUserInfo maintUserInfo = new MaintUserInfo();
        maintUserInfo.setId(0);
        maintUserInfo.setNewData(true);
        maintUserInfo.setPasswordChanged(true);
        maintUserInfo.setRoleId(UserRoleId.NORMAL.getInt());

        return maintUserInfo;
    }

    public MaintUserInfo getMaintUsernfo(int id) {
        UserInfo userInfo = this.entityManager.find(UserInfo.class, id);
        if (userInfo == null) {
            throw new SimpleHealthLogException(
                    this.messageBundle.getString("contents.maint.user.userEditing.msg.notFound"));
        }

        if (userInfo.getDeleted()) {
            throw new SimpleHealthLogException(
                    this.messageBundle.getString("contents.maint.user.userEditing.msg.alreadyDeleted"));
        }
        MaintUserInfo maintUserInfo = new MaintUserInfo();
        try {
            BeanUtils.copyProperties(maintUserInfo, userInfo);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return maintUserInfo;
    }
}
