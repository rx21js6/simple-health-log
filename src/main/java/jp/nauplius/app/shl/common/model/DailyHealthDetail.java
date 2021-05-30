package jp.nauplius.app.shl.common.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the DAILY_HEALTH_DETAIL database table.
 * 
 */
@Entity
@Table(name="DAILY_HEALTH_DETAIL")
@NamedQuery(name="DailyHealthDetail.findAll", query="SELECT d FROM DailyHealthDetail d")
public class DailyHealthDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private DailyHealthDetailPK id;

	@Column(name="CREATED_DATE")
	private Timestamp createdDate;

	@Column(name="HEALTH_DETAIL_ID")
	private int healthDetailId;

	@Column(name="MODIFIED_DATE")
	private Timestamp modifiedDate;

	private int value;

	//bi-directional many-to-one association to DailyHealthDetailTemplate
	@ManyToOne
	@JoinColumn(name="TEMPLATE_ID")
	private DailyHealthDetailTemplate dailyHealthDetailTemplate;

	//bi-directional many-to-one association to DailyHealthRecord
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name="LOGIN_USER_ID", referencedColumnName="LOGIN_USER_ID"),
		@JoinColumn(name="POSTED_DATE", referencedColumnName="POSTED_DATE")
		})
	private DailyHealthRecord dailyHealthRecord;

	public DailyHealthDetail() {
	}

	public DailyHealthDetailPK getId() {
		return this.id;
	}

	public void setId(DailyHealthDetailPK id) {
		this.id = id;
	}

	public Timestamp getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public int getHealthDetailId() {
		return this.healthDetailId;
	}

	public void setHealthDetailId(int healthDetailId) {
		this.healthDetailId = healthDetailId;
	}

	public Timestamp getModifiedDate() {
		return this.modifiedDate;
	}

	public void setModifiedDate(Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public int getValue() {
		return this.value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public DailyHealthDetailTemplate getDailyHealthDetailTemplate() {
		return this.dailyHealthDetailTemplate;
	}

	public void setDailyHealthDetailTemplate(DailyHealthDetailTemplate dailyHealthDetailTemplate) {
		this.dailyHealthDetailTemplate = dailyHealthDetailTemplate;
	}

	public DailyHealthRecord getDailyHealthRecord() {
		return this.dailyHealthRecord;
	}

	public void setDailyHealthRecord(DailyHealthRecord dailyHealthRecord) {
		this.dailyHealthRecord = dailyHealthRecord;
	}

}