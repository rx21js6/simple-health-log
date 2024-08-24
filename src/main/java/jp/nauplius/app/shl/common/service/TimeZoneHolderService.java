package jp.nauplius.app.shl.common.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import org.apache.commons.lang3.StringUtils;

import jp.nauplius.app.shl.common.ui.bean.TimeZoneInfo;

/**
 * タイムゾーン設定サービス
 *
 */
@Named
@SessionScoped
public class TimeZoneHolderService extends AbstractService {
    public List<TimeZoneInfo> getTimeZoneInfos() {
        LocalDateTime localDateTime = LocalDateTime.now();

        List<TimeZoneInfo> timeZoneInfos = new ArrayList<>();

        // デフォルト値
        TimeZoneInfo defaultTimeZoneInfo = new TimeZoneInfo();
        defaultTimeZoneInfo.setZoneId(StringUtils.EMPTY);

        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        ZoneOffset zoneOffset = zonedDateTime.getOffset();
        defaultTimeZoneInfo
                .setZoneIdLabelText(String.format("(SYSTEM): (%s)", zoneOffset.getId().replaceAll("Z", "+00:00")));
        timeZoneInfos.add(defaultTimeZoneInfo);

        List<String> availableZoneIds = ZoneId.getAvailableZoneIds().stream().sorted().collect(Collectors.toList());
        for (String zoneId : availableZoneIds) {
            TimeZoneInfo timeZoneInfo = new TimeZoneInfo();
            timeZoneInfo.setZoneId(zoneId);
            zonedDateTime = localDateTime.atZone(ZoneId.of(zoneId));
            zoneOffset = zonedDateTime.getOffset();
            String zoneIdLabelText = String.format("%s: (%s)", zoneId, zoneOffset.getId().replaceAll("Z", "+00:00"));
            timeZoneInfo.setZoneIdLabelText(zoneIdLabelText);

            timeZoneInfos.add(timeZoneInfo);
        }

        return timeZoneInfos;

    }
}
