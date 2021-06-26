package jp.nauplius.app.shl.user.service;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.slf4j.Logger;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.model.UserInfo;
import jp.nauplius.app.shl.common.model.UserToken;
import jp.nauplius.app.shl.common.service.KeyIvHolderService;
import jp.nauplius.app.shl.common.util.CipherUtil;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
import jp.nauplius.app.shl.user.bean.MaintUserInfo;
import jp.nauplius.app.shl.user.bean.PasswordResetForm;
import jp.nauplius.app.shl.user.constants.UserRoleId;
import jp.nauplius.app.shl.user.constants.UserStatus;

/**
 * 利用者サービス
 */
@Named
public class UserService implements Serializable {
    @Inject
    private Logger logger;

    @Inject
    private LoginInfo loginInfo;

    @Inject
    private EntityManager em;

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
        TypedQuery<UserInfo> userInfoQuery = this.em
                .createQuery("SELECT ui FROM UserInfo ui WHERE ui.loginId = :loginId", UserInfo.class);
        userInfoQuery.setParameter("loginId", loginId);
        List<UserInfo> results = userInfoQuery.getResultList();
        if (0 < results.size()) {
            throw new SimpleHealthLogException(String.format("該当利用者は登録済みです。: %s", loginId));
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
        this.em.persist(userInfo);

        // 仮トークン生成
        // token = this.createToken(userInfo, true);
        // TODO: メールで送信

        this.em.flush();
        this.em.clear();

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
        TypedQuery<UserInfo> userInfoQuery = this.em
                .createQuery("SELECT ui FROM UserInfo ui WHERE ui.loginId = :loginId", UserInfo.class);
        userInfoQuery.setParameter("loginId", loginId);
        List<UserInfo> results = userInfoQuery.getResultList();

        if (results.size() == 0) {
            throw new SimpleHealthLogException(String.format("該当利用者が存在しません。: %s", loginId));
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
        this.em.merge(userInfo);
        this.em.flush();
        this.em.clear();

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
        TypedQuery<UserInfo> userInfoQuery = this.em
                .createQuery("SELECT ui FROM UserInfo ui WHERE ui.loginId = :loginId", UserInfo.class);
        userInfoQuery.setParameter("loginId", loginId);
        List<UserInfo> results = userInfoQuery.getResultList();
        if (results.size() == 0) {
            throw new SimpleHealthLogException(String.format("該当利用者は削除済みです。: %s", loginId));
        }
        UserInfo tmpUserInfo = results.get(0);

        Timestamp updatedDate = Timestamp.valueOf(LocalDateTime.now());
        tmpUserInfo.setDeleted(true);
        tmpUserInfo.setDeletedDate(updatedDate);
        tmpUserInfo.setModifiedBy(this.loginInfo.getUserInfo().getId());
        tmpUserInfo.setModifiedDate(updatedDate);
        this.em.merge(tmpUserInfo);
        this.em.flush();
        this.em.clear();
    }

    /**
     * トークン文字列生成～更新
     *
     * @param userInfo {@link UserInfo}
     * @param newRec   新規作成の場合true
     * @return 生成したトークン文字列
     */
    private String createToken(UserInfo userInfo, boolean newRec) {
        try {
            SecureRandom random = new SecureRandom();
            TypedQuery<UserToken> query = this.em.createQuery("SELECT t FROM UserToken t WHERE t.token = :token",
                    UserToken.class);

            String token = null;
            List<UserToken> results = null;

            do {
                byte[] randomBytes = random.generateSeed(64);
                token = Base64.getEncoder().encodeToString(randomBytes);
                query.setParameter("token", token);
                results = query.getResultList();
            } while (0 < results.size());

            UserToken userToken = null;
            if (newRec) {
                userToken = new UserToken();
                userToken.setId(userInfo.getId());
                this.em.persist(userToken);
            } else {
                userToken = this.em.find(UserToken.class, userInfo.getId());
            }
            userToken.setToken(token);
            this.em.merge(userToken);

            return token;

        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 利用者管理画面用の利用者一覧取得
     *
     * @return
     */
    public List<UserInfo> loadMaintUserInfos() {
        TypedQuery<UserInfo> query = this.em
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
        UserInfo userInfo = this.em.find(UserInfo.class, id);
        if (userInfo == null) {
            throw new SimpleHealthLogException("利用者が見つかりません。");
        }

        if (userInfo.getDeleted()) {
            throw new SimpleHealthLogException("利用者は削除済みです。");
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
