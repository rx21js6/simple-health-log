package jp.nauplius.app.shl.page.login.service;

import java.io.Serializable;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpSession;

import org.apache.deltaspike.jpa.api.transaction.Transactional;

import jp.nauplius.app.shl.common.constants.ShlConstants;
import jp.nauplius.app.shl.common.model.LoginUser;
import jp.nauplius.app.shl.common.service.KeyIvHolderService;
import jp.nauplius.app.shl.common.util.CipherUtil;
import jp.nauplius.app.shl.page.login.backing.LoginForm;
import jp.nauplius.app.shl.ws.bean.GetUsersResponse;

@Named
@ViewScoped
public class LoginService implements Serializable {
    @Inject
    private EntityManager em;

    @Inject
    private CipherUtil cipherUtil;

    @Inject
    private KeyIvHolderService keyIvHolderService;

    public boolean login(LoginForm loginForm) {

        byte[] keyBytes = this.keyIvHolderService.getKeyBytes();
        byte[] ivBytes = this.keyIvHolderService.getIvBytes();

        TypedQuery<LoginUser> query = this.em.createQuery("SELECT lu FROM LoginUser lu WHERE lu.loginId = :loginId AND lu.disabled = cast('false' as boolean)", LoginUser.class);
        query.setParameter("loginId", loginForm.getLoginId());
        List<LoginUser> results = query.getResultList();
        if (results.size() == 0) {
            // TODO: 例外
            return false;
        }

        LoginUser loginUser = results.get(0);
        String encryptedPassword = loginUser.getEncryptedPassword();
        String password = this.cipherUtil.decrypt(encryptedPassword, keyBytes, ivBytes);
        if (!password.equals(loginForm.getPassword())) {
            return false;
            // TODO: 例外
        }

        // セッション登録
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession httpSession = (HttpSession) context.getExternalContext().getSession(true);
        httpSession.setAttribute(ShlConstants.LOGIN_SESSION_KEY, loginUser);

        return true;
    }

    public void logout() {
        // セッション削除
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession httpSession = (HttpSession) context.getExternalContext().getSession(true);
        httpSession.removeAttribute(ShlConstants.LOGIN_SESSION_KEY);
    }

    @Transactional
    public GetUsersResponse getUsers() {
        GetUsersResponse response = new GetUsersResponse();

        // クエリの生成
        TypedQuery<LoginUser> q = em.createQuery("SELECT lu FROM LoginUser lu", LoginUser.class);

        // 抽出
        response.setLoginUsers(q.getResultList());
        response.setCount(response.getLoginUsers().size());
        return response;
    }
}
