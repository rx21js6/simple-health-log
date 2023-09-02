package jp.nauplius.app.shl.page.record.bean;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import jp.nauplius.app.shl.common.db.model.PhysicalCondition;

public interface ConditionNoteSplitter extends Serializable {
    PhysicalCondition getPhysicalCondition();

    default List<String> splitConditionNote() {
        if (Objects.isNull(this.getPhysicalCondition())
                || StringUtils.isEmpty(this.getPhysicalCondition().getConditionNote())) {
            return Collections.emptyList();
        }

        return Arrays
                .asList(this.getPhysicalCondition().getConditionNote().replace("\\r", StringUtils.EMPTY).split("\\n+"));
    }
}
