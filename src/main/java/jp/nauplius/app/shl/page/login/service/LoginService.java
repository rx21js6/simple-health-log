package jp.nauplius.app.shl.page.login.service;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.slf4j.Logger;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.model.UserInfo;
import jp.nauplius.app.shl.common.model.UserToken;
import jp.nauplius.app.shl.common.service.KeyIvHolderService;
import jp.nauplius.app.shl.common.util.CipherUtil;
import jp.nauplius.app.shl.page.login.bean.LoginForm;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
import jp.nauplius.app.shl.page.login.bean.LoginResponse;
import jp.nauplius.app.shl.ws.bean.GetUsersResponse;

@Named
@SessionScoped
public class LoginService implements Serializable {
    @Inject
    private Logger logger;

    @Inject
    private transient EntityManager em;

    @Inject
    private CipherUtil cipherUtil;

    @Inject
    private KeyIvHolderService keyIvHolderService;

    @Inject
    private LoginInfo loginInfo;

    /**
     * ログイン
     */
    @Transactional
    public LoginResponse login(LoginForm loginForm) {
        LoginResponse loginResponse = new LoginResponse();

        byte[] keyBytes = this.keyIvHolderService.getKeyBytes();
        byte[] ivBytes = this.keyIvHolderService.getIvBytes();

        TypedQuery<UserInfo> query = this.em.createQuery(
                "SELECT ui FROM UserInfo ui WHERE ui.loginId = :loginId AND ui.deleted = cast('false' as boolean)",
                UserInfo.class);
        query.setParameter("loginId", loginForm.getLoginId());
        List<UserInfo> results = query.getResultList();
        if (results.size() == 0) {
            throw new SimpleHealthLogException("ログインIDかパスワードが不正です。");
        }

        UserInfo userInfo = results.get(0);
        String encryptedPassword = userInfo.getEncryptedPassword();
        String password = this.cipherUtil.decrypt(encryptedPassword, keyBytes, ivBytes);
        if (!password.equals(loginForm.getPassword())) {
            throw new SimpleHealthLogException("ログインIDかパスワードが不正です。");
        }

        // トークン登録
        UserToken userToken = this.createToken(userInfo, loginForm.isKeepLogin());

        this.loginInfo.setUserInfo(userInfo);

        loginResponse.setUserInfo(userInfo);
        loginResponse.setUserToken(userToken);
        return loginResponse;
    }

    public void logout() {
        this.logger.info("LoginService#logout");
        // セッション削除
        this.loginInfo.setUserInfo(null);
    }

    @Transactional
    public GetUsersResponse getUsers() {
        GetUsersResponse response = new GetUsersResponse();

        // クエリの生成
        TypedQuery<UserInfo> q = em.createQuery("SELECT ui FROM UserInfo ui", UserInfo.class);

        // 抽出
        response.setUserInfos(q.getResultList());
        response.setCount(response.getUserInfos().size());
        return response;
    }

    public UserInfo loginFromToken(String token) {
        this.logger.info("loginFromToken");

        this.logger.debug(String.format("token: %s", token));

        TypedQuery<UserInfo> query = this.em.createQuery(
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
     * @param userInfo ユーザ情報
     * @param tokenUpdate トークンを更新する場合はtrue
     * @return
     */
    @Transactional
    private synchronized UserToken createToken(UserInfo userInfo, boolean tokenUpdate) {
        List<UserToken> results = null;
        UserToken userToken = null;

        try {
            userToken = this.em.find(UserToken.class, userInfo.getId());
            if (Objects.isNull(userToken)) {
                userToken = new UserToken();
                userToken.setId(userInfo.getId());
                this.em.persist(userToken);
            }

            // 一次的に他の端末から参照する場合の対応（トークンを更新しない）
            if (!tokenUpdate && !StringUtils.isEmpty(userToken.getToken()) ) {
                return userToken;
            }

            TypedQuery<UserToken> query = this.em.createQuery("SELECT t FROM UserToken t WHERE t.token = :token",
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
            this.em.merge(userToken);
            this.em.flush();
            return userToken;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new SimpleHealthLogException(e);
        }
    }
}
