package jp.nauplius.app.shl.page.record.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import jp.nauplius.app.shl.page.record.bean.RecordHolder;
import lombok.Getter;
import lombok.Setter;

@Named
@SessionScoped
public class MonthlyRecordModel implements Serializable {
    @Getter
    @Setter
    private LocalDate today;

    @Getter
    @Setter
    private List<RecordHolder> monthlyRecords;
}
