package jp.nauplius.app.shl.common.ui.converter;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Objects;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

/**
 * 時刻表示の変換
 */
@FacesConverter("timeConverter")
public class TimeConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        String[] timeArray = value.split(":");
        if (timeArray.length < 2) {
            return null;
        }

        int hour = Integer.valueOf(timeArray[0]);
        int minute = Integer.valueOf(timeArray[1]);

        LocalTime localTime = LocalTime.of(hour, minute);
        return Time.valueOf(localTime);
    }

    @Override
    public String getAsString(FacesContext conext, UIComponent component, Object value) {
        if (!(value instanceof Time)) {
            return StringUtils.EMPTY;
        }

        if (Objects.isNull(value)) {
            return StringUtils.EMPTY;
        }
        Time timeValue = (Time) value;
        LocalTime localTime = timeValue.toLocalTime();
        String timeText = String.format("%02d:%02d", localTime.getHour(), localTime.getMinute());
        return timeText;
    }
}
