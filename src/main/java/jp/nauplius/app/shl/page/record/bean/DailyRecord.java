package jp.nauplius.app.shl.page.record.bean;

import java.io.Serializable;

import jp.nauplius.app.shl.common.model.PhysicalCondition;
import jp.nauplius.app.shl.common.model.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyRecord implements Serializable {
    private UserInfo userInfo;
    private PhysicalCondition physicalCondition;
}
