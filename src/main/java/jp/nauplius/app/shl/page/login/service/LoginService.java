package jp.nauplius.app.shl.page.login.service;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.slf4j.Logger;

import jp.nauplius.app.shl.common.constants.SecurityLevel;
import jp.nauplius.app.shl.common.db.model.UserInfo;
import jp.nauplius.app.shl.common.db.model.UserToken;
import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.service.AbstractService;
import jp.nauplius.app.shl.common.ui.bean.KeyIvHolder;
import jp.nauplius.app.shl.common.util.CipherUtil;
import jp.nauplius.app.shl.common.util.PasswordUtil;
import jp.nauplius.app.shl.common.util.PasswordUtil.PasswordStrength;
import jp.nauplius.app.shl.page.login.bean.LoginForm;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
import jp.nauplius.app.shl.page.login.bean.LoginResponse;
import jp.nauplius.app.shl.user.bean.PasswordResetForm;
import jp.nauplius.app.shl.ws.bean.GetUsersResponse;

/**
 * ログインサービス
 *
 */
@Named
@SessionScoped
public class LoginService extends AbstractService {
    @Inject
    private Logger logger;

    @Inject
    private transient ResourceBundle messageBundle;

    @Inject
    private CipherUtil cipherUtil;

    @Inject
    private KeyIvHolder keyIvHolder;

    @Inject
    private LoginMailSender loginMailSender;

    @Inject
    private LoginInfo loginInfo;

    private SecureRandom random = new SecureRandom();

    private static final int DEFAULT_EXPIRATION_DATE = 3;

    /**
     * ログイン
     */
    @Transactional
    public LoginResponse login(LoginForm loginForm) {
        this.logger.info(String.format("login: loginId: %s / keepLogin: %n", loginForm.getLoginId()),
                loginForm.isLoggingPersistent());

        LoginResponse loginResponse = new LoginResponse();

        byte[] keyBytes = this.keyIvHolder.getKeyBytes();
        byte[] ivBytes = this.keyIvHolder.getIvBytes();
        String salt = this.keyIvHolder.getSalt();

        TypedQuery<UserInfo> query = this.entityManager.createQuery(
                "SELECT ui FROM UserInfo ui WHERE ui.loginId = :loginId AND ui.deleted = cast('false' as boolean)",
                UserInfo.class);
        query.setParameter("loginId", loginForm.getLoginId());
        List<UserInfo> results = query.getResultList();
        if (results.size() == 0) {
            throw new SimpleHealthLogException(this.messageBundle.getString("login.login.msg.idPasswordInvalid"));
        }

        UserInfo userInfo = results.get(0);
        String encryptedPassword = userInfo.getEncryptedPassword();

        String password = null;

        if (userInfo.getSecurityLevel() == SecurityLevel.LEVEL0.getInt()) {
            // 旧型式
            boolean oldFormat = false;
            try {
                password = this.cipherUtil.decrypt(userInfo, encryptedPassword, keyBytes, ivBytes, salt);
            } catch (SimpleHealthLogException e) {
                // 旧型式でデコード
                this.logger.warn("Cipher format is old. decrypt to new format.");
                password = this.cipherUtil.decryptOld(encryptedPassword, keyBytes, ivBytes);
                oldFormat = true;
            }

            if (!password.equals(loginForm.getPassword())) {
                throw new SimpleHealthLogException(this.messageBundle.getString("login.login.msg.idPasswordInvalid"));
            }

            if (oldFormat) {
                // 旧型式の場合は一旦新形式にエンコードしなおして保存
                this.logger.warn("Cipher format is old. Changing to new format.");
                UserInfo userInfoForUpdate = this.entityManager.find(UserInfo.class, userInfo.getId());
                String encrypedPasswordNew = this.cipherUtil.encrypt(userInfo, password, keyBytes, ivBytes, salt);
                userInfoForUpdate.setEncryptedPassword(encrypedPasswordNew);
                userInfoForUpdate.setModifiedBy(userInfo.getId());
                userInfoForUpdate.setModifiedDate(Timestamp.valueOf(LocalDateTime.now()));
                this.entityManager.merge(userInfoForUpdate);
                this.entityManager.flush();
            }
        } else {
            // 新形式ででコード
            try {
                password = this.cipherUtil.decrypt(userInfo, encryptedPassword, keyBytes, ivBytes, salt);
            } catch (SimpleHealthLogException e) {
                // デコード失敗
                throw new SimpleHealthLogException(this.messageBundle.getString("login.login.msg.idPasswordInvalid"));
            }

            // 比較
            if (!StringUtils.equals(password, loginForm.getPassword())) {
                throw new SimpleHealthLogException(this.messageBundle.getString("login.login.msg.idPasswordInvalid"));
            }
        }

        // トークン登録
        UserToken userToken = null;
        if (loginForm.isLoggingPersistent()) {
            userToken = this.createToken(userInfo, loginForm.isLoggingPersistent());
        }

        this.loginInfo.setUserInfo(userInfo);

        loginResponse.setUserInfo(userInfo);
        loginResponse.setUserToken(userToken);
        return loginResponse;
    }

