package jp.nauplius.app.shl.common.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the DAILY_HEALTH_DETAIL database table.
 * 
 */
@Embeddable
public class DailyHealthDetailPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="LOGIN_USER_ID", insertable=false, updatable=false)
	private int loginUserId;

	@Temporal(TemporalType.DATE)
	@Column(name="POSTED_DATE", insertable=false, updatable=false)
	private java.util.Date postedDate;

	@Column(name="TEMPLATE_ID", insertable=false, updatable=false)
	private int templateId;

	public DailyHealthDetailPK() {
	}
	public int getLoginUserId() {
		return this.loginUserId;
	}
	public void setLoginUserId(int loginUserId) {
		this.loginUserId = loginUserId;
	}
	public java.util.Date getPostedDate() {
		return this.postedDate;
	}
	public void setPostedDate(java.util.Date postedDate) {
		this.postedDate = postedDate;
	}
	public int getTemplateId() {
		return this.templateId;
	}
	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof DailyHealthDetailPK)) {
			return false;
		}
		DailyHealthDetailPK castOther = (DailyHealthDetailPK)other;
		return 
			(this.loginUserId == castOther.loginUserId)
			&& this.postedDate.equals(castOther.postedDate)
			&& (this.templateId == castOther.templateId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.loginUserId;
		hash = hash * prime + this.postedDate.hashCode();
		hash = hash * prime + this.templateId;
		
		return hash;
	}
}