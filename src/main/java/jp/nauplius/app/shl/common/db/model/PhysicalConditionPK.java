package jp.nauplius.app.shl.common.db.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the physical_condition database table.
 * 
 */
@Embeddable
public class PhysicalConditionPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(insertable=false, updatable=false)
	private Integer id;

	@Column(name="recording_date")
	private String recordingDate;

	public PhysicalConditionPK() {
	}
	public Integer getId() {
		return this.id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getRecordingDate() {
		return this.recordingDate;
	}
	public void setRecordingDate(String recordingDate) {
		this.recordingDate = recordingDate;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof PhysicalConditionPK)) {
			return false;
		}
		PhysicalConditionPK castOther = (PhysicalConditionPK)other;
		return 
			this.id.equals(castOther.id)
			&& this.recordingDate.equals(castOther.recordingDate);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.id.hashCode();
		hash = hash * prime + this.recordingDate.hashCode();
		
		return hash;
	}
}