    /**
     * ログアウト
     */
    public void logout() {
        this.logger.info("#logout()");
        this.logger.info(String.format("logout: %s",
                (Objects.isNull(this.loginInfo.getUserInfo())
                        ? "UNKNOWN ID"
                        : this.loginInfo.getUserInfo().getLoginId())));
        // セッション削除
        this.loginInfo.setUserInfo(null);
    }

    /**
     * 利用者情報を取得
     *
     * @return
     */
    @Transactional
    public GetUsersResponse getUsers() {
        GetUsersResponse response = new GetUsersResponse();

        // クエリの生成
        TypedQuery<UserInfo> q = entityManager.createQuery("SELECT ui FROM UserInfo ui", UserInfo.class);

        // 抽出
        response.setUserInfos(q.getResultList());
        response.setCount(response.getUserInfos().size());
        return response;
    }

    /**
     * トークンでログイン
     *
     * @param token
     * @return 存在、かつ期限内の情報がある場合はログイン情報、ヒットしない場合はnull
     */
    @Transactional
    public UserInfo loginFromToken(String token) {
        this.logger.info("#loginFromToken() begin");

        this.logger.debug(String.format("token: %s", token));

        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);

        TypedQuery<UserInfo> query = this.entityManager
                .createQuery(
                        "SELECT ui FROM UserInfo ui INNER JOIN UserToken ut ON ui.id = ut.id WHERE ut.token = :token "
                                + "AND ui.deleted = cast('false' as boolean) "
                                + "AND ut.expirationDate IS NOT NULL AND :expirationDate < ut.expirationDate ",
                        UserInfo.class);
        query.setParameter("token", token);
        query.setParameter("expirationDate", timestamp);
        List<UserInfo> results = query.getResultList();
        if (results.size() == 0) {
            this.logger.warn("Token unmatched or expired.");
            this.loginInfo.setUserInfo(null);
            return null;
        }

        UserInfo userInfo = results.get(0);
        this.logger.debug(String.format("userInfo: %s", userInfo));

        // トークン更新
        this.createToken(userInfo, false);

        this.loginInfo.setUserInfo(userInfo);

