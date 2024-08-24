package jp.nauplius.app.shl.user.bean;

import java.io.Serializable;
import java.util.List;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import jp.nauplius.app.shl.common.ui.bean.TimeZoneInfo;
import lombok.Data;

@Named
@SessionScoped
@Data
public class UserEditingModel implements Serializable {
    private UserEditingFormModel userEditingFormModel;

    private boolean newData;

    private List<TimeZoneInfo> timeZoneInfos;
}
