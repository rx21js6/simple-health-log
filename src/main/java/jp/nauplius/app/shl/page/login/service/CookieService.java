package jp.nauplius.app.shl.page.login.service;

import java.io.Serializable;
import java.util.Objects;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import jp.nauplius.app.shl.common.constants.ShlConstants;

/**
 * クッキー管理サービス
 */
@Named
@SessionScoped
public class CookieService implements Serializable {
    @Inject
    private Logger logger;

    /**
     * トークンをクッキーに設定
     *
     * @param context
     * @param token
     */
    public void registerToken(FacesContext context, String token) {
        this.logger.debug("#registerToken()");

        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        Cookie[] cookies = request.getCookies();
        Cookie loginInfoCookie = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(ShlConstants.COOKIE_KEY_TOKEN)) {
                    loginInfoCookie = cookie;
                    loginInfoCookie.setSecure(true);
                    break;
                }
            }
        }

        if (loginInfoCookie == null) {
            loginInfoCookie = new Cookie(ShlConstants.COOKIE_KEY_TOKEN, token);
        }
        loginInfoCookie.setValue(token);
        loginInfoCookie.setSecure(true);
        loginInfoCookie.setMaxAge(60 * 60 * 24 * 10);
        loginInfoCookie.setPath("/simple-health-log");
        response.addCookie(loginInfoCookie);
    }

    /**
     * 認証用トークン取得
     * 
     * @param context
     * @return
     */
    public String getToken(FacesContext context) {
        String token = null;
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        Cookie[] cookies = request.getCookies();
        Cookie loginInfoCookie = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(ShlConstants.COOKIE_KEY_TOKEN)) {
                    loginInfoCookie = cookie;
                    break;
                }
            }
        }

        if (!Objects.isNull(loginInfoCookie)) {
            token = loginInfoCookie.getValue();
        }
        return token;
    }

    /**
     * クッキー削除
     *
     * @param context
     */
    public void removeToken(FacesContext context) {
        this.logger.debug("#removeToken()");

        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        Cookie[] cookies = request.getCookies();
        Cookie loginInfoCookie = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(ShlConstants.COOKIE_KEY_TOKEN)) {
                    loginInfoCookie = cookie;
                    loginInfoCookie.setSecure(true);
                    break;
                }
            }
        }
        if (loginInfoCookie == null) {
            loginInfoCookie = new Cookie(ShlConstants.COOKIE_KEY_TOKEN, null);
            loginInfoCookie.setSecure(true);
        }
        loginInfoCookie.setValue(null);
        loginInfoCookie.setMaxAge(0);
        loginInfoCookie.setPath("/simple-health-log");
        response.addCookie(loginInfoCookie);

    }
}
