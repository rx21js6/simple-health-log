package jp.nauplius.app.shl.common.model;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The primary key class for the DAILY_HEALTH_RECORD database table.
 *
 */
@Embeddable
public class DailyHealthRecordPK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name="LOGIN_USER_ID", insertable=false, updatable=false)
    private int loginUserId;

    @Column(name="POSTED_DATE")
    private LocalDate postedDate;

    public DailyHealthRecordPK() {
    }
    public int getLoginUserId() {
        return this.loginUserId;
    }
    public void setLoginUserId(int loginUserId) {
        this.loginUserId = loginUserId;
    }
    public LocalDate getPostedDate() {
        return this.postedDate;
    }
    public void setPostedDate(LocalDate postedDate) {
        this.postedDate = postedDate;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DailyHealthRecordPK)) {
            return false;
        }
        DailyHealthRecordPK castOther = (DailyHealthRecordPK)other;
        return
            (this.loginUserId == castOther.loginUserId)
            && this.postedDate.equals(castOther.postedDate);
    }

    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.loginUserId;
        hash = hash * prime + this.postedDate.hashCode();

        return hash;
    }
}