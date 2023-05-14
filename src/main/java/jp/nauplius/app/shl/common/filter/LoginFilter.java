package jp.nauplius.app.shl.common.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.faces.application.ResourceHandler;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import jp.nauplius.app.shl.common.service.AuthService;
import jp.nauplius.app.shl.common.service.KeyIvHolderService;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;

@Named
@WebFilter(urlPatterns = { "/*" })
public class LoginFilter implements Filter {
    @Inject
    private Logger logger;

    @Inject
    private KeyIvHolderService keyIvHolderService;

    @Inject
    private AuthService authService;

    @Inject
    private LoginInfo loginInfo;

    private static final List<String> ALLOWED_PATHS_INITIAL = Collections
            .unmodifiableList(Arrays.asList(ResourceHandler.RESOURCE_IDENTIFIER, "/rest", "/contents/initial"));

    private static final List<String> ALLOWED_PASSWORD_RESET = Collections
            .unmodifiableList(Arrays.asList("/contents/password/passwordReset.xhtml"));

    private static final List<String> ALLOWED_PATHS = Collections
            .unmodifiableList(Arrays.asList(ResourceHandler.RESOURCE_IDENTIFIER, "/rest",
                    "/contents/record/recordInput.xhtml", "/contents/initial/initialSettingComplete.xhtml"));

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.logger.info("#init()");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        this.logger.debug("#doFilter()");

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String requestUri = httpServletRequest.getRequestURI();
        this.logger.debug("requestUri: " + requestUri);
        String contextPath = httpServletRequest.getContextPath();
        String servletPath = httpServletRequest.getServletPath();

        if (requestUri.startsWith(httpServletRequest.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER)) {
            // Skip JSF
            // resources
            // (CSS/JS/Images/etc)
            httpServletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
            httpServletResponse.setHeader("Pragma", "no-cache"); // HTTP 1.0.
            httpServletResponse.setDateHeader("Expires", 0); // Proxies.

            chain.doFilter(request, response);
            return;
        }

        // セッション確認
        String path = requestUri.substring(contextPath.length()).replaceAll("[/]+$", "");

        if (!this.keyIvHolderService.isRegistered()) {
            // 初期設定前
            this.logger.info("Key/IV Not registered.");
            // 初期設定
            boolean pathAllowed = ALLOWED_PATHS_INITIAL.stream().anyMatch(allowedPath -> path.startsWith(allowedPath));
            if (pathAllowed) {
                chain.doFilter(request, response);
                return;
            }
            // それ以外
            httpServletResponse.sendRedirect(contextPath + "/contents/initial/initialSetting.xhtml");

        } else {
            // ログイン済み確認（restは許可）
            boolean loggedIn = !Objects.isNull(this.loginInfo.getUserInfo());
            boolean pathAllowed = ALLOWED_PATHS.stream().anyMatch(allowedPath -> path.startsWith(allowedPath));
            boolean passwordResetAllowd = ALLOWED_PASSWORD_RESET.stream()
                    .anyMatch(allowedPath -> path.startsWith(allowedPath));

            this.logger.debug(String.format("loggedIn: %s, pathAllowed: %s", loggedIn, pathAllowed));

            if (passwordResetAllowd || pathAllowed) {
                // パスワードリセット
                chain.doFilter(request, response);
            } else if (loggedIn) {
                if (servletPath.equals("/")) {
                    // 入力画面に遷移
                    httpServletResponse.sendRedirect(contextPath + "/contents/record/recordInput.xhtml");
                    return;
                } else if (!this.authService.isUserAccessible(servletPath)) {
                    // 表示権限がない場合
                    httpServletResponse.sendRedirect(contextPath + "/error/authError.xhtml");
                    return;
                }

                chain.doFilter(request, response);
            } else {
                this.logger.debug("redirect to recordInput.xhtml");
                httpServletResponse.sendRedirect(contextPath + "/contents/record/recordInput.xhtml");
            }
        }

    }

    @Override
    public void destroy() {
    }
}
