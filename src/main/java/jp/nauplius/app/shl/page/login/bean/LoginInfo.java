package jp.nauplius.app.shl.page.login.bean;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import org.apache.commons.lang3.StringUtils;

import jp.nauplius.app.shl.common.constants.ShlConstants;
import jp.nauplius.app.shl.common.db.model.UserInfo;
import lombok.Data;

/**
 * セッションに格納するログイン情報
 *
 */
@Named
@SessionScoped
@Data
public class LoginInfo implements Serializable {
    private UserInfo userInfo;

    /**
     * ログインユーザの「当日」を返す。
     *
     * @return {@link LocalDate}
     */
    public LocalDate getUsersLocalToday() {
        ZoneId zoneId = (Objects.isNull(this.userInfo) || StringUtils.isEmpty(this.userInfo.getZoneId()))
                ? ZoneId.systemDefault()
                : ZoneId.of(this.userInfo.getZoneId());
        return ZonedDateTime.now(zoneId).toLocalDate();
    }

    /**
     * 当日日付をキー文字列に変換して返す。
     *
     * @return ログインユーザの「当日」を"yyyyMMdd"形式に変換した文字列
     */
    public String getInputToday() {
        return ShlConstants.RECORDING_DATE_FORMATTER.format(this.getUsersLocalToday());
    }
}
