package jp.nauplius.app.shl.page.record.bean;

import jp.nauplius.app.shl.common.model.DailyHealthRecord;
import lombok.Getter;
import lombok.Setter;

public class RecordHolder {
    @Getter
    @Setter
    private String dateText;
    @Getter
    @Setter
    private DailyHealthRecord dailyHealthRecord;

    public boolean recordExists() {
        return (this.dailyHealthRecord != null);
    }

    public boolean detailExists() {
        return (this.dailyHealthRecord != null) && (this.dailyHealthRecord.getDailyHealthDetails() != null);
    }
}
