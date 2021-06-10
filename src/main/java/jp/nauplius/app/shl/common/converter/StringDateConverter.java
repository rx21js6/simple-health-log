package jp.nauplius.app.shl.common.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

import jp.nauplius.app.shl.common.constants.ShlConstants;

@FacesConverter("stringDateConverter")
public class StringDateConverter implements Converter<String> {

    @Override
    public String getAsObject(FacesContext context, UIComponent component, String value) {
        return null;
    }

    /**
     * yyyyMMdd to yyyy-MM-dd
     */
    @Override
    public String getAsString(FacesContext context, UIComponent component, String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        LocalDate date = null;
        date = LocalDate.parse(value, ShlConstants.RECORDING_DATE_FORMATTER);
        String dateText = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        return dateText;
    }

}
