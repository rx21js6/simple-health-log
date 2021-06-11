package jp.nauplius.app.shl.page.record.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import jp.nauplius.app.shl.common.constants.ShlConstants;
import jp.nauplius.app.shl.page.record.bean.DailyRecord;
import jp.nauplius.app.shl.page.record.service.DailyRecordService;
import lombok.Getter;
import lombok.Setter;

@Named
@SessionScoped
public class DailyRecordListController implements Serializable {
    @Getter
    private List<DailyRecord> dailyRecords;

    @Getter
    @Setter
    private LocalDate today;

    @Inject
    private DailyRecordService dailyRecordService;

    /**
     * 全利用者リスト
     *
     * @return
     */
    public String showDailyList() {
        this.dailyRecords = this.dailyRecordService.getDailyRecords(this.today);
        return "/contents/record/dailyRecord.xhtml?faces-redirect=true";
    }

    /**
     * 前日表示
     *
     * @return null
     */
    public String loadYesterday() {
        this.today = this.today.minusDays(1);
        this.dailyRecords = this.dailyRecordService.getDailyRecords(this.today);
        return null;
    }

    /**
     * 当日表示
     *
     * @return null
     */
    public String loadToday() {
        this.today = LocalDate.now();
        this.dailyRecords = this.dailyRecordService.getDailyRecords(this.today);
        return null;
    }

    /**
     * 翌日表示
     *
     * @return null
     */
    public String loadTomorrow() {
        this.today = this.today.plusDays(1);
        this.dailyRecords = this.dailyRecordService.getDailyRecords(today);
        return null;
    }

    public String getTodayText() {
        return this.today.format(ShlConstants.RECORDING_DATE_FORMATTER);
    }

}
