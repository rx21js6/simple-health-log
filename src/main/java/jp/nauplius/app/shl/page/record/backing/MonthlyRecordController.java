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
public class MonthlyRecordController implements Serializable {
    @Inject
    private MonthlyRecordService monthlyRecordService;;

    @Getter
    @Setter
    private LocalDate today;

    @Getter
    @Setter
    private List<RecordHolder> monthlyRecords;

    public String loadPreviousMonth() {
        return null;
    }

    public String loadCurrentMonth() {
        this.monthlyRecords = this.monthlyRecordService.getMontylyRecords(this.today);

        return "/contents/record/monthlyRecord.xhtml?faces-redirect=true";
    }

    public String loadNextMonth() {
        return null;
    }
}
