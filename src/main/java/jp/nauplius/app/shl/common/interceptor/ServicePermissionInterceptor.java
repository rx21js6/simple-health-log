package jp.nauplius.app.shl.common.interceptor;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.inject.Named;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
import jp.nauplius.app.shl.user.constants.UserRoleId;

@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
@PermissionInterceptor
@Named
public class ServicePermissionInterceptor implements Serializable {
    @Inject
    private transient ResourceBundle messageBundle;

    @Inject
    private Logger logger;

    @Inject
    private LoginInfo loginInfo;

    @AroundInvoke
    public Object aroundInvoke(InvocationContext ctx) throws Exception {
        if (this.loginInfo.getUserInfo().getRoleId() != UserRoleId.ADMIN.getInt()) {
            String message = this.messageBundle.getString("common.msg.notPermitted");
            this.logger.error(String.format("%s: %s", message, this.loginInfo.getUserInfo().getId()));
            throw new SimpleHealthLogException(message);
        }

        return ctx.proceed();
    }
}
