package jp.nauplius.app.shl.page.record.backing;

import java.io.Serializable;
import java.time.LocalDate;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;

import jp.nauplius.app.shl.page.record.service.MonthlyRecordService;
import lombok.Getter;
import lombok.Setter;

@Named
@SessionScoped
public class MonthlyRecordListController implements Serializable {
    @Inject
    private MonthlyRecordService monthlyRecordService;

    @Inject
    private MonthlyRecordModel monthlyRecordModel;

    @Getter
    @Setter
    private LocalDate today;

    /**
     * 月次画面の初期表示
     */
    public void init() {
        if (CollectionUtils.isEmpty(this.monthlyRecordModel.getMonthlyRecords())) {
            this.monthlyRecordService.loadMonthlyRecords();
        }
    }

    /**
     * 前月表示
     *
     * @return null
     */
    public String loadPreviousMonth() {
        this.today = this.today.plusMonths(-1);
        this.monthlyRecordModel.setToday(this.today);
        this.monthlyRecordService.loadMonthlyRecords();

        return null;
    }

    /**
     * 当月表示
     *
     * @return null
     */
    public String loadCurrentMonth() {
        this.today = LocalDate.now();
        this.today = LocalDate.of(this.today.getYear(), this.today.getMonth(), 1);
        this.monthlyRecordModel.setToday(this.today);
        this.monthlyRecordService.loadMonthlyRecords();

        return null;
    }

    /**
     * 翌月表示
     *
     * @return null
     */
    public String loadNextMonth() {
        this.today = this.today.plusMonths(1);
        this.monthlyRecordModel.setToday(this.today);
        this.monthlyRecordService.loadMonthlyRecords();

        return null;
    }

    /**
     * 日次入力画面表示
     *
     * @return 日次入力画面
     */
    public String showRecordInput() {
        return "/contents/record/recordInput.xhtml?faces-redirect=true";
    }
}
