package jp.nauplius.app.shl.common.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The persistent class for the user_role database table.
 *
 */
@Entity
@Table(name="user_role")
@NamedQuery(name="UserRole.findAll", query="SELECT u FROM UserRole u")
@NoArgsConstructor
public class UserRole implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    @Id
    @Column(unique=true, nullable=false)
    private Integer id;

    @Getter
    @Setter
    @Column(name="created_date")
    private Timestamp createdDate;

    @Getter
    @Setter
    @Column(name="modified_date")
    private Timestamp modifiedDate;

    @Getter
    @Setter
    @Column(name="role_name", length=20)
    private String roleName;

    //bi-directional many-to-one association to LoginUser
    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name="id", nullable=false, insertable=false, updatable=false)
    private LoginUser loginUser;
}