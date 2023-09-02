package jp.nauplius.app.shl.common.db.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "not_entered_notice")
@NamedQuery(name = "NntEnteredNotice.findAll", query = "SELECT n FROM NotEnteredNotice n")
@Data
public class NotEnteredNotice {
    @Id
    private Integer id;

    @Column(name = "type_key")
    private String typeKey;

    @Column(name = "message_id")
    private String messageId;

    @Column(name = "checked")
    private boolean checked;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "modified_by")
    private Integer modifiedBy;

    @Column(name = "modified_date")
    private Timestamp modifiedDate;

}
