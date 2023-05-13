package jp.nauplius.app.shl.maint.bean;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import jp.nauplius.app.shl.common.ui.bean.TimeZoneInfo;
import lombok.Data;

@Named
@SessionScoped
@Data
public class TimeZoneInputModel implements Serializable {
    private List<TimeZoneInfo> timeZoneInfos;

    private String selectedZoneId;
}
