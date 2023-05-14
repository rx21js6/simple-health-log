package jp.nauplius.app.shl.common.filter;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

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
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import jp.nauplius.app.shl.common.service.LocaleService;

@WebFilter(urlPatterns = {"/*"})
@Named
public class LocaleFilter implements Filter {
    @Inject
    private Logger logger;

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
        HttpSession httpSession = httpServletRequest.getSession(true);
        if (httpSession.getAttribute(LocaleService.SESSION_KEY) == null) {
            Locale locale = request.getLocale();
            if (Objects.isNull(locale) || StringUtils.isEmpty(locale.getLanguage())) {
                locale = new Locale(LocaleService.DEFAULT_LANG);
            }
            httpServletResponse.setLocale(locale);
            httpSession.setAttribute(LocaleService.SESSION_KEY, locale);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
