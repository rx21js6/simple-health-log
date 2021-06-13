package jp.nauplius.app.shl.page.record.bean;

import java.util.Objects;

import jp.nauplius.app.shl.common.model.PhysicalCondition;
import lombok.Getter;
import lombok.Setter;

public class RecordHolder {
    @Getter
    @Setter
    private String dateText;
    @Getter
    @Setter
    private PhysicalCondition physicalCondition;

    public boolean recordExists() {
        return !Objects.isNull(this.physicalCondition);
    }
}
