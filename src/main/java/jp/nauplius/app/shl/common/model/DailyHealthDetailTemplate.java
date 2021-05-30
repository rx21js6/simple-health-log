package jp.nauplius.app.shl.common.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the DAILY_HEALTH_DETAIL_TEMPLATE database table.
 * 
 */
@Entity
@Table(name="DAILY_HEALTH_DETAIL_TEMPLATE")
@NamedQuery(name="DailyHealthDetailTemplate.findAll", query="SELECT d FROM DailyHealthDetailTemplate d")
public class DailyHealthDetailTemplate implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	@Column(name="CREATED_DATE")
	private Timestamp createdDate;

	private boolean deleted;

	@Column(name="MODIFIED_DATE")
	private Timestamp modifiedDate;

	private String name;

	//bi-directional many-to-one association to DailyHealthDetail
	@OneToMany(mappedBy="dailyHealthDetailTemplate")
	private List<DailyHealthDetail> dailyHealthDetails;

	public DailyHealthDetailTemplate() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Timestamp getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public boolean getDeleted() {
		return this.deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Timestamp getModifiedDate() {
		return this.modifiedDate;
	}

	public void setModifiedDate(Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DailyHealthDetail> getDailyHealthDetails() {
		return this.dailyHealthDetails;
	}

	public void setDailyHealthDetails(List<DailyHealthDetail> dailyHealthDetails) {
		this.dailyHealthDetails = dailyHealthDetails;
	}

	public DailyHealthDetail addDailyHealthDetail(DailyHealthDetail dailyHealthDetail) {
		getDailyHealthDetails().add(dailyHealthDetail);
		dailyHealthDetail.setDailyHealthDetailTemplate(this);

		return dailyHealthDetail;
	}

	public DailyHealthDetail removeDailyHealthDetail(DailyHealthDetail dailyHealthDetail) {
		getDailyHealthDetails().remove(dailyHealthDetail);
		dailyHealthDetail.setDailyHealthDetailTemplate(null);

		return dailyHealthDetail;
	}

}