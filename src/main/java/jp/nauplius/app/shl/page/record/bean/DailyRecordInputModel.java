package jp.nauplius.app.shl.page.record.bean;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.Objects;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jp.nauplius.app.shl.common.constants.ShlConstants;
import jp.nauplius.app.shl.common.db.model.PhysicalCondition;
import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import lombok.Getter;
import lombok.Setter;

@Named
@SessionScoped
public class DailyRecordInputModel implements Serializable {
    @Inject
    @Getter
    private DailyRecordForm dailyRecordForm;

    @Getter
    @Setter
    private DailyRecordForm previousDailyRecordForm;

    // @Inject
    private PhysicalCondition conditionMirror;

    @Getter
    @Setter
    private String selectedDate;

    @PostConstruct
    public void postConstruct() {
        this.conditionMirror = new PhysicalCondition();
    }

    public void setPhysicalCondition(PhysicalCondition physicalCondition) {
        try {
            BeanUtils.copyProperties(this.dailyRecordForm, physicalCondition);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        this.setMirror();
    }

    private void setMirror() {
        try {
            BeanUtils.copyProperties(this.conditionMirror, this.dailyRecordForm);
            if (Objects.isNull(this.conditionMirror.getConditionNote())) {
                this.conditionMirror.setConditionNote(StringUtils.EMPTY);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new SimpleHealthLogException(e);
        }
    }

    public void reset() {
        try {
            BeanUtils.copyProperties(this.dailyRecordForm, this.conditionMirror);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new SimpleHealthLogException(e);
        }
    }

    public boolean isModelEdited() {
        return !(Objects.equals(this.dailyRecordForm.getAwakeTime(), this.conditionMirror.getAwakeTime())
                && Objects.equals(this.dailyRecordForm.getBedTime(), this.conditionMirror.getBedTime())
                && Objects.equals(this.dailyRecordForm.getBodyTemperatureMorning(),
                        this.conditionMirror.getBodyTemperatureMorning())
                && Objects.equals(this.dailyRecordForm.getBodyTemperatureEvening(),
                        this.conditionMirror.getBodyTemperatureEvening())
                && Objects.equals(this.dailyRecordForm.getOxygenSaturationMorning(),
                        this.conditionMirror.getOxygenSaturationMorning())
                && Objects.equals(this.dailyRecordForm.getOxygenSaturationEvening(),
                        this.conditionMirror.getOxygenSaturationEvening())
                && Objects.equals(this.dailyRecordForm.getConditionNote(), this.conditionMirror.getConditionNote()));
    }

    /**
     * 選択している日付をLocalDateに変換して返す。 未指定の場合はnullを返す
     *
     * @return
     */
    public LocalDate parseSelectedDate() {
        if (StringUtils.isEmpty(this.selectedDate)) {
            return null;
        }

        return LocalDate.parse(this.selectedDate, ShlConstants.RECORDING_DATE_FORMATTER);
    }
}
