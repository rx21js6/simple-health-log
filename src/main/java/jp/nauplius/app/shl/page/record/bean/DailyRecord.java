package jp.nauplius.app.shl.page.record.bean;

import jp.nauplius.app.shl.common.model.PhysicalCondition;
import jp.nauplius.app.shl.common.model.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyRecord implements ConditionNoteSplitter {
    private UserInfo userInfo;
    private PhysicalCondition physicalCondition;
}
