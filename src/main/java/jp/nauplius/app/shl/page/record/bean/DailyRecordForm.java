package jp.nauplius.app.shl.page.record.bean;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.beanutils.BeanUtils;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import jp.nauplius.app.shl.common.db.model.PhysicalCondition;
import jp.nauplius.app.shl.common.db.model.PhysicalConditionPK;
import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import lombok.Data;

/**
 * 日次入力画面フォーム
 */
@Named
@SessionScoped
@Data
public class DailyRecordForm implements Serializable {
    private PhysicalConditionPK id;

    private Time awakeTime;
    private Time bedTime;

    @DecimalMin(value = "0")
    @DecimalMax(value = "99.9")
    private BigDecimal bodyTemperatureMorning;

    @DecimalMin(value = "0")
    @DecimalMax(value = "99.9")
    private BigDecimal bodyTemperatureEvening;

    @Min(0)
    @Max(100)
    private Integer oxygenSaturationMorning;

    @Min(0)
    @Max(100)
    private Integer oxygenSaturationEvening;

    @Size(max = 4096)
    String conditionNote;

    private Integer createdBy;

    private Timestamp createdDate;

    private Integer modifiedBy;

    private Timestamp modifiedDate;

    public DailyRecordForm() {
        this.id = new PhysicalConditionPK();
    }

    public PhysicalCondition toPhysicalCondition() {
        PhysicalCondition condition = new PhysicalCondition();
        try {
            BeanUtils.copyProperties(condition, this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        condition.setId(id);

        return condition;
    }

    public static DailyRecordForm valueOf(PhysicalCondition physicalCondition) {
        DailyRecordForm form = new DailyRecordForm();
        try {
            BeanUtils.copyProperties(form, physicalCondition);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new SimpleHealthLogException(e);
        }

        return form;

    }
}
