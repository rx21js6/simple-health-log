package jp.nauplius.app.shl.page.record.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import jp.nauplius.app.shl.page.record.bean.DailyRecord;
import jp.nauplius.app.shl.page.record.bean.HealthDetail;
import jp.nauplius.app.shl.page.record.service.DailyRecordService;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class MonthlyListHolder implements Serializable {
    private static List<String> HEALTH_DETAIL_NAMES;
    static {
        List<String> stringList = Arrays.asList("頭痛", "吐き気", "耳の違和感", "めまい、ふらつき", "右首痛", "左首痛"

        );
        HEALTH_DETAIL_NAMES = Collections.unmodifiableList(stringList);
    }

    @Inject
    private DailyRecordService dailyRecordService;

    @Getter
    @Setter
    private int year;
    @Getter
    @Setter
    private int month;

    public MonthlyListHolder() {
        LocalDate now = LocalDate.now();
        this.year = now.getYear();
        this.month = now.getMonthValue();
    }

    public String getYearMonthText() {
        return String.format("%04d-%02d", this.year, this.month);
    }

    public List<DailyRecord> getDailyRecords() {
        System.out.println(String.format("%d-%d", this.year, this.month));
        List<DailyRecord> dailyRecords = new ArrayList<>();

        LocalDate date = LocalDate.of(year, month, 1);
        while (month == date.getMonthValue()) {
            DailyRecord tmpRec = new DailyRecord();
            tmpRec.setHealthDetails(getHealthDetails(date));
            tmpRec.setDateText(date.toString());
            dailyRecords.add(tmpRec);
            System.out.println(tmpRec);
            date = date.plusDays(1);
        }

        return dailyRecords;
    }

    private List<HealthDetail> getHealthDetails(LocalDate date) {
        List<HealthDetail> healthDetails = new ArrayList<>();

        IntStream.range(0, HEALTH_DETAIL_NAMES.size()).forEach(idx -> {
            HealthDetail detail = new HealthDetail();
            detail.setId(idx + 1);
            detail.setName(HEALTH_DETAIL_NAMES.get(idx));
            detail.setValue(0);
            healthDetails.add(detail);
        });

        return healthDetails;
    }

    public void nextMonth() {
        LocalDate date = LocalDate.of(year, month, 1);
        date = date.plusMonths(1L);
        this.year = date.getYear();
        this.month = date.getMonthValue();
    }

    public void prevMonth() {
        LocalDate date = LocalDate.of(year, month, 1);
        date = date.plusMonths(-1L);
        this.year = date.getYear();
        this.month = date.getMonthValue();
    }

    public void currentMonth() {
        LocalDate date = LocalDate.now();
        this.year = date.getYear();
        this.month = date.getMonthValue();
    }
}
