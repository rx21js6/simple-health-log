package jp.nauplius.app.shl.page.record.backing;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import jp.nauplius.app.shl.page.record.bean.DailyRecord;
import jp.nauplius.app.shl.page.record.bean.HealthDetail;
import jp.nauplius.app.shl.page.record.service.DailyRecordService;
import lombok.Getter;
import lombok.Setter;

@Named
public class DailyRecordController {
    private static List<String> HEALTH_DETAIL_NAMES;
    static {
        List<String> stringList = Arrays.asList("頭痛", "吐き気", "耳の違和感", "めまい、ふらつき", "右首痛", "左首痛"

        );
        HEALTH_DETAIL_NAMES = Collections.unmodifiableList(stringList);
    }

    @Inject
    @Getter
    @Setter
    private DailyRecord dailyRecord;

    @Inject
    private DailyRecordService dailyRecordService;

    @PostConstruct
    public void init() {
        System.out.printf("DailyRecordController#init()");
        LocalDate now = LocalDate.now();
        this.dailyRecord.setYear(now.getYear());
        this.dailyRecord.setMonth(now.getMonthValue());
        this.dailyRecord.setDay(now.getDayOfMonth());
        this.dailyRecord.setConditionNote("問題なし");
        this.dailyRecord.setHealthDetails(getHealthDetails(now));
    }

    public String getCurrentDateText() {
        return String.format("%04d-%02d-%02d", this.dailyRecord.getYear(), this.dailyRecord.getMonth(),
                this.dailyRecord.getDay());
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

        System.out.print(healthDetails.size());
        return healthDetails;
    }

    public String register() {
        return null;
    }
}
