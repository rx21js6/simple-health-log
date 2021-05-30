package jp.nauplius.app.shl.common.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.faces.application.ResourceHandler;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.nauplius.app.shl.common.constants.ShlConstants;
import jp.nauplius.app.shl.common.service.KeyIvHolderService;

@WebFilter(urlPatterns = { "/*" })
public class LoginFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginFilter.class);

    @Inject
    private KeyIvHolderService keyIvHolderService;

    private static final List<String> ALLOWED_PATHS_INITIAL = Collections
            .unmodifiableList(Arrays.asList("/javax.faces.resource", "/rest", "/contents/initial"));

    private static final List<String> ALLOWED_PATHS = Collections
            .unmodifiableList(Arrays.asList("/javax.faces.resource", "/rest", "/contents/record/recordInput.xhtml",
                    "/contents/initial/initialSettingComplete.xhtml"));

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("LoginFilter#init");
        LOGGER.info("init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        LOGGER.info("doFilter");
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String requestUri = httpServletRequest.getRequestURI();

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
        HttpSession httpSession = httpServletRequest.getSession(false);
        String path = requestUri.substring(httpServletRequest.getContextPath().length()).replaceAll("[/]+$", "");

        if (!this.keyIvHolderService.isRegistered()) {
            // 初期設定
            boolean pathAllowed = ALLOWED_PATHS_INITIAL.stream().anyMatch(allowedPath -> path.startsWith(allowedPath));
            if (pathAllowed) {
                chain.doFilter(request, response);
            } else {
                httpServletResponse
                        .sendRedirect(httpServletRequest.getContextPath() + "/contents/initial/initialSetting.xhtml");
            }
        } else { // ログイン済み確認（restは許可）

            boolean loggedIn = (httpSession != null
                    && httpSession.getAttribute(ShlConstants.LOGIN_SESSION_KEY) != null);
            boolean pathAllowed = ALLOWED_PATHS.stream().anyMatch(allowedPath -> path.startsWith(allowedPath));

            if (loggedIn || pathAllowed) {
                chain.doFilter(request, response);
            } else {
                // httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/contents/login/login.xhtml");
                httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/contents/record/recordInput.xhtml");

            }
        }

    }

    @Override
    public void destroy() {
    }
}
