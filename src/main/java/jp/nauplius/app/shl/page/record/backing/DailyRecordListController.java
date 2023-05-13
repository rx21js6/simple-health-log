package jp.nauplius.app.shl.page.record.backing;

import java.io.Serializable;
import java.time.LocalDate;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import jp.nauplius.app.shl.common.constants.ShlConstants;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
import jp.nauplius.app.shl.page.record.service.DailyRecordService;
import lombok.Getter;
import lombok.Setter;

@Named
@SessionScoped
public class DailyRecordListController implements Serializable {
    @Inject
    private LoginInfo loginInfo;

    @Getter
    @Setter
    private LocalDate today;

    @Inject
    private DailyRecordService dailyRecordService;

    /**
     * 前日表示
     *
     * @return null
     */
    public String loadYesterday() {
        this.today = this.today.minusDays(1);
        this.dailyRecordService.loadDailyRecords(this.today);
        return null;
    }

    /**
     * 当日表示
     *
     * @return null
     */
    public String loadToday() {
        this.today = this.loginInfo.getUsersLocalToday();
        this.dailyRecordService.loadDailyRecords(this.today);
        return null;
    }

    /**
     * 翌日表示
     *
     * @return null
     */
    public String loadTomorrow() {
        this.today = this.today.plusDays(1);
        this.dailyRecordService.loadDailyRecords(today);
        return null;
    }

    /**
     * 当日の日付フォーマット
     *
     * @return
     */
    public String getTodayText() {
        return this.today.format(ShlConstants.RECORDING_DATE_FORMATTER);
    }
}
