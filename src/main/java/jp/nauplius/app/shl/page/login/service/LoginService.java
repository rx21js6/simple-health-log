package jp.nauplius.app.shl.page.login.service;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.slf4j.Logger;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.model.UserInfo;
import jp.nauplius.app.shl.common.model.UserToken;
import jp.nauplius.app.shl.common.service.AbstractService;
import jp.nauplius.app.shl.common.service.MailSenderService;
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
    private CipherUtil cipherUtil;

    @Inject
    private KeyIvHolder keyIvHolder;

    @Inject
    private MailSenderService meiMailSenderService;

    @Inject
    private LoginInfo loginInfo;

    /**
     * ログイン
     */
    @Transactional
    public LoginResponse login(LoginForm loginForm) {
        this.logger.info(String.format("login: loginId: %s / keepLogin: %n", loginForm.getLoginId()),
                loginForm.isKeepLogin());

        LoginResponse loginResponse = new LoginResponse();

        byte[] keyBytes = this.keyIvHolder.getKeyBytes();
        byte[] ivBytes = this.keyIvHolder.getIvBytes();

        TypedQuery<UserInfo> query = this.entityManager.createQuery(
                "SELECT ui FROM UserInfo ui WHERE ui.loginId = :loginId AND ui.deleted = cast('false' as boolean)",
                UserInfo.class);
        query.setParameter("loginId", loginForm.getLoginId());
        List<UserInfo> results = query.getResultList();
        if (results.size() == 0) {
            throw new SimpleHealthLogException("ログインIDかパスワードが不正です。");
        }

        UserInfo userInfo = results.get(0);
        String encryptedPassword = userInfo.getEncryptedPassword();

        String password = null;
        boolean oldFormat = false;
        try {
            password = this.cipherUtil.decrypt(encryptedPassword, keyBytes, ivBytes);
        } catch (SimpleHealthLogException e) {
            // 旧型式でデコード
            this.logger.warn("Cipher format is old. decrypt to new format.");
            password = this.cipherUtil.decryptOld(encryptedPassword, keyBytes, ivBytes);
            oldFormat = true;
        }

        if (!password.equals(loginForm.getPassword())) {
            throw new SimpleHealthLogException("ログインIDかパスワードが不正です。");
        }

        if (oldFormat) {
            // 旧型式の場合は一旦新形式にエンコードしなおして保存
            this.logger.warn("Cipher format is old. Changing to new format.");
            UserInfo userInfoForUpdate = this.entityManager.find(UserInfo.class, userInfo.getId());
            String encrypedPasswordNew = this.cipherUtil.encrypt(password, keyBytes, ivBytes);
            userInfoForUpdate.setEncryptedPassword(encrypedPasswordNew);
            userInfoForUpdate.setModifiedBy(userInfo.getId());
            userInfoForUpdate.setModifiedDate(Timestamp.valueOf(LocalDateTime.now()));
            this.entityManager.merge(userInfoForUpdate);
            this.entityManager.flush();

        }

        // トークン登録
        UserToken userToken = null;
        if (loginForm.isKeepLogin()) {
            userToken = this.createToken(userInfo, loginForm.isKeepLogin());
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
        this.logger.info("LoginService#logout");
        // セッション削除
        this.loginInfo.setUserInfo(null);
    }

    /**
     * 利用者情報を取得
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
     * @param token
     * @return
     */
    public UserInfo loginFromToken(String token) {
        this.logger.info("loginFromToken");

        this.logger.debug(String.format("token: %s", token));

        TypedQuery<UserInfo> query = this.entityManager.createQuery(
                "SELECT ui FROM UserInfo ui INNER JOIN UserToken ut ON ui.id = ut.id WHERE ut.token = :token AND ui.deleted = cast('false' as boolean)",
                UserInfo.class);
        query.setParameter("token", token);
        List<UserInfo> results = query.getResultList();
        if (results.size() == 0) {
            this.loginInfo.setUserInfo(null);
            return null;
        }

        UserInfo userInfo = results.get(0);
        this.logger.debug(String.format("userInfo: %s", userInfo));
        this.loginInfo.setUserInfo(userInfo);
        return userInfo;
    }

    /**
     * トークン生成、更新
     *
     * @param userInfo    ユーザ情報
     * @param tokenUpdate トークンを更新する場合はtrue
     * @return
     */
    @Transactional
    private synchronized UserToken createToken(UserInfo userInfo, boolean tokenUpdate) {
        List<UserToken> results = null;
        UserToken userToken = null;

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
                return userToken;
            }

            TypedQuery<UserToken> query = this.entityManager.createQuery("SELECT t FROM UserToken t WHERE t.token = :token",
                    UserToken.class);

            String token = null;

            do {
                SecureRandom random = new SecureRandom();
                byte[] randomBytes = random.generateSeed(64);
                token = Base64.getEncoder().encodeToString(randomBytes);
                query.setParameter("token", token);
                results = query.getResultList();
            } while (0 < results.size());

            userToken.setToken(token);
            this.entityManager.merge(userToken);
            this.entityManager.flush();
            return userToken;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new SimpleHealthLogException(e);
        }
    }

    /**
     * パスワード初期化
     *
     * @param passwordResetForm 初期化情報
     */
    @Transactional
    public void resetPassword(PasswordResetForm passwordResetForm) {
        // adminは不可
        if (passwordResetForm.getLoginId().equals("admin")) {
            throw new SimpleHealthLogException("管理者は変更できません。");
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
            throw new SimpleHealthLogException("ログイン情報が不正です。");
        }

        UserInfo userInfo = results.get(0);
        if (!userInfo.getName().equals(name) || !userInfo.getMailAddress().equals(mailAddress)) {
            throw new SimpleHealthLogException("ログイン情報が不正です。");
        }

        // すべて一致したらパスワードをリセット
        String randomPassword = PasswordUtil.createRandomText(PasswordStrength.SIMPLE);

        byte[] keyBytes = this.keyIvHolder.getKeyBytes();
        byte[] ivBytes = this.keyIvHolder.getIvBytes();

        userInfo.setEncryptedPassword(this.cipherUtil.encrypt(randomPassword, keyBytes, ivBytes));
        userInfo.setModifiedBy(userInfo.getId());
        userInfo.setModifiedDate(Timestamp.valueOf(LocalDateTime.now()));
        this.entityManager.merge(userInfo);

        // メール送信
        this.meiMailSenderService.sendPasswordResetMail(randomPassword, mailAddress, getAdminMailAddress());

        this.entityManager.flush();

    }

    /**
     * 管理者のメールアドレスを取得
     *
     * @return 管理者メールアドレス
     */
    private String getAdminMailAddress() {
        TypedQuery<UserInfo> query = this.entityManager.createQuery(
                "SELECT ui FROM UserInfo ui WHERE ui.roleId = 0 AND ui.deleted = cast('false' as boolean)",
                UserInfo.class);
        List<UserInfo> results = query.getResultList();
        UserInfo userInfo = results.get(0);
        return userInfo.getMailAddress();
    }
}
