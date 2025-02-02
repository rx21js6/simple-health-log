package jp.nauplius.app.shl.common.ui.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jp.nauplius.app.shl.common.constants.ShlConstants;

/**
 * 日付表示の変換
 */
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
        String textValue = (String) value;
        if (StringUtils.isEmpty(textValue)) {
            return null;
        }

        LocalDate date = null;
        date = LocalDate.parse(textValue, ShlConstants.RECORDING_DATE_FORMATTER);
        String dateText = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        return dateText;
    }
}
