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
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The persistent class for the login_user database table.
 *
 */
@Entity
@Table(name="login_user")
@NamedQuery(name="LoginUser.findAll", query="SELECT l FROM LoginUser l")
@NoArgsConstructor
public class LoginUser implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Integer id;

    @Getter
    @Setter
    @Column(name="created_date", nullable=false)
    private Timestamp createdDate;

    @Getter
    @Setter
    @Column(nullable=false)
    private Boolean disabled;

    @Getter
    @Setter
    @Column(name="encrypted_password", length=2147483647)
    private String encryptedPassword;

    @Getter
    @Setter
    @Column(name="login_id", nullable=false, length=12)
    private String loginId;

    @Getter
    @Setter
    @Column(name="mail_address", length=2147483647)
    private String mailAddress;

    @Getter
    @Setter
    @Column(name="modified_date", nullable=false)
    private Timestamp modifiedDate;

    @Getter
    @Setter
    @Column(nullable=false, length=2147483647)
    private String name;

    //bi-directional many-to-one association to UserRole
    @Getter
    @Setter
    @OneToMany(mappedBy="loginUser")
    private List<UserRole> userRoles;

    public UserRole addUserRole(UserRole userRole) {
        getUserRoles().add(userRole);
        userRole.setLoginUser(this);

        return userRole;
    }

    public UserRole removeUserRole(UserRole userRole) {
        getUserRoles().remove(userRole);
        userRole.setLoginUser(null);

        return userRole;
    }

}