        this.logger.info("#loginFromToken() complete");
        return userInfo;
    }

    /**
     * トークン生成、更新
     *
     * @param userInfo
     *            ユーザ情報
     * @param tokenUpdate
     *            トークンを更新する場合はtrue
     * @return
     */
    @Transactional
    private synchronized UserToken createToken(UserInfo userInfo, boolean tokenUpdate) {
        List<UserToken> findUserTokenResults = null;
        UserToken userToken = null;
        LocalDateTime now = LocalDateTime.now();

        try {
            userToken = this.entityManager.find(UserToken.class, userInfo.getId());
            if (Objects.isNull(userToken)) {
                userToken = new UserToken();
                userToken.setId(userInfo.getId());
                userToken.setToken(StringUtils.EMPTY);
                this.entityManager.persist(userToken);
            }

            // 一次的に他の端末から参照する場合の対応（トークンを更新しない）
            if (!tokenUpdate && !StringUtils.isEmpty(userToken.getToken())) {
                // 日付のみ更新
                userToken.setExpirationDate(Timestamp.valueOf(now.plusDays(DEFAULT_EXPIRATION_DATE)));
                this.entityManager.merge(userToken);
                this.entityManager.flush();

                return userToken;
            }

            TypedQuery<UserToken> findUserTokenQuery = this.entityManager
                    .createQuery("SELECT t FROM UserToken t WHERE t.token = :token", UserToken.class);

            String token = null;

            do {
                byte[] randomBytes = this.random.generateSeed(64);
                token = Base64.getEncoder().encodeToString(randomBytes);
                findUserTokenQuery.setParameter("token", token);
                findUserTokenResults = findUserTokenQuery.getResultList();
            } while (0 < findUserTokenResults.size());

            userToken.setToken(token);
            userToken.setExpirationDate(Timestamp.valueOf(now.plusDays(DEFAULT_EXPIRATION_DATE)));
            this.entityManager.merge(userToken);

            // 期限切れのトークンが存在する場合は削除
            this.removeExipredToken(userToken.getId(), now);

            this.entityManager.flush();
            return userToken;
        } catch (Throwable e) {
            e.printStackTrace();
            this.logger.error(e.getMessage());
            throw new SimpleHealthLogException(e);
        }
    }

    /**
     * パスワード初期化
     *
     * @param passwordResetForm
     *            初期化情報
     */
    @Transactional
    public void resetPassword(PasswordResetForm passwordResetForm) {
        // adminは不可
        if (passwordResetForm.getLoginId().equals("admin")) {
            throw new SimpleHealthLogException(this.messageBundle.getString("resetPassword.resetPassword.msg.admin"));
        }

        // ユーザ存在チェック
        String loginId = passwordResetForm.getLoginId();
        String name = passwordResetForm.getName();
        String mailAddress = passwordResetForm.getMailAddress().toLowerCase();
        TypedQuery<UserInfo> query = this.entityManager.createQuery(
                "SELECT ui FROM UserInfo ui WHERE ui.loginId = :loginId AND ui.deleted = cast('false' as boolean)",
                UserInfo.class);
        query.setParameter("loginId", loginId);
        List<UserInfo> results = query.getResultList();
        if (results.size() == 0) {
            throw new SimpleHealthLogException(
                    this.messageBundle.getString("resetPassword.resetPassword.msg.invalidLoginInfo"));
        }

        byte[] keyBytes = this.keyIvHolder.getKeyBytes();
        byte[] ivBytes = this.keyIvHolder.getIvBytes();
        String salt = this.keyIvHolder.getSalt();

        UserInfo userInfo = results.get(0);
        String plainMailAddress = this.cipherUtil.decrypt(userInfo, userInfo.getEncryptedMailAddress(), keyBytes,
                ivBytes, salt);

        if (!userInfo.getName().equals(name) || !StringUtils.equals(mailAddress, plainMailAddress)) {
            throw new SimpleHealthLogException(
                    this.messageBundle.getString("contents.maint.settings.cutomSetting.msg.nameOrMailAddressUnmatch"));
        }

        // すべて一致したらパスワードをリセット
        String randomPassword = PasswordUtil.createRandomText(PasswordStrength.SIMPLE);
        userInfo.setEncryptedPassword(this.cipherUtil.encrypt(userInfo, randomPassword, keyBytes, ivBytes, salt));

        userInfo.setModifiedBy(userInfo.getId());
        userInfo.setModifiedDate(Timestamp.valueOf(LocalDateTime.now()));
        this.entityManager.merge(userInfo);

        // メール送信
        this.loginMailSender.sendPasswordResetMail(randomPassword, mailAddress, getAdminMailAddress());

        this.entityManager.flush();

    }

    /**
     * 期限切れトークンを削除
     *
     * @param id
     * @param now
     */
    public void removeExipredToken(int id, LocalDateTime now) {
        Timestamp expirationDate = Timestamp.valueOf(now);
        TypedQuery<UserToken> findExpiredTokenQuery = this.entityManager.createQuery(
                "SELECT t FROM UserToken t WHERE t.id = :id AND t.expirationDate <= :now", UserToken.class);
        findExpiredTokenQuery.setParameter("id", id);
        findExpiredTokenQuery.setParameter("now", expirationDate);
        List<UserToken> findExpiredTokenResults = findExpiredTokenQuery.getResultList();
        for (UserToken expiredToken : findExpiredTokenResults) {
            this.entityManager.remove(expiredToken);
        }

    }

    /**
     * ログイン中のユーザ名を表示
     * 
     * @return
     */
    public String showLoggingUserName() {
        String userName = StringUtils.EMPTY;

        UserInfo userInfo = this.loginInfo.getUserInfo();
        if (Objects.isNull(userInfo)) {
            return userName;
        }

        if (userInfo.getSecurityLevel() == SecurityLevel.LEVEL0.getInt()) {
            userName = userInfo.getName();
        } else {
            byte[] keyBytes = this.keyIvHolder.getKeyBytes();
            byte[] ivBytes = this.keyIvHolder.getIvBytes();
            String salt = this.keyIvHolder.getSalt();
            userName = this.cipherUtil.decrypt(userInfo, userInfo.getEncryptedName(), keyBytes, ivBytes, salt);
        }

        return userName;
    }
}
