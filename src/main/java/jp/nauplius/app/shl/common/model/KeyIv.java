package jp.nauplius.app.shl.common.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the KEY_IV database table.
 * 
 */
@Entity
@Table(name="KEY_IV")
@NamedQuery(name="KeyIv.findAll", query="SELECT k FROM KeyIv k")
public class KeyIv implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	@Column(name="CREATED_DATE")
	private Timestamp createdDate;

	@Column(name="ENCRYPTION_IV")
	private String encryptionIv;

	@Column(name="ENCRYPTION_KEY")
	private String encryptionKey;

	@Column(name="MODIFIED_DATE")
	private Timestamp modifiedDate;

	public KeyIv() {
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

	public String getEncryptionIv() {
		return this.encryptionIv;
	}

	public void setEncryptionIv(String encryptionIv) {
		this.encryptionIv = encryptionIv;
	}

	public String getEncryptionKey() {
		return this.encryptionKey;
	}

	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}

	public Timestamp getModifiedDate() {
		return this.modifiedDate;
	}

	public void setModifiedDate(Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

}