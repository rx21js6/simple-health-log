package jp.nauplius.app.shl.user.bean;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

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
