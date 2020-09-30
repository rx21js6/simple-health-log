package jp.nauplius.app.shl.page.record.bean;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class DailyRecord {
    @Getter
    @Setter
    private int year;
    @Getter
    @Setter
    private int month;
    @Getter
    @Setter
    private int day;
    @Getter
    @Setter
    private String dateText;

    @Getter
    @Setter
    private double tempMorning;

    @Getter
    @Setter
    private double tempEvening;

    @Getter
    @Setter
    private String conditionNote;

    @Getter
    @Setter
    private List<HealthDetail> healthDetails;
}
