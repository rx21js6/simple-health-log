package jp.nauplius.app.shl.page.record.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import jp.nauplius.app.shl.page.record.bean.RecordHolder;
import jp.nauplius.app.shl.page.record.service.MonthlyRecordService;
import lombok.Getter;
import lombok.Setter;

@Named
@SessionScoped
public class MonthlyRecordListController implements Serializable {
    @Inject
    private MonthlyRecordService monthlyRecordService;;

    @Getter
    @Setter
    private LocalDate today;

    @Getter
    @Setter
    private List<RecordHolder> monthlyRecords;

    public String showMonthlyList() {
        this.today = LocalDate.of(this.today.getYear(), this.today.getMonth(), 1);
        this.monthlyRecords = this.monthlyRecordService.getMontylyRecords(this.today);

        return "/contents/record/monthlyRecord.xhtml?faces-redirect=true";
    }

    /**
     * 前月表示
     * @return null
     */
    public String loadPreviousMonth() {
        this.today = this.today.plusMonths(-1);
        this.monthlyRecords = this.monthlyRecordService.getMontylyRecords(this.today);

        return null;
    }

    /**
     * 当月表示
     * @return null
     */
    public String loadCurrentMonth() {
        this.today = LocalDate.now();
        this.today = LocalDate.of(this.today.getYear(), this.today.getMonth(), 1);
        this.monthlyRecords = this.monthlyRecordService.getMontylyRecords(this.today);

        return null;
    }

    /**
     * 翌月表示
     * @return null
     */
    public String loadNextMonth() {
        this.today = this.today.plusMonths(1);
        this.monthlyRecords = this.monthlyRecordService.getMontylyRecords(this.today);

        return null;
    }
}
