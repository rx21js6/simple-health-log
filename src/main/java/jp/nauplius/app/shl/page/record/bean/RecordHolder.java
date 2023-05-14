package jp.nauplius.app.shl.page.record.bean;

import java.util.Objects;

import jp.nauplius.app.shl.common.db.model.PhysicalCondition;
import lombok.Data;

@Data
public class RecordHolder implements ConditionNoteSplitter {
    private String dateText;
    private PhysicalCondition physicalCondition;
    private boolean today;

    public boolean recordExists() {
        return !Objects.isNull(this.physicalCondition);
    }
}
