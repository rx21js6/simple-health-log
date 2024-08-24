package jp.nauplius.app.shl.common.db.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;

/**
 * The persistent class for the user_token database table.
 *
 */
@Entity
@Table(name = "user_token")
@NamedQuery(name = "UserToken.findAll", query = "SELECT u FROM UserToken u")
public class UserToken implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "expiration_date")
    private Timestamp expirationDate;

    private String token;

    public UserToken() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Timestamp getExpirationDate() {
        return this.expirationDate;
    }

    public void setExpirationDate(Timestamp expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
