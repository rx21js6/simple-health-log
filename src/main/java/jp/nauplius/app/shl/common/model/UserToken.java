package jp.nauplius.app.shl.common.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the user_token database table.
 * 
 */
@Entity
@Table(name="user_token")
@NamedQuery(name="UserToken.findAll", query="SELECT u FROM UserToken u")
public class UserToken implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;

	private String token;

	public UserToken() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}