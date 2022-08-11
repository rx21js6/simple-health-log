package jp.nauplius.app.shl.common.converter;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

import jp.nauplius.app.shl.common.constants.UserRole;

@FacesConverter("userRoleIdConverter")
public class UserRoleIdConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return null;
    }

    @Override
    public String getAsString(FacesContext conext, UIComponent component, Object value) {

        ResourceBundle messageBundle = ResourceBundle.getBundle("i18n.messages", Locale.getDefault());

        String result = StringUtils.EMPTY;
        if (value instanceof Integer) {
            UserRole userRole = UserRole.valueOf((int) value);
            switch (userRole) {
            case ADMIN:
                result = messageBundle.getString("contents.maint.user.label.admin");
                break;
            case NORMAL:
                result = messageBundle.getString("contents.maint.user.label.normal");
                break;
            case RESTRICTED:
                result = messageBundle.getString("contents.maint.user.label.restricted");
                break;
            default:
                result = StringUtils.EMPTY;
                break;
            }

        }
        return result;
    }

}
