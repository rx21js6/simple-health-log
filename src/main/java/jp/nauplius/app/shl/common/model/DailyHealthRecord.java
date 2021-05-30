package jp.nauplius.app.shl.common.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the DAILY_HEALTH_RECORD database table.
 * 
 */
@Entity
@Table(name="DAILY_HEALTH_RECORD")
@NamedQuery(name="DailyHealthRecord.findAll", query="SELECT d FROM DailyHealthRecord d")
public class DailyHealthRecord implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private DailyHealthRecordPK id;

	@Column(name="CONDITION_NOTE")
	private String conditionNote;

	@Column(name="CREATED_DATE")
	private Timestamp createdDate;

	@Column(name="MODIFIED_DATE")
	private Timestamp modifiedDate;

	@Column(name="TEMP_EVENING")
	private double tempEvening;

	@Column(name="TEMP_MORNING")
	private double tempMorning;

	//bi-directional many-to-one association to DailyHealthDetail
	@OneToMany(mappedBy="dailyHealthRecord")
	private List<DailyHealthDetail> dailyHealthDetails;

	//bi-directional many-to-one association to LoginUser
	@ManyToOne
	@JoinColumn(name="LOGIN_USER_ID")
	private LoginUser loginUser;

	public DailyHealthRecord() {
	}

	public DailyHealthRecordPK getId() {
		return this.id;
	}

	public void setId(DailyHealthRecordPK id) {
		this.id = id;
	}

	public String getConditionNote() {
		return this.conditionNote;
	}

	public void setConditionNote(String conditionNote) {
		this.conditionNote = conditionNote;
	}

	public Timestamp getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public Timestamp getModifiedDate() {
		return this.modifiedDate;
	}

	public void setModifiedDate(Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public double getTempEvening() {
		return this.tempEvening;
	}

	public void setTempEvening(double tempEvening) {
		this.tempEvening = tempEvening;
	}

	public double getTempMorning() {
		return this.tempMorning;
	}

	public void setTempMorning(double tempMorning) {
		this.tempMorning = tempMorning;
	}

	public List<DailyHealthDetail> getDailyHealthDetails() {
		return this.dailyHealthDetails;
	}

	public void setDailyHealthDetails(List<DailyHealthDetail> dailyHealthDetails) {
		this.dailyHealthDetails = dailyHealthDetails;
	}

	public DailyHealthDetail addDailyHealthDetail(DailyHealthDetail dailyHealthDetail) {
		getDailyHealthDetails().add(dailyHealthDetail);
		dailyHealthDetail.setDailyHealthRecord(this);

		return dailyHealthDetail;
	}

	public DailyHealthDetail removeDailyHealthDetail(DailyHealthDetail dailyHealthDetail) {
		getDailyHealthDetails().remove(dailyHealthDetail);
		dailyHealthDetail.setDailyHealthRecord(null);

		return dailyHealthDetail;
	}

	public LoginUser getLoginUser() {
		return this.loginUser;
	}

	public void setLoginUser(LoginUser loginUser) {
		this.loginUser = loginUser;
	}

}