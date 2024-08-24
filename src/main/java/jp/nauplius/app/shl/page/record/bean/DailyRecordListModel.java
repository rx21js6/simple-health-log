package jp.nauplius.app.shl.page.record.bean;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import lombok.Getter;
import lombok.Setter;

@Named
@SessionScoped
public class DailyRecordListModel implements Serializable {
    @Getter
    @Setter
    private LocalDate today;

    @Getter
    @Setter
    private List<DailyRecord> dailyRecords;
}
