package jp.nauplius.app.shl.common.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the LOGIN_USER database table.
 * 
 */
@Entity
@Table(name="LOGIN_USER")
@NamedQuery(name="LoginUser.findAll", query="SELECT l FROM LoginUser l")
public class LoginUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	@Column(name="CREATED_DATE")
	private Timestamp createdDate;

	private boolean disabled;

	@Column(name="ENCRYPTED_PASSWORD")
	private String encryptedPassword;

	@Column(name="LOGIN_ID")
	private String loginId;

	@Column(name="MAIL_ADDRESS")
	private String mailAddress;

	@Column(name="MODIFIED_DATE")
	private Timestamp modifiedDate;

	private String name;

	//bi-directional many-to-one association to DailyHealthRecord
	@OneToMany(mappedBy="loginUser")
	private List<DailyHealthRecord> dailyHealthRecords;

	//bi-directional one-to-one association to UserRole
	@OneToOne(mappedBy="loginUser")
	private UserRole userRole;

	public LoginUser() {
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

	public boolean getDisabled() {
		return this.disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getEncryptedPassword() {
		return this.encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public String getLoginId() {
		return this.loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getMailAddress() {
		return this.mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
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

	public List<DailyHealthRecord> getDailyHealthRecords() {
		return this.dailyHealthRecords;
	}

	public void setDailyHealthRecords(List<DailyHealthRecord> dailyHealthRecords) {
		this.dailyHealthRecords = dailyHealthRecords;
	}

	public DailyHealthRecord addDailyHealthRecord(DailyHealthRecord dailyHealthRecord) {
		getDailyHealthRecords().add(dailyHealthRecord);
		dailyHealthRecord.setLoginUser(this);

		return dailyHealthRecord;
	}

	public DailyHealthRecord removeDailyHealthRecord(DailyHealthRecord dailyHealthRecord) {
		getDailyHealthRecords().remove(dailyHealthRecord);
		dailyHealthRecord.setLoginUser(null);

		return dailyHealthRecord;
	}

	public UserRole getUserRole() {
		return this.userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

}