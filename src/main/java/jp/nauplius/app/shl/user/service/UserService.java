package jp.nauplius.app.shl.user.service;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.TypedQuery;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.jpa.api.transaction.Transactional;

import jp.nauplius.app.shl.common.constants.SecurityLevel;
import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.interceptor.PermissionInterceptor;
import jp.nauplius.app.shl.common.model.UserInfo;
import jp.nauplius.app.shl.common.model.UserToken;
import jp.nauplius.app.shl.common.service.AbstractService;
import jp.nauplius.app.shl.common.service.KeyIvHolderService;
import jp.nauplius.app.shl.common.util.CipherUtil;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
import jp.nauplius.app.shl.user.bean.MaintUserInfo;
import jp.nauplius.app.shl.user.bean.UserInfoListItem;
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
    @PermissionInterceptor
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
        String salt = this.keyIvHolderService.getSalt();

        // 登録
        UserInfo userInfo = new UserInfo();
        LocalDateTime timestamp = LocalDateTime.now();
        userInfo.setEncryptedPassword(StringUtils.EMPTY);
        userInfo.setLoginId(maintUserInfo.getLoginId());
        userInfo.setName(StringUtils.EMPTY);
        userInfo.setMailAddress(StringUtils.EMPTY);
        userInfo.setRoleId(maintUserInfo.getRoleId());
        userInfo.setZoneId(maintUserInfo.getZoneId());
        userInfo.setStatus(UserStatus.REGISTERED.getInt()); // いきなり本登録、本来は仮登録
        userInfo.setDeleted(false);
        userInfo.setCreatedBy(this.loginInfo.getUserInfo().getId());
        userInfo.setCreatedDate(Timestamp.valueOf(timestamp));
        userInfo.setModifiedBy(this.loginInfo.getUserInfo().getId());
        userInfo.setModifiedDate(Timestamp.valueOf(timestamp));
        userInfo.setSecurityLevel(SecurityLevel.LEVEL1.getInt());

        this.entityManager.persist(userInfo);

        this.entityManager.flush();

        // 暗号化項目の設定
        String encryptedPassword = this.cipherUtil.encrypt(userInfo, maintUserInfo.getPassword(), keyBytes, ivBytes,
                salt);
        userInfo.setEncryptedPassword(encryptedPassword);

        String encryptedName = this.cipherUtil.encrypt(userInfo, maintUserInfo.getName(), keyBytes, ivBytes, salt);
        userInfo.setEncryptedName(encryptedName);

        String encryptedMailAddress = this.cipherUtil.encrypt(userInfo, maintUserInfo.getMailAddress().toLowerCase(),
                keyBytes, ivBytes, salt);
        userInfo.setEncryptedMailAddress(encryptedMailAddress);

        this.entityManager.merge(userInfo);
        this.entityManager.flush();
        this.entityManager.clear();

        // 仮トークン生成
        // token = this.createToken(userInfo, true);
        // TODO: メールで送信

    }

    /**
     * 更新
     *
     * @param maintUserInfo 利用者情報
     */
    @PermissionInterceptor
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
        String salt = this.keyIvHolderService.getSalt();

        // 更新
        UserInfo userInfo = results.get(0);
        userInfo.setName(StringUtils.EMPTY);
        userInfo.setMailAddress(StringUtils.EMPTY);
        userInfo.setRoleId(maintUserInfo.getRoleId());
        userInfo.setZoneId(maintUserInfo.getZoneId());
        userInfo.setSecurityLevel(SecurityLevel.LEVEL1.getInt()); // 固定

        LocalDateTime timestamp = LocalDateTime.now();

        // 暗号化項目の設定
        String encryptedName = this.cipherUtil.encrypt(userInfo, maintUserInfo.getName(), keyBytes, ivBytes, salt);
        userInfo.setEncryptedName(encryptedName);

        String encryptedMailAddress = this.cipherUtil.encrypt(userInfo, maintUserInfo.getMailAddress().toLowerCase(),
                keyBytes, ivBytes, salt);
        userInfo.setEncryptedMailAddress(encryptedMailAddress);

        if (maintUserInfo.isPasswordChanged()) {
            String encryptedPassword = this.cipherUtil.encrypt(userInfo, maintUserInfo.getPassword(), keyBytes, ivBytes,
                    salt);
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
    @PermissionInterceptor
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
    @PermissionInterceptor
    public List<UserInfoListItem> loadMaintUserInfos() {
        List<UserInfoListItem> results = new ArrayList<>();

        TypedQuery<UserInfo> query = this.entityManager
                .createQuery("SELECT u FROM UserInfo u WHERE u.deleted = FALSE ORDER BY u.id", UserInfo.class);

        byte[] keyBytes = this.keyIvHolderService.getKeyBytes();
        byte[] ivBytes = this.keyIvHolderService.getIvBytes();
        String salt = this.keyIvHolderService.getSalt();

        List<UserInfo> userInfos = query.getResultList();
        for (UserInfo userInfo : userInfos) {

            UserInfoListItem item = new UserInfoListItem(userInfo.getId(), userInfo.getLoginId(), userInfo.getName(),
                    userInfo.getMailAddress(), userInfo.getRoleId());
            if (userInfo.getSecurityLevel() == SecurityLevel.LEVEL1.getInt()) {
                String plainName = this.cipherUtil.decrypt(userInfo, userInfo.getEncryptedName(), keyBytes, ivBytes,
                        salt);
                String plainMailAddress = this.cipherUtil.decrypt(userInfo, userInfo.getEncryptedMailAddress(),
                        keyBytes, ivBytes, salt);

                item.setName(plainName);
                item.setMailAddress(plainMailAddress);
            }

            results.add(item);

        }

        return results;
    }

    /**
     * 新規登録用情報生成
     *
     * @return {@link MaintUserInfo}
     */
    @PermissionInterceptor
    public MaintUserInfo createNewData() {
        MaintUserInfo maintUserInfo = new MaintUserInfo();
        maintUserInfo.setId(0);
        maintUserInfo.setNewData(true);
        maintUserInfo.setPasswordChanged(true);
        maintUserInfo.setRoleId(UserRoleId.NORMAL.getInt());

        return maintUserInfo;
    }

    /**
     * ユーザ管理画面用の情報を取得
     * @param id
     * @return {@link MaintUserInfo}
     */
    @PermissionInterceptor
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

            if (SecurityLevel.LEVEL0.getInt() < userInfo.getSecurityLevel()) {
                // 新形式の場合
                byte[] keyBytes = this.keyIvHolderService.getKeyBytes();
                byte[] ivBytes = this.keyIvHolderService.getIvBytes();
                String salt = this.keyIvHolderService.getSalt();

                String plainName = this.cipherUtil.decrypt(userInfo, userInfo.getEncryptedName(), keyBytes, ivBytes,
                        salt);
                String plainMailAddress = this.cipherUtil.decrypt(userInfo, userInfo.getEncryptedMailAddress(),
                        keyBytes, ivBytes, salt);

                maintUserInfo.setName(plainName);
                maintUserInfo.setMailAddress(plainMailAddress);
            }

        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();

        }

        return maintUserInfo;
    }

    /**
     * セキュリティ強化処理実行
     */
    @Transactional
    public void performSecurityEnhancement() {
        String loginId = this.loginInfo.getUserInfo().getLoginId();
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
        String salt = this.keyIvHolderService.getSalt();

        // 更新
        UserInfo userInfo = results.get(0);

        if (userInfo.getSecurityLevel() == SecurityLevel.LEVEL1.getInt()) {
            return;
        }

        // 現在のパスワードを取得
        String plainPassword = this.cipherUtil.decrypt(userInfo, userInfo.getEncryptedPassword(), keyBytes, ivBytes, salt);

        // 強化処理
        userInfo.setSecurityLevel(SecurityLevel.LEVEL1.getInt());

        String encryptedPassword = this.cipherUtil.encrypt(userInfo, plainPassword, keyBytes, ivBytes,
                salt);
        userInfo.setEncryptedPassword(encryptedPassword);

        String encryptedName = this.cipherUtil.encrypt(userInfo, userInfo.getName(), keyBytes, ivBytes, salt);
        userInfo.setEncryptedName(encryptedName);

        String encryptedMailAddress = this.cipherUtil.encrypt(userInfo, userInfo.getMailAddress().toLowerCase(),
                keyBytes, ivBytes, salt);
        userInfo.setEncryptedMailAddress(encryptedMailAddress);

        userInfo.setName(StringUtils.EMPTY);
        userInfo.setMailAddress(StringUtils.EMPTY);

        userInfo.setModifiedBy(this.loginInfo.getUserInfo().getId());
        userInfo.setModifiedDate(Timestamp.valueOf(LocalDateTime.now()));
        this.entityManager.merge(userInfo);
        this.entityManager.flush();
        this.entityManager.clear();

        // ログイン情報再設定
        this.loginInfo.setUserInfo(userInfo);
    }
}
