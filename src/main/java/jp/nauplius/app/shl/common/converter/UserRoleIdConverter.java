package jp.nauplius.app.shl.common.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

@FacesConverter("userRoleIdConverter")
public class UserRoleIdConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return null;
    }

    @Override
    public String getAsString(FacesContext conext, UIComponent component, Object value) {
        String result = StringUtils.EMPTY;
        if (value instanceof Integer) {
            switch ((Integer) value) {
            case 0:
                result = "管理者";
                break;
            case 1:
                result = "一般";
                break;
            case 2:
                result = "制限";
                break;
            default:
                result = "";
                break;
            }

        }
        return result;
    }

}
