package jp.nauplius.app.shl.page.record.backing;

import java.math.BigDecimal;

import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;

@Named
@Deprecated
public class DailyRecordForm {
    @Getter
    @Setter
    int year;
    @Getter
    @Setter
    int month;
    @Getter
    @Setter
    int day;
    @Getter
    @Setter
    BigDecimal tempMorning;
    @Getter
    @Setter
    BigDecimal tempEvening;
    @Getter
    @Setter
    String conditionNote;

    public DailyRecordForm() {
        this.tempMorning = new BigDecimal("36.0");
        this.tempEvening = new BigDecimal("36.0");
    }
}
