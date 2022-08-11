package jp.nauplius.app.shl.common.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the user_info database table.
 *
 */
@Entity
@Table(name = "user_info")
@NamedQuery(name = "UserInfo.findAll", query = "SELECT u FROM UserInfo u")
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "USER_INFO_ID_GENERATOR", sequenceName = "USER_INFO_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_INFO_ID_GENERATOR")
    private Integer id;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "created_date")
    private Timestamp createdDate;

    private Boolean deleted;

    @Column(name = "deleted_date")
    private Timestamp deletedDate;

    @Column(name = "encrypted_password")
    private String encryptedPassword;

    @Column(name = "login_id")
    private String loginId;

    @Column(name = "mail_address")
    private String mailAddress;

    @Column(name = "modified_by")
    private Integer modifiedBy;

    @Column(name = "modified_date")
    private Timestamp modifiedDate;

    private String name;

    @Column(name = "role_id")
    private Integer roleId;

    private Integer status;

    @Column(name = "security_level")
    private Integer securityLevel;

    @Column(name = "encrypted_name")
    private String encryptedName;

    @Column(name = "encrypted_mail_address")
    private String encryptedMailAddress;

    //bi-directional many-to-one association to PhysicalCondition
    @OneToMany(mappedBy = "userInfo")
    private List<PhysicalCondition> physicalConditions;

    public UserInfo() {
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

    public Boolean getDeleted() {
        return this.deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Timestamp getDeletedDate() {
        return this.deletedDate;
    }

    public void setDeletedDate(Timestamp deletedDate) {
        this.deletedDate = deletedDate;
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRoleId() {
        return this.roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(Integer securityLevel) {
        this.securityLevel = securityLevel;
    }

    public String getEncryptedName() {
        return encryptedName;
    }

    public void setEncryptedName(String encryptedName) {
        this.encryptedName = encryptedName;
    }

    public String getEncryptedMailAddress() {
        return encryptedMailAddress;
    }

    public void setEncryptedMailAddress(String encryptedMailAddress) {
        this.encryptedMailAddress = encryptedMailAddress;
    }

    public List<PhysicalCondition> getPhysicalConditions() {
        return this.physicalConditions;
    }

    public void setPhysicalConditions(List<PhysicalCondition> physicalConditions) {
        this.physicalConditions = physicalConditions;
    }

    public PhysicalCondition addPhysicalCondition(PhysicalCondition physicalCondition) {
        getPhysicalConditions().add(physicalCondition);
        physicalCondition.setUserInfo(this);

        return physicalCondition;
    }

    public PhysicalCondition removePhysicalCondition(PhysicalCondition physicalCondition) {
        getPhysicalConditions().remove(physicalCondition);
        physicalCondition.setUserInfo(null);

        return physicalCondition;
    }

}