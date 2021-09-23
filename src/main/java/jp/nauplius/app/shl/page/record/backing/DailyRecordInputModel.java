package jp.nauplius.app.shl.page.record.backing;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.model.PhysicalCondition;
import lombok.Getter;
import lombok.Setter;

@Named
@SessionScoped
public class DailyRecordInputModel implements Serializable {
    @Inject
    @Getter
    private PhysicalCondition physicalCondition;

    @Getter
    @Setter
    private PhysicalCondition previousPhysicalCondition;

    @Inject
    private PhysicalCondition conditionMirror;

    public void setPhysicalCondition(PhysicalCondition physicalCondition) {
        this.physicalCondition = physicalCondition;
        this.setMirror();
    }

    private void setMirror() {
        try {
            BeanUtils.copyProperties(this.conditionMirror, this.physicalCondition);
            if (Objects.isNull(this.conditionMirror.getConditionNote())) {
                this.conditionMirror.setConditionNote(StringUtils.EMPTY);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new SimpleHealthLogException(e);
        }
    }

    public void reset() {
        try {
            BeanUtils.copyProperties(this.physicalCondition, this.conditionMirror);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new SimpleHealthLogException(e);
        }
    }

    public boolean isModelEdited() {
        return !(Objects.equals(this.physicalCondition.getAwakeTime(), this.conditionMirror.getAwakeTime())
                && Objects.equals(this.physicalCondition.getBedTime(), this.conditionMirror.getBedTime())
                && Objects.equals(this.physicalCondition.getBodyTemperatureMorning(),
                        this.conditionMirror.getBodyTemperatureMorning())
                && Objects.equals(this.physicalCondition.getBodyTemperatureEvening(),
                        this.conditionMirror.getBodyTemperatureEvening())
                && Objects.equals(this.physicalCondition.getOxygenSaturationMorning(),
                        this.conditionMirror.getOxygenSaturationMorning())
                && Objects.equals(this.physicalCondition.getOxygenSaturationEvening(),
                        this.conditionMirror.getOxygenSaturationEvening())
                && Objects.equals(this.physicalCondition.getConditionNote(), this.conditionMirror.getConditionNote()));
    }

}
