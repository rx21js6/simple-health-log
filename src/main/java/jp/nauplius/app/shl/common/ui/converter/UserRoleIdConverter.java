package jp.nauplius.app.shl.common.ui.converter;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.servlet.http.HttpSession;
import jp.nauplius.app.shl.common.constants.UserRole;
import jp.nauplius.app.shl.common.service.LocaleService;

/**
 * 権限表示の変換
 */
@FacesConverter("userRoleIdConverter")
public class UserRoleIdConverter implements Converter<Integer> {
    @Override
    public Integer getAsObject(FacesContext context, UIComponent component, String value) {
        return Integer.valueOf(value);
    }

    @Override
    public String getAsString(FacesContext conext, UIComponent component, Integer value) {
        if (Objects.isNull(value)) {
            return StringUtils.EMPTY;
        }

        ExternalContext externalContext = conext.getExternalContext();
        HttpSession httpSession = (HttpSession) externalContext.getSession(true);
        Locale locale = (Locale) httpSession.getAttribute(LocaleService.SESSION_KEY);

        ResourceBundle messageBundle = ResourceBundle.getBundle("i18n.messages", locale);

        String result = StringUtils.EMPTY;
        UserRole userRole = UserRole.valueOf((int) value);
        switch (userRole) {
        case ADMIN:
            result = messageBundle.getString("contents.maint.user.role.label.admin");
            break;
        case USER:
            result = messageBundle.getString("contents.maint.user.role.label.user");
            break;
        case RESTRICTED:
            result = messageBundle.getString("contents.maint.user.role.label.restricted");
            break;
        default:
            result = StringUtils.EMPTY;
            break;
        }
        return result;
    }

}
