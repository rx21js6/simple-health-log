package jp.nauplius.app.shl.common.model;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;


/**
 * The persistent class for the encryptionKey_encryptionIv database table.
 *
 */
@Entity
@Table(name="key_iv")
@NamedQuery(name="KeyIv.findAll", query="SELECT k FROM KeyIv k")
@NoArgsConstructor
public class KeyIv implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Integer id;

    @Getter
    @Setter
    @Column(name="encryption_key", length=2147483647)
    private String encryptionKey;

    @Getter
    @Setter
    @Column(name="encryption_iv", length=2147483647)
    private String encryptionIv;

    @Getter
    @Setter
    @Column(name="created_date")
    private Timestamp createdDate;

    @Getter
    @Setter
    @Column(name="modified_date")
    private Timestamp modifiedDate;
}