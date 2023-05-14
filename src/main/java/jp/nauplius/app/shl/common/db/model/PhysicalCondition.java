package jp.nauplius.app.shl.common.db.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * The persistent class for the physical_condition database table.
 *
 */
@Entity
@Table(name = "physical_condition")
@NamedQuery(name = "PhysicalCondition.findAll", query = "SELECT p FROM PhysicalCondition p")
public class PhysicalCondition implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private PhysicalConditionPK id;

    @Column(name = "awake_time")
    private Time awakeTime;

    @Column(name = "bed_time")
    private Time bedTime;

    @Column(name = "body_temperature_evening")
    private BigDecimal bodyTemperatureEvening;

    @Column(name = "body_temperature_morning")
    private BigDecimal bodyTemperatureMorning;

    @Column(name = "condition_note")
    private String conditionNote;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "modified_by")
    private Integer modifiedBy;

    @Column(name = "modified_date")
    private Timestamp modifiedDate;

    @Column(name = "oxygen_saturation_evening")
    private Integer oxygenSaturationEvening;

    @Column(name = "oxygen_saturation_morning")
    private Integer oxygenSaturationMorning;

    // bi-directional many-to-one association to UserInfo
    @ManyToOne
    @JoinColumn(name = "id")
    private UserInfo userInfo;

    public PhysicalCondition() {
    }

    public PhysicalConditionPK getId() {
        return this.id;
    }

    public void setId(PhysicalConditionPK id) {
        this.id = id;
    }

    public Time getAwakeTime() {
        return this.awakeTime;
    }

    public void setAwakeTime(Time awakeTime) {
        this.awakeTime = awakeTime;
    }

    public Time getBedTime() {
        return this.bedTime;
    }

    public void setBedTime(Time bedTime) {
        this.bedTime = bedTime;
    }

    public BigDecimal getBodyTemperatureEvening() {
        return this.bodyTemperatureEvening;
    }

    public void setBodyTemperatureEvening(BigDecimal bodyTemperatureEvening) {
        this.bodyTemperatureEvening = bodyTemperatureEvening;
    }

    public BigDecimal getBodyTemperatureMorning() {
        return this.bodyTemperatureMorning;
    }

    public void setBodyTemperatureMorning(BigDecimal bodyTemperatureMorning) {
        this.bodyTemperatureMorning = bodyTemperatureMorning;
    }

    public String getConditionNote() {
        return this.conditionNote;
    }

    public void setConditionNote(String conditionNote) {
        this.conditionNote = conditionNote;
    }

    public Integer getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getModifiedBy() {
        return this.modifiedBy;
    }

    public void setModifiedBy(Integer modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Timestamp getModifiedDate() {
        return this.modifiedDate;
    }

    public void setModifiedDate(Timestamp modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Integer getOxygenSaturationEvening() {
        return this.oxygenSaturationEvening;
    }

    public void setOxygenSaturationEvening(Integer oxygenSaturationEvening) {
        this.oxygenSaturationEvening = oxygenSaturationEvening;
    }

    public Integer getOxygenSaturationMorning() {
        return this.oxygenSaturationMorning;
    }

    public void setOxygenSaturationMorning(Integer oxygenSaturationMorning) {
        this.oxygenSaturationMorning = oxygenSaturationMorning;
    }

    public UserInfo getUserInfo() {
        return this.userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

}