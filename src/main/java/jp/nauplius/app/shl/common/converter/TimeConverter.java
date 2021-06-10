package jp.nauplius.app.shl.common.converter;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Objects;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

@FacesConverter("timeConverter")
public class TimeConverter implements Converter<Time> {
    @Override
    public Time getAsObject(FacesContext context, UIComponent component, String value) {
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
    public String getAsString(FacesContext conext, UIComponent component, Time value) {
        if (Objects.isNull(value)) {
            return StringUtils.EMPTY;
        }
        LocalTime localTime = value.toLocalTime();
        String timeText = String.format("%02d:%02d", localTime.getHour(), localTime.getMinute());
        return timeText;
    }
}