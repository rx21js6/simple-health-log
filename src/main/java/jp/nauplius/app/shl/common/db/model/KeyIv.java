package jp.nauplius.app.shl.common.db.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;

/**
 * The persistent class for the key_iv database table.
 *
 */
@Entity
@Table(name = "key_iv")
@NamedQuery(name = "KeyIv.findAll", query = "SELECT k FROM KeyIv k")
public class KeyIv implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "encryption_iv")
    private String encryptionIv;

    @Column(name = "encryption_key")
    private String encryptionKey;

    @Column(name = "modified_by")
    private Integer modifiedBy;

    @Column(name = "modified_date")
    private Timestamp modifiedDate;

    public KeyIv() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
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

}
