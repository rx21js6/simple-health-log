package jp.nauplius.app.shl.page.record.backing;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import jp.nauplius.app.shl.common.model.PhysicalCondition;
import lombok.Data;

@Named
@SessionScoped
@Data
public class DailyRecordInputModel implements Serializable {
    @Inject
    private PhysicalCondition physicalCondition;

}
