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
import org.slf4j.Logger;

import jp.nauplius.app.shl.common.constants.SecurityLevel;
import jp.nauplius.app.shl.common.db.model.UserInfo;
import jp.nauplius.app.shl.common.db.model.UserToken;
import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.interceptor.PermissionInterceptor;
import jp.nauplius.app.shl.common.service.AbstractService;
import jp.nauplius.app.shl.common.service.KeyIvHolderService;
import jp.nauplius.app.shl.common.service.TimeZoneHolderService;
import jp.nauplius.app.shl.common.util.CipherUtil;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
import jp.nauplius.app.shl.user.bean.UserEditingFormModel;
import jp.nauplius.app.shl.user.bean.UserEditingModel;
import jp.nauplius.app.shl.user.bean.UserInfoListItem;
import jp.nauplius.app.shl.user.bean.UserListModel;
import jp.nauplius.app.shl.user.constants.UserRoleId;
import jp.nauplius.app.shl.user.constants.UserStatus;

/**
 * 利用者サービス
 */
@Named
public class UserService extends AbstractService {
    @Inject
    private Logger logger;

    @Inject
    private transient ResourceBundle messageBundle;

    @Inject
    private LoginInfo loginInfo;

    @Inject
    private CipherUtil cipherUtil;

    @Inject
    private KeyIvHolderService keyIvHolderService;

    @Inject
    private TimeZoneHolderService timeZoneHolderService;

    @Inject
    private UserEditingModel userEditingModel;

    @Inject
    private UserListModel userListModel;

    /**
     * 画面から登録
     *
     */
    @PermissionInterceptor
    @Transactional
    public void register() {
        UserEditingFormModel userEditingFormModel = this.userEditingModel.getUserEditingFormModel();
        // ユーザ存在チェック
        String loginId = userEditingFormModel.getLoginId();
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
        userInfo.setLoginId(loginId);
        userInfo.setName(StringUtils.EMPTY);
        userInfo.setMailAddress(StringUtils.EMPTY);
        userInfo.setRoleId(userEditingFormModel.getRoleId());
        userInfo.setZoneId(userEditingFormModel.getZoneId());
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
        String encryptedPassword = this.cipherUtil.encrypt(userInfo, userEditingFormModel.getPassword(), keyBytes,
                ivBytes, salt);
        userInfo.setEncryptedPassword(encryptedPassword);

        String encryptedName = this.cipherUtil.encrypt(userInfo, userEditingFormModel.getName(), keyBytes, ivBytes,
                salt);
        userInfo.setEncryptedName(encryptedName);

        String encryptedMailAddress = this.cipherUtil.encrypt(userInfo,
                userEditingFormModel.getMailAddress().toLowerCase(), keyBytes, ivBytes, salt);
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
     */
    @PermissionInterceptor
    @Transactional
    public void update() {
        UserEditingFormModel userEditingFormModel = this.userEditingModel.getUserEditingFormModel();
        // ユーザ存在チェック
        String loginId = userEditingFormModel.getLoginId();
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
        userInfo.setRoleId(userEditingFormModel.getRoleId());
        userInfo.setZoneId(userEditingFormModel.getZoneId());
        userInfo.setSecurityLevel(SecurityLevel.LEVEL1.getInt()); // 固定

        LocalDateTime timestamp = LocalDateTime.now();

        // 暗号化項目の設定
        String encryptedName = this.cipherUtil.encrypt(userInfo, userEditingFormModel.getName(), keyBytes, ivBytes,
                salt);
        userInfo.setEncryptedName(encryptedName);

        String encryptedMailAddress = this.cipherUtil.encrypt(userInfo,
                userEditingFormModel.getMailAddress().toLowerCase(), keyBytes, ivBytes, salt);
        userInfo.setEncryptedMailAddress(encryptedMailAddress);

        if (userEditingFormModel.isPasswordChanged()) {
            String encryptedPassword = this.cipherUtil.encrypt(userInfo, userEditingFormModel.getPassword(), keyBytes,
                    ivBytes, salt);
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
     */
    @PermissionInterceptor
    @Transactional
    public void delete() {
        UserEditingFormModel userEditingFormModel = this.userEditingModel.getUserEditingFormModel();
        // ユーザ存在チェック
        String loginId = userEditingFormModel.getLoginId();

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
     */
    @PermissionInterceptor
    public void loadUserInfoListItems() {
        List<UserInfoListItem> userInfoListItems = new ArrayList<>();

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

            userInfoListItems.add(item);

        }

        this.userListModel.setUserInfoListItems(userInfoListItems);
    }

    /**
     * 新規登録用情報生成
     *
     */
    @PermissionInterceptor
    public void createNewData() {
        UserEditingFormModel userEditingFormModel = new UserEditingFormModel();
        userEditingFormModel.setId(0);
        userEditingFormModel.setPasswordChanged(true);
        userEditingFormModel.setRoleId(UserRoleId.USER.getInt());

        this.userEditingModel.setNewData(true);
        this.userEditingModel.setTimeZoneInfos(this.timeZoneHolderService.getTimeZoneInfos());

        this.userEditingModel.setUserEditingFormModel(userEditingFormModel);
    }

    /**
     * ユーザ管理画面用の情報を取得
     *
     */
    @PermissionInterceptor
    public void loadMaintUsernfo() {
        UserInfo userInfo = this.entityManager.find(UserInfo.class, this.userListModel.getSelectedId());
        if (userInfo == null) {
            throw new SimpleHealthLogException(
                    this.messageBundle.getString("contents.maint.user.userEditing.msg.notFound"));
        }

        if (userInfo.getDeleted()) {
            throw new SimpleHealthLogException(
                    this.messageBundle.getString("contents.maint.user.userEditing.msg.alreadyDeleted"));
        }
        try {
            UserEditingFormModel userEditingFormModel = new UserEditingFormModel();
            BeanUtils.copyProperties(userEditingFormModel, userInfo);

            this.userEditingModel.setNewData(false);
            this.userEditingModel.setTimeZoneInfos(this.timeZoneHolderService.getTimeZoneInfos());

            if (SecurityLevel.LEVEL0.getInt() < userInfo.getSecurityLevel()) {
                // 新形式の場合
                byte[] keyBytes = this.keyIvHolderService.getKeyBytes();
                byte[] ivBytes = this.keyIvHolderService.getIvBytes();
                String salt = this.keyIvHolderService.getSalt();

                String plainName = this.cipherUtil.decrypt(userInfo, userInfo.getEncryptedName(), keyBytes, ivBytes,
                        salt);
                String plainMailAddress = this.cipherUtil.decrypt(userInfo, userInfo.getEncryptedMailAddress(),
                        keyBytes, ivBytes, salt);

                userEditingFormModel.setName(plainName);
                userEditingFormModel.setMailAddress(plainMailAddress);
            }

            this.userEditingModel.setUserEditingFormModel(userEditingFormModel);

        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            this.logger.error(e.getMessage());
        }
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
        String plainPassword = this.cipherUtil.decrypt(userInfo, userInfo.getEncryptedPassword(), keyBytes, ivBytes,
                salt);

        // 強化処理
        userInfo.setSecurityLevel(SecurityLevel.LEVEL1.getInt());

        String encryptedPassword = this.cipherUtil.encrypt(userInfo, plainPassword, keyBytes, ivBytes, salt);
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
